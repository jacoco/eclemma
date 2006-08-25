/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public abstract class AbstractCoverageTabGroup implements ILaunchConfigurationTabGroup {
  
  private static final String DELEGATE_LAUNCHMODE = "run";

  private ILaunchConfigurationTabGroup tabGroupDelegate;
  private ILaunchConfigurationTab coverageTab;
  
  public AbstractCoverageTabGroup(String type) throws CoreException {
    this.tabGroupDelegate = createDelegate(type);
  }
  
  protected ILaunchConfigurationTabGroup createDelegate(String type) throws CoreException {
    IExtensionPoint extensionpoint = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.debug.ui.launchConfigurationTabGroups");
    IConfigurationElement[] tabGroupConfigs = extensionpoint.getConfigurationElements();
    for (int i = 0; i < tabGroupConfigs.length; i++) {
      IConfigurationElement tabGroupConfig = tabGroupConfigs[i];
      if (type.equals(tabGroupConfig.getAttribute("type"))) {
        IConfigurationElement[] modeConfigs = tabGroupConfig.getChildren("launchMode");
        for (int j = 0; j < modeConfigs.length; j++) {
          if (DELEGATE_LAUNCHMODE.equals(modeConfigs[j].getAttribute("mode"))) {
            return (ILaunchConfigurationTabGroup) tabGroupConfig.createExecutableExtension("class");
          }
        }
      }
    }
    throw new RuntimeException("No tab group registered to run " + type);
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
