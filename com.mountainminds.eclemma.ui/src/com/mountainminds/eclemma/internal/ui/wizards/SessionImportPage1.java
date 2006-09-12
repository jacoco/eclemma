/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This wizard page allows selecting a coverage file and class path entries
 * for import.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionImportPage1 extends WizardPage {
  
  private static final String ID = "SessionImportPage1"; //$NON-NLS-1$
  
  private static final int TEXT_FIELD_WIDTH = 250;
  
  private Combo sourcecombo;
  private TableViewer packagestable;
  private Button referenceradio;
  private Button copyradio;


  protected SessionImportPage1() {
    super(ID);
    setTitle(UIMessages.ImportSessionPage1_title);
    setDescription(UIMessages.ImportSessionPage1_description);
  }

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    parent = new Composite(parent, SWT.NONE);
    parent.setLayout(new GridLayout(3, false));
    new Label(parent, SWT.NONE).setText(UIMessages.ImportSessionPage1CoverageFile_label);
    sourcecombo = new Combo(parent, SWT.BORDER);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = TEXT_FIELD_WIDTH;
    sourcecombo.setLayoutData(gd);
    Button browsebutton = new Button(parent, SWT.NONE);
    browsebutton.setText(UIMessages.Browse_action);
    setButtonLayoutData(browsebutton);
    browsebutton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        openBrowseDialog();
      }
    });
    packagestable = new TableViewer(parent, SWT.BORDER | SWT.READ_ONLY);
    gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 3;
    packagestable.getControl().setLayoutData(gd);
    Group group = new Group(parent, SWT.NONE);
    group.setText(UIMessages.ImportSessionPage1ModeGroup_label);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 3;
    group.setLayoutData(gd);
    group.setLayout(new GridLayout());
    referenceradio = new Button(group, SWT.RADIO);
    referenceradio.setText(UIMessages.ImportSessionPage1Reference_label);
    copyradio = new Button(group, SWT.RADIO);
    copyradio.setText(UIMessages.ImportSessionPage1Copy_label);
    setControl(parent);
  }

  private void openBrowseDialog() {
    FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
    fd.setText(UIMessages.ImportSessionPage1BrowseDialog_title);
    fd.setFileName(sourcecombo.getText());
    fd.setFilterExtensions(new String[] { "*.ec", "*.es", "*.*"} );
    String file = fd.open();
    if (file != null) {
      sourcecombo.setText(file);
    }
  }
  
}
