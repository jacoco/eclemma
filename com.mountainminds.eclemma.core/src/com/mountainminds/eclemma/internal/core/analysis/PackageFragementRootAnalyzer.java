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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;

import com.mountainminds.eclemma.core.EclEmmaStatus;

/**
 * Analyzes the class files that belong to given package fragment roots. This
 * analyzer implements an cache to remember the class files that have been
 * analyzed before.
 */
final class PackageFragementRootAnalyzer {

  private final ExecutionDataStore executiondata;
  private final Map<IPath, AnalyzedNodes> cache;

  PackageFragementRootAnalyzer(final ExecutionDataStore executiondata) {
    this.executiondata = executiondata;
    this.cache = new HashMap<IPath, AnalyzedNodes>();
  }

  AnalyzedNodes analyze(final IPackageFragmentRoot root) throws CoreException {
    final IPath path = getClassfilesLocation(root);
    AnalyzedNodes nodes = cache.get(path);
    if (nodes == null) {
      nodes = analyze(root, path);
      cache.put(path, nodes);
    }
    return nodes;
  }

  private AnalyzedNodes analyze(final IPackageFragmentRoot root,
      final IPath path) throws CoreException {
    final CoverageBuilder builder = new CoverageBuilder();
    final Analyzer analyzer = new Analyzer(executiondata, builder);

    try {
      analyzer.analyzeAll(path.toFile());
    } catch (Exception e) {
      throw new CoreException(EclEmmaStatus.BUNDLE_ANALYSIS_ERROR.getStatus(
          root.getElementName(), path, e));
    }

    return new AnalyzedNodes(builder.getClasses(), builder.getSourceFiles());
  }

  private IPath getClassfilesLocation(IPackageFragmentRoot root)
      throws CoreException {

    // 1. Find path depending on root type (source/binary)
    IPath path;
    if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
      path = root.getRawClasspathEntry().getOutputLocation();
      if (path == null) {
        path = root.getJavaProject().getOutputLocation();
      }
    } else {
      path = root.getPath();
    }

    // 2. Determine absolute path
    if (path.getDevice() == null) {
      final IWorkspace ws = root.getJavaProject().getProject().getWorkspace();
      final IResource res = ws.getRoot().findMember(path);
      if (res != null) {
        return res.getLocation();
      }
    }
    return path;
  }

}
