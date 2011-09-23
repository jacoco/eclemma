/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.runtime.AgentOptions;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.launching.CoverageLaunchInfo;

/**
 * Abstract base class for coverage mode launchers. Coverage launchers perform
 * class instrumentation and then delegate to the corresponding launcher
 * responsible for the "run" mode.
 */
public abstract class CoverageLauncher implements ICoverageLauncher,
    IExecutableExtension {

  /** Launch mode for the launch delegates used internally. */
  public static final String DELEGATELAUNCHMODE = ILaunchManager.RUN_MODE;

  protected String launchtype;

  protected ILaunchConfigurationDelegate launchdelegate;

  protected ILaunchConfigurationDelegate2 launchdelegate2;

  /**
   * Adds the coverage agent to the launch configuration before it is passed on
   * to the delegate launcher.
   * 
   * @param workingcopy
   *          Configuration to modify
   * @param info
   *          Info object of this launch
   * @throws CoreException
   *           may be thrown by implementations
   */
  private void addCoverageAgent(ILaunchConfigurationWorkingCopy workingcopy,
      ICoverageLaunchInfo info) throws CoreException {
    final AgentOptions options = new AgentOptions();
    options.setDestfile(info.getExecutionDataFile().toOSString());
    final File agentfile = CoverageTools.getRuntimeAgentJar().toFile();
    final String arg = options.getVMArgument(agentfile);
    addVMArgument(workingcopy, arg);
  }

  /**
   * Adds the given single argument to the VM arguments. If it contains white
   * spaces the argument is included in double quotes.
   * 
   * @param workingcopy
   *          configuration to modify
   * @param arg
   *          additional VM argument
   * @throws CoreException
   *           may be thrown by the launch configuration
   */
  private void addVMArgument(ILaunchConfigurationWorkingCopy workingcopy,
      String arg) throws CoreException {
    String vmargskey = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
    StringBuilder sb = new StringBuilder(
        workingcopy.getAttribute(vmargskey, "")); //$NON-NLS-1$
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

  // IExecutableExtension interface:

  public void setInitializationData(IConfigurationElement config,
      String propertyName, Object data) throws CoreException {
    launchtype = config.getAttribute("type"); //$NON-NLS-1$
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
    ICoverageLaunchInfo info = CoverageTools.getLaunchInfo(launch);
    if (info == null) {
      // Must not happen as we should have created the launch
      throw new CoreException(
          EclEmmaStatus.MISSING_LAUNCH_INFO_ERROR.getStatus(null));
    }
    if (monitor.isCanceled()) {
      return;
    }
    ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
    addCoverageAgent(wc, info);
    launchdelegate.launch(wc, DELEGATELAUNCHMODE, launch,
        new SubProgressMonitor(monitor, 1));
    monitor.done();
  }

  // ILaunchConfigurationDelegate2 interface:

  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
      throws CoreException {
    ILaunch launch = new Launch(configuration, CoverageTools.LAUNCH_MODE, null);
    new CoverageLaunchInfo(launch);
    return launch;
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
    if (CoverageTools.getClassFilesForInstrumentation(configuration).length == 0) {
      IStatus status = EclEmmaStatus.NO_INSTRUMENTED_CLASSES.getStatus();
      EclEmmaCorePlugin.getInstance().showPrompt(status, configuration);
      return false;
    }
    // Then allow the delegate's veto:
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

  // ICoverageLauncher interface:

  /*
   * The default implementation delegates to the classpath provider.
   */
  public IClassFiles[] getClassFiles(ILaunchConfiguration configuration,
      boolean includebinaries) throws CoreException {
    List<IClassFiles> l = new ArrayList<IClassFiles>();
    IRuntimeClasspathEntry[] entries = JavaRuntime
        .computeUnresolvedRuntimeClasspath(configuration);
    entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
    for (final IRuntimeClasspathEntry entry : entries) {
      if (entry.getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
        IClassFiles ic = CoverageTools.getClassFilesAtAbsoluteLocation(entry
            .getLocation());
        if (ic != null && (includebinaries || !ic.isBinary())) {
          l.add(ic);
        }
      }
    }
    IClassFiles[] arr = new IClassFiles[l.size()];
    return (IClassFiles[]) l.toArray(arr);
  }

}
