/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
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
  
  public void runWithEvent(Event event) {
    ICoverageSession[] sessions = CoverageTools.getSessionManager().getSessions();
    MergeSessionsDialog d = new MergeSessionsDialog(window.getShell(), sessions, "Merged");
    d.open();
  }

}
