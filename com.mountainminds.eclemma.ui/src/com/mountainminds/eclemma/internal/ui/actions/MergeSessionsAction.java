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
package com.mountainminds.eclemma.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.dialogs.MergeSessionsDialog;

/**
 * This action launches the merge sessions dialog.
 */
public class MergeSessionsAction extends Action {

  private final IWorkbenchWindow window;

  public MergeSessionsAction(IWorkbenchWindow window) {
    this.window = window;
    setText(UIMessages.MergeSessionsAction_label);
    setToolTipText(UIMessages.MergeSessionsAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.ELCL_MERGESESSIONS));
    setDisabledImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.DLCL_MERGESESSIONS));
  }

  public void run() {
    final ISessionManager sm = CoverageTools.getSessionManager();
    List<ICoverageSession> sessions = sm.getSessions();
    String descr = UIMessages.MergeSessionsDialogDescriptionDefault_value;
    descr = MessageFormat.format(descr, new Object[] { new Date() });
    final MergeSessionsDialog d = new MergeSessionsDialog(window.getShell(),
        sessions, descr);
    if (d.open() == IDialogConstants.OK_ID) {
      try {
        window.run(true, true,
            createJob(sm, d.getSessions(), d.getDescription()));
      } catch (InvocationTargetException e) {
        EclEmmaUIPlugin.log(e.getTargetException());
      } catch (InterruptedException e) {
        // ignore
      }
    }
  }

  private IRunnableWithProgress createJob(final ISessionManager sm,
      final Collection<ICoverageSession> sessions, final String description) {
    return new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException, InterruptedException {
        try {
          sm.mergeSessions(sessions, description, monitor);
        } catch (CoreException e) {
          EclEmmaUIPlugin.log(e);
        }
      }
    };
  }

}
