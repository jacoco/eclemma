/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * API for importing sessions. This interface is not intended to be implemented
 * by clients. Use {@link CoverageTools#getImporter()} to get an instance.
 */
public interface ISessionImporter {

  /**
   * Sets the description for the imported session.
   * 
   * @param description
   *          textual description of the session
   */
  public void setDescription(String description);

  /**
   * Sets the source for execution data.
   * 
   * @param source
   *          execution data source
   */
  public void setExecutionDataSource(IExecutionDataSource source);

  /**
   * Sets the set of package fragment roots that should be considered for
   * coverage analysis.
   * 
   * @param scope
   *          scope for analysis
   */
  public void setScope(Set<IPackageFragmentRoot> scope);

  /**
   * Specifies whether the original file should be copied while importing.
   * Otherwise the coverage file a referenced only.
   * 
   * @param copy
   *          flag, whether the coverage file should be copied
   */
  public void setCopy(boolean copy);

  /**
   * A call to this method triggers the actual import process.
   * 
   * @param monitor
   *          progress monitor
   * @throws CoreException
   *           if something goes wrong during export
   */
  public void importSession(IProgressMonitor monitor) throws CoreException;

}
