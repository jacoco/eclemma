/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.mountainminds.eclemma.core.IInstrumentation;

/**
 * TODO
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ICoverageLaunchInfo {
  
  public IPath getCoverageFile();
  
  public boolean getImportOnExit();

  public void instrument(IProgressMonitor monitor, boolean inplace) throws CoreException;

  public IInstrumentation[] getInstrumentations();
  
  public IInstrumentation getInstrumentation(String originalpath);
  
  public IPath getPropertiesJARFile();
  
  public void dispose();

}
