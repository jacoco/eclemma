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
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.UIPreferences;

/**
 * Status handler that issues a warning for in place instrumentation.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class InplaceInstrumentationHandler implements IStatusHandler {

  public Object handleStatus(IStatus status, Object source)
      throws CoreException {

    ILaunchConfiguration config = (ILaunchConfiguration) source;
    Shell parent = EclEmmaUIPlugin.getInstance().getShell();
    String title = UIMessages.InstrumentationWarning_title;
    String message = NLS.bind(UIMessages.InstrumentationWarning_message, config
        .getName());

    IPreferenceStore store = EclEmmaUIPlugin.getInstance().getPreferenceStore();

    if (MessageDialogWithToggle.ALWAYS.equals(store
        .getString(UIPreferences.PREF_ALLOW_INPLACE_INSTRUMENTATION))) {
      return Boolean.TRUE;
    }

    MessageDialogWithToggle dialog = new MessageDialogWithToggle(parent, title,
        null, message, MessageDialog.WARNING, new String[] {
            IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0,
        null, false);
    dialog.setPrefKey(UIPreferences.PREF_ALLOW_INPLACE_INSTRUMENTATION);
    dialog.setPrefStore(store);
    dialog.open();

    return Boolean.valueOf(dialog.getReturnCode() == IDialogConstants.OK_ID);
  }

}
