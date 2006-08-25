/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.jdt.core.IMethod;

/**
 * Callback used by {@link JavaElementsTraverser} to report methods within a
 * type.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface IMethodVisitor {

  /**
   * Called for every method.
   * 
   * @param method
   *          Java model handle
   * @param vmsignature
   *          VM signature (e.g. <code>toString()Ljava/lang/String;</code>)
   */
  public void visit(IMethod method, String vmsignature);

  /**
   * Called after all methods have been visited.
   */
  public void done();

}
