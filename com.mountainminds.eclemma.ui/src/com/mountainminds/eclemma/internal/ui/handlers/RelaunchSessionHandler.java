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
package com.mountainminds.eclemma.internal.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * Handler to re-launch the currently active coverage session.
 */
public class RelaunchSessionHandler extends AbstractSessionManagerHandler {

  public RelaunchSessionHandler() {
    super(CoverageTools.getSessionManager());
  }

  @Override
  public boolean isEnabled() {
    final ICoverageSession session = sessionManager.getActiveSession();
    return session != null && session.getLaunchConfiguration() != null;
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final ICoverageSession session = sessionManager.getActiveSession();
    final ILaunchConfiguration config = session.getLaunchConfiguration();
    DebugUITools.launch(config, CoverageTools.LAUNCH_MODE);
    return null;
  }

}
