/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    Brock Janiczak - link with selection option (SF #1774547)
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Action to enable linking of the coverage view's selection with the current
 * selection in the workbench. Internally used by the coverage view.
 */
class LinkWithSelectionAction extends Action {

  private final ViewSettings settings;
  private final SelectionTracker tracker;

  LinkWithSelectionAction(ViewSettings settings, SelectionTracker tracker) {
    super(UIMessages.CoverageViewLinkWithSelectionAction_label, AS_CHECK_BOX);
    setToolTipText(UIMessages.CoverageViewLinkWithSelectionAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.ELCL_LINKED));
    this.settings = settings;
    this.tracker = tracker;
    setChecked(settings.getLinked());
  }

  public void run() {
    boolean flag = isChecked();
    settings.setLinked(flag);
    tracker.setEnabled(flag);
  }

}
