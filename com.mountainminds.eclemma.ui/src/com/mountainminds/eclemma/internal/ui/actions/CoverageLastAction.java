/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.actions;

import org.eclipse.debug.internal.ui.actions.RelaunchLastAction;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

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
  
}
