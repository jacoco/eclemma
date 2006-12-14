/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.jdt.core.IType;

/**
 * Callback used by {@link TypeTraverser} to report traversed types.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ITypeVisitor {

  /**
   * Called for every type.
   * 
   * @param type
   *          Java model handle
   * @param binaryname
   *          VM name of the type (e.g. <code>java/util/Map$Entry</code>)
   */
  public void visit(IType type, String binaryname);

  /**
   * Called after all types have been visited.
   */
  public void done();

}
