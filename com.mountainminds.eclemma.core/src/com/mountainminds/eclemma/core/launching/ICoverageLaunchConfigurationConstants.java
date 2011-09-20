/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Constants for coverage specific launch configuration entries.
 */
public interface ICoverageLaunchConfigurationConstants {

  /**
   * List of workspace relative paths for instrumentation. If unspecified all
   * output locations of source folders will be instrumented.
   */
  public static final String ATTR_INSTRUMENTATION_PATHS = EclEmmaCorePlugin.ID
      + ".INSTRUMENTATION_PATHS"; //$NON-NLS-1$

}
