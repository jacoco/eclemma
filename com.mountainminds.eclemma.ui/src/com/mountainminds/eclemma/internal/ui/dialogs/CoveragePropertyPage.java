/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.analysis.ICounter;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Property page for coverage details of a Java element.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class CoveragePropertyPage extends PropertyPage {

  private static final int COLUMN_COUNTER = 0;
  private static final int COLUMN_COVERAGE = 1;
  private static final int COLUMN_COVERED = 2;
  private static final int COLUMN_MISSED = 3;
  private static final int COLUMN_TOTAL = 4;

  private static final DecimalFormat COVERAGE_VALUE = new DecimalFormat(
      UIMessages.CoveragePropertyPageColumnCoverage_value);

  protected Control createContents(Composite parent) {
    ContextHelp.setHelp(parent, ContextHelp.COVERAGE_PROPERTIES);
    noDefaultAndApplyButton();
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    parent.setLayout(layout);

    Label l1 = new Label(parent, SWT.NONE);
    l1.setText(UIMessages.CoveragePropertyPageSession_label);
    l1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

    Text t1 = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
    t1.setText(getSessionDescription());
    t1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

    Control table = createTable(parent);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd.horizontalSpan = 2;
    table.setLayoutData(gd);

    return parent;
  }

  private String getSessionDescription() {
    ICoverageSession session = CoverageTools.getSessionManager()
        .getActiveSession();
    return session == null ? UIMessages.CoveragePropertyPageNoSession_value
        : session.getDescription();
  }

  private Control createTable(Composite parent) {
    Table table = new Table(parent, SWT.BORDER);
    initializeDialogUnits(table);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    createColumn(table, SWT.LEFT, 24,
        UIMessages.CoveragePropertyPageColumnCounter_label);
    createColumn(table, SWT.RIGHT, 16,
        UIMessages.CoveragePropertyPageColumnCoverage_label);
    createColumn(table, SWT.RIGHT, 16,
        UIMessages.CoveragePropertyPageColumnCovered_label);
    createColumn(table, SWT.RIGHT, 16,
        UIMessages.CoveragePropertyPageColumnMissed_label);
    createColumn(table, SWT.RIGHT, 16,
        UIMessages.CoveragePropertyPageColumnTotal_label);
    TableViewer viewer = new TableViewer(table);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.addFilter(new ViewerFilter() {
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        return ((Line) element).counter.getTotalCount() != 0;
      }
    });
    viewer.setInput(getLines());
    viewer.setLabelProvider(new CounterLabelProvider());
    return table;
  }

  private void createColumn(Table table, int align, int width, String caption) {
    TableColumn column = new TableColumn(table, align);
    column.setText(caption);
    column.setWidth(convertWidthInCharsToPixels(width));
  }

  private Line[] getLines() {
    IJavaElementCoverage c = CoverageTools.getCoverageInfo(getElement());
    if (c == null) {
      return new Line[0];
    } else {
      return new Line[] {
          new Line(UIMessages.CoveragePropertyPageInstructions_label, c
              .getInstructionCounter()),
          new Line(UIMessages.CoveragePropertyPageBlocks_label, c
              .getBlockCounter()),
          new Line(UIMessages.CoveragePropertyPageLines_label, c
              .getLineCounter()),
          new Line(UIMessages.CoveragePropertyPageMethods_label, c
              .getMethodCounter()),
          new Line(UIMessages.CoveragePropertyPageTypes_label, c
              .getTypeCounter()) };
    }
  }

  private static class Line {
    public final String label;
    public final ICounter counter;

    public Line(String label, ICounter counter) {
      this.label = label;
      this.counter = counter;
    }
  }

  private static class CounterLabelProvider extends LabelProvider implements
      ITableLabelProvider {

    public Image getColumnImage(Object element, int columnIndex) {
      if (columnIndex == COLUMN_COUNTER) {
        Line l = (Line) element;
        return EclEmmaUIPlugin.getCoverageImage(l.counter.getRatio());
      } else {
        return null;
      }
    }

    public String getColumnText(Object element, int columnIndex) {
      Line l = (Line) element;
      switch (columnIndex) {
      case COLUMN_COUNTER:
        return l.label;
      case COLUMN_COVERAGE:
        return COVERAGE_VALUE.format(l.counter.getRatio());
      case COLUMN_COVERED:
        return String.valueOf(l.counter.getCoveredCount());
      case COLUMN_MISSED:
        return String.valueOf(l.counter.getMissedCount());
      case COLUMN_TOTAL:
        return String.valueOf(l.counter.getTotalCount());
      default:
        return ""; //$NON-NLS-1$
      }
    }

  }

}
