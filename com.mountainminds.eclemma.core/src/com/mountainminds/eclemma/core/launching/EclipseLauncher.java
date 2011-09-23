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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Laucher for the Eclipse runtime workbench.
 */
public class EclipseLauncher extends CoverageLauncher {

  protected static final String PLUGIN_NATURE = "org.eclipse.pde.PluginNature"; //$NON-NLS-1$

  /*
   * We list all source based class files of all plugins in the workspace.
   */
  public IClassFiles[] getClassFiles(ILaunchConfiguration configuration,
      boolean includebinaries) throws CoreException {
    final IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
        .getRoot());
    final List<IClassFiles> l = new ArrayList<IClassFiles>();
    for (final IJavaProject project : model.getJavaProjects()) {
      if (project.getProject().hasNature(PLUGIN_NATURE)) {
        for (final IClassFiles cf : EclEmmaCorePlugin.getClassFiles(project)) {
          if (!cf.isBinary()) {
            l.add(cf);
          }
        }
      }
    }
    return l.toArray(new IClassFiles[0]);
  }

}
