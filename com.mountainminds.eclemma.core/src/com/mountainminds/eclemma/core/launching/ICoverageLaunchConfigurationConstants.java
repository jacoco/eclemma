/*
 * $Id$
 */
package com.mountainminds.eclemma.core.launching;

import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Constants for coverage specific launch configuration entries. 
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public interface ICoverageLaunchConfigurationConstants {

  /**
   * This boolean attribute specifies, whether class files should be
   * instrumented in-place. If unspecified inplace instrumentation is not
   * performed.
   */
  public static final String ATTR_INPLACE_INSTRUMENTATION = EclEmmaCorePlugin.ID
      + ".INPLACE_INSTRUMENTATION"; //$NON-NLS-1$

  /**
   * List of workspace relative paths for instrumentation. If unspecified all
   * output locations of source folders will be instrumented. 
   */
  public static final String ATTR_INSTRUMENTATION_PATHS = EclEmmaCorePlugin.ID
      + ".INSTRUMENTATION_PATHS"; //$NON-NLS-1$
  
}
