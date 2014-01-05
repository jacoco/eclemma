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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.mountainminds.eclemma.core.ScopeUtils;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.ScopeViewer;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This wizard page allows selecting a coverage file and class path entries for
 * import.
 */
public class SessionImportPage2 extends WizardPage {

  private static final String ID = "SessionImportPage2"; //$NON-NLS-1$

  private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
  private static final String STORE_SCOPE = STORE_PREFIX + "scope"; //$NON-NLS-1$
  private static final String STORE_BINARIES = STORE_PREFIX + "binaries"; //$NON-NLS-1$

  private Text descriptiontext;
  private ScopeViewer scopeviewer;
  private Button binariescheck;

  protected SessionImportPage2() {
    super(ID);
    setTitle(UIMessages.ImportSessionPage1_title);
    setDescription(UIMessages.ImportSessionPage1_description);
  }

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    parent.setLayout(layout);
    createNameBlock(parent);
    createScopeBlock(parent);
    createButtonsBlock(parent);
    setControl(parent);
    ContextHelp.setHelp(parent, ContextHelp.SESSION_IMPORT);
    restoreWidgetValues();
    update();
  }

  private void createNameBlock(Composite parent) {
    parent = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    parent.setLayout(layout);
    parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    new Label(parent, SWT.NONE)
        .setText(UIMessages.ImportSessionPage1Description_label);
    descriptiontext = new Text(parent, SWT.BORDER);
    descriptiontext.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        update();
      }
    });
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    descriptiontext.setLayoutData(gd);
  }

  private void createScopeBlock(Composite parent) {
    scopeviewer = new ScopeViewer(parent, SWT.BORDER);
    try {
      scopeviewer.setInput(ScopeUtils.getWorkspaceScope());
    } catch (JavaModelException e) {
      EclEmmaUIPlugin.log(e);
    }
    scopeviewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        update();
      }
    });
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.widthHint = convertHorizontalDLUsToPixels(120);
    gd.heightHint = convertHeightInCharsToPixels(8);
    scopeviewer.getTable().setLayoutData(gd);
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
        scopeviewer.setIncludeBinaries(binariescheck.getSelection());
        update();
      }
    });
    binariescheck.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
    Button buttonSelectAll = new Button(parent, SWT.PUSH);
    buttonSelectAll.setText(UIMessages.SelectAllAction_label);
    buttonSelectAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        scopeviewer.selectAll();
        update();
      }
    });
    setButtonLayoutData(buttonSelectAll);
    Button buttonDeselectAll = new Button(parent, SWT.PUSH);
    buttonDeselectAll.setText(UIMessages.DeselectAllAction_label);
    buttonDeselectAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        scopeviewer.deselectAll();
        update();
      }
    });
    setButtonLayoutData(buttonDeselectAll);
  }

  private void update() {
    if (getSessionDescription().length() == 0) {
      setErrorMessage(UIMessages.ImportReportPage1NoDescription_message);
      setPageComplete(false);
      return;
    }
    if (getScope().isEmpty()) {
      setErrorMessage(UIMessages.ImportReportPage1NoClassFiles_message);
      setPageComplete(false);
      return;
    }
    setErrorMessage(null);
    setPageComplete(true);
  }

  private void restoreWidgetValues() {
    String descr = UIMessages.ImportSessionPage1Description_value;
    Object[] arg = new Object[] { new Date() };
    descriptiontext.setText(MessageFormat.format(descr, arg));
    IDialogSettings settings = getDialogSettings();
    boolean binaries = settings.getBoolean(STORE_BINARIES);
    scopeviewer.setIncludeBinaries(binaries);
    binariescheck.setSelection(binaries);
    String[] classes = settings.getArray(STORE_SCOPE);
    if (classes != null) {
      scopeviewer
          .setSelectedScope(ScopeUtils.readScope(Arrays.asList(classes)));
    }
  }

  public void saveWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    settings.put(
        STORE_SCOPE,
        ScopeUtils.writeScope(scopeviewer.getSelectedScope()).toArray(
            new String[0]));
    settings.put(STORE_BINARIES, binariescheck.getSelection());
  }

  public String getSessionDescription() {
    return descriptiontext.getText().trim();
  }

  public Set<IPackageFragmentRoot> getScope() {
    return scopeviewer.getSelectedScope();
  }

}
