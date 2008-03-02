/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionExporter;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This wizard page allows selecting a coverage session, the output format and
 * destination.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionExportPage1 extends WizardPage {
  
  private static final String ID = "SessionExportPage1"; //$NON-NLS-1$
  
  private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
  private static final String STORE_FORMAT = STORE_PREFIX + "format"; //$NON-NLS-1$
  private static final String STORE_DESTINATIONS = STORE_PREFIX + "destinations"; //$NON-NLS-1$
  private static final String STORE_OPENREPORT = STORE_PREFIX + "openreport"; //$NON-NLS-1$

  private TableViewer sessionstable;
  private Combo formatcombo;
  private Combo destinationcombo;
  private Button opencheckbox;
  
  public SessionExportPage1() {
    super(ID);
    setTitle(UIMessages.ExportReportPage1_title);
    setDescription(UIMessages.ExportReportPage1_description);
  }
  
  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    parent = new Composite(parent, SWT.NONE);
    parent.setLayout(new GridLayout());
    new Label(parent, SWT.NONE).setText(UIMessages.ExportReportPage1Sessions_label);
    sessionstable = new TableViewer(parent, SWT.BORDER);
    sessionstable.setLabelProvider(new WorkbenchLabelProvider());
    sessionstable.setContentProvider(new ArrayContentProvider());
    sessionstable.setInput(CoverageTools.getSessionManager().getSessions());
    ICoverageSession active = CoverageTools.getSessionManager().getActiveSession();
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
    opencheckbox = new Button(parent, SWT.CHECK);
    opencheckbox.setText(UIMessages.ExportReportOpenReport_label);
    setControl(parent);
    ContextHelp.setHelp(parent, ContextHelp.SESSION_EXPORT);
    restoreWidgetValues();
  }
  
  private void createExportOptionsGroup(Composite parent) {
    parent.setLayout(new GridLayout(3, false));
    new Label(parent, SWT.NONE).setText(UIMessages.ExportReportPage1Format_label);
    formatcombo = new Combo(parent, SWT.READ_ONLY);
    formatcombo.add(UIMessages.ExportReportPage1HTMLFormat_value);
    formatcombo.add(UIMessages.ExportReportPage1XMLFormat_value);
    formatcombo.add(UIMessages.ExportReportPage1TextFormat_value);
    formatcombo.add(UIMessages.ExportReportPage1EMMAFormat_value);
    formatcombo.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        // Adjust the extension to the new format
        IPath path = Path.fromOSString(destinationcombo.getText());
        path = path.removeFileExtension();
        String ext = ISessionExporter.DEFAULT_EXTENSIONS[formatcombo.getSelectionIndex()];
        path = path.addFileExtension(ext);
        destinationcombo.setText(path.toOSString());
      }
    });
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    formatcombo.setLayoutData(gd);
    new Label(parent, SWT.NONE).setText(UIMessages.ExportReportPage1Destination_label);
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
        openBrowseDialog();
      }
    });
    update();
  }
  
  private void openBrowseDialog() {
    FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
    fd.setText(UIMessages.ExportReportPage1BrowseDialog_title);
    fd.setFileName(destinationcombo.getText());
    String ext = ISessionExporter.DEFAULT_EXTENSIONS[formatcombo.getSelectionIndex()];
    fd.setFilterExtensions(new String[] { "*." + ext, "*.*"} ); //$NON-NLS-1$ //$NON-NLS-2$
    String file = fd.open();
    if (file != null) {
      destinationcombo.setText(file);
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
    // the destination must be a file and must be in a existing directory
    File f = new File(getDestination());
    File p = f.getParentFile();
    if (f.isDirectory() || (p != null && !p.isDirectory())) {
      setErrorMessage(UIMessages.ExportReportPage1InvalidDestination_message);
      setPageComplete(false);
      return;
    }
    // the extension should correspond to the report type
    String exta = Path.fromOSString(getDestination()).getFileExtension();
    String exte = ISessionExporter.DEFAULT_EXTENSIONS[getReportFormat()];
    if (!exte.equalsIgnoreCase(exta)) {
      setMessage(NLS.bind(UIMessages.ExportReportPage1WrongExtension_message, exte), WARNING);
      setPageComplete(true);
      return;
    }
    setErrorMessage(null);
    setMessage(null);
    setPageComplete(true);
  }
  
  protected void restoreWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    try {
      formatcombo.select(settings.getInt(STORE_FORMAT));
    } catch (NumberFormatException nfe) {
      formatcombo.select(0);
    }
    ComboHistory.restore(settings, STORE_DESTINATIONS, destinationcombo);
    opencheckbox.setSelection(settings.getBoolean(STORE_OPENREPORT));
  }
  
  public void saveWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    settings.put(STORE_FORMAT, formatcombo.getSelectionIndex());
    ComboHistory.save(settings, STORE_DESTINATIONS, destinationcombo);
    settings.put(STORE_OPENREPORT, opencheckbox.getSelection());
  }
  
  public ICoverageSession getSelectedSession() {
    IStructuredSelection sel = (IStructuredSelection) sessionstable.getSelection();
    return (ICoverageSession) sel.getFirstElement();
  }
  
  public int getReportFormat() {
    return formatcombo.getSelectionIndex();
  }
  
  public String getDestination() {
    return destinationcombo.getText().trim();
  }
  
  public boolean getOpenReport() {
    return opencheckbox.getSelection();
  }

}
