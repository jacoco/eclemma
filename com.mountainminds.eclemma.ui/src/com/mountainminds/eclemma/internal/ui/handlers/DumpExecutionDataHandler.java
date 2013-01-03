/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.launching.ICoverageLaunch;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.UIPreferences;

public class DumpExecutionDataHandler extends AbstractHandler {

  private ILaunchesListener launchListener = new ILaunchesListener() {
    public void launchesRemoved(ILaunch[] launches) {
      fireEnablementChanged();
    }

    public void launchesChanged(ILaunch[] launches) {
      fireEnablementChanged();
    }

    public void launchesAdded(ILaunch[] launches) {
      fireEnablementChanged();
    }
  };

  private IDebugEventSetListener debugListener = new IDebugEventSetListener() {
    public void handleDebugEvents(DebugEvent[] events) {
      for (final DebugEvent e : events) {
        if (e.getSource() instanceof IProcess
            && e.getKind() == DebugEvent.TERMINATE) {
          fireEnablementChanged();
        }
      }
    }
  };

  private void fireEnablementChanged() {
    fireHandlerChanged(new HandlerEvent(DumpExecutionDataHandler.this, true,
        false));
  }

  public DumpExecutionDataHandler() {
    final DebugPlugin debug = DebugPlugin.getDefault();
    debug.getLaunchManager().addLaunchListener(launchListener);
    debug.addDebugEventListener(debugListener);
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final List<ICoverageLaunch> launches = CoverageTools
        .getRunningCoverageLaunches();
    if (launches.size() == 1) {
      requestDump(launches.get(0));
    } else if (launches.size() > 1) {
      final ICoverageLaunch launch = openDialog(event, launches);
      if (launch != null) {
        requestDump(launch);
      }
    }
    return null;
  }

  private ICoverageLaunch openDialog(ExecutionEvent event,
      List<ICoverageLaunch> launches) {
    final ListDialog dialog = new ListDialog(HandlerUtil.getActiveShell(event)) {
      protected void configureShell(Shell shell) {
        super.configureShell(shell);
        ContextHelp.setHelp(shell, ContextHelp.DUMP_EXECUTION_DATA);
      }
    };
    dialog.setTitle(UIMessages.DumpExecutionDataDialog_title);
    dialog.setMessage(UIMessages.DumpExecutionDataDialog_message);
    dialog.setContentProvider(new ArrayContentProvider());
    dialog.setLabelProvider(new LaunchLabelProvider());
    dialog.setInput(launches);
    if (dialog.open() == Dialog.OK || dialog.getResult().length == 1) {
      return (ICoverageLaunch) dialog.getResult()[0];
    }
    return null;
  }

  public static void requestDump(final ICoverageLaunch launch) {
    new Job(UIMessages.DumpExecutionData_task) {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        try {
          final boolean reset = EclEmmaUIPlugin.getInstance()
              .getPreferenceStore()
              .getBoolean(UIPreferences.PREF_RESET_ON_DUMP);
          launch.requestDump(reset);
        } catch (CoreException e) {
          return e.getStatus();
        }
        return Status.OK_STATUS;
      }
    }.schedule();
  }

  @Override
  public boolean isEnabled() {
    return !CoverageTools.getRunningCoverageLaunches().isEmpty();
  }

  @Override
  public void dispose() {
    final DebugPlugin debug = DebugPlugin.getDefault();
    debug.getLaunchManager().removeLaunchListener(launchListener);
    DebugPlugin.getDefault().addDebugEventListener(debugListener);
  }

}
