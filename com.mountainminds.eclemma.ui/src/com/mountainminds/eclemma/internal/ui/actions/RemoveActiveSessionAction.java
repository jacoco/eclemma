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
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action removes the active coverage session.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class RemoveActiveSessionAction extends Action {
  
  public RemoveActiveSessionAction() {
    setText(UIMessages.RemoveActiveSessionAction_label);
    setToolTipText(UIMessages.RemoveActiveSessionAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_REMOVE));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_REMOVE));
    setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
  }
  
  public void run() {
    ISessionManager manager = CoverageTools.getSessionManager();
    ICoverageSession session = manager.getActiveSession();
    if (session != null) {
      manager.removeSession(session);
    }
  }

}
