/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;

/**
 * Action to select the counter mode. Internally used by the coverage view.
 *  
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class SelectCounterModeAction extends Action {

  private final int modeidx;
  private final ViewSettings settings;
  private final CoverageView view;
  
  SelectCounterModeAction(int modeidx, ViewSettings settings, CoverageView view) {
    super(ViewSettings.COUNTERMODES[modeidx].getActionLabel(), AS_RADIO_BUTTON);
    this.modeidx = modeidx;
    this.settings = settings;
    this.view = view;
    setChecked(modeidx == settings.getCounterMode().getIdx());
  }

  public void run() {
    settings.setCounterMode(modeidx);
    view.updateColumnHeaders();
    view.refreshViewer();
  }

}
