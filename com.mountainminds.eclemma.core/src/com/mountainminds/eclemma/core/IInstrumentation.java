/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Instances of {@link IInstrumentation} describe a set of instrumented Java
 * classes. This interface is not intended to be implemented by clients,
 * instances are returned by
 * {@link IClassFiles#instrument(boolean, IProgressMonitor)}.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public interface IInstrumentation {

  /**
   * Returns the {@link IClassFiles} object that describe the source of this
   * instrumentation.
   * 
   * @return {@link IClassFiles} object
   */
  public IClassFiles getClassFiles();

  /**
   * Returns <code>true</code> if this instrumentation has been performed
   * in-place.
   * 
   * @return <code>true</code> if this instrumentation was in-place
   */
  public boolean isInplace();

  /**
   * Returns the absolute path to the instrumented class files. If in-place
   * instrumentation was performed, this corresponds to the location of the
   * original class files.
   * 
   * @return absolute path to the instrumented class files
   */
  public IPath getOutputLocation();

  /**
   * Returns the absolute path to the Emma meta data file that has been written
   * during instrumentation.
   * 
   * @return absolute path to the meta data file
   */
  public IPath getMetaDataFile();

}
