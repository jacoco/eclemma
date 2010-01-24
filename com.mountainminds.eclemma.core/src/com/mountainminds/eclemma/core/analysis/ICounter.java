/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.analysis;

/**
 * A counter holds the total and the covered number of particular items like
 * classes, methods, blocks or instructions. Counters provide canonical
 * comparison of their covered/total ratios. This interface is not intended to
 * be implemented or extended by clients.
 * 
 * @see IJavaElementCoverage#getLineCounter()
 * @see IJavaElementCoverage#getBlockCounter()
 * @see IJavaElementCoverage#getInstructionCounter()
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public interface ICounter extends Comparable {

  /**
   * Returns the total count number of instrumented items.
   * 
   * @return total count of instrumented items
   */
  public long getTotalCount();

  /**
   * Returns the total count number of covered items.
   * 
   * @return total count of covered items
   */
  public long getCoveredCount();

  /**
   * Returns the total count number of missed items.
   * 
   * @return total count of missed items
   */
  public long getMissedCount();

  /**
   * Calculates the ratio of covered to total count items. If total count items
   * is 0 this method returns NaN.
   * 
   * @return ratio of covered to total count items
   */
  public double getRatio();

}
