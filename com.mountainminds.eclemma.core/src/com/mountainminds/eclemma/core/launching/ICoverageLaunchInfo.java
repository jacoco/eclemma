/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.mountainminds.eclemma.core.IInstrumentation;

/**
 * Descriptor how a particular launch was instrumented. To every launch in
 * "Coverage" mode an instance is attached.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ICoverageLaunchInfo {
  
  /**
   * Returns the location of the coverage data file (<code>*.ec</code>) for this
   * launch.
   * 
   * @return  absolute path of the coverage data file
   */
  public IPath getCoverageFile();

  /**
   * Instruments the selected class path entries.
   * 
   * @param monitor  monitor for progress feedback and cancelation
   * @param inplace  if <code>true</code>, class files will be directly modified
   * @throws CoreException  in case of internal inconsistencies
   */
  public void instrument(IProgressMonitor monitor, boolean inplace) throws CoreException;

  /**
   * Returns the list of locations with instrumented class path entries along
   * with instumentation meta data.
   *  
   * @return intrumentaion data for class path entries
   */
  public IInstrumentation[] getInstrumentations();
  
  /**
   * Returns the instrumentation information for the class path with the given
   * original path. If this class path entry has not been instrumented 
   * <code>null</code> is returned.
   * 
   * @param originalpath  original class path location
   * @return  instrumentation information or <code>null</code>
   */
  public IInstrumentation getInstrumentation(String originalpath);
  
  /**
   * Return the absolute location of the JAR file that will be created to
   * inject EMMA properties into the target application.
   * 
   * @return absolute path of the EMMA properties JAR file
   */
  public IPath getPropertiesJARFile();
  
  /**
   * Allow the implementation to perform internal cleanup when this info object
   * is no longer required.
   */
  public void dispose();

}
