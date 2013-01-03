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
package com.mountainminds.eclemma.internal.core.analysis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jacoco.core.data.ExecutionDataStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link PackageFragmentRootAnalyzer}.
 */
public class PackageFragmentRootAnalyzerTest {

  private JavaProjectKit javaProject;
  private PackageFragementRootAnalyzer analyzer;

  @Before
  public void setup() throws Exception {
    javaProject = new JavaProjectKit("project");
    final ExecutionDataStore data = new ExecutionDataStore();
    analyzer = new PackageFragementRootAnalyzer(data);
  }

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  @Test
  public void testSourceFolder() throws Exception {
    javaProject.setDefaultOutputLocation("classes");
    final IPackageFragmentRoot root = javaProject.createSourceFolder("src");
    javaProject.createCompilationUnit(root, "testdata/src",
        "typetraverser/Samples.java");
    JavaProjectKit.waitForBuild();

    final AnalyzedNodes nodes = analyzer.analyze(root);
    assertNotNull(nodes.getClassCoverage("typetraverser/Samples"));

    // Caching:
    assertSame(nodes, analyzer.analyze(root));
  }

  @Test
  public void testSourceWithOutputFolder() throws Exception {
    final IPackageFragmentRoot root = javaProject.createSourceFolder("src",
        "myclasses");
    javaProject.createCompilationUnit(root, "testdata/src",
        "typetraverser/Samples.java");
    JavaProjectKit.waitForBuild();

    final AnalyzedNodes nodes = analyzer.analyze(root);
    assertNotNull(nodes.getClassCoverage("typetraverser/Samples"));
  }

  @Test
  public void testJar() throws Exception {
    final IPackageFragmentRoot root = javaProject.createJAR(
        "testdata/bin/signatureresolver.jar", "/sample.jar", null, null);

    JavaProjectKit.waitForBuild();

    final AnalyzedNodes nodes = analyzer.analyze(root);
    assertNotNull(nodes.getClassCoverage("signatureresolver/Samples"));
  }

  @Test
  public void testExternalJar() throws Exception {
    final IPackageFragmentRoot root = javaProject.createExternalJAR(
        "testdata/bin/signatureresolver.jar", null, null);

    JavaProjectKit.waitForBuild();

    final AnalyzedNodes nodes = analyzer.analyze(root);
    assertNotNull(nodes.getClassCoverage("signatureresolver/Samples"));
  }

}
