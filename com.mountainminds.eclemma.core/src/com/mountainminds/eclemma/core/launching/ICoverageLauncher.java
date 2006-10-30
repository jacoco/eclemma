/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;

import com.mountainminds.eclemma.core.IClassFiles;

/**
 * The launch delegate for coverage configurations.
 *  
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public interface ICoverageLauncher extends ILaunchConfigurationDelegate2 {

  /**
   * Returns all class file descriptors for the given launch configuration.
   * 
   * @param configuration
   *          launch configuration to look for class files 
   * @param includebinaries 
   *          flag whether binary classpath entries should be included
   * 
   * @return descriptors for all class for instrumentation
   *   
   * @throws CoreException
   */
  public IClassFiles[] getClassFiles(ILaunchConfiguration configuration, boolean includebinaries) throws CoreException;

  
}
