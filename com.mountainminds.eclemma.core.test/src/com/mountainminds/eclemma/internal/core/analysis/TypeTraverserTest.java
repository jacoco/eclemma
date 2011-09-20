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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link TypeTraverser}.
 */
public class TypeTraverserTest extends TestCase {

  private static final IProgressMonitor MONITOR = new NullProgressMonitor();

  private JavaProjectKit javaProject;

  private IPackageFragmentRoot root;

  protected void setUp() throws Exception {
    javaProject = new JavaProjectKit();
    root = javaProject.createSourceFolder("src");
    javaProject.createCompilationUnit(root, "testdata/src",
        "typetraverser/Samples.java");
    JavaProjectKit.waitForBuild();
    javaProject.assertNoErrors();
  }

  protected void tearDown() throws Exception {
    javaProject.destroy();
  }

  private static final String[] EXPECTEDTYPES = new String[] {
      "typetraverser/Samples", "typetraverser/Samples$1",
      "typetraverser/Samples$1$InnerB", "typetraverser/Samples$2",
      "typetraverser/Samples$2$InnerC", "typetraverser/Samples$3",
      "typetraverser/Samples$4", "typetraverser/Samples$5",
      "typetraverser/Samples$InnerA" };

  public void testTraverse1() throws Exception {
    final Set expected = new HashSet(Arrays.asList(EXPECTEDTYPES));
    TypeTraverser t = new TypeTraverser(root);
    t.process(new ITypeVisitor() {
      public void visit(IType type, String vmname) {
        assertTrue("Unexpected type: " + vmname, expected.remove(vmname));
      }

      public void visit(ICompilationUnit unit) throws JavaModelException {
      }
    }, MONITOR);
    assertTrue("Not all types processed: " + expected, expected.isEmpty());
  }

}
