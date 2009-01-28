/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Instances of {@link IClassFiles} describe a folder or archive containing
 * class files. Class files can be binary libraries or the compilation output of
 * source folders. These class file may be instrumented for coverage analysis.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public interface IClassFiles {

  /**
   * This method returns <code>true</code> if the class files described by this
   * instance are not compiled from a source folder.
   * 
   * @return <code>true</code> if the class files are a binary resource only
   */
  public boolean isBinary();

  /**
   * Returns the list of package fragment roots that belong to the class files.
   * The list may have more than one entry, if several source folders share the
   * same output location or if multiple library classpath entries point to the
   * same location.
   * 
   * @return list of package fragment roots
   */
  public IPackageFragmentRoot[] getPackageFragmentRoots();

  /**
   * Returns the workspace relative path to of the class files, either a
   * directory or a JAR file.
   * 
   * @return location of the class files
   */
  public IPath getLocation();

  /**
   * Determines the source locations that belong to the class files. If no
   * source is known for this class files a empty list is returned.
   * 
   * @return List of {@link ISourceLocation} objects
   * @throws JavaModelException
   *           May be thrown while querying the Java model
   */
  public ISourceLocation[] getSourceLocations() throws JavaModelException;

  /**
   * Instruments the class files contained in the folder or archive returned by
   * {@link #getLocation()}. If in-place mode is enabled, the original class
   * files or archives will be replaced.
   * 
   * @param inplace
   *          if <code>true</code> the original class files will be overwritten
   * @param monitor
   *          optional progress monitor
   * @return {@link IInstrumentation} object describing the instrumentation
   *         result
   * @throws CoreException
   *           thrown if the instrumentation process fails
   */
  public IInstrumentation instrument(boolean inplace, IProgressMonitor monitor)
      throws CoreException;

}
