/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action removes all coverage sessions. Internally used by the coverage
 * view.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class RemoveAllSessionsAction extends Action {
  
  RemoveAllSessionsAction() {
    setText(UIMessages.SessionsView_removeAllSessionsActionLabel);
    setToolTipText(UIMessages.SessionsView_removeAllSessionsActionTooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_REMOVEALL));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_REMOVEALL));
    setEnabled(false);
  }
  
  public void run() {
    ISessionManager manager = CoverageTools.getSessionManager();
    manager.removeAllSessions();
  }

}
