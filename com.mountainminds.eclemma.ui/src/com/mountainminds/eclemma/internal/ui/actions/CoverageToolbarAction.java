/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageToolbarAction extends AbstractLaunchToolbarAction {

  public CoverageToolbarAction() {
    super(EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP);
  }
  
}
