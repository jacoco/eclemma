/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import com.mountainminds.eclemma.core.analysis.ILineCoverage;
import com.mountainminds.eclemma.internal.core.analysis.Lines;

import junit.framework.TestCase;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class LinesTest extends TestCase {

  protected Lines lines;
  
  protected void setUp() throws Exception {
    lines = new Lines();
  }
  
  public void testContinuousExpansionRight() {
    int start = 123;
    int end = 2345;
    for (int i = start; i <= end; i++) {
      lines.addLines(new int[] {i}, false);
      assertEquals("first line", start, lines.getFirstLine());
      assertEquals("last line", i, lines.getLastLine());
    }
  }

  public void testContinuousExpansionLeft() {
    int start = 123;
    int end = 2345;
    for (int i = end; i >= start; i--) {
      lines.addLines(new int[] {i}, false);
      assertEquals("first line", i, lines.getFirstLine());
      assertEquals("last line", end, lines.getLastLine());
    }
  }
  
  public void testStepExpansionRight() {
    int start = 123;
    int end = 23456;
    for (int i = start; i <= end; i += 733) {
      lines.addLines(new int[] {i}, false);
      assertEquals("first line", start, lines.getFirstLine());
      assertEquals("last line", i, lines.getLastLine());
    }
  }
  
  public void testStepExpansionLeft() {
    int start = 123;
    int end = 23456;
    for (int i = end; i >= start; i -= 733) {
      lines.addLines(new int[] {i}, false);
      assertEquals("first line", i, lines.getFirstLine());
      assertEquals("last line", end, lines.getLastLine());
    }
  }
  
  public void testLineCoveredCovered() {
    lines.addLines(new int[] {3}, true);
    lines.addLines(new int[] {3}, true);
    assertEquals("total", 1, lines.getTotalCount());
    assertEquals("covered", 1, lines.getCoveredCount());
    assertEquals("line 3", ILineCoverage.FULLY_COVERED, lines.getCoverage()[lines.getOffset() + 3]);
  }
  
  public void testLineCoveredUncovered() {
    lines.addLines(new int[] {3}, true);
    lines.addLines(new int[] {3}, false);
    assertEquals("total", 1, lines.getTotalCount());
    assertEquals("covered", 1, lines.getCoveredCount());
    assertEquals("line 3", ILineCoverage.PARTLY_COVERED, lines.getCoverage()[lines.getOffset() + 3]);
  }
  
  public void testLineUncoveredCovered() {
    lines.addLines(new int[] {3}, false);
    lines.addLines(new int[] {3}, true);
    assertEquals("total", 1, lines.getTotalCount());
    assertEquals("covered", 1, lines.getCoveredCount());
    assertEquals("line 3", ILineCoverage.PARTLY_COVERED, lines.getCoverage()[lines.getOffset() + 3]);
  }
  
  public void testLineUncoveredUncovered() {
    lines.addLines(new int[] {3}, false);
    lines.addLines(new int[] {3}, false);
    assertEquals("total", 1, lines.getTotalCount());
    assertEquals("covered", 0, lines.getCoveredCount());
    assertEquals("line 3", ILineCoverage.NOT_COVERED, lines.getCoverage()[lines.getOffset() + 3]);
  }
  
  public void testLineCoveredUncoveredCovered() {
    lines.addLines(new int[] {3}, true);
    lines.addLines(new int[] {3}, false);
    lines.addLines(new int[] {3}, true);
    assertEquals("total", 1, lines.getTotalCount());
    assertEquals("covered", 1, lines.getCoveredCount());
    assertEquals("line 3", ILineCoverage.PARTLY_COVERED, lines.getCoverage()[lines.getOffset() + 3]);
  }
  
  public void testLineUncoveredCoveredUncovered() {
    lines.addLines(new int[] {3}, false);
    lines.addLines(new int[] {3}, true);
    lines.addLines(new int[] {3}, false);
    assertEquals("total", 1, lines.getTotalCount());
    assertEquals("covered", 1, lines.getCoveredCount());
    assertEquals("line 3", ILineCoverage.PARTLY_COVERED, lines.getCoverage()[lines.getOffset() + 3]);
  }
  
  /* Line   A  B  C  Coverage
   * 10     +           +
   * 11     +  +        +
   * 12     +  +  +     +
   * 13        +  +     +
   * 14           +     +
   */
  public void testScenario1() {
    lines.addLines(new int[] {10, 11, 12}, true);
    lines.addLines(new int[] {11, 12, 13}, true);
    lines.addLines(new int[] {12, 13, 14}, true);
    assertEquals("total", 5, lines.getTotalCount());
    assertEquals("covered", 5, lines.getCoveredCount());
    byte[] c = lines.getCoverage();
    assertEquals("line 10", ILineCoverage.FULLY_COVERED, c[lines.getOffset() + 10]);
    assertEquals("line 11", ILineCoverage.FULLY_COVERED, c[lines.getOffset() + 11]);
    assertEquals("line 12", ILineCoverage.FULLY_COVERED, c[lines.getOffset() + 12]);
    assertEquals("line 13", ILineCoverage.FULLY_COVERED, c[lines.getOffset() + 13]);
    assertEquals("line 14", ILineCoverage.FULLY_COVERED, c[lines.getOffset() + 14]);
  }

  /* Line   A  B  C  Coverage
   * 10     +           +
   * 11     +  +        +
   * 12     +  +  -     ?
   * 13        +  -     ?
   * 14           -     -
   */
  public void testScenario2() {
    lines.addLines(new int[] {10, 11, 12}, true);
    lines.addLines(new int[] {11, 12, 13}, true);
    lines.addLines(new int[] {12, 13, 14}, false);
    assertEquals("total", 5, lines.getTotalCount());
    assertEquals("covered", 4, lines.getCoveredCount());
    byte[] c = lines.getCoverage();
    assertEquals("line 10", ILineCoverage.FULLY_COVERED,  c[lines.getOffset() + 10]);
    assertEquals("line 11", ILineCoverage.FULLY_COVERED,  c[lines.getOffset() + 11]);
    assertEquals("line 12", ILineCoverage.PARTLY_COVERED, c[lines.getOffset() + 12]);
    assertEquals("line 13", ILineCoverage.PARTLY_COVERED, c[lines.getOffset() + 13]);
    assertEquals("line 14", ILineCoverage.NOT_COVERED,    c[lines.getOffset() + 14]);
  }
  
  /* Line   A  B  C  Coverage
   * 10     -           -
   * 11     -  +        ?
   * 12     -  +  -     ?
   * 13        +  -     ?
   * 14           -     -
   */
  public void testScenario3() {
    lines.addLines(new int[] {10, 11, 12}, false);
    lines.addLines(new int[] {11, 12, 13}, true);
    lines.addLines(new int[] {12, 13, 14}, false);
    assertEquals("total", 5, lines.getTotalCount());
    assertEquals("covered", 3, lines.getCoveredCount());
    byte[] c = lines.getCoverage();
    assertEquals("line 10", ILineCoverage.NOT_COVERED,    c[lines.getOffset() + 10]);
    assertEquals("line 11", ILineCoverage.PARTLY_COVERED, c[lines.getOffset() + 11]);
    assertEquals("line 12", ILineCoverage.PARTLY_COVERED, c[lines.getOffset() + 12]);
    assertEquals("line 13", ILineCoverage.PARTLY_COVERED, c[lines.getOffset() + 13]);
    assertEquals("line 14", ILineCoverage.NOT_COVERED,    c[lines.getOffset() + 14]);
  }
  
  public void testIncrement() {
    try {
      lines.increment(2, 1);
      fail("Must not work.");
    } catch (UnsupportedOperationException ex) { }
    
  }
  
}
