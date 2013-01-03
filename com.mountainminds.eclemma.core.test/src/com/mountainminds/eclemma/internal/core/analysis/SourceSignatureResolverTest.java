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

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Test {@link SignatureResolver} based on Java source.
 */
public class SourceSignatureResolverTest extends SignatureResolverTestBase {

  private JavaProjectKit javaProject;

  @Before
  public void setup() throws Exception {
    javaProject = new JavaProjectKit();
    javaProject.enableJava5();
    final IPackageFragmentRoot root = javaProject.createSourceFolder("src");
    final ICompilationUnit compilationUnit = javaProject.createCompilationUnit(
        root, "testdata/src", "signatureresolver/Samples.java");
    JavaProjectKit.waitForBuild();
    javaProject.assertNoErrors();
    type = compilationUnit.getTypes()[0];
    createMethodIndex();
  }

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  @Test
  public void test_innerClassTypeVariable() throws Exception {
    final IMethod method = type.getType("Inner").getMethods()[0];
    assertEquals(SignatureResolver.getParameters(method),
        "Ljava/lang/Comparable;");
  }

}
