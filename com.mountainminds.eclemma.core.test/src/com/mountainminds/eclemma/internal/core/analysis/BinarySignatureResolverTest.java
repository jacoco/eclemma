/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
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

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Test {@link SignatureResolver} based on Java binaries.
 */
public class BinarySignatureResolverTest extends SignatureResolverTestBase {

  private JavaProjectKit javaProject;

  @Before
  public void setup() throws Exception {
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

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  @Test
  public void testGetParameterNoArgs() {
    assertEquals("", SignatureResolver.getParameters("()Ljava.lang.Integer;"));
  }

  @Test
  public void testGetParameterWithArgs() {
    assertEquals("[[Ljava/util/Map$Entry;",
        SignatureResolver.getParameters("([[Ljava/util/Map$Entry;)I"));
  }

}
