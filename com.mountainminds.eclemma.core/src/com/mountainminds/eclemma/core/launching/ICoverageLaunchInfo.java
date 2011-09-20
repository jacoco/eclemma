/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import org.eclipse.core.runtime.IPath;

import com.mountainminds.eclemma.core.IClassFiles;

/**
 * Descriptor how a particular launch was instrumented. To every launch in
 * "Coverage" mode an instance is attached.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public interface ICoverageLaunchInfo {

  /**
   * Returns the location of the execution data file for this launch.
   * 
   * @return absolute path of the coverage data file
   */
  public IPath getExecutionDataFile();

  /**
   * Returns the list of {@link IClassFiles} considered for this launch.
   * 
   * @return class files for this launch
   */
  public IClassFiles[] getClassFiles();

  /**
   * Allow the implementation to perform internal cleanup when this info object
   * is no longer required.
   */
  public void dispose();

}
