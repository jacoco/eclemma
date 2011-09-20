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

import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Action to hide types not loaded at all. Internally used by the coverage view.
 */
class HideUnusedTypesAction extends Action {

  private final ViewSettings settings;
  private final CoverageView view;

  HideUnusedTypesAction(ViewSettings settings, CoverageView view) {
    super(UIMessages.CoverageViewHideUnusedTypesAction_label, AS_CHECK_BOX);
    this.settings = settings;
    this.view = view;
    setChecked(settings.getHideUnusedTypes());
  }

  public void run() {
    settings.setHideUnusedTypes(isChecked());
    view.refreshViewer();
  }

}
