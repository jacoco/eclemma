/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ScopeUtils;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.launching.AgentArgumentSupport;
import com.mountainminds.eclemma.internal.core.launching.AgentServer;
import com.mountainminds.eclemma.internal.core.launching.CoverageLaunch;

/**
 * Abstract base class for coverage mode launchers. Coverage launchers perform
 * adjust the launch configuration to inject the JaCoCo coverage agent and then
 * delegate to the corresponding launcher responsible for the "run" mode.
 */
public abstract class CoverageLauncher implements ICoverageLauncher,
    IExecutableExtension {

  /** Launch mode for the launch delegates used internally. */
  public static final String DELEGATELAUNCHMODE = ILaunchManager.RUN_MODE;

  protected ILaunchConfigurationDelegate launchdelegate;

  protected ILaunchConfigurationDelegate2 launchdelegate2;

  // IExecutableExtension interface:

  public void setInitializationData(IConfigurationElement config,
      String propertyName, Object data) throws CoreException {
    final String launchtype = config.getAttribute("type"); //$NON-NLS-1$
    launchdelegate = getLaunchDelegate(launchtype);
    if (launchdelegate instanceof ILaunchConfigurationDelegate2) {
      launchdelegate2 = (ILaunchConfigurationDelegate2) launchdelegate;
    }
  }

  private ILaunchConfigurationDelegate getLaunchDelegate(String launchtype)
      throws CoreException {
    ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
        .getLaunchConfigurationType(launchtype);
    if (type == null) {
      throw new CoreException(
          EclEmmaStatus.UNKOWN_LAUNCH_TYPE_ERROR.getStatus(launchtype));
    }
    return type.getDelegates(Collections.singleton(DELEGATELAUNCHMODE))[0]
        .getDelegate();
  }

  // ILaunchConfigurationDelegate interface:

  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(
        NLS.bind(CoreMessages.Launching_task, configuration.getName()), 2);
    if (monitor.isCanceled()) {
      return;
    }

    // Start agent server
    final CoverageLaunch coverageLaunch = (CoverageLaunch) launch;
    final AgentServer server = coverageLaunch.getAgentServer();
    server.start();

    // Delegate to run mode launcher
    final AgentArgumentSupport argSupport = new AgentArgumentSupport();
    final ILaunchConfiguration adjusted = argSupport.addArgument(
        server.getPort(), configuration);
    launchdelegate.launch(adjusted, DELEGATELAUNCHMODE, launch,
        new SubProgressMonitor(monitor, 1));

    monitor.done();
  }

  // ILaunchConfigurationDelegate2 interface:

  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
      throws CoreException {
    return new CoverageLaunch(configuration,
        ScopeUtils.getConfiguredScope(configuration));
  }

  public boolean buildForLaunch(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) throws CoreException {
    if (launchdelegate2 == null) {
      return true;
    } else {
      return launchdelegate2.buildForLaunch(configuration, DELEGATELAUNCHMODE,
          monitor);
    }
  }

  public boolean preLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) throws CoreException {
    if (launchdelegate2 == null) {
      return true;
    } else {
      return launchdelegate2.preLaunchCheck(configuration, DELEGATELAUNCHMODE,
          monitor);
    }
  }

  public boolean finalLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) throws CoreException {
    if (launchdelegate2 == null) {
      return true;
    } else {
      return launchdelegate2.finalLaunchCheck(configuration,
          DELEGATELAUNCHMODE, monitor);
    }
  }

}
