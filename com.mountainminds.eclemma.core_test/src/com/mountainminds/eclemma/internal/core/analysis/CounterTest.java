/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import junit.framework.TestCase;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CounterTest extends TestCase {

  public void testInit1() {
    Counter c = new Counter();
    assertEquals(0, c.getTotalCount());
    assertEquals(0, c.getCoveredCount());
  }

  public void testInit2() {
    Counter c = new Counter(33, 15);
    assertEquals(33, c.getTotalCount());
    assertEquals(15, c.getCoveredCount());
  }
  
  public void testIncrement() {
    Counter c = new Counter(11, 5);
    c.increment(7, 3);
    assertEquals(18, c.getTotalCount());
    assertEquals(8, c.getCoveredCount());
  }
  
  public void testGetRatio1() {
    Counter c = new Counter(20, 10);
    assertEquals(0.5, c.getRatio(), 0.0);
  }

  public void testGetRatio2() {
    Counter c = new Counter(20, 0);
    assertEquals(0.0, c.getRatio(), 0.0);
  }

  public void testGetRatio3() {
    Counter c = new Counter(0, 20);
    assertEquals(Double.POSITIVE_INFINITY, c.getRatio(), 0.0);
  }

  public void testGetRatio4() {
    Counter c = new Counter(0, 0);
    assertTrue(Double.isNaN(c.getRatio()));
  }

  public void testCompareTo1() {
    Counter c1 = new Counter(20, 10);
    Counter c2 = new Counter(30, 10);
    assertTrue(c1.compareTo(c2) > 0);
  }

  public void testCompareTo2() {
    Counter c1 = new Counter(20, 10);
    Counter c2 = new Counter(30, 15);
    assertEquals(0, c1.compareTo(c2));
  }

  public void testEquals1() {
    Counter c1 = new Counter(300, 123);
    Counter c2 = new Counter(300, 123);
    assertEquals(c1, c2);
  }
  
  public void testEquals2() {
    Counter c1 = new Counter(300, 123);
    Counter c2 = new Counter(400, 123);
    assertFalse(c1.equals(c2));
  }

  public void testEquals3() {
    Counter c1 = new Counter(300, 123);
    Counter c2 = new Counter(300, 124);
    assertFalse(c1.equals(c2));
  }

  public void testEquals4() {
    Counter c = new Counter(300, 123);
    assertFalse(c.equals(new Integer(123)));
  }

  public void testHashCode1() {
    Counter c1 = new Counter(300, 123);
    Counter c2 = new Counter(300, 123);
    assertEquals(c1.hashCode(), c2.hashCode());
  }

  public void testHashCode2() {
    Counter c1 = new Counter(300, 123);
    Counter c2 = new Counter(400, 123);
    assertFalse(c1.hashCode() == c2.hashCode());
  }

  public void testHashCode3() {
    Counter c1 = new Counter(300, 123);
    Counter c2 = new Counter(300, 124);
    assertFalse(c1.hashCode() == c2.hashCode());
  }
  
  public void testToString() {
    Counter c = new Counter(300, 123);
    assertEquals("Counter[123/300]", c.toString());
  }
  
}
