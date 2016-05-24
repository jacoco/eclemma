/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathContainer;
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
   * Reads a coverage scope from a collection of element ids.
   * 
   * @param ids
   *          List of {@link String} ids
   * @return scope as {@link IPackageFragmentRoot} collection
   */
  public static Set<IPackageFragmentRoot> readScope(Collection<?> ids) {
    final Set<IPackageFragmentRoot> scope = new HashSet<IPackageFragmentRoot>();
    for (final Object handle : ids) {
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
  public static List<String> writeScope(Set<IPackageFragmentRoot> scope) {
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
  public static Set<IPackageFragmentRoot> getOverallScope(
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
  public static Set<IPackageFragmentRoot> getConfiguredScope(
      final ILaunchConfiguration configuration) throws CoreException {
    final Set<IPackageFragmentRoot> all = getOverallScope(configuration);
    @SuppressWarnings("rawtypes")
    final List<?> selection = configuration.getAttribute(
        ICoverageLaunchConfigurationConstants.ATTR_SCOPE_IDS, (List) null);
    if (selection == null) {
      final DefaultScopeFilter filter = new DefaultScopeFilter(
          EclEmmaCorePlugin.getInstance().getPreferences());
      return filter.filter(all, configuration);
    } else {
      all.retainAll(readScope(selection));
      return all;
    }
  }

  /**
   * Determines all package fragment roots in the workspace.
   * 
   * @return all package fragment roots
   */
  public static Set<IPackageFragmentRoot> getWorkspaceScope()
      throws JavaModelException {
    final Set<IPackageFragmentRoot> scope = new HashSet<IPackageFragmentRoot>();
    final IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
        .getRoot());
    for (IJavaProject p : model.getJavaProjects()) {
      scope.addAll(Arrays.asList(p.getPackageFragmentRoots()));
    }
    return filterJREEntries(scope);
  }

  /**
   * Remove all JRE runtime entries from the given set
   * 
   * @param scope
   *          set to filter
   * @return filtered set without JRE runtime entries
   */
  public static Set<IPackageFragmentRoot> filterJREEntries(
      Collection<IPackageFragmentRoot> scope) throws JavaModelException {
    final Set<IPackageFragmentRoot> filtered = new HashSet<IPackageFragmentRoot>();
    for (final IPackageFragmentRoot root : scope) {
      final IClasspathEntry entry = root.getRawClasspathEntry();
      switch (entry.getEntryKind()) {
      case IClasspathEntry.CPE_SOURCE:
      case IClasspathEntry.CPE_LIBRARY:
      case IClasspathEntry.CPE_VARIABLE:
        filtered.add(root);
        break;
      case IClasspathEntry.CPE_CONTAINER:
        IClasspathContainer container = JavaCore.getClasspathContainer(
            entry.getPath(), root.getJavaProject());
        if (container != null
            && container.getKind() == IClasspathContainer.K_APPLICATION) {
          filtered.add(root);
        }
        break;
      }
    }
    return filtered;
  }

}