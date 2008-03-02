/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * API for importing sessions. This interface is not intended to be implemented
 * by clients. Use {@link CoverageTools#getImporter()} to get an instance.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
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
   * Sets the path to the coverage file to import.
   * 
   * @param file
   *          coverage file to import
   */
  public void setCoverageFile(String file);

  /**
   * Sets the list of class files that should be considered for coverage
   * analysis.
   * 
   * @param classfiles
   *          class files for analysis
   */
  public void setClassFiles(IClassFiles[] classfiles);

  /**
   * Specifies whether the original file should be copied while importing.
   * Otherwise the coverage file a referenced only.
   * 
   * @param copy
   *          flag, whether the coverage file should be copied
   */
  public void setCopy(boolean copy);

  /**
   * Imported session files might come with their own meta data. Here we can set
   * whether this data should be used. Otherwise Meta data is extracted from the
   * local class files.
   * 
   * @param flag
   *          true, if external Meta data should be used
   */
  public void setUseImportedMetaData(boolean flag);
  
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
