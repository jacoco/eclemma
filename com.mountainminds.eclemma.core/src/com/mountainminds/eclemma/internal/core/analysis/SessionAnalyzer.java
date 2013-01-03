/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
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
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.internal.analysis.BundleCoverageImpl;

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

  private ExecutionDataStore executiondatastore;

  private SessionInfoStore sessioninfostore;

  public IJavaModelCoverage processSession(ICoverageSession session,
      IProgressMonitor monitor) throws CoreException {
    PERFORMANCE.startTimer();
    PERFORMANCE.startMemoryUsage();
    modelcoverage = new JavaModelCoverage();
    final Collection<IPackageFragmentRoot> roots = session.getScope();
    monitor.beginTask(
        NLS.bind(CoreMessages.AnalyzingCoverageSession_task,
            session.getDescription()), 1 + roots.size());
    executiondatastore = new ExecutionDataStore();
    sessioninfostore = new SessionInfoStore();
    session.accept(executiondatastore, sessioninfostore);
    monitor.worked(1);

    final PackageFragementRootAnalyzer analyzer = new PackageFragementRootAnalyzer(
        executiondatastore);

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

  public List<SessionInfo> getSessionInfos() {
    return sessioninfostore.getInfos();
  }

  public Collection<ExecutionData> getExecutionData() {
    return executiondatastore.getContents();
  }

  private void processPackageFragmentRoot(IPackageFragmentRoot root,
      PackageFragementRootAnalyzer analyzer, IProgressMonitor monitor)
      throws CoreException {
    final TypeVisitor visitor = new TypeVisitor(analyzer.analyze(root));
    new TypeTraverser(root).process(visitor, monitor);

    final IBundleCoverage bundle = new BundleCoverageImpl(getName(root),
        visitor.getClasses(), visitor.getSources());
    modelcoverage.putFragmentRoot(root, bundle);
    putPackages(bundle.getPackages(), root);
  }

  // package private for testing
  String getName(IPackageFragmentRoot root) {
    IPath path = root.getPath();
    if (!root.isExternal() && path.segmentCount() > 1) {
      return path.removeFirstSegments(1).toString();
    } else {
      return path.lastSegment();
    }
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

    private final Set<IClassCoverage> classes;
    private final Set<ISourceFileCoverage> sources;

    TypeVisitor(AnalyzedNodes nodes) {
      this.nodes = nodes;
      this.classes = new HashSet<IClassCoverage>();
      this.sources = new HashSet<ISourceFileCoverage>();
    }

    Collection<IClassCoverage> getClasses() {
      return classes;
    }

    Collection<ISourceFileCoverage> getSources() {
      return sources;
    }

    public void visit(IType type, String vmname) {
      final IClassCoverage coverage = nodes.getClassCoverage(vmname);
      if (coverage != null) {
        classes.add(coverage);
        modelcoverage.putType(type, coverage);
      }
    }

    public void visit(IClassFile classfile) throws JavaModelException {
      final String vmname = classfile.getType().getFullyQualifiedName()
          .replace('.', '/');
      final IClassCoverage coverage = nodes.getClassCoverage(vmname);
      if (coverage != null) {
        modelcoverage.putClassFile(classfile, coverage);
        // Add source file coverage manually in case of binary roots
        // as we will not see compilation units:
        final ISourceFileCoverage source = nodes.getSourceFileCoverage(
            coverage.getPackageName(), coverage.getSourceFileName());
        if (source != null) {
          sources.add(source);
        }
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
