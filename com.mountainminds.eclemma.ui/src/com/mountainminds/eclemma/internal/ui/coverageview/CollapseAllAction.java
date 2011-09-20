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
import org.eclipse.jface.viewers.TreeViewer;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action will collapse all node in the given tree.
 */
class CollapseAllAction extends Action {

  private final TreeViewer viewer;

  CollapseAllAction(TreeViewer viewer) {
    super(UIMessages.CoverageViewCollapseAllAction_label, AS_PUSH_BUTTON);
    setToolTipText(UIMessages.CoverageViewCollapseAllAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.ELCL_COLLAPSEALL));
    this.viewer = viewer;
  }

  public void run() {
    viewer.collapseAll();
  }

}
