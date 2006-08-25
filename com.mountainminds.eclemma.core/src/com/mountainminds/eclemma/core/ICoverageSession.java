/*
 * $Id$
 */
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * A coverage session is the result of a coverage run (or multiple merged runs)
 * or coverage data imported from an external source. It is an immutable
 * container for all data necessary to
 * 
 * <ul>
 * <li>provide coverage highlighting in Java editors,</li>
 * <li>populate the coverage view and</li>
 * <li>export coverage reports using Emma's reporting capabilities.</li>
 * </ul>
 * 
 * This interface is not intended to be implemented by clients.
 * 
 * @see CoverageTools#createCoverageSession(String, IInstrumentation[],
 *      IPath[], ILaunchConfiguration)
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ICoverageSession extends IAdaptable {

  /**
   * Returns a readable description for this coverage session.
   * 
   * @return readable description
   */
  public String getDescription();

  /**
   * Returns the array of instrumentation information objects for this session.
   * 
   * @return array of {@link IInstrumentation} used for this session
   */
  public IInstrumentation[] getInstrumentations();

  /**
   * Returns a list of absolute paths to the Emma coverage data files that
   * belong to this session.
   * 
   * @return list of absolute paths to the Emma coverage data files
   */
  public IPath[] getCoverageDataFiles();

  /**
   * If this session was the result of a Eclipse launch this method returns the
   * respective launch configutation. Otherwise <code>null</code> is returned.
   * 
   * @return launch configutation or <code>null</code>
   */
  public ILaunchConfiguration getLaunchConfiguration();

}