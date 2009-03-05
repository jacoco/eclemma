/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id:  $
 ******************************************************************************/
package signatureresolver;

import java.util.Iterator;

/**
 * Collections of methods with different Signatures.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class Samples<R, S extends Comparable, T extends Thread & Iterator> {
  
  class Inner {
    
    /* (Ljava/lang/Comparable;)V */
    void test_innerClassTypeVariable(S param) {
    }
    
  }
  
  public static class Generic<V> {
  }
 
  /* ()V */
  void noArgs() {
  }
  
  /* (I)V */
  void method_int(int i) {
  }

  /* (IIII)V */
  void method_int_int_int(int i, int j, int k) {
  }
  
  /* (S)V */
  void method_short(short s) {
  }
  
  /* ([C)V */
  void method_charArr(char[] i) {
  }
  
  /* ([[[B)V */
  void method_byteArr3(byte[][][] i) {
  }
  
  /* (Ljava/lang/String;)V */
  void method_String(String s) {
  } 

  /* (Lsignatureresolver/Samples$Inner;)V */
  void method_Inner(Inner i) {
  }
  
  /* (Ljava/util/Date;)V */
  void method_javautilDate(java.util.Date d) {
  }

  /* (Ljava/util/Map$Entry;)V */
  void method_javautilMapEntry(java.util.Map.Entry e) {
  }
  
  /* (Lsignatureresolver/Samples$Generic;)V */
  void method_parameterizedType(Generic<Integer> i) {
  }
  
  /* (Lsignatureresolver/Samples$Generic;)V */
  void method_parameterizedParameterizedType(Generic<Generic<?>> param) {
  }
  
  /* (Lsignatureresolver/Samples$Generic;)V */
  void method_parameterizedTypeWildcard(Generic<?> i) {
  }

  /* (Lsignatureresolver/Samples$Generic;)V */
  void method_parameterizedTypeExtends(Generic<? extends Number> i) {
  }

  /* (Lsignatureresolver/Samples$Generic;)V */
  void method_parameterizedTypeSuper(Generic<? super Number> i) {
  }
  
  /* (Ljava/lang/Object;)V */
  <E> void method_methodTypeVariable(E param) {
  }
  
  /* (Ljava/lang/Thread;)V */
  <E extends Thread> void method_methodTypeVariableExtends(E param) {
  }
  
  /* (Ljava/lang/Number;)V */
  <E extends Number & Runnable> void method_methodTypeVariableExtends2(E param) {
  }
  
  /* (Ljava/lang/Object;)V */
  void method_classTypeVariable(R param) {
  }
  
  /* (Ljava/lang/Comparable;)V */
  void method_classTypeVariableExtends(S param) {
  }
  
  /* (Ljava/lang/Thread;)V */
  void method_classTypeVariableExtends2(T param) {
  }

}
