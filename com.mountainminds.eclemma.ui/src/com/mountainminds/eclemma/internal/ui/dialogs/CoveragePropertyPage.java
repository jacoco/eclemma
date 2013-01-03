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
package com.mountainminds.eclemma.internal.ui.dialogs;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.RedGreenBar;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Property page for coverage details of a Java element.
 */
public class CoveragePropertyPage extends PropertyPage {

  private static final NumberFormat COVERAGE_VALUE = new DecimalFormat(
      UIMessages.CoveragePropertyPageColumnCoverage_value);

  private static final NumberFormat COUNTER_VALUE = DecimalFormat
      .getIntegerInstance();

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
    t1.setBackground(t1.getDisplay()
        .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

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
    final Table table = new Table(parent, SWT.BORDER);
    initializeDialogUnits(table);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    TableViewer viewer = new TableViewer(table);
    createColumn(viewer, SWT.LEFT, 20,
        UIMessages.CoveragePropertyPageColumnCounter_label,
        new CellLabelProvider() {
          @Override
          public void update(ViewerCell cell) {
            final Line line = (Line) cell.getElement();
            cell.setText(line.label);
          }
        });
    createColumn(viewer, SWT.RIGHT, 20,
        UIMessages.CoveragePropertyPageColumnCoverage_label,
        new OwnerDrawLabelProvider() {
          @Override
          public void update(ViewerCell cell) {
            final Line line = (Line) cell.getElement();
            cell.setText(COVERAGE_VALUE.format(line.counter.getCoveredRatio()));
          }

          @Override
          protected void paint(Event event, Object element) {
            final Line line = (Line) element;
            RedGreenBar
                .draw(event, table.getColumn(1).getWidth(), line.counter);
          }

          @Override
          protected void erase(Event event, Object element) {
          }

          @Override
          protected void measure(Event event, Object element) {
          }
        });
    createColumn(viewer, SWT.RIGHT, 16,
        UIMessages.CoveragePropertyPageColumnCovered_label,
        new CellLabelProvider() {
          @Override
          public void update(ViewerCell cell) {
            final Line line = (Line) cell.getElement();
            cell.setText(COUNTER_VALUE.format(line.counter.getCoveredCount()));
          }
        });
    createColumn(viewer, SWT.RIGHT, 16,
        UIMessages.CoveragePropertyPageColumnMissed_label,
        new CellLabelProvider() {
          @Override
          public void update(ViewerCell cell) {
            final Line line = (Line) cell.getElement();
            cell.setText(COUNTER_VALUE.format(line.counter.getMissedCount()));
          }
        });
    createColumn(viewer, SWT.RIGHT, 16,
        UIMessages.CoveragePropertyPageColumnTotal_label,
        new CellLabelProvider() {
          @Override
          public void update(ViewerCell cell) {
            final Line line = (Line) cell.getElement();
            cell.setText(COUNTER_VALUE.format(line.counter.getTotalCount()));
          }
        });
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.addFilter(new ViewerFilter() {
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        return ((Line) element).counter.getTotalCount() != 0;
      }
    });
    viewer.setInput(getLines());
    return table;
  }

  private void createColumn(TableViewer viewer, int align, int width,
      String caption, CellLabelProvider labelProvider) {
    TableViewerColumn column = new TableViewerColumn(viewer, align);
    column.getColumn().setText(caption);
    column.getColumn().setWidth(convertWidthInCharsToPixels(width));
    column.setLabelProvider(labelProvider);
  }

  private Line[] getLines() {
    ICoverageNode c = CoverageTools.getCoverageInfo(getElement());
    if (c == null) {
      return new Line[0];
    } else {
      return new Line[] {
          new Line(UIMessages.CoveragePropertyPageInstructions_label,
              c.getInstructionCounter()),
          new Line(UIMessages.CoveragePropertyPageBranches_label,
              c.getBranchCounter()),
          new Line(UIMessages.CoveragePropertyPageLines_label,
              c.getLineCounter()),
          new Line(UIMessages.CoveragePropertyPageMethods_label,
              c.getMethodCounter()),
          new Line(UIMessages.CoveragePropertyPageTypes_label,
              c.getClassCounter()),
          new Line(UIMessages.CoveragePropertyPageComplexity_label,
              c.getComplexityCounter()) };
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

}
