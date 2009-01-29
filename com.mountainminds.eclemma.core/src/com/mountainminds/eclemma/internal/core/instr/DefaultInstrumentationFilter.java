/*******************************************************************************
 * Copyright (c) 2006 ,2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.instr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.JavaRuntime;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ICorePreferences;

/**
 * Utility to retrieve the list of {@link IClassFiles} that will be instrumented
 * by default.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class DefaultInstrumentationFilter {

  private final ICorePreferences preferences;

  /**
   * Creates a new filter based on the given preferences.
   * 
   * @param preferences
   *          call-back to retrieve current settings from.
   */
  public DefaultInstrumentationFilter(final ICorePreferences preferences) {
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
  public IClassFiles[] filter(final IClassFiles[] classfiles,
      final ILaunchConfiguration configuration) throws CoreException {
    final List list = new ArrayList(Arrays.asList(classfiles));
    if (preferences.getDefaultInstrumentationSourceFoldersOnly()) {
      sourceFoldersOnly(list);
    }
    if (preferences.getDefaultInstrumentationSameProjectOnly()) {
      sameProjectOnly(list, configuration);
    }
    String filter = preferences.getDefaultInstrumentationFilter();
    if (filter != null && filter.length() > 0) {
      matchingPathsOnly(list, filter);
    }
    return (IClassFiles[]) list.toArray(new IClassFiles[list.size()]);
  }

  private void sourceFoldersOnly(final List list) {
    for (final Iterator i = list.iterator(); i.hasNext();) {
      final IClassFiles c = (IClassFiles) i.next();
      if (c.isBinary()) {
        i.remove();
      }
    }
  }

  private void sameProjectOnly(final List list,
      final ILaunchConfiguration configuration) throws CoreException {
    final IJavaProject javaProject = JavaRuntime.getJavaProject(configuration);
    if (javaProject != null) {
      for (final Iterator i = list.iterator(); i.hasNext();) {
        if (!isSameProject((IClassFiles) i.next(), javaProject)) {
          i.remove();
        }
      }
    }
  }

  private boolean isSameProject(final IClassFiles classfiles,
      final IJavaProject javaProject) {
    final IPackageFragmentRoot[] roots = classfiles.getPackageFragmentRoots();
    for (int i = 0; i < roots.length; i++) {
      if (javaProject.equals(roots[i].getJavaProject())) {
        return true;
      }
    }
    return false;
  }

  private void matchingPathsOnly(final List list, final String filter) {
    final String[] matchStrings = filter.split(","); //$NON-NLS-1$
    for (final Iterator i = list.iterator(); i.hasNext();) {
      if (!isPathMatch((IClassFiles) i.next(), matchStrings)) {
        i.remove();
      }
    }
  }

  private boolean isPathMatch(final IClassFiles classfiles,
      final String[] matchStrings) {
    final IPackageFragmentRoot[] roots = classfiles.getPackageFragmentRoots();
    for (int i = 0; i < roots.length; i++) {
      final String path = roots[i].getPath().toString();
      for (int j = 0; j < matchStrings.length; j++) {
        if (path.indexOf(matchStrings[j]) != -1) {
          return true;
        }
      }
    }
    return false;
  }

}
