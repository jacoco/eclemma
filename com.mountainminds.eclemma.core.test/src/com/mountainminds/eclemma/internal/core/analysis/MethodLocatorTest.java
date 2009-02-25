/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import junit.framework.TestCase;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * @author Marc R. Hoffmann
 * @version $Revision: 171 $
 */
public class MethodLocatorTest extends TestCase {

  private JavaProjectKit javaProject;

  private MethodLocator methodLocator;

  protected void setUp() throws Exception {
    javaProject = new JavaProjectKit();
    javaProject.enableJava5();
    final IPackageFragmentRoot root = javaProject.createSourceFolder("src");
    final ICompilationUnit compilationUnit = javaProject.createCompilationUnit(
        root, "testdata/src", "methodlocator/Samples.java");
    JavaProjectKit.waitForBuild();
    javaProject.assertNoErrors();
    methodLocator = new MethodLocator(compilationUnit.getTypes()[0]);
  }

  protected void tearDown() throws Exception {
    javaProject.destroy();
  }

  private final void assertMethod(final String expectedKey, final String name,
      final String signature) {
    final IMethod method = methodLocator.findMethod(name, signature);
    assertNotNull(method);
    assertEquals(expectedKey, method.getKey());
  }

  public void testUnambiguousConstructor() {
    assertMethod("Lmethodlocator/Samples;.Samples()V", "<init>", "()V");
  }

  public void testAmbiguousConstructor1() {
    assertMethod("Lmethodlocator/Samples;.Samples(QString;)V", "<init>",
        "(Ljava/lang/String;)V");
  }

  public void testAmbiguousConstructor2() {
    assertMethod("Lmethodlocator/Samples;.Samples(I)V", "<init>", "(I)V");
  }

  public void testUnambiguousMethod() {
    assertMethod("Lmethodlocator/Samples;.m1(QString;)V", "m1",
        "(Ljava/lang/String;)V");
  }

  public void testAmbiguousMethod1() {
    assertMethod("Lmethodlocator/Samples;.m2(QInteger;)V", "m2",
        "(Ljava/lang/Integer;)V");
  }

  public void testAmbiguousMethod2() {
    assertMethod("Lmethodlocator/Samples;.m2(QNumber;)V", "m2",
        "(Ljava/lang/Number;)V");
  }

}
