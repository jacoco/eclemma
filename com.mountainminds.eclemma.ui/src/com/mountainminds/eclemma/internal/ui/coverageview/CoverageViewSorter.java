/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
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

import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeColumn;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;

import com.mountainminds.eclemma.core.CoverageTools;

/**
 * Internal sorter for the coverage view.
 */
class CoverageViewSorter extends ViewerComparator {

  private final ViewSettings settings;
  private final CoverageView view;
  private final ViewerComparator elementsorter = new JavaElementComparator();

  public CoverageViewSorter(ViewSettings settings, CoverageView view) {
    this.settings = settings;
    this.view = view;
  }

  void addColumn(final TreeViewerColumn viewerColumn, final int columnidx) {
    final TreeColumn column = viewerColumn.getColumn();
    if (settings.getSortColumn() == columnidx) {
      setSortColumnAndDirection(column, settings.isReverseSort());
    }
    column.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        settings.toggleSortColumn(columnidx);
        setSortColumnAndDirection(column, settings.isReverseSort());
        view.refreshViewer();
      }

      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });
  }

  private void setSortColumnAndDirection(TreeColumn sortColumn, boolean reverse) {
    sortColumn.getParent().setSortColumn(sortColumn);
    sortColumn.getParent().setSortDirection(reverse ? SWT.DOWN : SWT.UP);
  }

  public int compare(Viewer viewer, Object e1, Object e2) {
    CounterEntity counters = settings.getCounters();
    ICounter c1 = CoverageTools.getCoverageInfo(e1).getCounter(counters);
    ICounter c2 = CoverageTools.getCoverageInfo(e2).getCounter(counters);
    int res = 0;
    switch (settings.getSortColumn()) {
    case CoverageView.COLUMN_ELEMENT:
      res = elementsorter.compare(viewer, e1, e2);
      break;
    case CoverageView.COLUMN_RATIO:
      res = Double.compare(c1.getCoveredRatio(), c2.getCoveredRatio());
      break;
    case CoverageView.COLUMN_COVERED:
      res = (int) (c1.getCoveredCount() - c2.getCoveredCount());
      break;
    case CoverageView.COLUMN_MISSED:
      res = (int) (c1.getMissedCount() - c2.getMissedCount());
      break;
    case CoverageView.COLUMN_TOTAL:
      res = (int) (c1.getTotalCount() - c2.getTotalCount());
      break;
    }
    if (res == 0) {
      res = elementsorter.compare(viewer, e1, e2);
    } else {
      res = settings.isReverseSort() ? -res : res;
    }
    return res;
  }

}
