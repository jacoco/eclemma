/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogSettings;
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

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.viewers.ClassesViewer;

/**
 * This wizard page allows selecting a coverage file and class path entries
 * for import.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionImportPage1 extends WizardPage {
  
  private static final String ID = "SessionImportPage1"; //$NON-NLS-1$

  private static final int LIST_HEIGHT = 120;
  private static final int TEXT_FIELD_WIDTH = 250;
  
  private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
  private static final String STORE_FILES = STORE_PREFIX + "files"; //$NON-NLS-1$
  private static final String STORE_CLASSES = STORE_PREFIX + "classes"; //$NON-NLS-1$
  private static final String STORE_BINARIES = STORE_PREFIX + "binaries"; //$NON-NLS-1$
  private static final String STORE_COPY = STORE_PREFIX + "copy"; //$NON-NLS-1$
  
  private Combo filecombo;
  private ClassesViewer classesviewer;
  private Button binariescheck;
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
    filecombo = new Combo(parent, SWT.BORDER);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = TEXT_FIELD_WIDTH;
    filecombo.setLayoutData(gd);
    Button browsebutton = new Button(parent, SWT.NONE);
    browsebutton.setText(UIMessages.Browse_action);
    setButtonLayoutData(browsebutton);
    browsebutton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        openBrowseDialog();
      }
    });
    classesviewer = new ClassesViewer(parent, SWT.BORDER);
    try {
      classesviewer.setInput(CoverageTools.getClassFiles());
    } catch (CoreException e) {
      EclEmmaUIPlugin.log(e);
    }
    gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 3;
    gd.heightHint = LIST_HEIGHT;
    classesviewer.getTable().setLayoutData(gd);
    binariescheck = new Button(parent, SWT.CHECK);
    binariescheck.setText(UIMessages.ImportSessionPage1Binaries_label);
    binariescheck.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        classesviewer.setIncludeBinaries(binariescheck.getSelection());
      }
    });
    gd = new GridData();
    gd.horizontalSpan = 3;
    binariescheck.setLayoutData(gd);    
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
    restoreWidgetValues();
  }

  private void openBrowseDialog() {
    FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
    fd.setText(UIMessages.ImportSessionPage1BrowseDialog_title);
    fd.setFileName(filecombo.getText());
    fd.setFilterExtensions(new String[] { "*.ec", "*.es", "*.*"} ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String file = fd.open();
    if (file != null) {
      filecombo.setText(file);
    }
  }
  
  protected void restoreWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    ComboHistory.restore(settings, STORE_FILES, filecombo);
    boolean binaries = settings.getBoolean(STORE_BINARIES);
    classesviewer.setIncludeBinaries(binaries);
    binariescheck.setSelection(binaries);
    String[] classes = settings.getArray(STORE_CLASSES);
    if (classes != null) {
      classesviewer.setSelectedClasses(classes);
    }
    boolean copy = settings.getBoolean(STORE_COPY);
    referenceradio.setSelection(!copy);
    copyradio.setSelection(copy);
  }
  
  public void saveWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    ComboHistory.save(settings, STORE_FILES, filecombo);
    settings.put(STORE_CLASSES, classesviewer.getSelectedClassesLocations());
    settings.put(STORE_BINARIES, binariescheck.getSelection());
    settings.put(STORE_COPY, copyradio.getSelection());
  }
  
}
