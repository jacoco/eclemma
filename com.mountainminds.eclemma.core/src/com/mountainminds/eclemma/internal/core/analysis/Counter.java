/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import com.mountainminds.eclemma.core.analysis.ICounter;

/**
 * ICounter implementations.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class Counter implements ICounter {

  protected long total;
  protected long covered;

  public Counter() {
    this(0, 0);
  }

  public Counter(int total, int covered) {
    this.total = total;
    this.covered = covered;
  }

  public void increment(int total, int covered) {
    this.total += total;
    this.covered += covered;
  }

  // ICounter implementation

  public long getTotalCount() {
    return total;
  }

  public long getCoveredCount() {
    return covered;
  }

  public double getRatio() {
    return (double) covered / (double) total;
  }

  public int compareTo(Object obj) {
    ICounter counter = (ICounter) obj;
    return Double.compare(getRatio(), counter.getRatio());
  }

  public boolean equals(Object obj) {
    if (obj instanceof ICounter) {
      ICounter counter = (ICounter) obj;
      return getTotalCount() == counter.getTotalCount()
          && getCoveredCount() == counter.getCoveredCount();
    } else {
      return false;
    }
  }

  public int hashCode() {
    long t = getTotalCount();
    long c = 17 * getCoveredCount();
    return (int) (t ^ (t >>> 32) ^ c ^ (c >>> 32));
  }

  public String toString() {
    StringBuffer b = new StringBuffer("Counter["); //$NON-NLS-1$
    b.append(getCoveredCount());
    b.append('/').append(getTotalCount());
    b.append(']');
    return b.toString();
  }

}
