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
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Status handler that issues an error message when to launch a configuration
 * that does not specify any classes for instrumentation. The used may decide to
 * open the launch dialog directly.
 */
public class NoInstrumentedClassesHandler implements IStatusHandler {

  public Object handleStatus(IStatus status, final Object source)
      throws CoreException {

    final Shell parent = EclEmmaUIPlugin.getInstance().getShell();
    String title = UIMessages.NoInstrumentedClassesError_title;
    String message = UIMessages.NoInstrumentedClassesError_message;

    MessageDialog d = new MessageDialog(parent, title, null, message,
        MessageDialog.ERROR, new String[] { IDialogConstants.YES_LABEL,
            IDialogConstants.NO_LABEL }, 0);
    if (d.open() == 0) {
      parent.getDisplay().asyncExec(new Runnable() {
        public void run() {
          DebugUITools.openLaunchConfigurationDialogOnGroup(parent,
              new StructuredSelection(source),
              EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP);
        }
      });
    }
    return Boolean.FALSE;
  }

}
