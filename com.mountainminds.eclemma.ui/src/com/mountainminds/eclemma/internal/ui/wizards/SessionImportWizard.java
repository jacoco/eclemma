/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * The import wizard for coverage sessions.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionImportWizard extends Wizard implements IExportWizard {

  private static final String SETTINGSID = "SessionImportWizard"; //$NON-NLS-1$

  private SessionImportPage1 page1;

  public SessionImportWizard() {
    IDialogSettings pluginsettings = EclEmmaUIPlugin.getInstance()
        .getDialogSettings();
    IDialogSettings wizardsettings = pluginsettings.getSection(SETTINGSID);
    if (wizardsettings == null) {
      wizardsettings = pluginsettings.addNewSection(SETTINGSID);
    }
    setDialogSettings(wizardsettings);
    setWindowTitle(UIMessages.ImportSession_title);
    // TODO create proper wizban
    // setDefaultPageImageDescriptor(EclEmmaUIPlugin
    //    .getImageDescriptor(EclEmmaUIPlugin.WIZBAN_EXPORT_SESSION));
    setNeedsProgressMonitor(true);
  }

  public void init(IWorkbench workbench, IStructuredSelection selection) {
    // TODO Auto-generated method stub
  }

  public void addPages() {
    addPage(page1 = new SessionImportPage1());
    super.addPages();
  }

  public boolean performFinish() {
    // TODO Auto-generated method stub
    return true;
  }

}
