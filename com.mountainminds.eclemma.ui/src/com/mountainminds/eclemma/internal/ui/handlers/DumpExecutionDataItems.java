/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.handlers;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.launching.ICoverageLaunch;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * Dynamically created menu items for selecting the coverage launch to dump
 * execution data from.
 */
public class DumpExecutionDataItems extends ContributionItem {

  @Override
  public boolean isDynamic() {
    return true;
  }

  @Override
  public void fill(final Menu menu, int index) {
    for (ICoverageLaunch launch : CoverageTools.getRunningCoverageLaunches()) {
      createItem(menu, index++, launch);
    }
  }

  private void createItem(final Menu parent, final int index,
      final ICoverageLaunch launch) {
    final MenuItem item = new MenuItem(parent, SWT.PUSH, index);
    item.setImage(EclEmmaUIPlugin.getImage(EclEmmaUIPlugin.ELCL_DUMP));
    item.setText(LaunchLabelProvider.getLaunchText(launch));
    item.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        DumpExecutionDataHandler.requestDump(launch);
      }
    });
  }

}
