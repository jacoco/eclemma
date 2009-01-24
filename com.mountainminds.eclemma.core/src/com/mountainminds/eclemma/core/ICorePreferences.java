/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.core;

/**
 * Clients may implement this interface to customize the behavior of the EclEmma
 * core plug-in and pass a instance to
 * {@link CoverageTools#setPreferences(ICorePreferences)}. This interface
 * decouples the core e.g. from the UI preferences.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public interface ICorePreferences {

  /**
   * Default behavior if no customization is set.
   */
  public static final ICorePreferences DEFAULT = new ICorePreferences() {
    public boolean getActivateNewSessions() {
      return true;
    }

    public boolean getAutoRemoveSessions() {
      return false;
    }

    public boolean getDefaultInstrumentationSourceFoldersOnly() {
      return true;
    }

    public boolean getDefaultInstrumentationSameProjectOnly() {
      return false;
    }

    public String getDefaultInstrumentationFilter() {
      return "";//$NON-NLS-1$
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
   * Specification of the default instrumentation behavior: Instrument source
   * folders only.
   * 
   * @return <code>true</code>, if source folders only should be instrumented by
   *         default
   */
  public boolean getDefaultInstrumentationSourceFoldersOnly();

  /**
   * Specification of the default instrumentation behavior: Instrument code in
   * the same project only. This filter works only for launch configuration
   * types that have a reference to a project.
   * 
   * @return <code>true</code>, if code in the same project should be
   *         instrumented only
   */
  public boolean getDefaultInstrumentationSameProjectOnly();

  /**
   * Returns a comma separated list of match strings that specifies patterns for
   * class path entries to be instrumented by default.
   * 
   * @return List of match strings
   */
  public String getDefaultInstrumentationFilter();

}
