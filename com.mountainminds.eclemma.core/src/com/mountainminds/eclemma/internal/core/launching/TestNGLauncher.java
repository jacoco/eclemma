/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;

/**
 * Launcher for TestNG runs.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class TestNGLauncher extends CoverageLauncher {

  public TestNGLauncher() throws CoreException {
    super("org.testng.eclipse.launchconfig"); //$NON-NLS-1$
  }

  protected void modifyConfiguration(ILaunchConfigurationWorkingCopy workingcopy,
      ICoverageLaunchInfo info) throws CoreException {
    workingcopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
        InstrumentedClasspathProvider.ID);
  }

}
