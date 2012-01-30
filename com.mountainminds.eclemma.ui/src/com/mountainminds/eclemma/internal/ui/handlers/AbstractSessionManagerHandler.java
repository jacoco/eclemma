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
package com.mountainminds.eclemma.internal.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * Abstract base for handlers that need a {@link ISessionManager} instance.
 */
public abstract class AbstractSessionManagerHandler extends AbstractHandler
    implements ISessionListener {

  protected final ISessionManager sessionManager;

  protected AbstractSessionManagerHandler(ISessionManager sessionManager) {
    this.sessionManager = sessionManager;
    sessionManager.addSessionListener(this);
  }

  @Override
  public void dispose() {
    sessionManager.removeSessionListener(this);
  }

  public void sessionAdded(ICoverageSession addedSession) {
    fireEnabledChanged();
  }

  public void sessionRemoved(ICoverageSession removedSession) {
    fireEnabledChanged();
  }

  public void sessionActivated(ICoverageSession session) {
    fireEnabledChanged();
  }

  private void fireEnabledChanged() {
    fireHandlerChanged(new HandlerEvent(this, true, false));
  }

}
