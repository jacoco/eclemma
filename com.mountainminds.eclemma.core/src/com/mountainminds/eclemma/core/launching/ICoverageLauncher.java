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
package com.mountainminds.eclemma.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;

import com.mountainminds.eclemma.core.IClassFiles;

/**
 * The launch delegate for coverage configurations.
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
  public IClassFiles[] getClassFiles(ILaunchConfiguration configuration,
      boolean includebinaries) throws CoreException;

}
