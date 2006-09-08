/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * This wizard page allows selecting a coverage session.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class SessionSelectionPage extends WizardPage {

  public static final String ID = "SessionSelectionPage";

  private TableViewer viewer; 
  
  public SessionSelectionPage() {
    super(ID);
    setTitle(UIMessages.ExportReport_title);
    setDescription(UIMessages.ExportReportSessionSelection_description);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent) {
    parent = new Composite(parent, SWT.NONE);
    parent.setLayout(new GridLayout());
    new Label(parent, SWT.NONE).setText("Available sessions:");
    viewer = new TableViewer(parent, SWT.BORDER);
    viewer.setLabelProvider(new WorkbenchLabelProvider());
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setInput(CoverageTools.getSessionManager().getSessions());
    viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
    Group group = new Group(parent, SWT.NONE);
    group.setText("Export Format and Destination");
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    createExportOptionsGroup(group);
    setControl(parent);
  }
  
  private void createExportOptionsGroup(Composite parent) {
    parent.setLayout(new GridLayout(4, false));
    new Button(parent, SWT.RADIO).setText("HTML files");
    new Button(parent, SWT.RADIO).setText("XML file");
    new Button(parent, SWT.RADIO).setText("Text files");
    new Button(parent, SWT.RADIO).setText("EMMA session file");
  }

}
