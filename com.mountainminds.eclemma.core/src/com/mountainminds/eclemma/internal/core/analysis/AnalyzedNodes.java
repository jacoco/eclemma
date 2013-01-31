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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

/**
 * Internally used container for {@link IClassCoverage} and
 * {@link ISourceFileCoverage} nodes.
 */
final class AnalyzedNodes {

  static final AnalyzedNodes EMPTY = new AnalyzedNodes(
      Collections.<IClassCoverage> emptySet(),
      Collections.<ISourceFileCoverage> emptySet());

  private final Map<String, IClassCoverage> classmap;
  private final Map<String, ISourceFileCoverage> sourcemap;

  AnalyzedNodes(final Collection<IClassCoverage> classes,
      final Collection<ISourceFileCoverage> sourcefiles) {
    this.classmap = new HashMap<String, IClassCoverage>();
    for (final IClassCoverage c : classes) {
      classmap.put(c.getName(), c);
    }
    this.sourcemap = new HashMap<String, ISourceFileCoverage>();
    for (final ISourceFileCoverage s : sourcefiles) {
      final String key = sourceKey(s.getPackageName(), s.getName());
      sourcemap.put(key, s);
    }
  }

  IClassCoverage getClassCoverage(final String vmname) {
    return classmap.get(vmname);
  }

  ISourceFileCoverage getSourceFileCoverage(final String vmpackagename,
      final String filename) {
    return sourcemap.get(sourceKey(vmpackagename, filename));
  }

  private String sourceKey(final String vmpackagename, final String filename) {
    return vmpackagename + '/' + filename;
  }

}
