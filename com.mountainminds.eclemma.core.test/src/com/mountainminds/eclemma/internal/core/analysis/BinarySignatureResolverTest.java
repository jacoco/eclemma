/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Test SignatureResolver based on Java binaries.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: 171 $
 */
public class BinarySignatureResolverTest extends AbstractSignatureResolverTest {

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
    assertEquals("[[Ljava/util/Map$Entry;", SignatureResolver
        .getParameters("([[Ljava/util/Map$Entry;)I"));
  }

}
