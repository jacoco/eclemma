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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ISessionImporter;

/**
 * Implementation of ISessionImporter.
 */
public class SessionImporter implements ISessionImporter {

  private String description;
  private String coveragefile;
  private Set<IPackageFragmentRoot> scope;
  private boolean copy;

  public void setDescription(String description) {
    this.description = description;
  }

  public void setExecutionDataFile(String file) {
    this.coveragefile = file;
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
        createCopy(monitor), null);
    CoverageTools.getSessionManager().addSession(session, true, null);
    monitor.done();
  }

  private IPath createCopy(IProgressMonitor monitor) throws CoreException {
    IPath file = Path.fromOSString(coveragefile);
    if (copy) {
      file = EclEmmaCorePlugin.getInstance().getExecutionDataFiles().newFile();
      File source = new File(coveragefile);
      monitor.beginTask("", (int) source.length()); //$NON-NLS-1$
      byte[] buffer = new byte[0x1000];
      try {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(file.toFile());
        int l;
        while ((l = in.read(buffer)) != -1) {
          out.write(buffer, 0, l);
          monitor.worked(l);
        }
        in.close();
        out.close();
      } catch (IOException e) {
        throw new CoreException(EclEmmaStatus.IMPORT_ERROR.getStatus(e));
      }
    }
    monitor.done();
    return file;
  }

}
