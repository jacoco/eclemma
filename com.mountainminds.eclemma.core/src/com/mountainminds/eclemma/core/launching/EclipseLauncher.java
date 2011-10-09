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
package com.mountainminds.eclemma.core.launching;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

/**
 * Laucher for the Eclipse runtime workbench.
 */
public class EclipseLauncher extends CoverageLauncher {

  protected static final String PLUGIN_NATURE = "org.eclipse.pde.PluginNature"; //$NON-NLS-1$

  /*
   * The overall scope are all plug-in projects in the workspace.
   */
  public Set<IPackageFragmentRoot> getOverallScope(
      ILaunchConfiguration configuration) throws CoreException {
    final IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
        .getRoot());
    final Set<IPackageFragmentRoot> result = new HashSet<IPackageFragmentRoot>();
    for (final IJavaProject project : model.getJavaProjects()) {
      if (project.getProject().hasNature(PLUGIN_NATURE)) {
        result.addAll(Arrays.asList(project.getPackageFragmentRoots()));
      }
    }
    return result;
  }

}
