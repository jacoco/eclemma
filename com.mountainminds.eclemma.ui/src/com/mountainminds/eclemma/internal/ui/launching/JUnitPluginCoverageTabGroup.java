/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * TODO
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JUnitPluginCoverageTabGroup extends AbstractCoverageTabGroup {

  public JUnitPluginCoverageTabGroup() throws CoreException {
    super("org.eclipse.pde.ui.JunitLaunchConfig");
  }
  
  protected ILaunchConfigurationTab createCoverageTab(ILaunchConfigurationDialog dialog, String mode) {
    return new CoverageTab(true);
  }

}
