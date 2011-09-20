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
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.internal.ui.actions.RelaunchLastAction;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Action to re-launch the last launch in coverage mode.
 */
public class CoverageLastAction extends RelaunchLastAction {

  @Override
  public String getMode() {
    return CoverageTools.LAUNCH_MODE;
  }

  @Override
  public String getLaunchGroupId() {
    return EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP;
  }

  @Override
  protected String getText() {
    return UIMessages.CoverageLastAction_label;
  }

  @Override
  protected String getTooltipText() {
    return UIMessages.CoverageLastAction_label;
  }

  @Override
  protected String getDescription() {
    return UIMessages.CoverageLastAction_label;
  }

  @Override
  protected String getCommandId() {
    return "com.mountainminds.eclemma.ui.commands.CoverageLast"; //$NON-NLS-1$
  }

}
