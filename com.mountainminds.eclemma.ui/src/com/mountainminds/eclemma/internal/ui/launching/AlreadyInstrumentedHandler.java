/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Status handler that issues an error message when trying to instrument classes
 * that are already instrumented.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class AlreadyInstrumentedHandler implements IStatusHandler {

  public Object handleStatus(IStatus status, Object source)
      throws CoreException {

    Shell parent = EclEmmaUIPlugin.getInstance().getShell();
    String title = UIMessages.AlreadyInstrumentedError_title;
    String message = UIMessages.AlreadyInstrumentedError_message;

    MessageDialog.openError(parent, title, message);
    return Boolean.FALSE;
  }

}
