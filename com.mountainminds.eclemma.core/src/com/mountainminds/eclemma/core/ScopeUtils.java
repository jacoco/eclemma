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
package com.mountainminds.eclemma.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.core.launching.ICoverageLaunchConfigurationConstants;
import com.mountainminds.eclemma.core.launching.ICoverageLauncher;
import com.mountainminds.eclemma.internal.core.DefaultScopeFilter;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Collection of utility methods to deal with analysis scope.
 */
public final class ScopeUtils {

  private ScopeUtils() {
  }

  /**
   * Reads a coverage scope from a list of element ids.
   * 
   * @param list
   *          List of {@link String} ids
   * @return scope as {@link IPackageFragmentRoot} collection
   */
  public static Collection<IPackageFragmentRoot> readScope(List<?> list) {
    final Collection<IPackageFragmentRoot> scope = new ArrayList<IPackageFragmentRoot>();
    for (final Object handle : list) {
      final IJavaElement element = JavaCore.create((String) handle);
      if (element instanceof IPackageFragmentRoot) {
        scope.add((IPackageFragmentRoot) element);
      }
    }
    return scope;
  }

  /**
   * Writes a coverage scope as a list of ids.
   * 
   * @param scope
   *          Scope as {@link IPackageFragmentRoot} collection
   * @return List of ids
   */
  public static List<String> writeScope(Collection<IPackageFragmentRoot> scope) {
    final List<String> ids = new ArrayList<String>();
    for (final IPackageFragmentRoot root : scope) {
      ids.add(root.getHandleIdentifier());
    }
    return ids;
  }

  /**
   * Determines all {@link IPackageFragmentRoot}s that potentially referenced by
   * a given launch configuration.
   * 
   * @param configuration
   *          launch configuration to determine overall scope
   * 
   * @return overall scope
   */
  public static Collection<IPackageFragmentRoot> getOverallScope(
      ILaunchConfiguration configuration) throws CoreException {
    ICoverageLauncher launcher = (ICoverageLauncher) configuration.getType()
        .getDelegates(Collections.singleton(CoverageTools.LAUNCH_MODE))[0]
        .getDelegate();
    return launcher.getOverallScope(configuration);
  }

  /**
   * Returns the scope configured with the given configuration. If no scope has
   * been explicitly defined, the default filter settings are applied to the
   * overall scope.
   * 
   * @param configuration
   *          launch configuration to read scope from
   * 
   * @return configured scope
   */
  public static Collection<IPackageFragmentRoot> getConfiguredScope(
      final ILaunchConfiguration configuration) throws CoreException {
    final Collection<IPackageFragmentRoot> all = getOverallScope(configuration);
    final List<?> selection = configuration.getAttribute(
        ICoverageLaunchConfigurationConstants.ATTR_SCOPE_IDS, (List<?>) null);
    if (selection != null) {
      all.retainAll(readScope(selection));
    }
    final DefaultScopeFilter filter = EclEmmaCorePlugin.getInstance()
        .createDefaultIntrumentationFilter();
    return filter.filter(all, configuration);
  }

  /**
   * Determines all package fragment roots in the workspace.
   * 
   * @return all package fragment roots
   */
  public static Collection<IPackageFragmentRoot> getWorkspaceScope()
      throws JavaModelException {
    final Collection<IPackageFragmentRoot> scope = new ArrayList<IPackageFragmentRoot>();
    final IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
        .getRoot());
    for (IJavaProject p : model.getJavaProjects()) {
      for (final IPackageFragmentRoot root : p.getPackageFragmentRoots()) {
        final IClasspathEntry cpentry = root.getRawClasspathEntry();
        switch (cpentry.getEntryKind()) {
        case IClasspathEntry.CPE_SOURCE:
        case IClasspathEntry.CPE_LIBRARY:
          scope.add(root);
        }
      }
    }
    return scope;
  }
}