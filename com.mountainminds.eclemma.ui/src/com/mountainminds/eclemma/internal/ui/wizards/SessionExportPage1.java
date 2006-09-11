/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
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
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This wizard page allows selecting a coverage session.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionExportPage1 extends WizardPage {
  
  private static final String ID = "SessionExportPage1"; //$NON-NLS-1$
  
  private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
  
  private static final String STORE_FORMAT = STORE_PREFIX + "format"; //$NON-NLS-1$
  private static final String STORE_DESTINATIONS = STORE_PREFIX + "destinations"; //$NON-NLS-1$
  
  private static final int HISTORY_LIMIT = 10;

  private TableViewer viewer;
  private Combo formatcombo;
  private Combo destinationcombo;
  
  public SessionExportPage1() {
    super(ID);
    setTitle(UIMessages.ExportReport_title);
    setDescription(UIMessages.ExportReportPage1_description);
  }

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    parent = new Composite(parent, SWT.NONE);
    parent.setLayout(new GridLayout());
    new Label(parent, SWT.NONE).setText(UIMessages.ExportReportPage1Sessions_label);
    viewer = new TableViewer(parent, SWT.BORDER);
    viewer.setLabelProvider(new WorkbenchLabelProvider());
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setInput(CoverageTools.getSessionManager().getSessions());
    viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
    Group group = new Group(parent, SWT.NONE);
    group.setText(UIMessages.ExportReportPage1DestinationGroup_label);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    createExportOptionsGroup(group);
    setControl(parent);
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
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    formatcombo.setLayoutData(gd);
    new Label(parent, SWT.NONE).setText(UIMessages.ExportReportPage1Destination_label);
    destinationcombo = new Combo(parent, SWT.BORDER);
    destinationcombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    Button browsebutton = new Button(parent, SWT.NONE);
    browsebutton.setText(UIMessages.Browse_action);
    setButtonLayoutData(browsebutton);
    browsebutton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        openBrowseDialog();
      }
    });
  }
  
  private void openBrowseDialog() {
    FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
    fd.setText(UIMessages.ExportReportPage1BrowseDialog_title);
    fd.setFileName(destinationcombo.getText());
    String file = fd.open();
    if (file != null) {
      destinationcombo.setText(file);
    }
  }
  
  protected void restoreWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    try {
      formatcombo.select(settings.getInt(STORE_FORMAT));
    } catch (NumberFormatException nfe) {
      formatcombo.select(0);
    }
    String[] destinations = settings.getArray(STORE_DESTINATIONS);
    if (destinations != null) {
      destinationcombo.setItems(destinations);
      if (destinations.length > 0) {
        destinationcombo.setText(destinations[0]);
      }
    }
  }
  
  public void saveWidgetValues() {
    IDialogSettings settings = getDialogSettings();
    settings.put(STORE_FORMAT, formatcombo.getSelectionIndex());
    List history = new ArrayList(Arrays.asList(destinationcombo.getItems()));
    history.remove(destinationcombo.getText());
    history.add(0, destinationcombo.getText());
    if (history.size() > HISTORY_LIMIT) {
      history = history.subList(0, HISTORY_LIMIT);
    }
    settings.put(STORE_DESTINATIONS, (String[]) history.toArray(new String[0]));
  }
  
  

}
