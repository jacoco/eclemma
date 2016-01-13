/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Status handler that issues an error message when no coverage data has been
 * found.
 */
public class NoCoverageDataHandler implements IStatusHandler {

  public Object handleStatus(IStatus status, Object source)
      throws CoreException {

    Shell parent = EclEmmaUIPlugin.getInstance().getShell();
    String title = UIMessages.NoCoverageDataError_title;
    String message = UIMessages.NoCoverageDataError_message;

    MessageDialog.openError(parent, title, message);
    return Boolean.FALSE;
  }

}
