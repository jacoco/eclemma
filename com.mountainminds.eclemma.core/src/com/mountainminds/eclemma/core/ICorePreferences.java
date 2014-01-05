/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import org.jacoco.core.runtime.AgentOptions;

/**
 * Clients may implement this interface to customize the behavior of the EclEmma
 * core plug-in and pass a instance to
 * {@link CoverageTools#setPreferences(ICorePreferences)}. This interface
 * decouples the core e.g. from the UI preferences.
 */
public interface ICorePreferences {

  /**
   * Default behavior if no customization is set.
   */
  public static final ICorePreferences DEFAULT = new ICorePreferences() {

    private AgentOptions AGENT_DEFAULTS = new AgentOptions();

    public boolean getActivateNewSessions() {
      return true;
    }

    public boolean getAutoRemoveSessions() {
      return false;
    }

    public boolean getDefaultScopeSourceFoldersOnly() {
      return true;
    }

    public boolean getDefaultScopeSameProjectOnly() {
      return false;
    }

    public String getDefaultScopeFilter() {
      return "";//$NON-NLS-1$
    }

    public String getAgentIncludes() {
      return AGENT_DEFAULTS.getIncludes();
    }

    public String getAgentExcludes() {
      return AGENT_DEFAULTS.getExcludes();
    }

    public String getAgentExclClassloader() {
      return AGENT_DEFAULTS.getExclClassloader();
    }

  };

  /**
   * Determines whether new sessions should automatically be activated.
   * 
   * @return <code>true</code>, if sessions should be activated
   */
  public boolean getActivateNewSessions();

  /**
   * Determines whether sessions should automatically be removed when their
   * respective launch is removed from the debug environment.
   * 
   * @return <code>true</code>, if sessions should be removed
   */
  public boolean getAutoRemoveSessions();

  /**
   * Specification of the default coverage scope behavior: Analyze source
   * folders only.
   * 
   * @return <code>true</code>, if source folders only should be analyzed by
   *         default
   */
  public boolean getDefaultScopeSourceFoldersOnly();

  /**
   * Specification of the default coverage scope behavior: Analyze code in the
   * same project only. This filter works only for launch configuration types
   * that have a reference to a project.
   * 
   * @return <code>true</code>, if code in the same project should be analyzed
   *         only
   */
  public boolean getDefaultScopeSameProjectOnly();

  /**
   * Returns a comma separated list of match strings that specifies patterns for
   * class path entries to be in coverage scope by default.
   * 
   * @return List of match strings
   */
  public String getDefaultScopeFilter();

  /**
   * Returns the wildcard expression for classes to include.
   * 
   * @return wildcard expression for classes to include
   * @see org.jacoco.core.runtime.WildcardMatcher
   */
  public String getAgentIncludes();

  /**
   * Returns the wildcard expression for classes to exclude.
   * 
   * @return wildcard expression for classes to exclude
   * @see org.jacoco.core.runtime.WildcardMatcher
   */
  public String getAgentExcludes();

  /**
   * Returns the wildcard expression for excluded class loaders.
   * 
   * @return expression for excluded class loaders
   * @see org.jacoco.core.runtime.WildcardMatcher
   */
  public String getAgentExclClassloader();

}
