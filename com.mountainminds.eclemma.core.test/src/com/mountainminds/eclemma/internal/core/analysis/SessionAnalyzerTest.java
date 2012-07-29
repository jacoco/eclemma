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
package com.mountainminds.eclemma.internal.core.analysis;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link SessionAnalyzer}.
 */
public class SessionAnalyzerTest {

  private JavaProjectKit javaProject;
  private SessionAnalyzer sessionAnalyzer;

  @Before
  public void setup() throws Exception {
    javaProject = new JavaProjectKit();
    sessionAnalyzer = new SessionAnalyzer();
  }

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  @Test
  public void testGetNameSourceFolder() throws CoreException {
    IPackageFragmentRoot source = javaProject.createSourceFolder("src");
    assertEquals("src", sessionAnalyzer.getName(source));
  }

  @Test
  public void testGetNameNestedSourceFolder() throws CoreException {
    javaProject.project.getFolder("src").create(false, true, null);
    javaProject.project.getFolder("src/main").create(false, true, null);
    IPackageFragmentRoot source = javaProject
        .createSourceFolder("src/main/java");
    assertEquals("src/main/java", sessionAnalyzer.getName(source));
  }

  @Test
  public void testGetNameProjectRootSourceFolder() throws CoreException {
    IPackageFragmentRoot source = javaProject.javaProject
        .getPackageFragmentRoot(javaProject.project);
    javaProject.addClassPathEntry(JavaCore.newSourceEntry(source.getPath()));

    assertEquals("UnitTestProject", sessionAnalyzer.getName(source));
  }

  @Test
  public void testGetNameLocalLibrary() throws CoreException, IOException {
    IPackageFragmentRoot library = javaProject.createJAR(
        "testdata/bin/signatureresolver.jar", "/sample.jar", null, null);

    assertEquals("sample.jar", sessionAnalyzer.getName(library));
  }

  @Test
  public void testGetNameLocalLibraryInFolder() throws CoreException,
      IOException {
    javaProject.project.getFolder("libs").create(false, true, null);
    javaProject.project.getFolder("libs/runtime").create(false, true, null);
    IPackageFragmentRoot library = javaProject.createJAR(
        "testdata/bin/signatureresolver.jar", "/libs/runtime/sample.jar", null,
        null);

    assertEquals("libs/runtime/sample.jar", sessionAnalyzer.getName(library));
  }

  @Test
  public void testGetNameExternalLibrary() throws CoreException, IOException {
    IPackageFragmentRoot library = javaProject.createExternalJAR(
        "testdata/bin/signatureresolver.jar", null, null);

    assertEquals(library.getElementName(), sessionAnalyzer.getName(library));
  }

}
