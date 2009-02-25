/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id:  $
 ******************************************************************************/
package methodlocator;

import java.util.Date;

/**
 * Collections of methods with different Signatures.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: 13 $
 */
public class Samples {

  /* <init>()V */
  Samples() {
  }
  
  /* <init>(Ljava/lang/String;)V */
  Samples(String param) {
  }

  /* <init>(I)V */
  Samples(int param) {
  }
  
  /* (Ljava/lang/String;)V */
  void m1(String s) {
  } 

  /* (Ljava/lang/Integer;)V */
  void m2(Integer i) {
  } 

  /* (Ljava/lang/Number;)V */
  void m2(Number n) {
  } 
  
}
