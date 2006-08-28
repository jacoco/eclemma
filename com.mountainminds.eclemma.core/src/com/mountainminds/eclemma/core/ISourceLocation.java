/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Reference to a location where Java source code is stored. This information
 * is used for Emma report generation.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
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
   * the source archive or folder. Returns a non-<code>null</code> value if
   * and only if {@link #isArchive()} returns <code>true</code>.
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
