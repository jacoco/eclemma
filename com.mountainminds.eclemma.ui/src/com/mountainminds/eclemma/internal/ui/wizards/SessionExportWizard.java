/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * The export wizard for coverage sessions.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class SessionExportWizard extends Wizard implements IExportWizard {
  
  private static final String SETTINGSID = "SessionExportWizard"; //$NON-NLS-1$
  
  private SessionExportPage1 page1;
  
  public SessionExportWizard() {
    IDialogSettings pluginsettings = EclEmmaUIPlugin.getInstance().getDialogSettings();
    IDialogSettings wizardsettings = pluginsettings.getSection(SETTINGSID);
    if (wizardsettings == null) {
      wizardsettings = pluginsettings.addNewSection(SETTINGSID);
    }
    setDialogSettings(wizardsettings);
    setNeedsProgressMonitor(true);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
   */
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    // TODO Auto-generated method stub
    
  }
  
  
  
  public void addPages() {
    addPage(page1 = new SessionExportPage1());
  }



  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  public boolean performFinish() {
    page1.saveWidgetValues();
    return createReport();

  }

  private boolean createReport() {
    final ICoverageSession session = page1.getSelectedSession();
    final String destination = page1.getDestination();
    final int format = page1.getReportFormat();
    IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Creating report", IProgressMonitor.UNKNOWN);
        try {
          CoverageTools.exportSession(session, destination, format);
        } catch (CoreException e) {
          throw new InvocationTargetException(e);
        } finally {   
          monitor.done();
        }
      }
    };
    try {
      getContainer().run(true, true, op);
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException e) {
      EclEmmaUIPlugin.log(e.getTargetException());
      // TODO error message
      return false;
    }
    return true;
  }

}
