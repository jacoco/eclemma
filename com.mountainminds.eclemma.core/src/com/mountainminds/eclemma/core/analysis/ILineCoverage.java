/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.analysis;

/**
 * For all elements that sit in a compilation unit and for compilation units
 * itself individual line coverage may be described by this interface. This
 * interface is not intended to be implemented or extended by clients.
 * 
 * @see IJavaElementCoverage#getLineCoverage()
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ILineCoverage extends ICounter {

  /** Flag for lines that do not contain code (value is 0x00). */
  public static final byte NO_CODE = 0x00;

  /** Flag for lines that are not covered (value is 0x01). */
  public static final byte NOT_COVERED = 0x01;

  /** Flag for lines that are fully covered (value is 0x02). */
  public static final byte FULLY_COVERED = 0x02;

  /** Flag for lines that are partly covered (value is 0x03). */
  public static final byte PARTLY_COVERED = NOT_COVERED | FULLY_COVERED;

  /**
   * The number of the first line coverage information is available for.
   * 
   * @return number of the first line
   */
  public int getFirstLine();

  /**
   * The number of the last line coverage information is available for.
   * 
   * @return number of the last line
   */
  public int getLastLine();

  /**
   * Returns the line number of the first entry in the array returned by
   * {@link #getCoverage()}.
   * 
   * @return offset of the coverage data array
   */
  public int getOffset();

  /**
   * Returns an array of coverage flags defined as constants in this interface.
   * The first item of the returned array corresponds to the line returned by
   * {@link #getOffset()}. Note that the length of the array may superceed the
   * actual source file length.
   * 
   * @see #NO_CODE
   * @see #NOT_COVERED
   * @see #PARTLY_COVERED
   * @see #FULLY_COVERED
   * 
   * @return array of coverage flags
   */
  public byte[] getCoverage();

}
