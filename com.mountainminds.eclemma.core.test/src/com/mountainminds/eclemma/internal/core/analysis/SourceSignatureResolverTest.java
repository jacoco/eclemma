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
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Test {@link SignatureResolver} based on Java source.
 */
public class SourceSignatureResolverTest extends SignatureResolverTestBase {

  private JavaProjectKit javaProject;

  protected void setUp() throws Exception {
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

  protected void tearDown() throws Exception {
    javaProject.destroy();
  }

  public void test_innerClassTypeVariable() throws Exception {
    final IMethod method = type.getType("Inner").getMethods()[0];
    assertEquals(SignatureResolver.getParameters(method),
        "Ljava/lang/Comparable;");
  }

}
