/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.internal.analysis.BundleCoverageImpl;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Internal class to analyze all Java elements of a particular coverage session.
 */
public class SessionAnalyzer {

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;
  private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

  private JavaModelCoverage modelcoverage;

  public IJavaModelCoverage processSession(ICoverageSession session,
      IProgressMonitor monitor) throws CoreException {
    PERFORMANCE.startTimer();
    PERFORMANCE.startMemoryUsage();
    modelcoverage = new JavaModelCoverage();
    IPath[] coveragefiles = session.getCoverageDataFiles();
    IClassFiles[] classfiles = session.getClassFiles();
    monitor
        .beginTask(
            NLS.bind(CoreMessages.AnalyzingCoverageSession_task,
                session.getDescription()), coveragefiles.length
                + classfiles.length);
    ExecutionDataStore executiondata = new ExecutionDataStore();
    for (int i = 0; i < coveragefiles.length && !monitor.isCanceled(); i++) {
      loadExecutionDataFile(executiondata, coveragefiles[i]);
      monitor.worked(1);
    }
    for (int i = 0; i < classfiles.length && !monitor.isCanceled(); i++) {
      processClasspathEntry(executiondata, classfiles[i],
          new SubProgressMonitor(monitor, 1));
    }
    monitor.done();
    PERFORMANCE.stopTimer("loading " + session.getDescription()); //$NON-NLS-1$
    PERFORMANCE.stopMemoryUsage("loading " + session.getDescription()); //$NON-NLS-1$
    return modelcoverage;
  }

  private void loadExecutionDataFile(ExecutionDataStore store, IPath path)
      throws CoreException {
    try {
      File f = path.toFile();
      if (f.exists()) {
        final InputStream in = new FileInputStream(f);
        final ExecutionDataReader reader = new ExecutionDataReader(in);
        reader.setExecutionDataVisitor(store);
        reader.setSessionInfoVisitor(new SessionInfoStore());
        while (reader.read()) {
          // nothing here
        }
        in.close();
      }
    } catch (IOException e) {
      throw new CoreException(
          EclEmmaStatus.COVERAGEDATA_FILE_READ_ERROR.getStatus(path, e));
    }
  }

  private void processClasspathEntry(ExecutionDataStore executiondata,
      IClassFiles classfiles, IProgressMonitor monitor) throws CoreException {

    // Analyze all class files for this class path entry:
    CoverageBuilder builder = new CoverageBuilder();
    Analyzer analyzer = new Analyzer(executiondata, builder);
    try {
      analyzer.analyzeAll(EclEmmaCorePlugin.getAbsolutePath(
          classfiles.getLocation()).toFile());
    } catch (IOException e) {
      // TODO wrong status info
      throw new CoreException(
          EclEmmaStatus.METADATA_FILE_READ_ERROR.getStatus(e));
    }

    // Calculate coverage for each fragment root separately:
    final TypeVisitor visitor = new TypeVisitor(builder.getClasses(),
        builder.getSourceFiles());
    final IPackageFragmentRoot[] roots = classfiles.getPackageFragmentRoots();
    monitor.beginTask("", roots.length); //$NON-NLS-1$
    for (IPackageFragmentRoot root : roots) {
      final SubProgressMonitor submonitor = new SubProgressMonitor(monitor, 1);
      processPackageFragmentRoot(root, visitor, submonitor);
    }
    visitor.dumpRemainder();
    monitor.done();
  }

  private void processPackageFragmentRoot(IPackageFragmentRoot root,
      TypeVisitor visitor, IProgressMonitor monitor) throws JavaModelException {
    final TypeTraverser traverser = new TypeTraverser(root);
    visitor.reset();
    traverser.process(visitor, monitor);

    IBundleCoverage bundle = new BundleCoverageImpl(root.getElementName(),
        visitor.getClasses(), visitor.getSources());
    modelcoverage.putFragmentRoot(root, bundle);
    putPackages(bundle.getPackages(), root);
  }

  private void putPackages(Collection<IPackageCoverage> packages,
      IPackageFragmentRoot root) {
    for (IPackageCoverage c : packages) {
      final String name = c.getName().replace('/', '.');
      final IPackageFragment fragment = root.getPackageFragment(name);
      modelcoverage.putFragment(fragment, c);
    }
  }

  private class TypeVisitor implements ITypeVisitor {

    private final Map<String, IClassCoverage> classmap;
    private final Map<String, ISourceFileCoverage> sourcemap;
    private final Collection<IClassCoverage> classes;

    private final Collection<ISourceFileCoverage> sources;

    TypeVisitor(Collection<IClassCoverage> classes,
        Collection<ISourceFileCoverage> sourcefiles) {
      this.classmap = new HashMap<String, IClassCoverage>();
      for (final IClassCoverage c : classes) {
        classmap.put(c.getName(), c);
      }
      this.sourcemap = new HashMap<String, ISourceFileCoverage>();
      for (final ISourceFileCoverage s : sourcefiles) {
        final String key = s.getPackageName() + '/' + s.getName();
        sourcemap.put(key, s);
      }
      this.classes = new ArrayList<IClassCoverage>();
      this.sources = new ArrayList<ISourceFileCoverage>();
    }

    Collection<IClassCoverage> getClasses() {
      return classes;
    }

    Collection<ISourceFileCoverage> getSources() {
      return sources;
    }

    public void visit(IType type, String vmname) {
      IClassCoverage coverage = classmap.remove(vmname);
      if (coverage != null) {
        classes.add(coverage);
        modelcoverage.putType(type, coverage);
      }
    }

    public void visit(ICompilationUnit unit) throws JavaModelException {
      final String key = unit.getParent().getElementName().replace('.', '/')
          + '/' + unit.getElementName();
      ISourceFileCoverage coverage = sourcemap.remove(key);
      if (coverage != null) {
        sources.add(coverage);
        modelcoverage.putCompilationUnit(unit, coverage);
      }
    }

    void reset() {
    }

    void dumpRemainder() {
      // dump what's left
      for (final String name : classmap.keySet()) {
        TRACER.trace("Instrumented type {0} has not been processed.", name); //$NON-NLS-1$
      }
      for (final String name : sourcemap.keySet()) {
        TRACER.trace("Instrumented source {0} has not been processed.", name); //$NON-NLS-1$
      }
    }

  }

}
