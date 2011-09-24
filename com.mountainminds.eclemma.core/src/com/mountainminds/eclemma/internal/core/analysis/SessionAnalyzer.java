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
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.internal.analysis.BundleCoverageImpl;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * Internal class to analyze all Java elements of a particular coverage session.
 */
public class SessionAnalyzer {

  private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

  private JavaModelCoverage modelcoverage;

  public IJavaModelCoverage processSession(ICoverageSession session,
      IProgressMonitor monitor) throws CoreException {
    PERFORMANCE.startTimer();
    PERFORMANCE.startMemoryUsage();
    modelcoverage = new JavaModelCoverage();
    final Collection<IPath> executiondatafiles = session.getExecutionDataFiles();
    final Collection<IPackageFragmentRoot> roots = session.getScope();
    monitor
        .beginTask(
            NLS.bind(CoreMessages.AnalyzingCoverageSession_task,
                session.getDescription()),
            executiondatafiles.size() + roots.size());
    final ExecutionDataStore executiondata = new ExecutionDataStore();
    for (final IPath file : executiondatafiles) {
      if (monitor.isCanceled()) {
        break;
      }
      loadExecutionDataFile(executiondata, file);
      monitor.worked(1);
    }

    final PackageFragementRootAnalyzer analyzer = new PackageFragementRootAnalyzer(
        executiondata);

    for (final IPackageFragmentRoot root : roots) {
      if (monitor.isCanceled()) {
        break;
      }
      processPackageFragmentRoot(root, analyzer, new SubProgressMonitor(
          monitor, 1));
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

  private void processPackageFragmentRoot(IPackageFragmentRoot root,
      PackageFragementRootAnalyzer analyzer, IProgressMonitor monitor)
      throws CoreException {
    final TypeVisitor visitor = new TypeVisitor(analyzer.analyze(root));
    new TypeTraverser(root).process(visitor, monitor);

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

    private final AnalyzedNodes nodes;

    private final Collection<IClassCoverage> classes;
    private final Collection<ISourceFileCoverage> sources;

    TypeVisitor(AnalyzedNodes nodes) {
      this.nodes = nodes;
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
      IClassCoverage coverage = nodes.getClassCoverage(vmname);
      if (coverage != null) {
        classes.add(coverage);
        modelcoverage.putType(type, coverage);
      }
    }

    public void visit(ICompilationUnit unit) throws JavaModelException {
      final String vmpackagename = unit.getParent().getElementName()
          .replace('.', '/');
      final ISourceFileCoverage coverage = nodes.getSourceFileCoverage(
          vmpackagename, unit.getElementName());
      if (coverage != null) {
        sources.add(coverage);
        modelcoverage.putCompilationUnit(unit, coverage);
      }
    }

  }

}
