/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Reference to a location where Java source code is stored. This information is
 * used for report generation.
 */
public interface ISourceLocation {

  /**
   * Returns the absolute path to the source folder or archive.
   * 
   * @return absolute path to the source folder or archive
   */
  public IPath getPath();

  /**
   * Determines whether this source location points to an archive.
   * 
   * @return <code>true</code> if this source location is an archive
   */
  public boolean isArchive();

  /**
   * Returns the path within the source archive where package fragments are
   * located. An empty path indicates that packages are located at the root of
   * the source archive or folder. Returns a non-<code>null</code> value if and
   * only if {@link #isArchive()} returns <code>true</code>.
   * 
   * @return the path within the source archive, or <code>null</code> if not
   *         applicable
   */
  public IPath getRootPath();

  /**
   * If this source location points to an archive, calling this method will
   * extract the source files to an temporary local folder. The properties of
   * this descriptor object will be updated to the new location. If the source
   * files are already extracted this method does nothing.
   * 
   * @param monitor
   *          progress monitor or <code>null</code>
   * @throws CoreException
   *           Thrown if the archive content can't be extracted.
   */
  public void extract(IProgressMonitor monitor) throws CoreException;

}
