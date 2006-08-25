/*
 * $Id: $
 */
package com.mountainminds.eclemma.internal.core.analysis;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import com.mountainminds.eclemma.internal.core.analysis.MethodResolver;
import com.mountainminds.eclemma.internal.core.testutils.JavaProjectTestBase;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class MethodResolverTest1 extends JavaProjectTestBase {
  
  protected final String localName;
  protected final String resolvedExpected;
  
  protected IType theType;
  protected MethodResolver methodResolver;

  protected MethodResolverTest1(String localName, String resolvedExpected) {
    super(localName);
    this.localName = localName;
    this.resolvedExpected = resolvedExpected;    
  }
  
  protected void setUp() throws Exception {
    super.setUp();
    IPackageFragmentRoot src = createSourceFolder("src");
    theType = createCompilationUnit(src, "src-test", "methodresolver/p1/TheType.java").getTypes()[0];
    createCompilationUnit(src, "src-test", "methodresolver/p1/AnotherType.java");
    createCompilationUnit(src, "src-test", "methodresolver/p2/AnotherTypeInAnotherPackage.java");
    methodResolver = new MethodResolver(theType);
  }

  protected void runTest() throws Throwable {
    assertEquals("resolving the first time " + localName, resolvedExpected, new String(methodResolver.resolve(localName)));
    assertEquals("resolving from cache " + localName, resolvedExpected, new String(methodResolver.resolve(localName)));
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(MethodResolverTest1.class.getName());
    suite.addTest(new MethodResolverTest1("I", "I"));
    suite.addTest(new MethodResolverTest1("[[I", "[[I"));
    
    suite.addTest(new MethodResolverTest1("QString;", "Ljava/lang/String;"));
    suite.addTest(new MethodResolverTest1("[QString;", "[Ljava/lang/String;"));
    suite.addTest(new MethodResolverTest1("Qjava.lang.String;", "Ljava/lang/String;"));
    suite.addTest(new MethodResolverTest1("Ljava.lang.String;", "Ljava/lang/String;"));

    suite.addTest(new MethodResolverTest1("QTheType;", "Lmethodresolver/p1/TheType;"));
    suite.addTest(new MethodResolverTest1("QInner;", "Lmethodresolver/p1/TheType$Inner;"));
    
    suite.addTest(new MethodResolverTest1("QAnotherType;", "Lmethodresolver/p1/AnotherType;"));

    suite.addTest(new MethodResolverTest1("QAnotherTypeInAnotherPackage;", "Lmethodresolver/p2/AnotherTypeInAnotherPackage;"));
    suite.addTest(new MethodResolverTest1("Qmethodresolver.p2.AnotherTypeInAnotherPackage;", "Lmethodresolver/p2/AnotherTypeInAnotherPackage;"));

    suite.addTest(new MethodResolverTest1("QUnknownType;", "LUnknownType;"));
    suite.addTest(new MethodResolverTest1("Qunknownpackage.UnknownType;", "Lunknownpackage/UnknownType;"));

    return suite;
  }

}
