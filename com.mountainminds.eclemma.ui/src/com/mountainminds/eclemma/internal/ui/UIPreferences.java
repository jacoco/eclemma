/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;

import com.mountainminds.eclemma.core.ICorePreferences;

/**
 * Constants and initializer for the preference store.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class UIPreferences extends AbstractPreferenceInitializer {

  public static final String PREF_SHOW_COVERAGE_VIEW = EclEmmaUIPlugin.ID
     + ".show_coverage_view"; //$NON-NLS-1$
  
  public static final String PREF_ACTICATE_NEW_SESSIONS = EclEmmaUIPlugin.ID
    + ".activate_new_sessions"; //$NON-NLS-1$ 

  public static final String PREF_AUTO_REMOVE_SESSIONS = EclEmmaUIPlugin.ID
    + ".auto_remove_sessions"; //$NON-NLS-1$ 
  
  public static final String PREF_ALLOW_INPLACE_INSTRUMENTATION = EclEmmaUIPlugin.ID
    + ".allow_inplace_instrumentation"; //$NON-NLS-1$ 
  
  public static final ICorePreferences CORE_PREFERENCES = new ICorePreferences() {
    public boolean getActivateNewSessions() {
      return getPreferenceStore().getBoolean(PREF_ACTICATE_NEW_SESSIONS);
    }
    public boolean getAutoRemoveSessions() {
      return getPreferenceStore().getBoolean(PREF_AUTO_REMOVE_SESSIONS);
    }
  };

  public void initializeDefaultPreferences() {
    IPreferenceStore pref = getPreferenceStore();
    pref.setDefault(PREF_SHOW_COVERAGE_VIEW, true);
    pref.setDefault(PREF_ACTICATE_NEW_SESSIONS, ICorePreferences.DEFAULT.getActivateNewSessions());
    pref.setDefault(PREF_AUTO_REMOVE_SESSIONS, ICorePreferences.DEFAULT.getAutoRemoveSessions());
    pref.setDefault(PREF_ALLOW_INPLACE_INSTRUMENTATION, MessageDialogWithToggle.PROMPT);
  }

  private static IPreferenceStore getPreferenceStore() {
    return EclEmmaUIPlugin.getInstance().getPreferenceStore();
  }
  
}
