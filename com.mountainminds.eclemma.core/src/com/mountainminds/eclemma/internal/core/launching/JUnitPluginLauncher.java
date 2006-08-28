/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;

/**
 * Laucher for plug-in based JUnit tests.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class JUnitPluginLauncher extends CoverageLauncher {

  public static final String VMARGS = "vmargs"; //$NON-NLS-1$
  public static final String BOOTPATHARG = "-Xbootclasspath/a:"; //$NON-NLS-1$

  public JUnitPluginLauncher() throws CoreException {
    super("org.eclipse.pde.ui.JunitLaunchConfig"); //$NON-NLS-1$
  }

  protected boolean hasInplaceInstrumentation(ILaunchConfiguration configuration) {
    // Inplace instrumentation is required for plugin launches, as we can't
    // modify the classpath
    return true;
  }

  protected void modifyConfiguration(ILaunchConfigurationWorkingCopy workingcopy,
      ICoverageLaunchInfo info) throws CoreException {
    StringBuffer sb = new StringBuffer(workingcopy.getAttribute(VMARGS, ""));
    sb.append(" ").append(BOOTPATHARG);
    sb.append(info.getPropertiesJARFile());
    sb.append(";").append(CoverageTools.getEmmaJar().toOSString());
    workingcopy.setAttribute(VMARGS, sb.toString());
  }

}
