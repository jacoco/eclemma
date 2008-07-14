/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ISessionImporter;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * The import wizard for coverage sessions.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionImportWizard extends Wizard implements IImportWizard {

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
    setDefaultPageImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.WIZBAN_IMPORT_SESSION));
    setNeedsProgressMonitor(true);
  }

  public void init(IWorkbench workbench, IStructuredSelection selection) {
    // nothing to initialize
  }

  public void addPages() {
    addPage(page1 = new SessionImportPage1());
    super.addPages();
  }

  public boolean performFinish() {
    page1.saveWidgetValues();
    return importSession();
  }
  
  private boolean importSession() {
    final ISessionImporter importer = CoverageTools.getImporter();
    importer.setDescription(page1.getSessionDescription());
    importer.setCoverageFile(page1.getCoverageFile());
    importer.setClassFiles(page1.getClassFiles());
    importer.setCopy(page1.getCreateCopy());
    importer.setUseImportedMetaData(page1.getUseImportedMetaData());
    IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException, InterruptedException {
        try {
          importer.importSession(monitor);
        } catch (Exception e) {
          throw new InvocationTargetException(e);
        }
      }
    };
    try {
      getContainer().run(true, true, op);
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException ite) {
      Throwable ex = ite.getTargetException();
      EclEmmaUIPlugin.log(ex);
      String title = UIMessages.ImportReportErrorDialog_title;
      String msg = UIMessages.ImportReportErrorDialog_message;
      IStatus status;
      if (ex instanceof CoreException) {
        status = ((CoreException) ex).getStatus();
      } else {
        status = EclEmmaUIPlugin.errorStatus(String.valueOf(ex.getMessage()), ex);
      }
      ErrorDialog.openError(getShell(), title, msg, status);
      return false;
    }
    return true;
  }  

}
