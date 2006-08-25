/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageHistoryAction extends AbstractLaunchHistoryAction {

  public CoverageHistoryAction() {
    super(EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP);
  }
  
}
