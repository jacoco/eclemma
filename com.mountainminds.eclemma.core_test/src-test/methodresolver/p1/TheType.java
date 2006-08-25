/*
 * $Id: $
 */
package methodresolver.p1;

import methodresolver.p2.*;

/**
 * Test project for unit tests.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class TheType {

  class Inner { }
  
  /* test 0 */
  TheType() { }
  
  /* test 1 */
  public void meth1() { }
  
  /* test 2 */
  public Object meth2(Object arg) { return null; }
  
  /* test 3 */
  public java.lang.Object meth3(java.lang.Object arg) { return null; }
  
  /* test 4 */
  public String meth4(String arg) { return null; }
  
  /* test 5 */
  public java.lang.String meth5(java.lang.String arg) { return null; }
  
  /* test 6 */
  public void meth6(Object[] arg) { }
  
  /* test 7 */
  public void meth7(java.lang.Object[] arg) { }
  
  /* test 8 */
  public AnotherTypeInAnotherPackage meth8() { return null; }
  
}
