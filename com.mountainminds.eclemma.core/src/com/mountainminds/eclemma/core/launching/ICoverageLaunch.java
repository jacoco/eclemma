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

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * Extension of the {@link ILaunch} interface to keep specific information for
 * coverage launches.
 */
public interface ICoverageLaunch extends ILaunch {

  /**
   * Returns the location of the execution data file for this launch.
   * 
   * @return absolute path of the coverage data file
   */
  public IPath getExecutionDataFile();

  /**
   * Returns the collection of {@link IPackageFragmentRoot} considered as the
   * scope for this launch.
   * 
   * @return package fragment roots for this launch
   */
  public Set<IPackageFragmentRoot> getScope();

}
