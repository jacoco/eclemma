/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IProcess;
import org.osgi.framework.BundleContext;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICorePreferences;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.core.launching.CoverageLaunch;

/**
 * Bundle activator for the EclEmma core.
 */
public class EclEmmaCorePlugin extends Plugin {

  public static final String ID = "com.mountainminds.eclemma.core"; //$NON-NLS-1$

  /** Status used to trigger user prompts */
  private static final IStatus PROMPT_STATUS = new Status(IStatus.INFO,
      "org.eclipse.debug.ui", 200, "", null); //$NON-NLS-1$//$NON-NLS-2$

  private static EclEmmaCorePlugin instance;

  private ICorePreferences preferences = ICorePreferences.DEFAULT;

  private ISessionManager sessionManager;

  private JavaCoverageLoader coverageLoader;

  private ExecutionDataFiles executionDataFiles;

  private ILaunchListener launchListener = new ILaunchListener() {
    public void launchRemoved(ILaunch launch) {
      if (preferences.getAutoRemoveSessions()) {
        sessionManager.removeSessionsFor(launch);
      }
    }

    public void launchAdded(ILaunch launch) {
    }

    public void launchChanged(ILaunch launch) {
    }
  };

  private IDebugEventSetListener debugListener = new IDebugEventSetListener() {
    public void handleDebugEvents(DebugEvent[] events) {
      for (final DebugEvent e : events) {
        if (e.getSource() instanceof IProcess
            && e.getKind() == DebugEvent.TERMINATE) {
          final IProcess proc = (IProcess) e.getSource();
          final ILaunch launch = proc.getLaunch();
          if (launch instanceof CoverageLaunch) {
            final CoverageLaunch coverageLaunch = (CoverageLaunch) launch;
            coverageLaunch.getAgentServer().stop();
            checkExecutionData(coverageLaunch);
          }
        }
      }
    }

    private void checkExecutionData(CoverageLaunch launch) {
      if (!launch.getAgentServer().hasDataReceived()) {
        try {
          showPrompt(EclEmmaStatus.NO_COVERAGE_DATA_ERROR.getStatus(), launch);
        } catch (CoreException e) {
          getLog().log(e.getStatus());
        }
      }
    }
  };

  public void start(BundleContext context) throws Exception {
    super.start(context);
    executionDataFiles = new ExecutionDataFiles(getStateLocation());
    executionDataFiles.deleteTemporaryFiles();
    sessionManager = new SessionManager(executionDataFiles);
    coverageLoader = new JavaCoverageLoader(sessionManager);
    DebugPlugin.getDefault().getLaunchManager()
        .addLaunchListener(launchListener);
    DebugPlugin.getDefault().addDebugEventListener(debugListener);
    instance = this;
  }

  public void stop(BundleContext context) throws Exception {
    instance = null;
    executionDataFiles.deleteTemporaryFiles();
    DebugPlugin.getDefault().removeDebugEventListener(debugListener);
    DebugPlugin.getDefault().getLaunchManager()
        .removeLaunchListener(launchListener);
    executionDataFiles = null;
    coverageLoader.dispose();
    coverageLoader = null;
    sessionManager = null;
    super.stop(context);
  }

  public static EclEmmaCorePlugin getInstance() {
    return instance;
  }

  public void setPreferences(ICorePreferences preferences) {
    this.preferences = preferences;
  }

  public ICorePreferences getPreferences() {
    return this.preferences;
  }

  public ISessionManager getSessionManager() {
    return sessionManager;
  }

  public JavaCoverageLoader getJavaCoverageLoader() {
    return coverageLoader;
  }

  public ExecutionDataFiles getExecutionDataFiles() {
    return executionDataFiles;
  }

  /**
   * Issues an user prompt using the status handler registered for the given
   * status.
   * 
   * @param status
   *          IStatus object to find prompter for
   * @param info
   *          additional information passed to the handler
   * @return boolean result returned by the status handler
   * @throws CoreException
   *           if the status has severity error and no handler is available
   */
  private boolean showPrompt(IStatus status, Object info) throws CoreException {
    IStatusHandler prompter = DebugPlugin.getDefault().getStatusHandler(
        PROMPT_STATUS);
    if (prompter == null) {
      if (status.getSeverity() == IStatus.ERROR) {
        throw new CoreException(status);
      } else {
        return true;
      }
    } else {
      return ((Boolean) prompter.handleStatus(status, info)).booleanValue();
    }
  }

}
