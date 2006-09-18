/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.wizards.SessionExportWizard;

/**
 * This action launches the export session wizard.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class ExportSessionAction extends Action {
  
  private final IWorkbenchWindow window;
  
  public ExportSessionAction(IWorkbenchWindow window) {
    this.window = window;
    setText(UIMessages.ExportSessionAction_label);
    setToolTipText(UIMessages.ExportSessionAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_EXPORT));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_EXPORT));
  }
  
  public void run() {
    IWorkbenchWizard wizard = new SessionExportWizard();
    wizard.init(window.getWorkbench(), StructuredSelection.EMPTY);
    WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
    dialog.open();
  }

}
