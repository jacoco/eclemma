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
package com.mountainminds.eclemma.internal.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.mountainminds.eclemma.core.ICorePreferences;

/**
 * Constants and initializer for the preference store.
 */
public class UIPreferences extends AbstractPreferenceInitializer {

  public static final String PREF_SHOW_COVERAGE_VIEW = EclEmmaUIPlugin.ID
      + ".show_coverage_view"; //$NON-NLS-1$

  public static final String PREF_RESET_ON_DUMP = EclEmmaUIPlugin.ID
      + ".reset_on_dump"; //$NON-NLS-1$

  public static final String PREF_ACTICATE_NEW_SESSIONS = EclEmmaUIPlugin.ID
      + ".activate_new_sessions"; //$NON-NLS-1$ 

  public static final String PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY = EclEmmaUIPlugin.ID
      + ".default_scope_source_folders_only"; //$NON-NLS-1$ 

  public static final String PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY = EclEmmaUIPlugin.ID
      + ".default_scope_same_project_only"; //$NON-NLS-1$ 

  public static final String PREF_DEFAULT_SCOPE_FILTER = EclEmmaUIPlugin.ID
      + ".default_scope_filter"; //$NON-NLS-1$ 

  public static final String PREF_AUTO_REMOVE_SESSIONS = EclEmmaUIPlugin.ID
      + ".auto_remove_sessions"; //$NON-NLS-1$ 

  public static final String PREF_AGENT_INCLUDES = EclEmmaUIPlugin.ID
      + ".agent_includes"; //$NON-NLS-1$ 

  public static final String PREF_AGENT_EXCLUDES = EclEmmaUIPlugin.ID
      + ".agent_excludes"; //$NON-NLS-1$ 

  public static final String PREF_AGENT_EXCLCLASSLOADER = EclEmmaUIPlugin.ID
      + ".agent_exclclassloader"; //$NON-NLS-1$ 

  public static final ICorePreferences CORE_PREFERENCES = new ICorePreferences() {
    public boolean getActivateNewSessions() {
      return getPreferenceStore().getBoolean(PREF_ACTICATE_NEW_SESSIONS);
    }

    public boolean getAutoRemoveSessions() {
      return getPreferenceStore().getBoolean(PREF_AUTO_REMOVE_SESSIONS);
    }

    public boolean getDefaultScopeSourceFoldersOnly() {
      return getPreferenceStore().getBoolean(
          PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY);
    }

    public boolean getDefaultScopeSameProjectOnly() {
      return getPreferenceStore().getBoolean(
          PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY);
    }

    public String getDefaultScopeFilter() {
      return getPreferenceStore().getString(PREF_DEFAULT_SCOPE_FILTER);
    }

    public String getAgentIncludes() {
      return getPreferenceStore().getString(PREF_AGENT_INCLUDES);
    }

    public String getAgentExcludes() {
      return getPreferenceStore().getString(PREF_AGENT_EXCLUDES);
    }

    public String getAgentExclClassloader() {
      return getPreferenceStore().getString(PREF_AGENT_EXCLCLASSLOADER);
    }
  };

  public void initializeDefaultPreferences() {
    IPreferenceStore pref = getPreferenceStore();
    pref.setDefault(PREF_SHOW_COVERAGE_VIEW, true);
    pref.setDefault(PREF_RESET_ON_DUMP, false);
    pref.setDefault(PREF_ACTICATE_NEW_SESSIONS,
        ICorePreferences.DEFAULT.getActivateNewSessions());
    pref.setDefault(PREF_AUTO_REMOVE_SESSIONS,
        ICorePreferences.DEFAULT.getAutoRemoveSessions());
    pref.setDefault(PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY,
        ICorePreferences.DEFAULT.getDefaultScopeSourceFoldersOnly());
    pref.setDefault(PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY,
        ICorePreferences.DEFAULT.getDefaultScopeSameProjectOnly());
    pref.setDefault(PREF_DEFAULT_SCOPE_FILTER,
        ICorePreferences.DEFAULT.getDefaultScopeFilter());
    pref.setDefault(PREF_AGENT_INCLUDES,
        ICorePreferences.DEFAULT.getAgentIncludes());
    pref.setDefault(PREF_AGENT_EXCLUDES,
        ICorePreferences.DEFAULT.getAgentExcludes());
    pref.setDefault(PREF_AGENT_EXCLCLASSLOADER,
        ICorePreferences.DEFAULT.getAgentExclClassloader());
  }

  private static IPreferenceStore getPreferenceStore() {
    return EclEmmaUIPlugin.getInstance().getPreferenceStore();
  }

}
