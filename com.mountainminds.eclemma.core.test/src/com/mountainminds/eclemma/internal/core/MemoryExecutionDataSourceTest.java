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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MemoryExecutionDataSource}.
 */
public class MemoryExecutionDataSourceTest {

  private MemoryExecutionDataSource source;

  @Before
  public void setup() {
    source = new MemoryExecutionDataSource();
  }

  @Test
  public void testIsEmptyPositive() {
    assertTrue(source.isEmpty());
  }

  @Test
  public void testIsEmptyNegative() {
    source.visitSessionInfo(new SessionInfo("id", 1, 2));
    assertFalse(source.isEmpty());
  }

  @Test
  public void testAccept() throws Exception {
    SessionInfo info = new SessionInfo("id1", 1, 2);
    source.visitSessionInfo(info);
    source.visitClassExecution(new ExecutionData(123, "MyClass", new boolean[] {
        true, false }));

    SessionInfoStore sessionStore = new SessionInfoStore();
    ExecutionDataStore execStore = new ExecutionDataStore();
    source.accept(execStore, sessionStore);

    assertEquals(Collections.singletonList(info), sessionStore.getInfos());
    assertEquals("MyClass", execStore.get(123).getName());
  }

  @Test
  public void testReadFrom() throws Exception {
    ExecutionDataReader reader = new ExecutionDataReader(
        new ByteArrayInputStream(createSessionData()));
    source.readFrom(reader);

    SessionInfoStore sessionStore = new SessionInfoStore();
    ExecutionDataStore execStore = new ExecutionDataStore();
    source.accept(execStore, sessionStore);

    assertEquals(1, sessionStore.getInfos().size());
    assertEquals("MyClass", execStore.get(123).getName());
  }

  private byte[] createSessionData() throws Exception {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    ExecutionDataWriter writer = new ExecutionDataWriter(buffer);
    SessionInfo info = new SessionInfo("id1", 1, 2);
    writer.visitSessionInfo(info);
    writer.visitClassExecution(new ExecutionData(123, "MyClass", new boolean[] {
        true, false }));
    return buffer.toByteArray();
  }
}
