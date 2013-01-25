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
package com.mountainminds.eclemma.internal.ui.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionExporter.ExportFormat;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This wizard page allows selecting a coverage session, the output format and
 * destination.
 */
public class SessionExportPage1 extends WizardPage {

  private static final String ID = "SessionExportPage1"; //$NON-NLS-1$

  private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
  private static final String STORE_FORMAT = STORE_PREFIX + "format"; //$NON-NLS-1$
  private static final String STORE_DESTINATIONS = STORE_PREFIX
      + "destinations"; //$NON-NLS-1$

  private TableViewer sessionstable;
  private ComboViewer formatcombo;
  private Combo destinationcombo;

  public SessionExportPage1() {
    super(ID);
    setTitle(UIMessages.ExportReportPage1_title);
    setDescription(UIMessages.ExportReportPage1_description);
  }

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    parent = new Composite(parent, SWT.NONE);
    parent.setLayout(new GridLayout());
    new Label(parent, SWT.NONE)
        .setText(UIMessages.ExportReportPage1Sessions_label);
    sessionstable = new TableViewer(parent, SWT.BORDER);
    sessionstable.setLabelProvider(new WorkbenchLabelProvider());
    sessionstable.setContentProvider(new ArrayContentProvider());
    sessionstable.setInput(CoverageTools.getSessionManager().getSessions());
    ICoverageSession active = CoverageTools.getSessionManager()
        .getActiveSession();
    if (active != null) {
      sessionstable.setSelection(new StructuredSelection(active));
    }
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.heightHint = convertHeightInCharsToPixels(8);
    sessionstable.getControl().setLayoutData(gd);
    Group group = new Group(parent, SWT.NONE);
    group.setText(UIMessages.ExportReportPage1DestinationGroup_label);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    createExportOptionsGroup(group);
    setControl(parent);
    ContextHelp.setHelp(parent, ContextHelp.SESSION_EXPORT);
    restoreWidgetValues();
  }

  private void createExportOptionsGroup(Composite parent) {
    parent.setLayout(new GridLayout(3, false));
    new Label(parent, SWT.NONE)
        .setText(UIMessages.ExportReportPage1Format_label);
    formatcombo = new ComboViewer(parent, SWT.READ_ONLY);
    formatcombo.setContentProvider(new ArrayContentProvider());
    formatcombo.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        return ((ExportFormat) element).getLabel();
      }
    });
    formatcombo.setInput(ExportFormat.values());
    formatcombo.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        IPath path = Path.fromOSString(destinationcombo.getText());
        path = path.removeFileExtension();
        final ExportFormat format = getExportFormat();
        if (!format.isFolderOutput()) {
          path = path.addFileExtension(format.getFileExtension());
        }
        destinationcombo.setText(path.toOSString());
      }
    });
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    formatcombo.getControl().setLayoutData(gd);
    new Label(parent, SWT.NONE)
        .setText(UIMessages.ExportReportPage1Destination_label);
    destinationcombo = new Combo(parent, SWT.BORDER);
    destinationcombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        update();
      }
    });
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = convertHorizontalDLUsToPixels(120);
    destinationcombo.setLayoutData(gd);
    Button browsebutton = new Button(parent, SWT.NONE);
    browsebutton.setText(UIMessages.BrowseAction_label);
    setButtonLayoutData(browsebutton);
    browsebutton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (getExportFormat().isFolderOutput()) {
          openFolderDialog();
        } else {
          openFileDialog();
        }
      }
    });
    update();
  }

  private void openFileDialog() {
    FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
    fd.setText(UIMessages.ExportReportPage1BrowseDialog_title);
    fd.setFileName(destinationcombo.getText());
    String ext = getExportFormat().getFileExtension();
    fd.setFilterExtensions(new String[] { "*." + ext, "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
    String file = fd.open();
    if (file != null) {
      destinationcombo.setText(file);
    }
  }

  private void openFolderDialog() {
    final DirectoryDialog fd = new DirectoryDialog(getShell(), SWT.NONE);
    fd.setText(UIMessages.ExportReportPage1BrowseDialog_title);
    fd.setFilterPath(destinationcombo.getText());
    final String folder = fd.open();
    if (folder != null) {
      destinationcombo.setText(folder);
    }
  }

  private void update() {
    // make sure we have a session to export
    if (getSelectedSession() == null) {
      setErrorMessage(UIMessages.ExportReportPage1NoSession_message);
      setPageComplete(false);
      return;
    }
    // a destination file must be spezified
    if (getDestination().length() == 0) {
      setMessage(UIMessages.ExportReportPage1MissingDestination_message);
      setPageComplete(false);
      return;
    }
    final ExportFormat format = getExportFormat();
    if (!format.isFolderOutput()) {
      // the extension should correspond to the report type
      String exta = Path.fromOSString(getDestination()).getFileExtension();
      String exte = format.getFileExtension();
      if (!exte.equalsIgnoreCase(exta)) {
        setMessage(
            NLS.bind(UIMessages.ExportReportPage1WrongExtension_message, exte),
            WARNING);
        setPageComplete(true);
        return;
      }
    }
    setErrorMessage(null);
    setMessage(null);
    setPageComplete(true);
  }

  private void restoreWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    formatcombo.setSelection(new StructuredSelection(readFormat(settings)));
    WidgetHistory.restoreCombo(settings, STORE_DESTINATIONS, destinationcombo);
  }

  private ExportFormat readFormat(IDialogSettings settings) {
    final String format = settings.get(STORE_FORMAT);
    if (format != null) {
      try {
        return ExportFormat.valueOf(format);
      } catch (IllegalArgumentException e) {
        // we fall-back to default
      }
    }
    return ExportFormat.HTML;
  }

  public void saveWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    settings.put(STORE_FORMAT, getExportFormat().name());
    WidgetHistory.saveCombo(settings, STORE_DESTINATIONS, destinationcombo);
  }

  public ICoverageSession getSelectedSession() {
    IStructuredSelection sel = (IStructuredSelection) sessionstable
        .getSelection();
    return (ICoverageSession) sel.getFirstElement();
  }

  public ExportFormat getExportFormat() {
    final IStructuredSelection selection = (IStructuredSelection) formatcombo
        .getSelection();
    return (ExportFormat) selection.getFirstElement();
  }

  public String getDestination() {
    return destinationcombo.getText().trim();
  }

}
