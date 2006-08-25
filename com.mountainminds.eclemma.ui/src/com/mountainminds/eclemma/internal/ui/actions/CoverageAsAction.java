/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.ui.actions.LaunchShortcutsAction;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageAsAction extends LaunchShortcutsAction {

  public CoverageAsAction() {
    super(EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP);
  }
  
}
