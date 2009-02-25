/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package typetraverser;

/**
 * Test type for TypeTraverser.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class Samples {

  public static void doit() {
  }

  // Nested class with anonymous inner class

  static class InnerA {
    public void test() {
      new Runnable() {
        public void run() {
          doit();
        }
      };
    }
  }

  // Anonymous class with named inner class:

  static {
    new Runnable() {
      class InnerB {
        public void test() {
          doit();
        }
      }

      public void run() {
        new InnerB().test();
      }
    }.run();
  }

  // Another anonymous class with named inner class:

  static {
    new Runnable() {
      class InnerC {
        public void test() {
          doit();
        }
      }

      public void run() {
        new InnerC().test();
      }
    }.run();
  }

  // Member Initializer with anonymous class:

  public Object member1 = new Runnable() {
    public void run() {
      doit();
    }
  };

  // Method with anonymous class:

  public void test() {
    Runnable r = new Runnable() {
      public void run() {
        doit();
      }
    };
    r.run();
  };

  public static void main(String[] args) {

  }

}
