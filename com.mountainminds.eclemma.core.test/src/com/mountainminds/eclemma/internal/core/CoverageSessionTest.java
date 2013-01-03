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
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.IExecutionDataSource;
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
        Arrays.asList(root1, root2), source(), configuration);

    assertEquals("Description", session.getDescription());
    assertEquals(set(root1, root2), session.getScope());
    assertSame(configuration, session.getLaunchConfiguration());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testScopeUnmodifiable() throws CoreException {
    Collection<IPackageFragmentRoot> scope = new ArrayList<IPackageFragmentRoot>();
    scope.add(root1);
    scope.add(root2);
    final CoverageSession session = new CoverageSession("Description", scope,
        source(), configuration);

    session.getScope().clear();
  }

  @Test
  public void testReadExecutionData() throws IOException, CoreException {
    final CoverageSession session = new CoverageSession("Description",
        Arrays.asList(root1), source(), configuration);

    SessionInfoStore sessionStore = new SessionInfoStore();
    ExecutionDataStore execStore = new ExecutionDataStore();
    session.accept(execStore, sessionStore);

    assertEquals(1, sessionStore.getInfos().size());
    assertEquals("MyClass", execStore.get(123).getName());
  }

  private <E> Set<E> set(E... elements) {
    return new HashSet<E>(Arrays.asList(elements));
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
