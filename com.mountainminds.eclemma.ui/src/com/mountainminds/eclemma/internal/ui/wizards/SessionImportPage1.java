/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
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
  
  private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
  private static final String STORE_FILES = STORE_PREFIX + "files"; //$NON-NLS-1$
  private static final String STORE_CLASSES = STORE_PREFIX + "classes"; //$NON-NLS-1$
  private static final String STORE_BINARIES = STORE_PREFIX + "binaries"; //$NON-NLS-1$
  private static final String STORE_COPY = STORE_PREFIX + "copy"; //$NON-NLS-1$
  private static final String STORE_IMPORTMETADATA = STORE_PREFIX + "importmetadata"; //$NON-NLS-1$
  
  private Text descriptiontext;
  private Combo filecombo;
  private ClassesViewer classesviewer;
  private Button binariescheck;
  private Button referenceradio;
  private Button copyradio;
  private Button ideclassesradio;
  private Button importmetadataradio;

  protected SessionImportPage1() {
    super(ID);
    setTitle(UIMessages.ImportSessionPage1_title);
    setDescription(UIMessages.ImportSessionPage1_description);
  }

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    parent.setLayout(layout);
    createNameAndFileBlock(parent);
    createClassPathsBlock(parent);
    createButtonsBlock(parent);
    createOptionsBlock(parent);
    setControl(parent);
    ContextHelp.setHelp(parent, ContextHelp.SESSION_IMPORT);
    restoreWidgetValues();
    update();
  }

  private void createNameAndFileBlock(Composite parent) {
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    parent.setLayout(layout);
    parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    new Label(parent, SWT.NONE).setText(UIMessages.ImportSessionPage1Description_label);
    descriptiontext = new Text(parent, SWT.BORDER);
    descriptiontext.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        update();
      }
    });
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    descriptiontext.setLayoutData(gd);
    new Label(parent, SWT.NONE).setText(UIMessages.ImportSessionPage1CoverageFile_label);
    filecombo = new Combo(parent, SWT.BORDER);
    filecombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        update();
      }
    });
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = convertHorizontalDLUsToPixels(100);
    filecombo.setLayoutData(gd);
    Button browsebutton = new Button(parent, SWT.NONE);
    browsebutton.setText(UIMessages.BrowseAction_label);
    setButtonLayoutData(browsebutton);
    browsebutton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        openBrowseDialog();
      }
    });
  }
  
  private void createClassPathsBlock(Composite parent) {
    classesviewer = new ClassesViewer(parent, SWT.BORDER);
    try {
      classesviewer.setInput(CoverageTools.getClassFiles());
    } catch (CoreException e) {
      EclEmmaUIPlugin.log(e);
    }
    classesviewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        update();
      }
    });
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.widthHint = convertHorizontalDLUsToPixels(120);
    gd.heightHint = convertHeightInCharsToPixels(8);
    classesviewer.getTable().setLayoutData(gd);
  }
  
  private void createButtonsBlock(Composite parent) {
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    parent.setLayout(layout);
    parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    binariescheck = new Button(parent, SWT.CHECK);
    binariescheck.setText(UIMessages.ImportSessionPage1Binaries_label);
    binariescheck.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        classesviewer.setIncludeBinaries(binariescheck.getSelection());
        update();
      }
    });
    binariescheck.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
    Button buttonSelectAll = new Button(parent, SWT.PUSH);
    buttonSelectAll.setText(UIMessages.SelectAllAction_label);
    buttonSelectAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        classesviewer.selectAll();
        update();
      }
    });
    setButtonLayoutData(buttonSelectAll);
    Button buttonDeselectAll = new Button(parent, SWT.PUSH);
    buttonDeselectAll.setText(UIMessages.DeselectAllAction_label);
    buttonDeselectAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        classesviewer.deselectAll();
        update();
      }
    });
    setButtonLayoutData(buttonDeselectAll);
  }

  private void createOptionsBlock(Composite parent) {
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, true);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    parent.setLayout(layout);
    parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    Group group = new Group(parent, SWT.NONE);
    group.setText(UIMessages.ImportSessionPage1ModeGroup_label);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    group.setLayout(new GridLayout());
    referenceradio = new Button(group, SWT.RADIO);
    referenceradio.setText(UIMessages.ImportSessionPage1Reference_label);
    copyradio = new Button(group, SWT.RADIO);
    copyradio.setText(UIMessages.ImportSessionPage1Copy_label);
    group = new Group(parent, SWT.NONE);
    group.setText(UIMessages.ImportSessionPage1MetadataGroup_label);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    group.setLayout(new GridLayout());
    ideclassesradio = new Button(group, SWT.RADIO);
    ideclassesradio.setText(UIMessages.ImportSessionPage1IDEClasses_label);
    importmetadataradio = new Button(group, SWT.RADIO);
    importmetadataradio.setText(UIMessages.ImportSessionPage1ImportMetaData_label);
  }
  
  private void openBrowseDialog() {
    FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
    fd.setText(UIMessages.ImportSessionPage1BrowseDialog_title);
    fd.setFileName(filecombo.getText());
    fd.setFilterExtensions(new String[] { "*.ec;*.es", "*.*"} ); //$NON-NLS-1$ //$NON-NLS-2$
    String file = fd.open();
    if (file != null) {
      filecombo.setText(file);
    }
  }
  
  private void update() {
    if (getSessionDescription().length() == 0) {
      setMessage(UIMessages.ImportReportPage1NoDescription_message);
      setPageComplete(false);
      return;
    }
    File cf = new File(getCoverageFile());
    if (!cf.exists() || !cf.isFile()) {
      setMessage(UIMessages.ImportReportPage1NoCoverageFile_message);
      setPageComplete(false);
      return;
    }
    if (getClassFiles().length == 0) {
      setMessage(UIMessages.ImportReportPage1NoClassFiles_message);
      setPageComplete(false);
      return;
    }
    setErrorMessage(null);
    setMessage(null);
    setPageComplete(true);
  }
  
  protected void restoreWidgetValues() {
    String descr = UIMessages.ImportSessionPage1Description_value;
    Object[] arg = new Object[] { new Date() };
    descriptiontext.setText(MessageFormat.format(descr, arg));
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
    boolean importmetadata = settings.getBoolean(STORE_IMPORTMETADATA);
    ideclassesradio.setSelection(!importmetadata);
    importmetadataradio.setSelection(importmetadata);
  }
  
  public void saveWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    ComboHistory.save(settings, STORE_FILES, filecombo);
    settings.put(STORE_CLASSES, classesviewer.getSelectedClassesLocations());
    settings.put(STORE_BINARIES, binariescheck.getSelection());
    settings.put(STORE_COPY, copyradio.getSelection());
    settings.put(STORE_IMPORTMETADATA, importmetadataradio.getSelection());
  }
  
  public String getSessionDescription() {
    return descriptiontext.getText().trim();
  }

  public String getCoverageFile() {
    return filecombo.getText();
  }

  public IClassFiles[] getClassFiles() {
    return classesviewer.getSelectedClasses();
  }
  
  public boolean getCreateCopy() {
    return copyradio.getSelection();
  }
  
  public boolean getUseImportedMetaData() {
    return importmetadataradio.getSelection();
  }
  
}
