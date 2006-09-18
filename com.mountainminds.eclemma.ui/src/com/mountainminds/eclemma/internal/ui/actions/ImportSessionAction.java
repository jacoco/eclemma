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
import com.mountainminds.eclemma.internal.ui.wizards.SessionImportWizard;

/**
 * This action launches the import session wizard.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class ImportSessionAction extends Action {
  
  private final IWorkbenchWindow window;
  
  public ImportSessionAction(IWorkbenchWindow window) {
    this.window = window;
    setText(UIMessages.ImportSessionAction_label);
    setToolTipText(UIMessages.ImportSessionAction_tooltip);
    setImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.ELCL_IMPORT));
    setDisabledImageDescriptor(EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.DLCL_IMPORT));
  }
  
  public void run() {
    IWorkbenchWizard wizard = new SessionImportWizard();
    wizard.init(window.getWorkbench(), StructuredSelection.EMPTY);
    WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
    dialog.open();
  }

}
