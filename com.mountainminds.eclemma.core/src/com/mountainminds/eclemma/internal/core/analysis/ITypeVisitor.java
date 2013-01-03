/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Callback used by {@link TypeTraverser} to report traversed types.
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
   * Called for every compilation unit.
   * 
   * @param unit
   *          Java model handle
   */
  public void visit(ICompilationUnit unit) throws JavaModelException;

  /**
   * Called for every class file.
   * 
   * @param unit
   *          Java model handle
   */
  public void visit(IClassFile classfile) throws JavaModelException;

}
