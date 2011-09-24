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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * A {@link com.mountainminds.eclemma.core.ICoverageSession} implementation.
 */
public class CoverageSession extends PlatformObject implements ICoverageSession {

  private final String description;

  private final Collection<IPackageFragmentRoot> scope;

  private final Collection<IPath> executiondatafiles;

  private final ILaunchConfiguration launchconfiguration;

  private CoverageSession(String description,
      Collection<IPackageFragmentRoot> scope,
      Collection<IPath> executiondatafiles,
      ILaunchConfiguration launchconfiguration) {
    this.description = description;
    this.scope = scope;
    this.executiondatafiles = executiondatafiles;
    this.launchconfiguration = launchconfiguration;
  }

  public CoverageSession(String description,
      Collection<IPackageFragmentRoot> scope, IPath executiondatafile,
      ILaunchConfiguration launchconfiguration) {
    this(description, Collections
        .unmodifiableCollection(new ArrayList<IPackageFragmentRoot>(scope)),
        Collections.singleton(executiondatafile), launchconfiguration);
  }

  // ICoverageSession implementation

  public String getDescription() {
    return description;
  }

  public Collection<IPackageFragmentRoot> getScope() {
    return scope;
  }

  public Collection<IPath> getExecutionDataFiles() {
    return executiondatafiles;
  }

  public ILaunchConfiguration getLaunchConfiguration() {
    return launchconfiguration;
  }

  public ICoverageSession merge(ICoverageSession other, String description) {
    final Collection<IPackageFragmentRoot> scope = new ArrayList<IPackageFragmentRoot>(
        this.scope);
    scope.addAll(other.getScope());
    final Collection<IPath> files = new ArrayList<IPath>(
        this.executiondatafiles);
    files.addAll(other.getExecutionDataFiles());
    return new CoverageSession(description,
        Collections.unmodifiableCollection(scope),
        Collections.unmodifiableCollection(files), launchconfiguration);
  }

}
