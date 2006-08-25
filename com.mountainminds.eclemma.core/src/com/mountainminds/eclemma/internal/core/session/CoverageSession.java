/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core.session;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.IInstrumentation;

/**
 * A {@link com.mountainminds.eclemma.core.ICoverageSession} implementation.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageSession extends PlatformObject implements ICoverageSession {

  private final String description;
  private final IInstrumentation[] instrumentations;
  private final IPath[] coveragedatafiles;
  private final ILaunchConfiguration launchconfiguration;

  public CoverageSession(String description,
      IInstrumentation[] instrumentations, IPath[] coveragedatafiles,
      ILaunchConfiguration launchconfiguration) {
    this.description = description;
    this.instrumentations = instrumentations;
    this.coveragedatafiles = coveragedatafiles;
    this.launchconfiguration = launchconfiguration;
  }

  // ICoverageSession implementation

  public String getDescription() {
    return description;
  }

  public IInstrumentation[] getInstrumentations() {
    return instrumentations;
  }

  public IPath[] getCoverageDataFiles() {
    return this.coveragedatafiles;
  }

  public ILaunchConfiguration getLaunchConfiguration() {
    return launchconfiguration;
  }

}
