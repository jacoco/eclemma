/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jacoco.core.data.ExecutionDataWriter;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IExecutionDataSource;
import com.mountainminds.eclemma.core.URLExecutionDataSource;

/**
 * Internal utility to create and cleanup execution data files manage files in
 * the plugin's state location.
 */
public final class ExecutionDataFiles {

  private static final String FOLDER = ".execdata/"; //$NON-NLS-1$

  private final File folder;

  public ExecutionDataFiles(IPath stateLocation) {
    folder = stateLocation.append(FOLDER).toFile();
    folder.mkdirs();
  }

  /**
   * Delete any existing execution data file.
   */
  public void deleteTemporaryFiles() {
    final File[] files = folder.listFiles();
    for (final File file : files) {
      file.delete();
    }
  }

  /**
   * Creates a new execution data file containing the content of the given
   * source.
   * 
   * @param source
   *          source to dump into the file
   * @return created file
   */
  public IExecutionDataSource newFile(IExecutionDataSource source)
      throws CoreException {
    try {
      final File file = File.createTempFile("session", ".exec", folder); //$NON-NLS-1$ //$NON-NLS-2$
      final OutputStream out = new BufferedOutputStream(new FileOutputStream(
          file));
      final ExecutionDataWriter writer = new ExecutionDataWriter(out);
      source.accept(writer, writer);
      out.close();
      return new URLExecutionDataSource(file.toURL());
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.EXEC_FILE_CREATE_ERROR.getStatus(e));
    }
  }

}
