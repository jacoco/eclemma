/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import com.mountainminds.eclemma.internal.core.testutils.JavaProjectTestBase;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class JavaElementsTraverserTest extends JavaProjectTestBase {

  public static final IProgressMonitor MONITOR = new NullProgressMonitor();  
  IPackageFragmentRoot root;
  
  protected void setUp() throws Exception {
    super.setUp();
    root = createSourceFolder("src");
    createCompilationUnit(root, "src-test", "methodresolver/p1/TypeForTraverser.java");
  }
  
  private static final String[] EXPECTEDELEMENTS = new String[] {
    "methodresolver/p1/TypeForTraverser",
    "methodresolver/p1/TypeForTraverser$1",
    "run()V",
    "methodresolver/p1/TypeForTraverser$2",
    "run()V",
    "<init>()V",
    "method1()V",
    "methodresolver/p1/TypeForTraverser$3",
    "run()V"
  };
  
  public void testTraverse1() throws Exception {
    final List expected = new ArrayList(Arrays.asList(EXPECTEDELEMENTS));
    JavaElementsTraverser t = new JavaElementsTraverser(new IPackageFragmentRoot[] { root });
    t.process(new ITypeVisitor() {
      public IMethodVisitor visit(IType type, String vmname) {
        assertEquals(expected.remove(0), vmname);
        return new IMethodVisitor() {
          public void visit(IMethod method, String vmsignature) {
            assertEquals(expected.remove(0), vmsignature);
          }
          public void done() {
          }
        };
      }
      public void done() {
      }
    }, MONITOR);
    assertEquals(Collections.EMPTY_LIST, expected);
  }
  
}
