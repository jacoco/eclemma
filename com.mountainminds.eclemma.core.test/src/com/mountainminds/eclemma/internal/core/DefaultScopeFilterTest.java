/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.ICorePreferences;
import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link DefaultScopeFilter}.
 */
public class DefaultScopeFilterTest {

  private JavaProjectKit javaProject1;

  private JavaProjectKit javaProject2;

  private TestPreferences preferences;

  private DefaultScopeFilter filter;

  private ILaunchConfigurationWorkingCopy configuration;

  private IPackageFragmentRoot rootSrc1;

  private IPackageFragmentRoot rootSrc2;

  private IPackageFragmentRoot rootBin1;

  @Before
  public void setup() throws Exception {
    javaProject1 = new JavaProjectKit("project1");
    javaProject2 = new JavaProjectKit("project2");
    preferences = new TestPreferences();
    filter = new DefaultScopeFilter(preferences);

    configuration = DebugPlugin
        .getDefault()
        .getLaunchManager()
        .getLaunchConfigurationType(
            "org.eclipse.jdt.launching.localJavaApplication")
        .newInstance(javaProject1.project, "test.launch");

    rootSrc1 = javaProject1.createSourceFolder("src1");

    rootSrc2 = javaProject2.createSourceFolder("testsrc");

    rootBin1 = javaProject1.createJAR("testdata/bin/signatureresolver.jar",
        "/sample.jar", new Path("/UnitTestProject/sample.jar"), null);
    JavaProjectKit.waitForBuild();
  }

  @After
  public void teardown() throws Exception {
    javaProject1.destroy();
    javaProject2.destroy();
  }

  @Test
  public void testNoFilters() throws CoreException {
    preferences.sourceFoldersOnly = false;
    final Set<IPackageFragmentRoot> input = set(rootSrc1, rootSrc2, rootBin1);
    final Set<IPackageFragmentRoot> output = filter
        .filter(input, configuration);
    assertEquals(input, output);
  }

  @Test
  public void testSourceFoldersOnly() throws CoreException {
    preferences.sourceFoldersOnly = true;
    final Set<IPackageFragmentRoot> input = set(rootSrc1, rootBin1);
    final Collection<IPackageFragmentRoot> output = filter.filter(input,
        configuration);
    assertEquals(set(rootSrc1), output);
  }

  @Test
  public void testSameProjectOnly() throws CoreException {
    preferences.sameProjectOnly = true;
    configuration.setAttribute(
        IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "project1");
    final Set<IPackageFragmentRoot> input = set(rootSrc1, rootSrc2);
    final Collection<IPackageFragmentRoot> output = filter.filter(input,
        configuration);
    assertEquals(set(rootSrc1), output);
  }

  @Test
  public void testFilter() throws CoreException {
    preferences.filter = "testsrc,abc";
    final Set<IPackageFragmentRoot> input = set(rootSrc1, rootSrc2);
    final Collection<IPackageFragmentRoot> output = filter.filter(input,
        configuration);
    assertEquals(set(rootSrc2), output);
  }

  private static class TestPreferences implements ICorePreferences {

    String filter;

    boolean sameProjectOnly;

    boolean sourceFoldersOnly;

    public String getDefaultScopeFilter() {
      return filter;
    }

    public boolean getDefaultScopeSameProjectOnly() {
      return sameProjectOnly;
    }

    public boolean getDefaultScopeSourceFoldersOnly() {
      return sourceFoldersOnly;
    }

    public boolean getActivateNewSessions() {
      return false;
    }

    public boolean getAutoRemoveSessions() {
      return false;
    }

    public String getAgentIncludes() {
      return null;
    }

    public String getAgentExcludes() {
      return null;
    }

    public String getAgentExclClassloader() {
      return null;
    }

  }

  private <E> Set<E> set(E... elements) {
    return new HashSet<E>(Arrays.asList(elements));
  }

}
