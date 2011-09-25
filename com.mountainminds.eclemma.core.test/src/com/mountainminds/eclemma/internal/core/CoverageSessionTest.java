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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link CoverageSession}.
 */
public class CoverageSessionTest {

  private JavaProjectKit javaProject;
  private IPackageFragmentRoot root1;
  private IPackageFragmentRoot root2;
  private ILaunchConfiguration configuration;

  @Before
  public void setup() throws Exception {
    javaProject = new JavaProjectKit("project");
    root1 = javaProject.createSourceFolder("src1");
    root2 = javaProject.createSourceFolder("src2");
    configuration = DebugPlugin
        .getDefault()
        .getLaunchManager()
        .getLaunchConfigurationType(
            "org.eclipse.jdt.launching.localJavaApplication")
        .newInstance(javaProject.project, "test.launch");
    JavaProjectKit.waitForBuild();
  }

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  @Test
  public void testAttributes() throws CoreException {
    final CoverageSession session = new CoverageSession("Description",
        Arrays.asList(root1, root2), new Path("example.exec"), configuration);

    assertEquals("Description", session.getDescription());
    assertEquals(set(root1, root2), set(session.getScope()));
    assertEquals(set(new Path("example.exec")),
        set(session.getExecutionDataFiles()));
    assertSame(configuration, session.getLaunchConfiguration());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testScopeUnmodifiable() throws CoreException {
    Collection<IPackageFragmentRoot> scope = new ArrayList<IPackageFragmentRoot>();
    scope.add(root1);
    scope.add(root2);
    final CoverageSession session = new CoverageSession("Description", scope,
        new Path("example.exec"), configuration);

    session.getScope().clear();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testExecutionDataFilesUnmodifiable() throws CoreException {
    final CoverageSession session = new CoverageSession("Description",
        Arrays.asList(root1, root2), new Path("example.exec"), configuration);

    session.getExecutionDataFiles().clear();
  }

  @Test
  public void testMerge1() throws CoreException {
    final CoverageSession session1 = new CoverageSession("Description1",
        Arrays.asList(root1), new Path("example1.exec"), configuration);
    final CoverageSession session2 = new CoverageSession("Description2",
        Arrays.asList(root2), new Path("example2.exec"), configuration);

    final ICoverageSession merged = session1.merge(session2, "Merged");

    assertEquals("Merged", merged.getDescription());
    assertEquals(set(root1, root2), set(merged.getScope()));
    assertEquals(set(new Path("example1.exec"), new Path("example2.exec")),
        set(merged.getExecutionDataFiles()));
    assertSame(configuration, merged.getLaunchConfiguration());
  }

  @Test
  public void testMerge2() throws CoreException {
    final CoverageSession session1 = new CoverageSession("Description1",
        Arrays.asList(root1, root2), new Path("example1.exec"), configuration);
    final CoverageSession session2 = new CoverageSession("Description2",
        Arrays.asList(root2), new Path("example1.exec"), configuration);

    final ICoverageSession merged = session1.merge(session2, "Merged");

    assertEquals("Merged", merged.getDescription());
    assertEquals(set(root1, root2), set(merged.getScope()));
    assertEquals(set(new Path("example1.exec")),
        set(merged.getExecutionDataFiles()));
    assertSame(configuration, merged.getLaunchConfiguration());
  }

  private <E> Set<E> set(E... elements) {
    return new HashSet<E>(Arrays.asList(elements));
  }

  private <E> Set<E> set(Collection<E> elements) {
    Set<E> set = new HashSet<E>();
    for (E e : elements) {
      assertTrue("Duplicate element " + e, set.add(e));
    }
    return set;
  }

}
