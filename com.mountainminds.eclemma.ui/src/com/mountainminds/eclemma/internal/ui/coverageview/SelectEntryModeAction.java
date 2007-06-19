/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action selects one of the counter modes. Internally used by the
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
        setText(UIMessages.CoverageViewShowProjectsAction_label);
        setToolTipText(UIMessages.CoverageViewShowProjectsAction_tooltip);
        iconkey = EclEmmaUIPlugin.ELCL_SHOWPROJECTS;
        break;
      case ViewSettings.ENTRYMODE_PACKAGEROOTS:
        setText(UIMessages.CoverageViewShowPackageRootsAction_label);
        setToolTipText(UIMessages.CoverageViewShowPackageRootsAction_tooltip);
        iconkey = EclEmmaUIPlugin.ELCL_SHOWPACKAGEROOTS;
        break;
      case ViewSettings.ENTRYMODE_PACKAGES: 
        setText(UIMessages.CoverageViewShowPackagesAction_label);
        setToolTipText(UIMessages.CoverageViewShowPackagesAction_tooltip);
        iconkey = EclEmmaUIPlugin.ELCL_SHOWPACKAGES;
        break;
      case ViewSettings.ENTRYMODE_TYPES: 
        setText(UIMessages.CoverageViewShowTypesAction_label);
        setToolTipText(UIMessages.CoverageViewShowTypesAction_tooltip);
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
