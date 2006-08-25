/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.ui.actions.OpenLaunchDialogAction;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class OpenCoverageConfigurations extends OpenLaunchDialogAction  {

    public OpenCoverageConfigurations() {
        super(EclEmmaUIPlugin.ID_COVERAGE_LAUNCH_GROUP);
    }
  
}
