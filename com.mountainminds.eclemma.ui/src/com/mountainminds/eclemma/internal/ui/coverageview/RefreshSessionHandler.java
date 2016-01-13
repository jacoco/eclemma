/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.handlers.AbstractSessionManagerHandler;

/**
 * This handler reloads the active coverage session.
 */
class RefreshSessionHandler extends AbstractSessionManagerHandler {

  public RefreshSessionHandler(ISessionManager sessionManager) {
    super(sessionManager);
  }

  public boolean isEnabled() {
    return sessionManager.getActiveSession() != null;
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    sessionManager.refreshActiveSession();
    return null;
  }

}
