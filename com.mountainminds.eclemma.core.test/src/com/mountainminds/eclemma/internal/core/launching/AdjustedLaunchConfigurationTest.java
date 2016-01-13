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
package com.mountainminds.eclemma.internal.core.launching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link AdjustedLaunchConfiguration}.
 */
public class AdjustedLaunchConfigurationTest {

  private ConfigurationMock mock;

  private AdjustedLaunchConfiguration adjusted;

  @Before
  public void setup() {
    mock = new ConfigurationMock();
    adjusted = new AdjustedLaunchConfiguration("EXTRA", mock.getMock());
  }

  @Test
  public void testHasAttributeNegative() throws CoreException {
    mock.pushResult(Boolean.FALSE);
    assertFalse(adjusted.hasAttribute("other"));
  }

  @Test
  public void testHasAttributeOther() throws CoreException {
    mock.pushResult(Boolean.TRUE);
    assertTrue(adjusted.hasAttribute("other"));
  }

  @Test
  public void testHasAttribute() throws CoreException {
    mock.pushResult(Boolean.FALSE);
    assertTrue(adjusted
        .hasAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS));
  }

  @Test
  public void testGetAttribute() throws CoreException {
    mock.pushResult("");
    assertEquals("EXTRA", adjusted.getAttribute(
        IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, ""));
  }

  @Test
  public void testGetAttributePrepended() throws CoreException {
    mock.pushResult("ORIGINAL");
    assertEquals("EXTRA ORIGINAL", adjusted.getAttribute(
        IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, ""));
  }

  @Test
  public void testGetAttributeOther() throws CoreException {
    mock.pushResult("OTHER");
    assertEquals("OTHER", adjusted.getAttribute("someid", ""));
  }

  @Test
  public void testGetAttributes() throws CoreException {
    mock.pushResult("ORIGINAL");
    mock.pushResult(Collections.singletonMap("otherkey", "othervalue"));

    final Map<?, ?> map = adjusted.getAttributes();

    assertEquals(
        new HashSet<String>(Arrays.asList("otherkey",
            IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS)), map.keySet());
    assertEquals("othervalue", map.get("otherkey"));
    assertEquals("EXTRA ORIGINAL",
        map.get(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS));
  }

}
