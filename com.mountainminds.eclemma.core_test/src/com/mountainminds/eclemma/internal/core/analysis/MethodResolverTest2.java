/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import com.mountainminds.eclemma.internal.core.testutils.JavaProjectTestBase;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class MethodResolverTest2 extends JavaProjectTestBase {
  
  protected final int idx;
  protected final String vmsignature;
  
  protected IType theType;
  protected MethodResolver methodResolver;

  protected MethodResolverTest2(int idx, String vmsignature) {
    super(vmsignature);
    this.idx = idx;
    this.vmsignature = vmsignature;    
  }
  
  protected void setUp() throws Exception {
    super.setUp();
    IPackageFragmentRoot src = createSourceFolder("src");
    theType = createCompilationUnit(src, "src-test", "methodresolver/p1/TheType.java").getTypes()[0];
    createCompilationUnit(src, "src-test", "methodresolver/p2/AnotherTypeInAnotherPackage.java");
    methodResolver = new MethodResolver(theType);
  }

  protected void runTest() throws Throwable {
    IMethod[] methods = theType.getMethods();
    assertEquals(vmsignature, methodResolver.resolve(methods[idx]));
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(MethodResolverTest2.class.getName());
    suite.addTest(new MethodResolverTest2(0, "<init>()V"));
    suite.addTest(new MethodResolverTest2(1, "meth1()V"));
    suite.addTest(new MethodResolverTest2(2, "meth2(Ljava/lang/Object;)Ljava/lang/Object;"));
    suite.addTest(new MethodResolverTest2(3, "meth3(Ljava/lang/Object;)Ljava/lang/Object;"));
    suite.addTest(new MethodResolverTest2(4, "meth4(Ljava/lang/String;)Ljava/lang/String;"));
    suite.addTest(new MethodResolverTest2(5, "meth5(Ljava/lang/String;)Ljava/lang/String;"));
    suite.addTest(new MethodResolverTest2(6, "meth6([Ljava/lang/Object;)V"));
    suite.addTest(new MethodResolverTest2(7, "meth7([Ljava/lang/Object;)V"));
    suite.addTest(new MethodResolverTest2(8, "meth8()Lmethodresolver/p2/AnotherTypeInAnotherPackage;"));

    return suite;
  }

}
