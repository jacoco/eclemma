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
package com.mountainminds.eclemma.internal.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * A {@link com.mountainminds.eclemma.core.ICoverageSession} implementation.
 */
public class CoverageSession extends PlatformObject implements ICoverageSession {

  private final String description;

  private final IClassFiles[] classfiles;

  private final IPath[] coveragedatafiles;

  private final ILaunchConfiguration launchconfiguration;

  public CoverageSession(String description, IClassFiles[] classfiles,
      IPath[] coveragedatafiles, ILaunchConfiguration launchconfiguration) {
    this.description = description;
    this.classfiles = classfiles;
    this.coveragedatafiles = coveragedatafiles;
    this.launchconfiguration = launchconfiguration;
  }

  // ICoverageSession implementation

  public String getDescription() {
    return description;
  }

  public IClassFiles[] getClassFiles() {
    return classfiles;
  }

  public IPath[] getCoverageDataFiles() {
    return this.coveragedatafiles;
  }

  public ILaunchConfiguration getLaunchConfiguration() {
    return launchconfiguration;
  }

  public ICoverageSession merge(ICoverageSession other, String description) {
    Set<IClassFiles> i = merge(classfiles, other.getClassFiles());
    Set<IPath> c = merge(coveragedatafiles, other.getCoverageDataFiles());
    return new CoverageSession(description,
        (IClassFiles[]) i.toArray(new IClassFiles[i.size()]),
        (IPath[]) c.toArray(new IPath[c.size()]), launchconfiguration);
  }

  private <T> Set<T> merge(T[] arr1, T[] arr2) {
    final Set<T> set = new HashSet<T>();
    set.addAll(Arrays.asList(arr1));
    set.addAll(Arrays.asList(arr2));
    return set;
  }

}
