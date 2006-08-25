/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.jdt.core.IType;

/**
 * Callback used by {@link JavaElementsTraverser} to report traversed types.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ITypeVisitor {

  /**
   * Called for every type. The method may return a {@link IMethodVisitor} if
   * contained methods should also be traversed.
   * 
   * @param type
   *          Java model handle
   * @param vmname
   *          VM name of the type (e.g. <code>java/util/Map$Entry</code>)
   * @return visitor for enclosed methods or <code>null</code>
   */
  public IMethodVisitor visit(IType type, String vmname);

  /**
   * Called after all types have been visited.
   */
  public void done();

}
