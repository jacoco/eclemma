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
package com.mountainminds.eclemma.internal.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import com.mountainminds.eclemma.core.ICorePreferences;

/**
 * Utility to calculate the default scope for a given launch configuration.
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
   * Returns a filtered copy of the given {@link IClassFiles} set.
   * 
   * @param classfiles
   *          {@link IClassFiles} to filter
   * @param configuration
   *          context information
   * @return filtered set
   * @throws CoreException
   *           may occur when accessing the Java model
   */
  public Set<IPackageFragmentRoot> filter(
      final Set<IPackageFragmentRoot> scope,
      final ILaunchConfiguration configuration) throws CoreException {
    final Set<IPackageFragmentRoot> filtered = new HashSet<IPackageFragmentRoot>(
        scope);
    if (preferences.getDefaultScopeSourceFoldersOnly()) {
      sourceFoldersOnly(filtered);
    }
    if (preferences.getDefaultScopeSameProjectOnly()) {
      sameProjectOnly(filtered, configuration);
    }
    String filter = preferences.getDefaultScopeFilter();
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
