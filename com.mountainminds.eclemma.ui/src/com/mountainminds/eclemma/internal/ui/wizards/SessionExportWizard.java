/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionExporter;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * The export wizard for coverage sessions.
 */
public class SessionExportWizard extends Wizard implements IExportWizard {

  public static final String ID = "com.mountainminds.eclemma.ui.sessionExportWizard"; //$NON-NLS-1$

  private static final String SETTINGSID = "SessionExportWizard"; //$NON-NLS-1$

  private SessionExportPage1 page1;

  public SessionExportWizard() {
    super();
    IDialogSettings pluginsettings = EclEmmaUIPlugin.getInstance()
        .getDialogSettings();
    IDialogSettings wizardsettings = pluginsettings.getSection(SETTINGSID);
    if (wizardsettings == null) {
      wizardsettings = pluginsettings.addNewSection(SETTINGSID);
    }
    setDialogSettings(wizardsettings);
    setWindowTitle(UIMessages.ExportReport_title);
    setDefaultPageImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.WIZBAN_EXPORT_SESSION));
    setNeedsProgressMonitor(true);
  }

  public void init(IWorkbench workbench, IStructuredSelection selection) {
  }

  public void addPages() {
    page1 = new SessionExportPage1();
    addPage(page1);
  }

  public boolean performFinish() {
    page1.saveWidgetValues();
    return createReport();
  }

  private boolean createReport() {
    final ICoverageSession session = page1.getSelectedSession();
    final ISessionExporter exporter = CoverageTools.getExporter(session);
    exporter.setFormat(page1.getExportFormat());
    exporter.setDestination(page1.getDestination());
    final IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException, InterruptedException {
        try {
          exporter.export(monitor);
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
      final Throwable ex = ite.getTargetException();
      EclEmmaUIPlugin.log(ex);
      final String title = UIMessages.ExportReportErrorDialog_title;
      String msg = UIMessages.ExportReportErrorDialog_message;
      msg = NLS.bind(msg, session.getDescription());
      final IStatus status;
      if (ex instanceof CoreException) {
        status = ((CoreException) ex).getStatus();
      } else {
        status = EclEmmaUIPlugin.errorStatus(String.valueOf(ex.getMessage()),
            ex);
      }
      ErrorDialog.openError(getShell(), title, msg, status);
      return false;
    }
    return true;
  }

}
