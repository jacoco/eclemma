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
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;

/**
 * Common interface for all sources of execution data.
 */
public interface IExecutionDataSource {

  /**
   * Emits all stored execution data in the given visitors.
   * 
   * @param executionDataVisitor
   *          visitor for execution data
   * @param visitor
   *          for session information
   */
  public abstract void accept(IExecutionDataVisitor executionDataVisitor,
      ISessionInfoVisitor sessionInfoVisitor) throws CoreException;

}