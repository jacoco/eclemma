/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbenchWindow;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.dialogs.MergeSessionsDialog;

/**
 * This action launches the merge sessions dialog.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
class MergeSessionsAction extends Action {
  
  private final IWorkbenchWindow window;
  
  MergeSessionsAction(IWorkbenchWindow window) {
    this.window = window; 
    setText(UIMessages.SessionsView_mergeSessionsLabel);
    setToolTipText(UIMessages.SessionsView_mergeSessionsTooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_MERGESESSIONS));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_MERGESESSIONS));
    setEnabled(false);
  }
  
  public void run() {
    ISessionManager sm = CoverageTools.getSessionManager();
    ICoverageSession[] sessions = sm.getSessions();
    String descr = UIMessages.MergeSessionsDialog_descriptionDefault;
    descr = MessageFormat.format(descr, new Object[] { new Date() });
    MergeSessionsDialog d = new MergeSessionsDialog(window.getShell(), sessions, descr);
    if (d.open() == IDialogConstants.OK_ID) {
      Object[] result = d.getResult();
      ICoverageSession merged = (ICoverageSession) result[0];
      for (int i = 1; i < result.length; i++) {
        merged = merged.merge((ICoverageSession) result[i], d.getDescription());
      }
      sm.addSession(merged, true, null);
      for (int i = 0; i < result.length; i++) {
        sm.removeSession((ICoverageSession) result[i]);
      }
    }
  }

}
