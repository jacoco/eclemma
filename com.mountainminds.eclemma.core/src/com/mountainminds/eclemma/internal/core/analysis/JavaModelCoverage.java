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
import org.eclipse.jdt.core.Signature;
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
  
  /** Maps IType to UnboundMethodCoverage[] for lazy method resolving */
  private final Map lazymethodcoverage = new HashMap();

  private static class UnboundMethodCoverage {
    final String name;
    final String signature;
    final IJavaElementCoverage coverage;
    UnboundMethodCoverage(String name, String signature, IJavaElementCoverage coverage) {
      this.name = name;
      this.signature = signature;
      this.coverage = coverage;
    }
  }
  
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
    monitor.beginTask(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()),
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
      TypeTraverser jep = new TypeTraverser(roots);
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

    public void visit(IType type, String vmname) {
      ClassDescriptor descriptor = (ClassDescriptor) descriptors.remove(vmname);
      if (descriptor != null) {
        DataHolder data = coveragedata == null ? null : coveragedata.getCoverage(descriptor);
        if (data != null && data.m_stamp != descriptor.getStamp()) {
          TRACER.trace("Invalid meta data signature for {0}.", descriptor.getClassVMName()); //$NON-NLS-1$
        } else {
          MethodDescriptor[] methods = descriptor.getMethods();
          UnboundMethodCoverage[] ubcoverage = new UnboundMethodCoverage[methods.length];
          boolean[][] covered = data == null ? null : data.m_coverage;
          for (int i = 0; i < methods.length; i++) {
            ubcoverage[i] = processMethodCoverage(type, methods[i], covered == null ? null : covered[i]);
          }
          lazymethodcoverage.put(type, ubcoverage);
          getCoverage(type, false).addType(data != null);
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
  
  private boolean isMethodCovered(boolean[] blocks) {
    for (int i = 0; blocks != null && i < blocks.length; i++) {
      if (blocks[i]) return true;
    }
    return false;
  }
  
  private UnboundMethodCoverage processMethodCoverage(IType parenttype, MethodDescriptor descriptor, boolean[] covered) {
    boolean haslines = descriptor.getBlockMap() != null;
    JavaElementCoverage parentcoverage = getCoverage(parenttype, haslines);
    IResource res = parenttype.getResource();
    // TODO this is the current time stamp, actually we would need the stamp
    // at the time the resource was instrumented.
    long stamp = res == null ? 0 : res.getModificationStamp();
    JavaElementCoverage coverage = new JavaElementCoverage(parentcoverage, haslines, stamp);
    coverage.addMethod(isMethodCovered(covered));
    int[] blocksizes = descriptor.getBlockSizes();
    if (blocksizes != null) {
      int blockcount = blocksizes.length;
      int[][] blocklines = descriptor.getBlockMap();
      for (int i = 0; i < blockcount; i++) {
        coverage.addBlock(blocksizes[i], blocklines == null ? null : blocklines[i],
            covered == null ? false : covered[i]);
      }
    }
    return new UnboundMethodCoverage(descriptor.getName(), descriptor.getDescriptor(), coverage);
  }

  private JavaElementCoverage getCoverage(IJavaElement element, boolean haslines) {
    if (element == null)
      return null;
    JavaElementCoverage c = (JavaElementCoverage) getCoverageFor(element);
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

  private void resolveUnboundMethods(IType type) {
    UnboundMethodCoverage[] ubcoverage = (UnboundMethodCoverage[]) lazymethodcoverage.remove(type);
    if (ubcoverage != null) {
      for (int i = 0; i < ubcoverage.length; i++) {
        String name = ubcoverage[i].name;
        if (name.equals("<init>")) {
          name = type.getElementName();
        }
        String[] paramtypes = Signature.getParameterTypes(ubcoverage[i].signature); 
        for (int j = 0; j < paramtypes.length; j++) {
          paramtypes[j] = paramtypes[j].replace('/', '.');
        }
        IMethod pattern = type.getMethod(name, paramtypes);
        IMethod[] hits = type.findMethods(pattern);
        if (hits != null && hits.length == 1) {
          coveragemap.put(hits[0], ubcoverage[i].coverage);
        } else {
          TRACER.trace("Method not found in Java model: {0}.{1}{2}", type.getElementName(), name, ubcoverage[i].signature);
        }
      }
    }
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
    IJavaElementCoverage c = (IJavaElementCoverage) coveragemap.get(element);
    if (c == null && element.getElementType() == IJavaElement.METHOD) {
      resolveUnboundMethods((IType) element.getParent());
      c = (IJavaElementCoverage) coveragemap.get(element);
    }
    return c;
  }

}
