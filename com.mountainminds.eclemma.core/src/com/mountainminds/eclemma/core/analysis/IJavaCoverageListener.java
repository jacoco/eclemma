/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.core.analysis;

/**
 * Callback interface implemented by clients that want to be informed, when the
 * current Java model coverage has changes.
 */
public interface IJavaCoverageListener {

  /**
   * Called when the current coverage data has changed.
   */
  public void coverageChanged();

}
