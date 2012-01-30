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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * This handler copies a textual representation of the current selection to the
 * clipboard.
 */
class CopyHandler extends AbstractHandler implements ISelectionChangedListener {

  private final Display display;
  private final ViewSettings settings;
  private final ITableLabelProvider labelprovider;
  private final ISelectionProvider selectionSource;

  public CopyHandler(Display display, ViewSettings settings,
      ITableLabelProvider labelprovider, ISelectionProvider selectionSource) {
    this.display = display;
    this.settings = settings;
    this.labelprovider = labelprovider;
    this.selectionSource = selectionSource;
    selectionSource.addSelectionChangedListener(this);
  }

  @Override
  public boolean isEnabled() {
    return !selectionSource.getSelection().isEmpty();
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final StringBuffer sb = new StringBuffer();

    // Header
    final String[] headers = settings.getColumnHeaders();
    sb.append(headers[CoverageView.COLUMN_ELEMENT]).append(SWT.TAB);
    sb.append(headers[CoverageView.COLUMN_RATIO]).append(SWT.TAB);
    sb.append(headers[CoverageView.COLUMN_COVERED]).append(SWT.TAB);
    sb.append(headers[CoverageView.COLUMN_MISSED]).append(SWT.TAB);
    sb.append(headers[CoverageView.COLUMN_TOTAL]).append(Text.DELIMITER);

    // Rows:
    final IStructuredSelection selection = (IStructuredSelection) selectionSource
        .getSelection();
    for (final Object element : selection.toList()) {
      appendColumn(sb, element, CoverageView.COLUMN_ELEMENT).append(SWT.TAB);
      appendColumn(sb, element, CoverageView.COLUMN_RATIO).append(SWT.TAB);
      appendColumn(sb, element, CoverageView.COLUMN_COVERED).append(SWT.TAB);
      appendColumn(sb, element, CoverageView.COLUMN_MISSED).append(SWT.TAB);
      appendColumn(sb, element, CoverageView.COLUMN_TOTAL).append(
          Text.DELIMITER);
    }

    copy(sb.toString());
    return null;
  }

  private StringBuffer appendColumn(StringBuffer sb, Object element, int column) {
    sb.append(labelprovider.getColumnText(element, column));
    return sb;
  }

  private void copy(String text) {
    final Clipboard cb = new Clipboard(display);
    final TextTransfer transfer = TextTransfer.getInstance();
    cb.setContents(new Object[] { text }, new Transfer[] { transfer });
    cb.dispose();
  }

  @Override
  public void dispose() {
    selectionSource.removeSelectionChangedListener(this);
  }

  public void selectionChanged(SelectionChangedEvent event) {
    fireHandlerChanged(new HandlerEvent(this, true, false));
  }

}
