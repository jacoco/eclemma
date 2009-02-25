/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Test SignatureResolver based on Java source.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: 171 $
 */
public class SourceSignatureResolverTest extends AbstractSignatureResolverTest {

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
