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
 * ICounter implementations. Implementing a factory pattern allows to share
 * counter instances.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public abstract class Counter implements ICounter {

  /** Max counter value for which singletons are created */
  private static final int SINGLETON_LIMIT = 10;

  private static final Counter[][] SINGLETONS = new Counter[SINGLETON_LIMIT + 1][];

  static {
    for (int i = 0; i <= SINGLETON_LIMIT; i++) {
      SINGLETONS[i] = new Counter[i + 1];
      for (int j = 0; j <= i; j++)
        SINGLETONS[i][j] = new Fix(i, j);
    }
  }

  /** Constant for Counter with 0/0 values. */
  public static final Counter COUNTER_0_0 = SINGLETONS[0][0];

  /**
   * Mutable version of the counter.
   */
  private static class Var extends Counter {
    public Var(long total, long covered) {
      super(total, covered);
    }

    public Counter increment(int total, int covered) {
      this.total += total;
      this.covered += covered;
      return this;
    }
  }

  /**
   * Immutable version of the counter.
   */
  private static class Fix extends Counter {
    public Fix(long total, long covered) {
      super(total, covered);
    }

    public Counter increment(int total, int covered) {
      return getInstance(this.total + total, this.covered + covered);
    }
  }

  /**
   * Factory method to retrieve a counter with the given number of items.
   * 
   * @param total
   *          total number of items
   * @param covered
   *          covered number of items
   * @return counter instance
   */
  public static Counter getInstance(long total, long covered) {
    if (total <= SINGLETON_LIMIT && covered <= total) {
      return SINGLETONS[(int) total][(int) covered];
    } else {
      return new Var(total, covered);
    }
  }

  protected long total;
  protected long covered;

  protected Counter(long total, long covered) {
    this.total = total;
    this.covered = covered;
  }

  /**
   * Returns a counter with incremented values. It is up to the implementation
   * whether this conter instance is modified or a new instance is returned.
   * 
   * @param total
   *          number of additional total items
   * @param covered
   *          number of additional covered items
   * @return counter instance with incremented values
   */
  public abstract Counter increment(int total, int covered);

  // ICounter implementation

  public long getTotalCount() {
    return total;
  }

  public long getCoveredCount() {
    return covered;
  }

  public long getMissedCount() {
    return total - covered;
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
