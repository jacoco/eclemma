/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * Analyzes the class files that belong to given package fragment roots. This
 * analyzer implements an cache to remember the class files that have been
 * analyzed before.
 */
final class PackageFragementRootAnalyzer {

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

  private final ExecutionDataStore executiondata;
  private final Map<Object, AnalyzedNodes> cache;

  PackageFragementRootAnalyzer(final ExecutionDataStore executiondata) {
    this.executiondata = executiondata;
    this.cache = new HashMap<Object, AnalyzedNodes>();
  }

  AnalyzedNodes analyze(final IPackageFragmentRoot root) throws CoreException {
    if (root.isExternal()) {
      return analyzeExternal(root);
    } else {
      return analyzeInternal(root);
    }
  }

  private AnalyzedNodes analyzeInternal(final IPackageFragmentRoot root)
      throws CoreException {
    IResource location = null;
    try {
      location = getClassfilesLocation(root);

      if (location == null) {
        TRACER.trace("No class files found for package fragment root {0}", //$NON-NLS-1$
            root.getPath());
        return AnalyzedNodes.EMPTY;
      }

      AnalyzedNodes nodes = cache.get(location);
      if (nodes != null) {
        return nodes;
      }

      final CoverageBuilder builder = new CoverageBuilder();
      final Analyzer analyzer = new Analyzer(executiondata, builder);
      new ResourceTreeWalker(analyzer).walk(location);
      nodes = new AnalyzedNodes(builder.getClasses(), builder.getSourceFiles());
      cache.put(location, nodes);
      return nodes;
    } catch (Exception e) {
      throw new CoreException(EclEmmaStatus.BUNDLE_ANALYSIS_ERROR.getStatus(
          root.getElementName(), location, e));
    }
  }

  private AnalyzedNodes analyzeExternal(final IPackageFragmentRoot root)
      throws CoreException {
    IPath location = null;
    try {
      location = root.getPath();

      AnalyzedNodes nodes = cache.get(location);
      if (nodes != null) {
        return nodes;
      }

      final CoverageBuilder builder = new CoverageBuilder();
      final Analyzer analyzer = new Analyzer(executiondata, builder);
      new ResourceTreeWalker(analyzer).walk(location);
      nodes = new AnalyzedNodes(builder.getClasses(), builder.getSourceFiles());
      cache.put(location, nodes);
      return nodes;
    } catch (Exception e) {
      throw new CoreException(EclEmmaStatus.BUNDLE_ANALYSIS_ERROR.getStatus(
          root.getElementName(), location, e));
    }
  }

  private IResource getClassfilesLocation(IPackageFragmentRoot root)
      throws CoreException {

    // For binary roots the underlying resource directly points to class files:
    if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
      return root.getResource();
    }

    // For source roots we need to find the corresponding output folder:
    IPath path = root.getRawClasspathEntry().getOutputLocation();
    if (path == null) {
      path = root.getJavaProject().getOutputLocation();
    }
    return root.getResource().getWorkspace().getRoot().findMember(path);
  }

}
