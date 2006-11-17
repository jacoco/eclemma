/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import com.mountainminds.eclemma.core.analysis.ICounter;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.core.analysis.ILineCoverage;

/**
 * IJavaElementCoverage implementation.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaElementCoverage implements IJavaElementCoverage {

  private Counter blockCounter;
  private Counter lineCounter;
  private Counter instructionsCounter;
  
  private final JavaElementCoverage parent;
  private final Lines lines;
  private final long modificationStamp;
  
  public JavaElementCoverage(JavaElementCoverage parent, boolean haslines, long stamp) {
    this.parent = parent;
    blockCounter = Counter.COUNTER_0_0;
    instructionsCounter = Counter.COUNTER_0_0;
    lineCounter = haslines ? null : Counter.COUNTER_0_0;
    modificationStamp = stamp;
    this.lines = haslines ? new Lines() : null;
  }

  public ICounter getBlockCounter() {
    return blockCounter;
  }

  public ICounter getLineCounter() {
    if (lines == null) {
      return lineCounter;
    } else {
      return lines; 
    }
  }

  public ICounter getInstructionCounter() {
    return instructionsCounter;
  }

  public ILineCoverage getLineCoverage() {
    return lines;
  }
  
  public long getResourceModificationStamp() {
    return modificationStamp;
  }
  
  public void addBlock(int instructions, int[] lines, boolean covered) {
    addBlock(instructions, lines, covered, 0, 0);
  }
  
  private void addBlock(int instructions, int[] lines, boolean covered, int totalLineDelta, int coveredLineDelta) {
    blockCounter = blockCounter.increment(1, covered ? 1 : 0);
    instructionsCounter = instructionsCounter.increment(instructions, covered ? instructions : 0);
    if (this.lines == null) {
      lineCounter = lineCounter.increment(totalLineDelta, coveredLineDelta);
      if (parent != null) {
        parent.addBlock(instructions, lines, covered, totalLineDelta, coveredLineDelta);
      }
    } else {
      if (lines != null) {
        long totalDelta = this.lines.getTotalCount();
        long coveredDelta = this.lines.getCoveredCount();
        this.lines.addLines(lines, covered);
        if (parent != null) {
          totalDelta = this.lines.getTotalCount() - totalDelta;
          coveredDelta = this.lines.getCoveredCount() - coveredDelta;
          parent.addBlock(instructions, lines, covered, (int) totalDelta, (int) coveredDelta);
        }
      }
    }
  }
  
}
