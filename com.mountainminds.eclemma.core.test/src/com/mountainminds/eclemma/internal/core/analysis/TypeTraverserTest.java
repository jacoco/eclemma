/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class TypeTraverserTest extends TestCase {

  public static final IProgressMonitor MONITOR = new NullProgressMonitor();

  private JavaProjectKit javaProject;

  private IPackageFragmentRoot root;

  protected void setUp() throws Exception {
    javaProject = new JavaProjectKit();
    root = javaProject.createSourceFolder("src");
    javaProject.createCompilationUnit(root, "testdata/src",
        "methodresolver/p1/TypeForTraverser.java");
    JavaProjectKit.waitForBuild();
  }

  protected void tearDown() throws Exception {
    javaProject.destroy();
  }

  private static final String[] EXPECTEDTYPES = new String[] {
      "methodresolver/p1/TypeForTraverser",
      "methodresolver/p1/TypeForTraverser$1",
      "methodresolver/p1/TypeForTraverser$1$InnerB",
      "methodresolver/p1/TypeForTraverser$2",
      "methodresolver/p1/TypeForTraverser$2$InnerC",
      "methodresolver/p1/TypeForTraverser$3",
      "methodresolver/p1/TypeForTraverser$4",
      "methodresolver/p1/TypeForTraverser$5",
      "methodresolver/p1/TypeForTraverser$InnerA" };

  public void testTraverse1() throws Exception {
    final Set expected = new HashSet(Arrays.asList(EXPECTEDTYPES));
    TypeTraverser t = new TypeTraverser(new IPackageFragmentRoot[] { root });
    t.process(new ITypeVisitor() {
      public void visit(IType type, String vmname) {
        assertTrue("Unexpected type: " + vmname, expected.remove(vmname));
      }

      public void done() {
      }
    }, MONITOR);
    assertTrue("Not all types processed: " + expected, expected.isEmpty());
  }

}
