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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.StateFiles;

/**
 * Implementation of {@link ICoverageLaunchInfo}.
 */
public class CoverageLaunchInfo implements ICoverageLaunchInfo {

  private static int idcounter = (int) System.currentTimeMillis();
  private static final Map<ILaunch, ICoverageLaunchInfo> instances = new WeakHashMap<ILaunch, ICoverageLaunchInfo>();

  private final ILaunchConfiguration configuration;
  private IPath coveragefile;
  private Collection<IPackageFragmentRoot> scope;

  public CoverageLaunchInfo(ILaunch launch) throws CoreException {
    final String id = Integer.toHexString(idcounter++);
    StateFiles statefiles = EclEmmaCorePlugin.getInstance().getStateFiles();
    IPath base = statefiles.getLaunchDataFolder().append(id);
    coveragefile = base.addFileExtension("exec"); //$NON-NLS-1$
    statefiles.registerForCleanup(coveragefile);

    configuration = launch.getLaunchConfiguration();
    scope = new ArrayList<IPackageFragmentRoot>();
    for (IClassFiles cf : CoverageTools
        .getClassFilesForInstrumentation(configuration)) {
      scope.addAll(Arrays.asList(cf.getPackageFragmentRoots()));
    }

    instances.put(launch, this);
  }

  /**
   * Returns the coverage launch info that is assoziated with the given launch.
   * If no info object is assoziated with the given launch <code>null</code> is
   * returned.
   * 
   * @param launch
   *          the launch object we need coverage data for
   * @return the info data object or <code>null</code>
   */
  public static ICoverageLaunchInfo getInfo(ILaunch launch) {
    return instances.get(launch);
  }

  // ICoverageLaunchInfo interface

  public IPath getExecutionDataFile() {
    return coveragefile;
  }

  public Collection<IPackageFragmentRoot> getScope() {
    return scope;
  }

  public void dispose() {
    // TODO check why this is still necessary, someone seems to hold a reference
    // to the launch objects.
    coveragefile = null;
  }

}
