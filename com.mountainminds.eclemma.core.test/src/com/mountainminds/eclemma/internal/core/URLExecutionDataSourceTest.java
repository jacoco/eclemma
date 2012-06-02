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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.mountainminds.eclemma.core.IExecutionDataSource;

/**
 * Tests for {@link URLExecutionDataSource}.
 */
public class URLExecutionDataSourceTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testAccept() throws IOException, CoreException {
    final IExecutionDataSource source = createValidSource();

    SessionInfoStore sessionStore = new SessionInfoStore();
    ExecutionDataStore execStore = new ExecutionDataStore();
    source.accept(execStore, sessionStore);

    assertEquals(1, sessionStore.getInfos().size());
    assertEquals("MyClass", execStore.get(123).getName());
  }

  @Test
  public void testAcceptWithError() throws IOException, CoreException {
    exception.expect(CoreException.class);
    exception.expectMessage("Error while reading execution data file");

    final IExecutionDataSource source = creatInvalidSource();

    SessionInfoStore sessionStore = new SessionInfoStore();
    ExecutionDataStore execStore = new ExecutionDataStore();
    source.accept(execStore, sessionStore);
  }

  private IExecutionDataSource createValidSource() throws IOException {
    File execfile = new File(folder.getRoot(), "test.exec");
    OutputStream out = new FileOutputStream(execfile);
    ExecutionDataWriter writer = new ExecutionDataWriter(out);
    writer.visitSessionInfo(new SessionInfo("id", 1, 2));
    writer.visitClassExecution(new ExecutionData(123, "MyClass", 15));
    out.close();
    return new URLExecutionDataSource(execfile.toURL());
  }

  private IExecutionDataSource creatInvalidSource() throws IOException {
    File execfile = new File(folder.getRoot(), "test.exec");
    OutputStream out = new FileOutputStream(execfile);
    out.write("invalid".getBytes());
    out.close();
    return new URLExecutionDataSource(execfile.toURL());
  }

}
