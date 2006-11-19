/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import com.mountainminds.eclemma.core.analysis.ILineCoverage;

/**
 * ILineCoverage implementation.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class Lines extends Counter implements ILineCoverage {

  /**
   * We allways increase the size of the coverage array by multiples of this
   * constant. Larger values will require more memory, a lower value will lead
   * to repeated memory allocation and copy operations.
   */
  private static final int BLOCK_INCREMENT = 64;

  /** Array of flags for each line */
  private byte[] coverage = null;

  private int firstline = 0;

  private int lastline = 0;

  private int offset = 0;
  
  protected Lines() {
    super(0, 0);
  }

  private int getBlockAlignedOffset(int line) {
    return line - (line % BLOCK_INCREMENT);
  }

  private int getBlockAlignedSize(int size) {
    return size + BLOCK_INCREMENT - (size % BLOCK_INCREMENT);
  }

  /**
   * Updates the firstline and lastline property. If the given line falls
   * outside the coverage array the array is increased accordingly.
   * 
   * @param line
   *          line number to check
   */
  private void ensureCapacity(int line) {
    if (coverage == null) {
      firstline = line;
      lastline = line;
      offset = getBlockAlignedOffset(line);
      coverage = new byte[BLOCK_INCREMENT];
      return;
    }
    if (firstline > line) {
      firstline = line;
      if (offset > line) {
        int newoffset = getBlockAlignedOffset(line);
        byte[] newcoverage = new byte[coverage.length + offset - newoffset];
        System.arraycopy(coverage, 0, newcoverage, offset - newoffset,
            coverage.length);
        offset = newoffset;
        coverage = newcoverage;
      }
      return;
    }
    if (lastline < line) {
      lastline = line;
      if (line - offset >= coverage.length) {
        byte[] newcoverage = new byte[getBlockAlignedSize(line - offset)];
        System.arraycopy(coverage, 0, newcoverage, 0, coverage.length);
        coverage = newcoverage;
      }
    }
  }

  public void addLines(int lines[], boolean isCovered) {
    for (int i = 0; i < lines.length; i++) {
      int line = lines[i];
      ensureCapacity(line);
      int idx = line - offset;
      switch (coverage[idx]) {
      case NO_CODE:
        total++;
        if (isCovered)
          covered++;
        break;
      case NOT_COVERED:
        if (isCovered)
          covered++;
        break;
      }
      coverage[idx] |= isCovered ? FULLY_COVERED : NOT_COVERED;
    }
  }

  public Counter increment(int total, int covered) {
    throw new UnsupportedOperationException();
  }
  
  // ILineCoverage interface:
  
  public byte[] getCoverage() {
    return coverage;
  }

  public int getFirstLine() {
    return firstline;
  }

  public int getLastLine() {
    return lastline;
  }

  public int getOffset() {
    return offset;
  }

}
