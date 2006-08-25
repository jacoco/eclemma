/*
 * $Id: JUnitPluginLauncher.java 32 2006-08-23 15:45:27Z mho $
 */
package com.mountainminds.eclemma.internal.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;

/**
 * Laucher for the Eclipse runtime workbench.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: 32 $
 */
public class WorkbenchLauncher extends CoverageLauncher {

  public static final String VMARGS = "vmargs"; //$NON-NLS-1$
  public static final String BOOTPATHARG = "-Xbootclasspath/a:"; //$NON-NLS-1$

  public WorkbenchLauncher() throws CoreException {
    super("org.eclipse.pde.ui.RuntimeWorkbench"); //$NON-NLS-1$
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
