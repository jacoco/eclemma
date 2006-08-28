/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

/**
 * The coverage tab group simply uses the tab group for the launch type "run"
 * and inserts the "Coverage" tab at the second position.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public abstract class AbstractCoverageTabGroup implements ILaunchConfigurationTabGroup {
  
  private static final String DELEGATE_LAUNCHMODE = ILaunchManager.RUN_MODE;

  private ILaunchConfigurationTabGroup tabGroupDelegate;
  private ILaunchConfigurationTab coverageTab;
  
  /**
   * Create a tab group for the given launch type.
   * 
   * @param type
   *           launch type id that is used to create the tabs from
   * @throws CoreException
   *           May happen when creating the tab group for delegation.
   */
  protected AbstractCoverageTabGroup(String type) throws CoreException {
    this.tabGroupDelegate = createDelegate(type);
  }
  
  protected ILaunchConfigurationTabGroup createDelegate(String type) throws CoreException {
    IExtensionPoint extensionpoint = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.debug.ui.launchConfigurationTabGroups"); //$NON-NLS-1$
    IConfigurationElement[] tabGroupConfigs = extensionpoint.getConfigurationElements();
    for (int i = 0; i < tabGroupConfigs.length; i++) {
      IConfigurationElement tabGroupConfig = tabGroupConfigs[i];
      if (type.equals(tabGroupConfig.getAttribute("type"))) { //$NON-NLS-1$
        IConfigurationElement[] modeConfigs = tabGroupConfig.getChildren("launchMode"); //$NON-NLS-1$
        for (int j = 0; j < modeConfigs.length; j++) {
          if (DELEGATE_LAUNCHMODE.equals(modeConfigs[j].getAttribute("mode"))) { //$NON-NLS-1$
            return (ILaunchConfigurationTabGroup) tabGroupConfig.createExecutableExtension("class"); //$NON-NLS-1$
          }
        }
      }
    }
    throw new RuntimeException("No tab group registered to run " + type); //$NON-NLS-1$
  }
  
  public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
    tabGroupDelegate.createTabs(dialog, mode);
    coverageTab = createCoverageTab(dialog, mode);
  }
  
  protected ILaunchConfigurationTab createCoverageTab(ILaunchConfigurationDialog dialog, String mode) {
    return new CoverageTab(false);
  }

  public ILaunchConfigurationTab[] getTabs() {
    return insertCoverageTab(tabGroupDelegate.getTabs(), coverageTab);
  }

  protected ILaunchConfigurationTab[] insertCoverageTab(ILaunchConfigurationTab[] delegateTabs, ILaunchConfigurationTab coverageTab) {
    ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[delegateTabs.length + 1];
    tabs[0] = delegateTabs[0];
    tabs[1] = coverageTab;
    System.arraycopy(delegateTabs, 1, tabs, 2, delegateTabs.length - 1);
    return tabs;
  }
  
  public void dispose() {
    tabGroupDelegate.dispose();
    coverageTab.dispose();
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    tabGroupDelegate.setDefaults(configuration);
    coverageTab.setDefaults(configuration);
  }

  public void initializeFrom(ILaunchConfiguration configuration) {
    tabGroupDelegate.initializeFrom(configuration);
    coverageTab.initializeFrom(configuration);
  }

  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    tabGroupDelegate.performApply(configuration);
    coverageTab.performApply(configuration);
  }

  public void launched(ILaunch launch) {
    // deprecated method will not be called
  }

}
