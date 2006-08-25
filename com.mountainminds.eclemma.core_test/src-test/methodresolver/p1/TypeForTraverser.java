/*
 * $Id$
 */
package methodresolver.p1;

/**
 * Test project for unit tests.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class TypeForTraverser {
  
  static {
    new Runnable() {
      public void run() {
        System.out.println("do something");
      }
    };
  }
  
  public Object member1 =  new Runnable() {
    public void run() {
      System.out.println("do something");
    }
  };
  
  public TypeForTraverser() {
    System.out.println("do something");
  }

  public void method1() {
    Runnable r = new Runnable() {
      public void run() {
        System.out.println("do something");
      }
    };
    r.run();
  };

}
