/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.ui.actions.ContextualLaunchAction;

import com.mountainminds.eclemma.core.CoverageTools;

/**
 * An action delegate for the "Coverage As" context menu entry.
 */
public class CoverageContextualLaunchAction extends ContextualLaunchAction {

  public CoverageContextualLaunchAction() {
    super(CoverageTools.LAUNCH_MODE);
  }

}
