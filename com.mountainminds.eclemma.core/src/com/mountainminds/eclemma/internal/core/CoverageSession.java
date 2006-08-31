/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

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
  private final boolean disposecoveragefiles;

  public CoverageSession(String description,
      IInstrumentation[] instrumentations, IPath[] coveragedatafiles,
      ILaunchConfiguration launchconfiguration, boolean disposecoveragefiles) {
    this.description = description;
    this.instrumentations = instrumentations;
    this.coveragedatafiles = coveragedatafiles;
    this.launchconfiguration = launchconfiguration;
    this.disposecoveragefiles = disposecoveragefiles;
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

  public void dispose() {
    if (disposecoveragefiles) {
      for (int i = 0; i < coveragedatafiles.length; i++) {
        coveragedatafiles[i].toFile().delete();
      }
    }
  }

}
