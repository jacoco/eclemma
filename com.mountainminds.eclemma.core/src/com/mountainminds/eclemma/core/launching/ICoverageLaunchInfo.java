/*
 * $Id$
 */
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
