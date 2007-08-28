/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 * 
 * Contributors:
 *   Brock Janiczak - link with selection option (SF #1774547)
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Action to enable linking of the coverage view's selection with the current
 * selection in the workbench. Internally used by the coverage view.
 *  
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class LinkWithSelectionAction extends Action {

  private final ViewSettings settings;
  private final SelectionTracker tracker;
  
  LinkWithSelectionAction(ViewSettings settings, SelectionTracker tracker) {
    super(UIMessages.CoverageViewLinkWithSelectionAction_label, AS_CHECK_BOX);
    setToolTipText(UIMessages.CoverageViewLinkWithSelectionAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_LINKED));
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
