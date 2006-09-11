/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.IInstrumentation;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;
import com.vladium.emma.data.ClassDescriptor;
import com.vladium.emma.data.DataFactory;
import com.vladium.emma.data.ICoverageData;
import com.vladium.emma.data.IMergeable;
import com.vladium.emma.data.IMetaData;
import com.vladium.emma.data.MethodDescriptor;
import com.vladium.emma.data.ICoverageData.DataHolder;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaModelCoverage extends JavaElementCoverage implements
    IJavaModelCoverage {

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;
  private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

  /** Maps Java elements to coverage objects */
  private final Map coveragemap = new HashMap();

  private final List projects = new ArrayList();

  private final List fragmentroots = new ArrayList();

  private final List fragments = new ArrayList();

  private final List types = new ArrayList();

  public JavaModelCoverage(ICoverageSession session, IProgressMonitor monitor)
      throws CoreException {
    super(null, false, 0);
    PERFORMANCE.startTimer();
    PERFORMANCE.startMemoryUsage();
    processSession(session, monitor);
    PERFORMANCE.stopTimer("loading " + session.getDescription()); //$NON-NLS-1$
    PERFORMANCE.stopMemoryUsage("loading " + session.getDescription()); //$NON-NLS-1$
  }

  private void processSession(ICoverageSession session, IProgressMonitor monitor)
      throws CoreException {
    IPath[] coveragefiles = session.getCoverageDataFiles();
    IInstrumentation[] instrumentations = session.getInstrumentations();
    monitor.beginTask(NLS.bind(CoreMessages.AnalyzingCoverageSessionTask, session.getDescription()),
        coveragefiles.length + instrumentations.length);
    ICoverageData coveragedata = null;
    for (int i = 0; i < coveragefiles.length && !monitor.isCanceled(); i++) {
      coveragedata = processCoveragedataFile(coveragedata, coveragefiles[i]);
      monitor.worked(1);
    }
    for (int i = 0; i < instrumentations.length && !monitor.isCanceled(); i++) {
      processMetadataFile(coveragedata, instrumentations[i],
          new SubProgressMonitor(monitor, 1));
    }
    monitor.done();
  }

  private ICoverageData processCoveragedataFile(ICoverageData coveragedata,
      IPath path) throws CoreException {
    try {
      File f = path.toFile();
      if (f.exists()) {
        IMergeable data = DataFactory.load(f)[DataFactory.TYPE_COVERAGEDATA];
        if (coveragedata == null) {
          coveragedata = (ICoverageData) data;
        } else {
          coveragedata = (ICoverageData) coveragedata.merge(data);
        }
      }
      return coveragedata;
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.COVERAGEDATA_FILE_READ_ERROR
          .getStatus(path, e));
    }
  }

  private void processMetadataFile(ICoverageData coveragedata, IInstrumentation instrumentation,
      IProgressMonitor monitor) throws CoreException {
    IPath metadatafile = instrumentation.getMetaDataFile();
    File f = metadatafile.toFile();
    if (f.exists()) {
      IMetaData metadata;
      try {
        metadata = (IMetaData) DataFactory.load(f)[DataFactory.TYPE_METADATA];
      } catch (IOException e) {
        throw new CoreException(EclEmmaStatus.METADATA_FILE_READ_ERROR.getStatus(
            metadatafile, e));
      }
      IPackageFragmentRoot[] roots = instrumentation.getClassFiles().getPackageFragmentRoots();
      JavaElementsTraverser jep = new JavaElementsTraverser(roots);
      jep.process(new TypeVisitor(metadata, coveragedata), monitor);
    }
  }
  
  private class TypeVisitor implements ITypeVisitor {
    
    private final ICoverageData coveragedata;
    private final Map descriptors;
    
    TypeVisitor(IMetaData metadata, ICoverageData coveragedata) {
      this.coveragedata = coveragedata;
      this.descriptors = new HashMap();
      for (Iterator i = metadata.iterator(); i.hasNext(); ) {
        ClassDescriptor cd = (ClassDescriptor) i.next();
        descriptors.put(cd.getClassVMName(), cd);
      }
    }

    public IMethodVisitor visit(IType type, String vmname) {
      ClassDescriptor descriptor = (ClassDescriptor) descriptors.remove(vmname);
      if (descriptor == null) {
        return null;
      } else {
        DataHolder data = coveragedata == null ? null : coveragedata
            .getCoverage(descriptor);
        if (data != null && data.m_stamp != descriptor.getStamp()) {
          TRACER.trace("Invalid meta data signature for {0}.", descriptor.getClassVMName()); //$NON-NLS-1$
          return null;
        } else {
          return new MethodVisitor(type, descriptor, data);
        }
      }
    }

    public void done() {
      // dump what's left
      for (Iterator i = descriptors.keySet().iterator(); i.hasNext(); ) {
        TRACER.trace("Instrumented type {0} has not been processed.", i.next()); //$NON-NLS-1$
      }
    }
    
  }
  
  private class MethodVisitor implements IMethodVisitor {

    private final IType type;
    private final Map descriptors;
    
    MethodVisitor(IType type, ClassDescriptor descriptor, DataHolder data) {
      this.type = type;
      descriptors = new HashMap();
      MethodDescriptor[] methods = descriptor.getMethods();
      boolean[][] covered = data == null ? null : data.m_coverage;
      for (int i = 0; i < methods.length; i++) {
        MethodDescriptor md = methods[i];
        MethodCoverage mc = new MethodCoverage(md, covered == null ? null : covered[i]);
        descriptors.put(md.getName() + md.getDescriptor(), mc);
      }
    }
    
    public void visit(IMethod method, String vmsignature) {
      MethodCoverage mc = (MethodCoverage) descriptors.remove(vmsignature);
      if (mc == null) {
        TRACER.trace("Method {0} not found in {1}", vmsignature, type.getElementName()); //$NON-NLS-1$
      } else {
        processElementCoverage(method, mc);
      }      
    }

    public void done() {
      // process additional code not available in the source model:
      for (Iterator i = descriptors.values().iterator(); i.hasNext(); ) {
        MethodCoverage mc = (MethodCoverage) i.next();
        processElementCoverage(type, mc);
      }      
    }
    
  }
  
  private static class MethodCoverage {
    final MethodDescriptor descriptor;
    final  boolean[] covered;
    MethodCoverage(MethodDescriptor descriptor, boolean[] covered) {
      this.descriptor = descriptor;
      this.covered = covered;
    }
  }
  
  private void processElementCoverage(IJavaElement element, MethodCoverage mc) {
    MethodDescriptor descriptor = mc.descriptor;
    JavaElementCoverage coverage = getCoverage(element, descriptor
        .getBlockMap() != null);
    int[] blocksizes = descriptor.getBlockSizes();
    if (blocksizes == null)
      return;
    int blockcount = blocksizes.length;
    int[][] blocklines = descriptor.getBlockMap();
    for (int i = 0; i < blockcount; i++) {
      coverage.addBlock(blocksizes[i], blocklines == null ? null : blocklines[i],
          mc.covered == null ? false : mc.covered[i]);
    }
  }

  private JavaElementCoverage getCoverage(IJavaElement element, boolean haslines) {
    if (element == null)
      return null;
    JavaElementCoverage c = (JavaElementCoverage) coveragemap.get(element);
    if (c == null) {
      IResource res = element.getResource();
      // TODO this is the current time stamp, actually we would need the stamp
      // at the time the resource was instrumented.
      long stamp = res == null ? 0 : res.getModificationStamp();
      switch (element.getElementType()) {
      case IJavaElement.JAVA_MODEL:
        c = this;
        break;
      case IJavaElement.JAVA_PROJECT:
        c = new JavaElementCoverage(getCoverage(element.getParent(), false),
            false, stamp);
        projects.add(element);
        break;
      case IJavaElement.PACKAGE_FRAGMENT_ROOT:
        c = new JavaElementCoverage(getCoverage(element.getParent(), false),
            false, stamp);
        fragmentroots.add(element);
        break;
      case IJavaElement.PACKAGE_FRAGMENT:
        c = new JavaElementCoverage(getCoverage(element.getParent(), false),
            false, stamp);
        fragments.add(element);
        break;
      case IJavaElement.TYPE:
        c = new JavaElementCoverage(getCoverage(element.getParent(), haslines),
            haslines, stamp);
        types.add(element);
        break;
      default:
        c = new JavaElementCoverage(getCoverage(element.getParent(), haslines),
            haslines, stamp);
      }
      coveragemap.put(element, c);
    }
    return c;
  }

  // IJavaModelCoverage interface

  public IJavaProject[] getInstrumentedProjects() {
    IJavaProject[] arr = new IJavaProject[projects.size()];
    return (IJavaProject[]) projects.toArray(arr);
  }

  public IPackageFragmentRoot[] getInstrumentedPackageFragmentRoots() {
    IPackageFragmentRoot[] arr = new IPackageFragmentRoot[fragmentroots.size()];
    return (IPackageFragmentRoot[]) fragmentroots.toArray(arr);
  }

  public IPackageFragment[] getInstrumentedPackageFragments() {
    IPackageFragment[] arr = new IPackageFragment[fragments.size()];
    return (IPackageFragment[]) fragments.toArray(arr);
  }

  public IType[] getInstrumentedTypes() {
    IType[] arr = new IType[types.size()];
    return (IType[]) types.toArray(arr);
  }

  public IJavaElementCoverage getCoverageFor(IJavaElement element) {
    return (IJavaElementCoverage) coveragemap.get(element);
  }

}
