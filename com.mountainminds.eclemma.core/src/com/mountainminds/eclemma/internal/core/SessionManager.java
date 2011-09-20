/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * ISessionManager implementation.
 */
public class SessionManager implements ISessionManager {

  private List<ICoverageSession> sessions = new ArrayList<ICoverageSession>();
  private Map<Object, ICoverageSession> keymap = new HashMap<Object, ICoverageSession>();
  private ICoverageSession activeSession = null;
  private List<ISessionListener> listeners = new ArrayList<ISessionListener>();

  public void addSession(ICoverageSession session, boolean activate, Object key) {
    if (session == null)
      throw new NullPointerException();
    if (!sessions.contains(session)) {
      sessions.add(session);
      if (key != null) {
        keymap.put(key, session);
      }
      fireSessionAdded(session);
      if (activate) {
        activeSession = session;
        fireSessionActivated(session);
      }
    }
  }

  public void removeSession(ICoverageSession session) {
    if (sessions.contains(session)) {
      boolean sessionActivated = false;
      if (session.equals(activeSession)) {
        activeSession = null;
        for (int i = sessions.size(); --i >= 0;) {
          if (!session.equals(sessions.get(i))) {
            activeSession = sessions.get(i);
            break;
          }
        }
        sessionActivated = true;
      }
      sessions.remove(session);
      keymap.values().remove(session);
      fireSessionRemoved(session);
      if (sessionActivated) {
        fireSessionActivated(activeSession);
      }
    }
  }

  public void removeSession(Object key) {
    removeSession(getSession(key));
  }

  public void removeAllSessions() {
    while (!sessions.isEmpty()) {
      ICoverageSession session = sessions.remove(0);
      keymap.values().remove(session);
      fireSessionRemoved(session);
    }
    if (activeSession != null) {
      activeSession = null;
      fireSessionActivated(null);
    }
  }

  public ICoverageSession[] getSessions() {
    return sessions.toArray(new ICoverageSession[sessions.size()]);
  }

  public ICoverageSession getSession(Object key) {
    return keymap.get(key);
  }

  public void activateSession(ICoverageSession session) {
    if (sessions.contains(session) && !session.equals(activeSession)) {
      activeSession = session;
      fireSessionActivated(session);
    }
  }

  public void activateSession(Object key) {
    activateSession(getSession(key));
  }

  public ICoverageSession getActiveSession() {
    return activeSession;
  }

  public void refreshActiveSession() {
    if (activeSession != null) {
      fireSessionActivated(activeSession);
    }
  }

  public void addSessionListener(ISessionListener listener) {
    if (listener == null)
      throw new NullPointerException();
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeSessionListener(ISessionListener listener) {
    listeners.remove(listener);
  }

  protected void fireSessionAdded(ICoverageSession session) {
    // avoid concurrent modification issues
    for (ISessionListener l : new ArrayList<ISessionListener>(listeners)) {
      l.sessionAdded(session);
    }
  }

  protected void fireSessionRemoved(ICoverageSession session) {
    // avoid concurrent modification issues
    for (ISessionListener l : new ArrayList<ISessionListener>(listeners)) {
      l.sessionRemoved(session);
    }
  }

  private void fireSessionActivated(ICoverageSession session) {
    // avoid concurrent modification issues
    for (ISessionListener l : new ArrayList<ISessionListener>(listeners)) {
      l.sessionActivated(session);
    }
  }

}
