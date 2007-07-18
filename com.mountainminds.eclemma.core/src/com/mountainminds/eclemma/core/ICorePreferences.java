/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.core;

/**
 * Clients may implement this interface to customize the behavior of the EclEmma
 * core plug-in and pass a instance to 
 * {@link com.mountainminds.eclemma.core.CoverageTools#setPreferences(ICorePreferences)}.
 * This interface decouples the core e.g. from the UI preferences.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public interface ICorePreferences {

  /**
   * Default behavior if no customization is set.
   */
  public static final ICorePreferences DEFAULT = new ICorePreferences() {
    public boolean getActivateNewSessions() { return true; }
    public boolean getAutoRemoveSessions() { return false; }
  };
  
  /**
   * Determines whether new sessions should automatically be activated.
   * 
   * @return true, if sessions should be activated
   */
  public boolean getActivateNewSessions();
  
  /**
   * Determines whether sessions should automatically be removed when their
   * respective lauch is removed from the debug environment
   *   
   * @return true, if sessions should be removed
   */
  public boolean getAutoRemoveSessions();
  
}
