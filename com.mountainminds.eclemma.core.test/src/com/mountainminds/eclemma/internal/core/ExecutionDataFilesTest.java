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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.mountainminds.eclemma.core.IExecutionDataSource;

/**
 * Tests for {@link ExecutionDataFiles}.
 */
public class ExecutionDataFilesTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private ExecutionDataFiles files;

  @Before
  public void setup() {
    final IPath path = Path.fromOSString(folder.getRoot().getAbsolutePath());
    files = new ExecutionDataFiles(path);
  }

  @Test
  public void testNewFile() throws Exception {
    files.newFile(source());

    final File[] execfiles = new File(folder.getRoot(), ".execdata")
        .listFiles();
    assertEquals(1, execfiles.length);
    final File execfile = execfiles[0];
    assertTrue(execfile.exists());
    assertTrue(execfile.isFile());

    final ExecutionDataReader reader = new ExecutionDataReader(
        new FileInputStream(execfile));
    final SessionInfoStore sessionInfoStore = new SessionInfoStore();
    final ExecutionDataStore executionDataStore = new ExecutionDataStore();
    reader.setSessionInfoVisitor(sessionInfoStore);
    reader.setExecutionDataVisitor(executionDataStore);
    reader.read();

    assertEquals("id", sessionInfoStore.getInfos().get(0).getId());
    assertEquals("MyClass", executionDataStore.get(123).getName());
  }

  @Test(expected = CoreException.class)
  public void testNewFileNegative() throws Exception {
    folder.delete();
    files.newFile(source());
  }

  @Test
  public void testDeleteTemporaryFiles() throws Exception {
    files.newFile(source());
    files.newFile(source());

    files.deleteTemporaryFiles();

    assertArrayEquals(new String[0],
        new File(folder.getRoot(), ".execdata").list());
  }

  private IExecutionDataSource source() {
    return new IExecutionDataSource() {
      public void accept(IExecutionDataVisitor executionDataVisitor,
          ISessionInfoVisitor sessionInfoVisitor) throws CoreException {
        sessionInfoVisitor.visitSessionInfo(new SessionInfo("id", 1, 2));
        executionDataVisitor.visitClassExecution(new ExecutionData(123,
            "MyClass", 15));
      }
    };
  }

}
