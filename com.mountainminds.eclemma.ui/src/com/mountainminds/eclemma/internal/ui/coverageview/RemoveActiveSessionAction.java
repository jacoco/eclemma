/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action reruns the active coverage session. Internally used by the
 * coverage view.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class RemoveActiveSessionAction extends Action {
  
  RemoveActiveSessionAction() {
    setText(UIMessages.SessionsView_removeActiveSessionActionLabel);
    setToolTipText(UIMessages.SessionsView_removeActiveSessionActionTooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_REMOVE));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_REMOVE));
    setEnabled(false);
  }
  
  public void run() {
    ISessionManager manager = CoverageTools.getSessionManager();
    ICoverageSession session = manager.getActiveSession();
    if (session != null) {
      manager.removeSession(session);
    }
  }

}
