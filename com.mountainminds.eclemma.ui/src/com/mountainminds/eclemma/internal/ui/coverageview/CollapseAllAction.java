/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action will collapse all node in the given tree.
 *  
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class CollapseAllAction extends Action {

  private final TreeViewer viewer;
  
  CollapseAllAction(TreeViewer viewer) {
    super(UIMessages.CoverageViewCollapseAllAction_label, AS_PUSH_BUTTON);
    setToolTipText(UIMessages.CoverageViewCollapseAllAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_COLLAPSEALL));
    this.viewer = viewer;
  }

  public void run() {
    viewer.collapseAll();
  }

}
