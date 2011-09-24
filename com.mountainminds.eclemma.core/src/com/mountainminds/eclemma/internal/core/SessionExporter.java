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

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionExporter;

/**
 * Implementation of ISessionExporter.
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
    throw new UnsupportedOperationException("Not yet implemented."); //$NON-NLS-1$
  }

}
