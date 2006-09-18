/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.jface.action.Action;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action reloads the active coverage session.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class RefreshSessionAction extends Action {
  
  public RefreshSessionAction() {
    setText(UIMessages.RefreshSessionAction_label);
    setToolTipText(UIMessages.RefreshSessionAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_REFRESH));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_REFRESH));
    setActionDefinitionId("org.eclipse.ui.file.refresh"); //$NON-NLS-1$
    setEnabled(false);
  }

  public void run() {
    CoverageTools.getSessionManager().refreshActiveSession();
  }

}
