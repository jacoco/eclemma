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

/**
 * Constants and initializer for the preference store.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class UIPreferences extends AbstractPreferenceInitializer {

  public static final String PREF_SHOW_COVERAGE_VIEW = EclEmmaUIPlugin.ID
     + ".show_coverage_view"; //$NON-NLS-1$ 

  public static final String PREF_ALLOW_INPLACE_INSTRUMENTATION = EclEmmaUIPlugin.ID
      + ".allow_inplace_instrumentation"; //$NON-NLS-1$ 

  public void initializeDefaultPreferences() {
    IPreferenceStore pref = EclEmmaUIPlugin.getInstance().getPreferenceStore();
    pref.setDefault(PREF_SHOW_COVERAGE_VIEW, true);
    pref.setDefault(PREF_ALLOW_INPLACE_INSTRUMENTATION, MessageDialogWithToggle.PROMPT);
  }

}
