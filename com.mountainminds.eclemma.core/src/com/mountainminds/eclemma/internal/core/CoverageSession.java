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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * A {@link com.mountainminds.eclemma.core.ICoverageSession} implementation.
 */
public class CoverageSession extends PlatformObject implements ICoverageSession {

  private final String description;
  private final Collection<IPackageFragmentRoot> scope;
  private final IPath executiondatafile;
  private final ILaunchConfiguration launchconfiguration;

  public CoverageSession(String description,
      Collection<IPackageFragmentRoot> scope, IPath executiondatafile,
      ILaunchConfiguration launchconfiguration) {
    this.description = description;
    this.scope = Collections
        .unmodifiableCollection(new ArrayList<IPackageFragmentRoot>(scope));
    this.executiondatafile = executiondatafile;
    this.launchconfiguration = launchconfiguration;
  }

  // ICoverageSession implementation

  public String getDescription() {
    return description;
  }

  public Collection<IPackageFragmentRoot> getScope() {
    return scope;
  }

  public ILaunchConfiguration getLaunchConfiguration() {
    return launchconfiguration;
  }

  public void readExecutionData(IExecutionDataVisitor executionDataVisitor,
      ISessionInfoVisitor sessionInfoVisitor, IProgressMonitor monitor)
      throws CoreException {
    monitor.beginTask(CoreMessages.ReadingExecutionDataFile_task,
        IProgressMonitor.UNKNOWN);
    try {
      final File f = executiondatafile.toFile();
      final InputStream in = new BufferedInputStream(new FileInputStream(f));
      final ExecutionDataReader reader = new ExecutionDataReader(in);
      reader.setExecutionDataVisitor(executionDataVisitor);
      reader.setSessionInfoVisitor(sessionInfoVisitor);
      while (!monitor.isCanceled() && reader.read()) {
        // nothing here
      }
      in.close();
    } catch (IOException e) {
      throw new CoreException(
          EclEmmaStatus.EXEC_FILE_READ_ERROR.getStatus(
              executiondatafile, e));
    }
    monitor.done();
  }

}
