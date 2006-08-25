/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action reruns the active coverage session. Internally used by the
 * coverage view.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class SelectEntryModeAction extends Action {
  
  private final ViewSettings settings;
  private final int mode;
  private final CoverageView view;

  
  SelectEntryModeAction(int mode, ViewSettings settings, CoverageView view) {
    super(null, IAction.AS_RADIO_BUTTON);
    this.mode = mode;
    this.settings = settings;
    this.view = view;
    setChecked(mode == settings.getEntryMode());
    String iconkey = null;
    switch (mode) {
      case ViewSettings.ENTRYMODE_PROJECTS:
        setText(UIMessages.SessionsView_showProjectsActionLabel);
        setToolTipText(UIMessages.SessionsView_showProjectsActionTooltip);
        iconkey = EclEmmaUIPlugin.ELCL_SHOWPROJECTS;
        break;
      case ViewSettings.ENTRYMODE_PACKAGEROOTS:
        setText(UIMessages.SessionsView_showPackageRootsActionLabel);
        setToolTipText(UIMessages.SessionsView_showPackageRootsActionTooltip);
        iconkey = EclEmmaUIPlugin.ELCL_SHOWPACKAGEROOTS;
        break;
      case ViewSettings.ENTRYMODE_PACKAGES: 
        setText(UIMessages.SessionsView_showPackagesActionLabel);
        setToolTipText(UIMessages.SessionsView_showPackagesActionTooltip);
        iconkey = EclEmmaUIPlugin.ELCL_SHOWPACKAGES;
        break;
      case ViewSettings.ENTRYMODE_TYPES: 
        setText(UIMessages.SessionsView_showTypesActionLabel);
        setToolTipText(UIMessages.SessionsView_showTypesActionTooltip);
        iconkey = EclEmmaUIPlugin.ELCL_SHOWTYPES;
        break;
    }
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(iconkey));
  }
  
  public void run() {
    settings.setEntryMode(mode);
    view.refreshViewer();
  }

}
