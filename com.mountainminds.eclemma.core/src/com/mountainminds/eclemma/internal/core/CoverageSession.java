/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.IInstrumentation;

/**
 * A {@link com.mountainminds.eclemma.core.ICoverageSession} implementation.
 * 
 * @author Marc R. Hoffmann
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

  public ICoverageSession merge(ICoverageSession other, String description) {
    List i = merge(instrumentations, other.getInstrumentations());
    List c = merge(coveragedatafiles, other.getCoverageDataFiles());
    return new CoverageSession(description, 
        (IInstrumentation[]) i.toArray(new IInstrumentation[i.size()]),
        (IPath[]) c.toArray(new IPath[c.size()]), launchconfiguration);
  }

  private List merge(Object[] arr1, Object[] arr2) {
    List l = new ArrayList(Arrays.asList(arr1));
    for (int i = 0; i < arr2.length; i++) {
      if (!l.contains(arr2[i])) {
        l.add(arr2[i]);
      }
    }
    return l;
  }

}
