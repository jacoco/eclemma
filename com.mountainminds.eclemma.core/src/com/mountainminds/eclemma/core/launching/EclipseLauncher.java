/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
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
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class EclipseLauncher extends CoverageLauncher {

  protected static final String PLUGIN_NATURE = "org.eclipse.pde.PluginNature"; //$NON-NLS-1$

  /*
   * We list all source based class files of all plugins in the workspace.
   */
  public IClassFiles[] getClassFiles(ILaunchConfiguration configuration,
      boolean includebinaries) throws CoreException {
    IJavaModel model = JavaCore
        .create(ResourcesPlugin.getWorkspace().getRoot());
    IJavaProject[] projects = model.getJavaProjects();
    List<IClassFiles> l = new ArrayList<IClassFiles>();
    for (int i = 0; i < projects.length; i++) {
      if (projects[i].getProject().hasNature(PLUGIN_NATURE)) {
        IClassFiles[] cf = EclEmmaCorePlugin.getClassFiles(projects[i]);
        for (int j = 0; j < cf.length; j++) {
          if (!cf[j].isBinary())
            l.add(cf[j]);
        }
      }
    }
    return l.toArray(new IClassFiles[0]);
  }

}
