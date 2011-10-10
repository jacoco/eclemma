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
package com.mountainminds.eclemma.internal.core.launching;

import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.launching.ICoverageLaunch;

/**
 * Implementation of {@link ICoverageLaunch}.
 */
public class CoverageLaunch extends Launch implements ICoverageLaunch {

  private final IPath executiondatafile;
  private final Set<IPackageFragmentRoot> scope;

  public CoverageLaunch(ILaunchConfiguration launchConfiguration,
      IPath executiondatafile, Set<IPackageFragmentRoot> scope) {
    super(launchConfiguration, CoverageTools.LAUNCH_MODE, null);
    this.executiondatafile = executiondatafile;
    this.scope = scope;
  }

  public IPath getExecutionDataFile() {
    return executiondatafile;
  }

  public Set<IPackageFragmentRoot> getScope() {
    return scope;
  }

}
