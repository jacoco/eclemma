/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IExecutionDataSource;
import com.mountainminds.eclemma.core.ISessionImporter;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * Implementation of ISessionImporter.
 */
public class SessionImporter implements ISessionImporter {

  private final ISessionManager sessionManager;
  private final ExecutionDataFiles executionDataFiles;

  private String description;
  private Callable<IExecutionDataSource> dataSourceFactory;
  private Set<IPackageFragmentRoot> scope;
  private boolean copy;

  public SessionImporter(ISessionManager sessionManager,
      ExecutionDataFiles executionDataFiles) {
    this.sessionManager = sessionManager;
    this.executionDataFiles = executionDataFiles;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setExecutionDataFile(final String file) {
    this.dataSourceFactory = new Callable<IExecutionDataSource>() {
      public IExecutionDataSource call() throws Exception {
        return new URLExecutionDataSource(new File(file).toURL());
      }
    };
  }

  public void setScope(Set<IPackageFragmentRoot> scope) {
    this.scope = scope;
  }

  public void setCopy(boolean copy) {
    this.copy = copy;
  }

  public void importSession(IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(CoreMessages.ImportingSession_task, 1);
    final CoverageSession session = new CoverageSession(description, scope,
        getSessionData(), null);
    sessionManager.addSession(session, true, null);
    monitor.done();
  }

  private IExecutionDataSource getSessionData() throws CoreException {
    try {
      final IExecutionDataSource source = dataSourceFactory.call();
      if (this.copy) {
        return this.executionDataFiles.newFile(source);
      } else {
        return source;
      }
    } catch (CoreException e) {
      throw e;
    } catch (Exception e) {
      throw new CoreException(EclEmmaStatus.IMPORT_ERROR.getStatus(e));
    }
  }

}
