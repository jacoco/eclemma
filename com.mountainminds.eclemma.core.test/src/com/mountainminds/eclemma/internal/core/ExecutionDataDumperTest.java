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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for {@link ExecutionDataDumper}.
 */
public class ExecutionDataDumperTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private ExecutionDataDumper dumper;

  private PipedOutputStream out;
  private RemoteControlWriter writer;

  @Before
  public void setup() throws IOException {
    final IPath path = Path.fromOSString(folder.getRoot().getAbsolutePath());
    out = new PipedOutputStream();
    final PipedInputStream in = new PipedInputStream();
    out.connect(in);
    writer = new RemoteControlWriter(out);
    dumper = new ExecutionDataDumper(new RemoteControlReader(in),
        new ExecutionDataFiles(path));
  }

  @Test
  public void testEmpty() throws Exception {
    out.close();
    assertNull(dumper.dump());
    assertFalse(dumper.hasDataReceived());
  }

  @Test
  public void testTwoSessions() throws Exception {
    writer.visitSessionInfo(new SessionInfo("Session", 10, 20));
    writer.visitClassExecution(new ExecutionData(11, "Clazz1", new boolean[8]));
    writer.sendCmdOk();
    verifyExecContent(dumper.dump(), "Clazz1");
    assertTrue(dumper.hasDataReceived());

    writer.visitSessionInfo(new SessionInfo("Session", 10, 20));
    writer.visitClassExecution(new ExecutionData(11, "Clazz2", new boolean[8]));
    writer.sendCmdOk();
    out.close();
    verifyExecContent(dumper.dump(), "Clazz2");
    assertTrue(dumper.hasDataReceived());

    assertNull(dumper.dump());
  }

  private void verifyExecContent(IPath path, String... classnames)
      throws Exception {
    final FileInputStream in = new FileInputStream(path.toFile());
    final ExecutionDataReader reader = new ExecutionDataReader(in);
    reader.setSessionInfoVisitor(new SessionInfoStore());
    final ExecutionDataStore store = new ExecutionDataStore();
    reader.setExecutionDataVisitor(store);
    while (reader.read()) {
    }
    in.close();
    final Set<String> actual = new HashSet<String>();
    for (ExecutionData data : store.getContents()) {
      actual.add(data.getName());
    }
    assertEquals(new HashSet<String>(Arrays.asList(classnames)), actual);
  }

}
