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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;

import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This action copies a textual representation of the current selection to the
 * clipboard.
 */
class CopyAction extends SelectionProviderAction {

  private final Display display;

  private final ViewSettings settings;

  private final ITableLabelProvider labelprovider;

  private final ISelectionProvider selectionSource;

  public CopyAction(Display display, ViewSettings settings,
      ITableLabelProvider labelprovider, ISelectionProvider selectionSource) {
    super(selectionSource, UIMessages.CopyAction_label);
    this.display = display;
    this.settings = settings;
    this.labelprovider = labelprovider;
    this.selectionSource = selectionSource;
    ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
    setImageDescriptor(sharedImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    setDisabledImageDescriptor(sharedImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
    setActionDefinitionId("org.eclipse.ui.edit.copy"); //$NON-NLS-1$
  }

  public void selectionChanged(IStructuredSelection selection) {
    setEnabled(!selection.isEmpty());
  }

  public void run() {
    final StringBuffer sb = new StringBuffer();

    // Header
    final String[] headers = settings.getCounterMode().getColumnHeaders();
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
  }

  private final StringBuffer appendColumn(StringBuffer sb, Object element,
      int column) {
    sb.append(labelprovider.getColumnText(element, column));
    return sb;
  }

  private void copy(String text) {
    final Clipboard cb = new Clipboard(display);
    final TextTransfer transfer = TextTransfer.getInstance();
    cb.setContents(new Object[] { text }, new Transfer[] { transfer });
    cb.dispose();
  }

}
