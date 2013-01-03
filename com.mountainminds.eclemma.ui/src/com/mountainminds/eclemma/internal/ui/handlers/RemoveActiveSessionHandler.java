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

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * Handler to remove the currently active coverage session.
 */
public class RemoveActiveSessionHandler extends AbstractSessionManagerHandler {

  public RemoveActiveSessionHandler() {
    super(CoverageTools.getSessionManager());
  }

  @Override
  public boolean isEnabled() {
    return sessionManager.getActiveSession() != null;
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final ICoverageSession session = sessionManager.getActiveSession();
    sessionManager.removeSession(session);
    return null;
  }

}
