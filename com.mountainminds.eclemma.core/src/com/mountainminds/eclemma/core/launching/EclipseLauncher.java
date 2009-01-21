/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.EclipseVersion;

/**
 * Laucher for the Eclipse runtime workbench.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class EclipseLauncher extends CoverageLauncher {

  protected static final String PLUGIN_NATURE = "org.eclipse.pde.PluginNature"; //$NON-NLS-1$
  
  /** Pre-Eclipse 3.2.0 VM arguments key */ 
  protected static final String PRE320VMARGS = "vmargs"; //$NON-NLS-1$
  protected static final String VMARGS = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
  
  public static final String BOOTPATHARG = "-Xbootclasspath/a:"; //$NON-NLS-1$

  /**
   * Returns the proper key for VM arguments depending on the Eclipse version
   * 
   * @return  launch configuration key for VM arguments
   */
  protected String getVMArgsKey() {
    boolean is320 = EclipseVersion.isGreaterOrEqualTo(EclipseVersion.V320);
    return is320 ? VMARGS : PRE320VMARGS;
  }
  
  /**
   * Adds the given single argument to the VM arguments. If it contains white
   * spaces the argument is included in double quotes.
   * 
   * @param workingcopy  configuration to modify
   * @param arg  additional VM argument
   * @throws CoreException  may be thrown by the launch configuration
   */
  protected void addVMArgument(ILaunchConfigurationWorkingCopy workingcopy, String arg) throws CoreException {
    String vmargskey = getVMArgsKey(); 
    StringBuffer sb = new StringBuffer(workingcopy.getAttribute(vmargskey, "")); //$NON-NLS-1$
    if (sb.length() > 0) {
      sb.append(' ');
    }
    if (arg.indexOf(' ') == -1) {
      sb.append(arg);
    } else {
      sb.append('"').append(arg).append('"');
    }
    workingcopy.setAttribute(vmargskey, sb.toString());
  }
  
  protected boolean hasInplaceInstrumentation(ILaunchConfiguration configuration) {
    // Inplace instrumentation is required for plugin launches, as we can't
    // modify the classpath
    return true;
  }
  
  protected void modifyConfiguration(ILaunchConfigurationWorkingCopy workingcopy,
      ICoverageLaunchInfo info) throws CoreException {
    StringBuffer sb = new StringBuffer(BOOTPATHARG);
    sb.append(info.getPropertiesJARFile());
    sb.append(File.pathSeparatorChar).append(CoverageTools.getEmmaJar().toOSString());
    addVMArgument(workingcopy, sb.toString());
  }

  /*
   * We list all source based class files of all plugins in the workspace. 
   */
  public IClassFiles[] getClassFiles(ILaunchConfiguration configuration, boolean includebinaries) throws CoreException {
    IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
    IJavaProject[] projects = model.getJavaProjects();
    List l = new ArrayList();
    for (int i = 0; i < projects.length; i++) {
      if (projects[i].getProject().hasNature(PLUGIN_NATURE)) {
        IClassFiles[] cf = EclEmmaCorePlugin.getClassFiles(projects[i]);
        for (int j = 0; j < cf.length; j++) {
          if (!cf[j].isBinary()) l.add(cf[j]);
        }
      }
    }
    return (IClassFiles[]) l.toArray(new IClassFiles[0]);
  }

}
