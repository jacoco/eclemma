/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Dialog to select session to merge and enter a description for the result.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class MergeSessionsDialog extends ListSelectionDialog {

  private String description;

  private Text textdescr;

  /**
   * Creates a new dialog with the given session list and description preset.
   * 
   * @param parent
   *          parent shell
   * @param sessions
   *          List of session the user can select from
   * @param description
   *          Preset value for the description
   */
  public MergeSessionsDialog(Shell parent, ICoverageSession[] sessions,
      String description) {
    super(parent, sessions, new ArrayContentProvider(),
        new WorkbenchLabelProvider(),
        UIMessages.MergeSessionsDialogSelection_label);
    setTitle(UIMessages.MergeSessionsDialog_title);
    setInitialSelections(sessions);
    this.description = description;
  }

  /**
   * Returns the session description entered by the user.
   * 
   * @return session description
   */
  public String getDescription() {
    return description;
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    ContextHelp.setHelp(shell, ContextHelp.MERGE_SESSIONS);
  }

  protected Label createMessageArea(Composite composite) {
    Label l = new Label(composite, SWT.NONE);
    l.setFont(composite.getFont());
    l.setText(UIMessages.MergeSessionsDialogDescription_label);

    textdescr = new Text(composite, SWT.BORDER);
    textdescr.setFont(composite.getFont());
    textdescr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    textdescr.setText(description);
    textdescr.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        updateButtonsEnableState();
      }
    });

    return super.createMessageArea(composite);
  }

  protected Control createDialogArea(Composite parent) {
    Control c = super.createDialogArea(parent);
    getViewer().addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(CheckStateChangedEvent event) {
        updateButtonsEnableState();
      }
    });
    Button b1 = getButton(IDialogConstants.SELECT_ALL_ID);
    b1.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        updateButtonsEnableState();
      }
    });
    Button b2 = getButton(IDialogConstants.DESELECT_ALL_ID);
    b2.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        updateButtonsEnableState();
      }
    });
    return c;
  }

  protected void okPressed() {
    description = textdescr.getText().trim();
    super.okPressed();
  }

  protected void updateButtonsEnableState() {
    Button okButton = getOkButton();
    if (okButton != null && !okButton.isDisposed()) {
      okButton.setEnabled(getViewer().getCheckedElements().length > 1
          && textdescr.getText().trim().length() > 0);
    }
  }

}
