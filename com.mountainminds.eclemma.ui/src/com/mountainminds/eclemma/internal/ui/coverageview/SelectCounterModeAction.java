/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;

/**
 * Action to select the counter mode. Internally used by the coverage view.
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
