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
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.mountainminds.eclemma.core.EclEmmaStatus;

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

  public void deleteTemporaryFiles() {
    deleteFiles(folder, false);
  }

  private static void deleteFiles(File file, boolean deleteparent) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (int i = 0; files != null && i < files.length; i++) {
        deleteFiles(files[i], true);
      }
    }
    if (deleteparent) {
      file.delete();
    }
  }

  /**
   * Create a new empty file to store execution data in.
   * 
   * @return path to execution data file
   */
  public IPath newFile() throws CoreException {
    try {
      final File file = File.createTempFile("session", ".exec", folder); //$NON-NLS-1$ //$NON-NLS-2$
      return Path.fromOSString(file.getAbsolutePath());
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.EXECFILE_ERROR.getStatus(e));
    }
  }

}
