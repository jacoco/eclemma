/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.analysis;

/**
 * Coverage data for a Java model element. This interface is not intended to be
 * implemented or extended by clients.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface IJavaElementCoverage {

  /**
   * Returns line coverage details if the corresponding Java element is a
   * compilation unit, type or method.
   * 
   * @return {@link ILineCoverage} object or <code>null</code>
   */
  public ILineCoverage getLineCoverage();

  /**
   * Returns the counter for blocks.
   * 
   * @return counter for blocks
   */
  public ICounter getBlockCounter();

  /**
   * Returns the counter for lines.
   * 
   * @return counter for lines
   */
  public ICounter getLineCounter();

  /**
   * Returns the counter for methods.
   * 
   * @return counter for methods
   */
  public ICounter getMethodCounter();
  
  /**
   * Returns the counter for types.
   * 
   * @return counter for types
   */
  public ICounter getTypeCounter();
  
  /**
   * Returns the counter for instructions.
   * 
   * @return counter for instructions
   */
  public ICounter getInstructionCounter();

  /**
   * Returns the modification stamp of the underlying resource. This can be used
   * by clients to check whether the current version of the respective resource
   * is identical to the version that was used for the coverage session.
   * 
   * @return modification stamp of the underlying resource
   */
  public long getResourceModificationStamp();

}
