/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.jacoco.agent.AgentJar;
import org.jacoco.core.runtime.AgentOptions;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICorePreferences;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Internal utility to calculate the agent VM parameter.
 */
public class AgentArgumentSupport {

  private final ICorePreferences preferences;

  protected AgentArgumentSupport(ICorePreferences preferences) {
    this.preferences = preferences;
  }

  public AgentArgumentSupport() {
    this(EclEmmaCorePlugin.getInstance().getPreferences());
  }

  /**
   * Returns a wrapper for the given launch configuration that adds the required
   * VM argument.
   * 
   * @param outputpath
   *          output location for execution data
   * @param config
   *          launch configuration to wrap
   * @return wrapped launch configuration
   */
  public ILaunchConfiguration addArgument(IPath outputpath,
      ILaunchConfiguration config) throws CoreException {
    return new AdjustedLaunchConfiguration(getArgument(outputpath), config);
  }

  protected String getArgument(IPath outputpath) throws CoreException {
    final AgentOptions options = new AgentOptions();
    options.setIncludes(preferences.getAgentIncludes());
    options.setExcludes(preferences.getAgentExcludes());
    options.setExclClassloader(preferences.getAgentExclClassloader());
    options.setDestfile(outputpath.toOSString());
    return quote(options.getVMArgument(getAgentFile()));
  }

  protected File getAgentFile() throws CoreException {
    try {
      final URL agentfileurl = FileLocator.toFileURL(AgentJar.getResource());
      return new Path(agentfileurl.getPath()).toFile();
    } catch (IOException e) {
      throw new CoreException(
          EclEmmaStatus.NO_LOCAL_AGENTJAR_ERROR.getStatus(e));
    }
  }

  protected String quote(String arg) {
    if (arg.indexOf(' ') == -1) {
      return arg;
    } else {
      return '"' + arg + '"';
    }
  }

}
