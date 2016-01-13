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
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.ui.actions.LaunchShortcutsAction;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * Action implementation for "Coverage as" menu.
 */
public class CoverageAsAction extends LaunchShortcutsAction {

  public CoverageAsAction() {
    super(EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP);
  }

}
