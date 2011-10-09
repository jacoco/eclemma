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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * The launch delegate for coverage configurations.
 */
public interface ICoverageLauncher extends ILaunchConfigurationDelegate2 {

  /**
   * Determines all {@link IPackageFragmentRoot}s that are part of the given
   * launch configuration.
   * 
   * @param configuration
   *          launch configuration to determine overall scope
   * 
   * @return overall scope as set of {@link IPackageFragmentRoot} elements
   * 
   * @throws CoreException
   */
  public Set<IPackageFragmentRoot> getOverallScope(
      ILaunchConfiguration configuration) throws CoreException;

}
