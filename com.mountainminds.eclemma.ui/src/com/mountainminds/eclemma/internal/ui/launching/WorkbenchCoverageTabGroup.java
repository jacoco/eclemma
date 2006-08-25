/*
 * $Id: JUnitPluginCoverageTabGroup.java 33 2006-08-23 15:46:24Z mho $
 */
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * TODO
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: 33 $
 */
public class WorkbenchCoverageTabGroup extends AbstractCoverageTabGroup {

  public WorkbenchCoverageTabGroup() throws CoreException {
    super("org.eclipse.pde.ui.RuntimeWorkbench");
  }
  
  protected ILaunchConfigurationTab createCoverageTab(ILaunchConfigurationDialog dialog, String mode) {
    return new CoverageTab(true);
  }

}
