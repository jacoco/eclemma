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
package com.mountainminds.eclemma.internal.core.launching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.ICorePreferences;

/**
 * Unit tests for {@link AgentArgumentSupport}.
 */
public class AgentArgumentSupportTest {

  private AgentArgumentSupport support;

  @Before
  public void setup() {
    support = new AgentArgumentSupport(ICorePreferences.DEFAULT);
  }

  @Test
  public void testQuote1() {
    assertEquals("abcdef", support.quote("abcdef"));
  }

  @Test
  public void testQuote2() {
    assertEquals("\"abc def\"", support.quote("abc def"));
  }

  @Test
  public void testGetAgentFile() throws CoreException {
    final File file = support.getAgentFile();
    assertTrue(file.exists());
    assertTrue(file.isFile());
  }

  @Test
  public void testGetArgument() throws CoreException {
    final String arg = support.getArgument(12345);
    assertTrue(arg, arg.startsWith("-javaagent:"));
    assertTrue(
        arg,
        arg.endsWith("jacocoagent.jar=includes=*,excludes=,exclclassloader=sun.reflect.DelegatingClassLoader,output=tcpclient,port=12345"));
  }

  @Test
  public void testAddArgument() throws CoreException {
    ConfigurationMock mock = new ConfigurationMock();
    mock.pushResult("OTHER");
    final ILaunchConfiguration config = support.addArgument(12345,
        mock.getMock());
    final String arg = config.getAttribute(
        IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
    assertTrue(arg, arg.startsWith("OTHER -javaagent:"));
  }

}
