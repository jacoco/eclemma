/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.jface.action.Action;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action removes all coverage sessions.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class RemoveAllSessionsAction extends Action {
  
  public RemoveAllSessionsAction() {
    setText(UIMessages.RemoveAllSessionsAction_label);
    setToolTipText(UIMessages.RemoveAllSessionsAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_REMOVEALL));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_REMOVEALL));
  }
  
  public void run() {
    ISessionManager manager = CoverageTools.getSessionManager();
    manager.removeAllSessions();
  }

}
