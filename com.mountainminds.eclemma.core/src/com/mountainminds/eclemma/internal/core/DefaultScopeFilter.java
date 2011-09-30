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
package com.mountainminds.eclemma.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import com.mountainminds.eclemma.core.ICorePreferences;

/**
 * Utility to retrieve the list of {@link IClassFiles} that will be instrumented
 * by default.
 */
public class DefaultScopeFilter {

  private final ICorePreferences preferences;

  /**
   * Creates a new filter based on the given preferences.
   * 
   * @param preferences
   *          call-back to retrieve current settings from.
   */
  public DefaultScopeFilter(final ICorePreferences preferences) {
    this.preferences = preferences;
  }

  /**
   * Returns a filtered copy of the given {@link IClassFiles} array.
   * 
   * @param classfiles
   *          {@link IClassFiles} to filter
   * @param configuration
   *          context information
   * @return filtered list
   * @throws CoreException
   *           may occur when accessing the Java model
   */
  public Collection<IPackageFragmentRoot> filter(
      final Collection<IPackageFragmentRoot> scope,
      final ILaunchConfiguration configuration) throws CoreException {
    final Collection<IPackageFragmentRoot> filtered = new ArrayList<IPackageFragmentRoot>(
        scope);
    if (preferences.getDefaultInstrumentationSourceFoldersOnly()) {
      sourceFoldersOnly(filtered);
    }
    if (preferences.getDefaultInstrumentationSameProjectOnly()) {
      sameProjectOnly(filtered, configuration);
    }
    String filter = preferences.getDefaultInstrumentationFilter();
    if (filter != null && filter.length() > 0) {
      matchingPathsOnly(filtered, filter);
    }
    return filtered;
  }

  private void sourceFoldersOnly(Collection<IPackageFragmentRoot> filtered)
      throws JavaModelException {
    for (final Iterator<IPackageFragmentRoot> i = filtered.iterator(); i
        .hasNext();) {
      if (i.next().getKind() != IPackageFragmentRoot.K_SOURCE) {
        i.remove();
      }
    }
  }

  private void sameProjectOnly(Collection<IPackageFragmentRoot> filtered,
      final ILaunchConfiguration configuration) throws CoreException {
    final IJavaProject javaProject = JavaRuntime.getJavaProject(configuration);
    if (javaProject != null) {
      for (final Iterator<IPackageFragmentRoot> i = filtered.iterator(); i
          .hasNext();) {
        if (!javaProject.equals(i.next().getJavaProject())) {
          i.remove();
        }
      }
    }
  }

  private void matchingPathsOnly(Collection<IPackageFragmentRoot> filtered,
      final String filter) {
    final String[] matchStrings = filter.split(","); //$NON-NLS-1$
    for (final Iterator<IPackageFragmentRoot> i = filtered.iterator(); i
        .hasNext();) {
      if (!isPathMatch(i.next(), matchStrings)) {
        i.remove();
      }
    }
  }

  private boolean isPathMatch(final IPackageFragmentRoot root,
      final String[] matchStrings) {
    final String path = root.getPath().toString();
    for (final String match : matchStrings) {
      if (path.indexOf(match) != -1) {
        return true;
      }
    }
    return false;
  }

}
