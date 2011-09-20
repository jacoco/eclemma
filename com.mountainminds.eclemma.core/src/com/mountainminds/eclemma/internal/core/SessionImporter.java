/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionImporter;

/**
 * Implementation of ISessionImporter.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionImporter implements ISessionImporter {

  private String description;
  private String coveragefile;
  private IClassFiles[] classfiles;
  private boolean copy;

  public void setDescription(String description) {
    this.description = description;
  }

  public void setCoverageFile(String file) {
    this.coveragefile = file;
  }

  public void setClassFiles(IClassFiles[] classfiles) {
    this.classfiles = classfiles;
  }

  public void setCopy(boolean copy) {
    this.copy = copy;
  }

  public void importSession(IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(CoreMessages.ImportingSession_task, 1);
    IPath[] cfiles = new IPath[1];
    cfiles[0] = createCopy(new SubProgressMonitor(monitor, 1));
    ICoverageSession s = CoverageTools.createCoverageSession(description,
        classfiles, cfiles, null);
    CoverageTools.getSessionManager().addSession(s, true, null);
    monitor.done();
  }

  private IPath createCopy(IProgressMonitor monitor) throws CoreException {
    IPath file = Path.fromOSString(coveragefile);
    if (copy) {
      file = EclEmmaCorePlugin.getInstance().getStateFiles()
          .getImportSessionFile(file);
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
