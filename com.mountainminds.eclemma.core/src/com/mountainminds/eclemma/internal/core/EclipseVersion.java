/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * Some constants to behave version specific in some situations. Unfortunately
 * this is necessary as EclEmma works on multiple Eclipse versions and relies on
 * internal implementation details. 
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class EclipseVersion {

  public static final Version CURRENT;
  
  public static final Version V320 = new Version("3.2.0"); //$NON-NLS-1$
  
  /**
   * Checks whether the current platform version is greater or equal than the
   * given version.
   * 
   * @param version  version to compare to
   * @return  true, if the current version is greater or equal than the given one
   */
  public static boolean isGreaterOrEqualTo(Version version) {
    return CURRENT.compareTo(version) >= 0;
  }
  
  static {
    Bundle rt = Platform.getBundle(Platform.PI_RUNTIME);
    String version = (String) rt.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
    CURRENT = new Version(version); 
  }
  
}
