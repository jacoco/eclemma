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
package com.mountainminds.eclemma.core;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;

/**
 * Tests for {@link EclEmmaStatus}.
 */
public class EclEmmaStatusTest extends TestCase {

  public void testCode1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_AGENTJAR_ERROR;
    IStatus status = estatus.getStatus();
    assertEquals(estatus.code, status.getCode());
  }

  public void testSeverity1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_AGENTJAR_ERROR;
    IStatus status = estatus.getStatus();
    assertEquals(estatus.severity, status.getSeverity());
  }

  public void testMessage1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_AGENTJAR_ERROR;
    IStatus status = estatus.getStatus();
    assertEquals("No local emma.jar available (code 5000).",
        status.getMessage());
  }

  public void testMessage2() {
    EclEmmaStatus estatus = EclEmmaStatus.UNKOWN_LAUNCH_TYPE_ERROR;
    IStatus status = estatus.getStatus("abcdef");
    assertEquals("Unknown launch type abcdef (code 5002).", status.getMessage());
  }

  public void testThrowable1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_AGENTJAR_ERROR;
    Throwable t = new Exception();
    IStatus status = estatus.getStatus(t);
    assertSame(t, status.getException());
  }

}
