/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.internal.ui.actions.RelaunchLastAction;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageLastAction extends RelaunchLastAction  {

  public String getMode() {
    return CoverageTools.LAUNCH_MODE;
  }   
    
  public String getLaunchGroupId() {
    return EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP;
  }
    
  /*
   * Implements abstract method defined in superclass since Eclipse 3.3
   */
  protected String getText() {
    return UIMessages.CoverageLastAction_label;
  }

  /*
   * Implements abstract method defined in superclass since Eclipse 3.3
   */
  protected String getTooltipText() {
    return UIMessages.CoverageLastAction_label;
  }

  /*
   * Implements abstract method defined in superclass since Eclipse 3.3
   */
  protected String getDescription() {
    return UIMessages.CoverageLastAction_label;
  }

  /*
   * Implements abstract method defined in superclass since Eclipse 3.3
   */
  protected String getCommandId() {
    return "com.mountainminds.eclemma.ui.commands.CoverageLast"; //$NON-NLS-1$
  }
  
}
