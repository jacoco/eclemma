/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionExporter;

/**
 * Implementation of ISessionExporter.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionExporter implements ISessionExporter {

  private static final String OUTPUT_ENCODING = "UTF-8"; //$NON-NLS-1$

  private final ICoverageSession session;
  private int format;
  private String destination;
  private Properties options;

  public SessionExporter(ICoverageSession session) {
    this.session = session;
  }

  public void setFormat(int format) {
    this.format = format;
  }

  public void setDestination(String filename) {
    this.destination = filename;
  }

  public void setReportOptions(Properties options) {
    this.options = options;
  }

  public void export(IProgressMonitor monitor) throws CoreException {
    createReport(monitor);
  }

  private void createReport(IProgressMonitor monitor) throws CoreException {
    IClassFiles[] classfiles = session.getClassFiles();
    IPath[] coveragefiles = session.getCoverageDataFiles();
    monitor.beginTask(
        NLS.bind(CoreMessages.ExportingSession_task, session.getDescription()),
        classfiles.length + coveragefiles.length + 1);
    // TODO: Use JaCoCo APIs
    monitor.done();
  }

}
