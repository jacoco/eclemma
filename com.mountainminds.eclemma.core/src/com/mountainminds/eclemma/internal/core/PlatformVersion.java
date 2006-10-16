/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.osgi.framework.Bundle;

/**
 * Some constants to behave version specific in some situations. Unfortunately
 * this is necessary as EclEmma works on multiple Eclipse versions and relies on
 * internal implementation details. 
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class PlatformVersion {

  public static final PluginVersionIdentifier CURRENT;
  
  public static final PluginVersionIdentifier V320 = new PluginVersionIdentifier("3.2.0"); //$NON-NLS-1$
  
  static {
    Bundle rt = Platform.getBundle(Platform.PI_RUNTIME);
    String version = (String) rt.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
    CURRENT = new PluginVersionIdentifier(version); 
  }
  
}
