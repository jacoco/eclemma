/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.jdt.ui.JavaElementSorter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeColumn;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.analysis.ICounter;
import com.mountainminds.eclemma.internal.ui.coverageview.ViewSettings.ICounterMode;

/**
 * Internal sorter for the coverage view.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class CoverageViewSorter extends ViewerSorter {

  private final ViewSettings settings;
  private final CoverageView view;
  private final ViewerSorter elementsorter = new JavaElementSorter();
    
  public CoverageViewSorter(ViewSettings settings, CoverageView view) {
    this.settings = settings;
    this.view = view;
  }
  
  void addColumn(TreeColumn column, final int columnidx) {
    column.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        settings.toggleSortColumn(columnidx);
        view.refreshViewer();
      }
      public void widgetDefaultSelected(SelectionEvent e) { }
    });
  }
  
  public int compare(Viewer viewer, Object e1, Object e2) {
    ICounterMode mode = settings.getCounterMode();
    ICounter c1 = mode.getCounter(CoverageTools.getCoverageInfo(e1));
    ICounter c2 = mode.getCounter(CoverageTools.getCoverageInfo(e2));
    int res = 0;
    switch (settings.getSortColumn()) {
      case CoverageView.COLUMN_ELEMENT:
        res = elementsorter.compare(viewer, e1, e2);
        break;
      case CoverageView.COLUMN_RATIO:
        res = Double.compare(c1.getRatio(), c2.getRatio());
        break;
      case CoverageView.COLUMN_COVERED:
        res = (int) (c1.getCoveredCount() - c2.getCoveredCount());
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
