/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
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
   * List of Java element ids pointing to package fragment roots that form the
   * scope of a coverage launch. If unspecified a default scope is calculated
   * based on the launch type and preferences..
   */
  public static final String ATTR_SCOPE_IDS = EclEmmaCorePlugin.ID
      + ".SCOPE_IDS"; //$NON-NLS-1$

}
