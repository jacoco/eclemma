/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.instr;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.JavaProjectKit;

public class ClassFilesStoreTest extends TestCase {

  private ClassFilesStore store;

  private JavaProjectKit javaProject1;

  private JavaProjectKit javaProject2;

  private IPackageFragmentRoot rootSrc1;

  private IPackageFragmentRoot rootBin;

  protected void setUp() throws Exception {
    store = new ClassFilesStore();
    javaProject1 = new JavaProjectKit("projectA");
    rootSrc1 = javaProject1.createSourceFolder("src1");
    rootBin = javaProject1.createJAR("testdata/bin/signatureresolver.jar",
        "sample.jar", new Path("/UnitTestProject/sample.jar"), null);

    javaProject2 = new JavaProjectKit("projectB");
    javaProject2.createSourceFolder("src2");
    JavaProjectKit.waitForBuild();
  }

  protected void tearDown() throws Exception {
    javaProject1.destroy();
    javaProject2.destroy();
  }

  public void testAddSrcRoot() throws JavaModelException {
    store.add(rootSrc1);
    final IClassFiles[] classfiles = store.getClassFiles();
    assertEquals(1, classfiles.length);
    assertEquals(new Path("/projectA/bin"), classfiles[0].getLocation());
    assertEquals(1, classfiles[0].getPackageFragmentRoots().length);
    assertEquals(rootSrc1, classfiles[0].getPackageFragmentRoots()[0]);
  }

  public void testAddBinRoot() throws JavaModelException {
    store.add(rootBin);
    final IClassFiles[] classfiles = store.getClassFiles();
    assertEquals(1, classfiles.length);
    assertEquals(new Path("/projectA/sample.jar"), classfiles[0].getLocation());
    assertEquals(1, classfiles[0].getPackageFragmentRoots().length);
    assertEquals(rootBin, classfiles[0].getPackageFragmentRoots()[0]);
  }

  public void testGetAtAbsoluteLocation() throws Exception {
    store.add(rootSrc1);
    final String loc = javaProject1.project.getFolder("bin").getLocation()
        .toOSString();
    final IClassFiles classfiles = store.getAtAbsoluteLocation(loc);
    assertNotNull(classfiles);
    assertEquals(new Path("/projectA/bin"), classfiles.getLocation());
    assertEquals(1, classfiles.getPackageFragmentRoots().length);
    assertEquals(rootSrc1, classfiles.getPackageFragmentRoots()[0]);
  }

  public void testAddProject() throws JavaModelException {
    store.add(javaProject1.javaProject);
    final String loc1 = javaProject1.project.getFolder("bin").getLocation()
        .toOSString();
    final String loc2 = javaProject1.project.getFile("sample.jar")
        .getLocation().toOSString();
    assertNotNull(store.getAtAbsoluteLocation(loc1));
    assertNotNull(store.getAtAbsoluteLocation(loc2));
  }

  public void testAddModel() throws JavaModelException {
    store.add(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
    final String loc1 = javaProject1.project.getFolder("bin").getLocation()
        .toOSString();
    final String loc2 = javaProject1.project.getFile("sample.jar")
        .getLocation().toOSString();
    final String loc3 = javaProject2.project.getFolder("bin").getLocation()
        .toOSString();
    assertNotNull(store.getAtAbsoluteLocation(loc1));
    assertNotNull(store.getAtAbsoluteLocation(loc2));
    assertNotNull(store.getAtAbsoluteLocation(loc3));
  }

}
