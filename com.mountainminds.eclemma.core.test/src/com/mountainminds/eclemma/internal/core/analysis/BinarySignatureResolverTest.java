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

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Test {@link SignatureResolver} based on Java binaries.
 */
public class BinarySignatureResolverTest extends SignatureResolverTestBase {

  private JavaProjectKit javaProject;

  protected void setUp() throws Exception {
    javaProject = new JavaProjectKit();
    javaProject.enableJava5();
    final IPackageFragmentRoot root = javaProject.createJAR(
        "testdata/bin/signatureresolver.jar", "/signatureresolver.jar",
        new Path("/UnitTestProject/signatureresolver.jar"), null);
    JavaProjectKit.waitForBuild();
    javaProject.assertNoErrors();
    final IClassFile classFile = root.getPackageFragment("signatureresolver")
        .getClassFile("Samples.class");
    type = classFile.getType();
    createMethodIndex();
  }

  protected void tearDown() throws Exception {
    javaProject.destroy();
  }

  public void testGetParameterNoArgs() {
    assertEquals("", SignatureResolver.getParameters("()Ljava.lang.Integer;"));
  }

  public void testGetParameterWithArgs() {
    assertEquals("[[Ljava/util/Map$Entry;",
        SignatureResolver.getParameters("([[Ljava/util/Map$Entry;)I"));
  }

}
