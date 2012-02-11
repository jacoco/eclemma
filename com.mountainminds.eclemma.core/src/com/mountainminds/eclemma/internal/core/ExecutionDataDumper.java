/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.SessionInfoStore;

/**
 * Utility to read execution data from an {@link ExecutionDataReader} and dump
 * the data to local files.
 */
public class ExecutionDataDumper {

  private final ExecutionDataReader reader;
  private final ExecutionDataFiles files;

  private boolean dataReceived;

  public ExecutionDataDumper(ExecutionDataReader reader,
      ExecutionDataFiles files) {
    this.reader = reader;
    this.files = files;
    this.dataReceived = false;
  }

  public IPath dump() throws IOException, CoreException {
    final SessionInfoStore sessionInfos = new SessionInfoStore();
    final ExecutionDataStore executionData = new ExecutionDataStore();
    reader.setSessionInfoVisitor(sessionInfos);
    reader.setExecutionDataVisitor(executionData);
    reader.read();
    if (sessionInfos.isEmpty()) {
      return null;
    }
    dataReceived = true;
    return createDataFile(sessionInfos, executionData);
  }

  private IPath createDataFile(SessionInfoStore sessionInfos,
      ExecutionDataStore executionData) throws IOException, CoreException {
    final IPath file = files.newFile();
    final OutputStream out = new BufferedOutputStream(new FileOutputStream(
        file.toFile()));
    final ExecutionDataWriter writer = new ExecutionDataWriter(out);
    sessionInfos.accept(writer);
    executionData.accept(writer);
    out.close();
    return file;
  }

  public boolean hasDataReceived() {
    return dataReceived;
  }

}
