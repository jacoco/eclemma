/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import static com.mountainminds.eclemma.internal.ui.UIMessages.BrowseAction_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportReportPage1NoExecutionDataAddress_message;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportReportPage1NoExecutionDataFile_message;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportReportPage1NoExecutionDataPort_message;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportReportPage1NoExecutionDataUrl_message;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1BrowseDialog_title;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1Copy_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1ExecutionDataAddress_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1ExecutionDataFile_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1ExecutionDataPort_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1ExecutionDataReset_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1ExecutionDataUrl_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1ModeGroup_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1Reference_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1_description;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ImportSessionPage1_title;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.jacoco.core.runtime.AgentOptions;

import com.mountainminds.eclemma.core.AgentExecutionDataSource;
import com.mountainminds.eclemma.core.IExecutionDataSource;
import com.mountainminds.eclemma.core.URLExecutionDataSource;
import com.mountainminds.eclemma.internal.ui.ContextHelp;

/**
 * This wizard page allows selecting a coverage file and class path entries for
 * import.
 */
public class SessionImportPage1 extends WizardPage {

  private static final String ID = "SessionImportPage1"; //$NON-NLS-1$

  private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
  private static final String STORE_SOURCE = STORE_PREFIX + "source"; //$NON-NLS-1$
  private static final String STORE_FILES = STORE_PREFIX + "files"; //$NON-NLS-1$
  private static final String STORE_URLS = STORE_PREFIX + "urls"; //$NON-NLS-1$
  private static final String STORE_ADDRESS = STORE_PREFIX + "address"; //$NON-NLS-1$
  private static final String STORE_PORT = STORE_PREFIX + "port"; //$NON-NLS-1$
  private static final String STORE_RESET = STORE_PREFIX + "reset"; //$NON-NLS-1$
  private static final String STORE_COPY = STORE_PREFIX + "copy"; //$NON-NLS-1$

  private Button fileradio, urlradio, agentradio;
  private Combo filecombo;
  private Button browsebutton;
  private Combo urlcombo;
  private Text addresstext, porttext;
  private Button resetcheck;
  private Button referenceradio, copyradio;

  private IExecutionDataSource dataSource;

  protected SessionImportPage1() {
    super(ID);
    setTitle(ImportSessionPage1_title);
    setDescription(ImportSessionPage1_description);
  }

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    parent.setLayout(layout);
    Composite sourceGroup = new Composite(parent, SWT.NONE);
    GridDataFactory.swtDefaults().grab(true, false)
        .align(SWT.FILL, SWT.BEGINNING).applyTo(sourceGroup);
    GridLayoutFactory.swtDefaults().numColumns(5).applyTo(sourceGroup);
    createFileBlock(sourceGroup);
    createUrlBlock(sourceGroup);
    createAgentBlock(sourceGroup);
    createOptionsBlock(parent);
    setControl(parent);
    ContextHelp.setHelp(parent, ContextHelp.SESSION_IMPORT);
    restoreWidgetValues();
    updateStatus();
  }

  private void createFileBlock(Composite parent) {
    fileradio = new Button(parent, SWT.RADIO);
    fileradio.setText(ImportSessionPage1ExecutionDataFile_label);
    fileradio.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateEnablement();
        updateStatus();
      }
    });
    filecombo = new Combo(parent, SWT.BORDER);
    filecombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        updateStatus();
      }
    });
    GridDataFactory.swtDefaults().span(3, 1).grab(true, false)
        .align(SWT.FILL, SWT.CENTER)
        .hint(convertHorizontalDLUsToPixels(80), SWT.DEFAULT)
        .applyTo(filecombo);
    browsebutton = new Button(parent, SWT.NONE);
    browsebutton.setText(BrowseAction_label);
    GridDataFactory
        .swtDefaults()
        .hint(convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
            SWT.DEFAULT).applyTo(browsebutton);
    browsebutton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        openBrowseDialog();
      }
    });
  }

  private void createUrlBlock(Composite parent) {
    urlradio = new Button(parent, SWT.RADIO);
    urlradio.setText(ImportSessionPage1ExecutionDataUrl_label);
    urlradio.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateEnablement();
        updateStatus();
      }
    });
    urlcombo = new Combo(parent, SWT.BORDER);
    urlcombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        updateStatus();
      }
    });
    GridDataFactory.swtDefaults().span(4, 1).align(SWT.FILL, SWT.CENTER)
        .applyTo(urlcombo);
  }

  private void createAgentBlock(Composite parent) {
    agentradio = new Button(parent, SWT.RADIO);
    agentradio.setText(ImportSessionPage1ExecutionDataAddress_label);
    agentradio.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateStatus();
        updateEnablement();
      }
    });
    addresstext = new Text(parent, SWT.BORDER);
    addresstext.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        updateStatus();
      }
    });
    GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
        .applyTo(addresstext);
    new Label(parent, SWT.NONE)
        .setText(ImportSessionPage1ExecutionDataPort_label);
    porttext = new Text(parent, SWT.BORDER);
    porttext.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        updateStatus();
      }
    });
    resetcheck = new Button(parent, SWT.CHECK);
    resetcheck.setText(ImportSessionPage1ExecutionDataReset_label);
    resetcheck.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateStatus();
      }
    });
  }

  private void createOptionsBlock(Composite parent) {
    parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    Group group = new Group(parent, SWT.NONE);
    group.setText(ImportSessionPage1ModeGroup_label);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    group.setLayout(new GridLayout());
    referenceradio = new Button(group, SWT.RADIO);
    referenceradio.setText(ImportSessionPage1Reference_label);
    copyradio = new Button(group, SWT.RADIO);
    copyradio.setText(ImportSessionPage1Copy_label);
  }

  private void openBrowseDialog() {
    FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
    fd.setText(ImportSessionPage1BrowseDialog_title);
    fd.setFileName(filecombo.getText());
    fd.setFilterExtensions(new String[] { "*.exec", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
    String file = fd.open();
    if (file != null) {
      filecombo.setText(file);
    }
  }

  private void updateEnablement() {
    filecombo.setEnabled(fileradio.getSelection());
    browsebutton.setEnabled(fileradio.getSelection());
    urlcombo.setEnabled(urlradio.getSelection());
    addresstext.setEnabled(agentradio.getSelection());
    porttext.setEnabled(agentradio.getSelection());
    resetcheck.setEnabled(agentradio.getSelection());
  }

  private void updateStatus() {
    dataSource = null;
    if (fileradio.getSelection()) {
      File execfile = new File(filecombo.getText());
      if (!execfile.exists() || !execfile.isFile()) {
        setErrorMessage(ImportReportPage1NoExecutionDataFile_message);
        setPageComplete(false);
        return;
      }
      try {
        dataSource = new URLExecutionDataSource(execfile.toURL());
      } catch (MalformedURLException e) {
        setErrorMessage(ImportReportPage1NoExecutionDataFile_message);
        setPageComplete(false);
        return;
      }
    }
    if (urlradio.getSelection()) {
      try {
        dataSource = new URLExecutionDataSource(new URL(urlcombo.getText()));
      } catch (MalformedURLException e) {
        setErrorMessage(ImportReportPage1NoExecutionDataUrl_message);
        setPageComplete(false);
        return;
      }
    }
    if (agentradio.getSelection()) {
      final String address = addresstext.getText();
      if (address.length() == 0) {
        setErrorMessage(ImportReportPage1NoExecutionDataAddress_message);
        setPageComplete(false);
        return;
      }
      try {
        int port = Integer.parseInt(porttext.getText());
        dataSource = new AgentExecutionDataSource(address, port,
            resetcheck.getSelection());
      } catch (NumberFormatException e) {
        setErrorMessage(ImportReportPage1NoExecutionDataPort_message);
        setPageComplete(false);
        return;
      }
    }
    setErrorMessage(null);
    setPageComplete(true);
  }

  private void restoreWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    WidgetHistory.restoreRadio(settings, STORE_SOURCE, fileradio, urlradio,
        agentradio);
    WidgetHistory.restoreCombo(settings, STORE_FILES, filecombo);
    WidgetHistory.restoreCombo(settings, STORE_URLS, urlcombo);
    WidgetHistory
        .restoreText(settings, STORE_ADDRESS, addresstext, "127.0.0.1"); //$NON-NLS-1$
    WidgetHistory.restoreText(settings, STORE_PORT, porttext,
        String.valueOf(AgentOptions.DEFAULT_PORT));
    WidgetHistory.restoreCheck(settings, STORE_RESET, resetcheck);
    WidgetHistory.restoreRadio(settings, STORE_COPY, referenceradio, copyradio);
    updateEnablement();
  }

  public void saveWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    WidgetHistory.saveRadio(settings, STORE_SOURCE, fileradio, urlradio,
        agentradio);
    WidgetHistory.saveCombo(settings, STORE_FILES, filecombo);
    WidgetHistory.saveCombo(settings, STORE_URLS, urlcombo);
    WidgetHistory.saveText(settings, STORE_ADDRESS, addresstext);
    WidgetHistory.saveText(settings, STORE_PORT, porttext);
    WidgetHistory.saveCheck(settings, STORE_RESET, resetcheck);
    WidgetHistory.saveRadio(settings, STORE_COPY, referenceradio, copyradio);
  }

  public IExecutionDataSource getExecutionDataSource() {
    return dataSource;
  }

  public boolean getCreateCopy() {
    return copyradio.getSelection();
  }

}
