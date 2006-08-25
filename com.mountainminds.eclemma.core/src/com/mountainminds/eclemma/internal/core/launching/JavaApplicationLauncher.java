/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;

/**
 * Launcher for local Java applications.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaApplicationLauncher extends CoverageLauncher {

  public JavaApplicationLauncher() throws CoreException {
    super("org.eclipse.jdt.launching.localJavaApplication"); //$NON-NLS-1$
  }

  protected void modifyConfiguration(ILaunchConfigurationWorkingCopy workingcopy,
      ICoverageLaunchInfo info) throws CoreException {
    workingcopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
        InstrumentedClasspathProvider.ID);
  }

}
