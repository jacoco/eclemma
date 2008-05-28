/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.IStatus;

import junit.framework.TestCase;

/**
 * Tests for EclEmmaStatus.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class EclEmmaStatusTest extends TestCase {
  
  public void testCode1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_EMMAJAR_ERROR;
    IStatus status = estatus.getStatus();
    assertEquals(estatus.code, status.getCode());
  }

  public void testSeverity1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_EMMAJAR_ERROR;
    IStatus status = estatus.getStatus();
    assertEquals(estatus.severity, status.getSeverity());
  }

  public void testSeverity2() {
    EclEmmaStatus estatus = EclEmmaStatus.INPLACE_INSTRUMENTATION_INFO;
    IStatus status = estatus.getStatus();
    assertEquals(estatus.severity, status.getSeverity());
  }

  public void testMessage1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_EMMAJAR_ERROR;
    IStatus status = estatus.getStatus();
    assertEquals("No local emma.jar available (code 5000).", status.getMessage());
  }

  public void testMessage2() {
    EclEmmaStatus estatus = EclEmmaStatus.UNKOWN_LAUNCH_TYPE_ERROR;
    IStatus status = estatus.getStatus("abcdef");
    assertEquals("Unknown launch type abcdef (code 5002).", status.getMessage());
  }
  
  public void testThrowable1() {
    EclEmmaStatus estatus = EclEmmaStatus.NO_LOCAL_EMMAJAR_ERROR;
    Throwable t = new Exception();
    IStatus status = estatus.getStatus(t);
    assertSame(t, status.getException());
  }

  
}
