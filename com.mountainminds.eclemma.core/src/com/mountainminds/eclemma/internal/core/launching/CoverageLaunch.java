/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.launching.ICoverageLaunch;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Implementation of {@link ICoverageLaunch}.
 */
public class CoverageLaunch extends Launch implements ICoverageLaunch {

  private final Set<IPackageFragmentRoot> scope;
  private final AgentServer agentServer;

  public CoverageLaunch(ILaunchConfiguration launchConfiguration,
      Set<IPackageFragmentRoot> scope) {
    super(launchConfiguration, CoverageTools.LAUNCH_MODE, null);
    this.scope = scope;
    final EclEmmaCorePlugin plugin = EclEmmaCorePlugin.getInstance();
    this.agentServer = new AgentServer(this, plugin.getSessionManager(),
        plugin.getExecutionDataFiles(), plugin.getPreferences());
  }

  public AgentServer getAgentServer() {
    return agentServer;
  }

  // ICoverageLaunch interface

  public Set<IPackageFragmentRoot> getScope() {
    return scope;
  }

  public void requestDump(boolean reset) throws CoreException {
    agentServer.requestDump(reset);
  }

}
