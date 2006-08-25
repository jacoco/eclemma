/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Constants and initializer for the preference store.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class UIPreferences extends AbstractPreferenceInitializer {

  public static final String PREF_ALLOW_INPLACE_INSTRUMENTATION = EclEmmaUIPlugin.ID
      + ".allow_inplace_instrumentation"; //$NON-NLS-1$ 

  public void initializeDefaultPreferences() {
    IPreferenceStore pref = EclEmmaUIPlugin.getInstance().getPreferenceStore();
    pref.setDefault(PREF_ALLOW_INPLACE_INSTRUMENTATION, MessageDialogWithToggle.PROMPT);
  }

}
