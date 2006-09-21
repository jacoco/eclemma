/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * ISessionManager implementation.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionManager implements ISessionManager {

  private List sessions = new ArrayList();
  private Map keymap = new HashMap();
  private ICoverageSession activeSession = null;
  private List listeners = new ArrayList();

  public void addSession(ICoverageSession session, boolean activate, Object key) {
    if (session == null) throw new NullPointerException();
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
            activeSession = (ICoverageSession) sessions.get(i);
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
      ICoverageSession session = (ICoverageSession) sessions.remove(0);
      keymap.values().remove(session);
      fireSessionRemoved(session);
    }
    if (activeSession != null) {
      activeSession = null;
      fireSessionActivated(null);
    }
  }

  public ICoverageSession[] getSessions() {
    return (ICoverageSession[]) sessions.toArray(new ICoverageSession[sessions.size()]);
  }

  public ICoverageSession getSession(Object key) {
    return (ICoverageSession) keymap.get(key);
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
    if (listener == null) throw new NullPointerException();
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeSessionListener(ISessionListener listener) {
    listeners.remove(listener);
  }
  
  protected void fireSessionAdded(ICoverageSession session) {
    // avoid concurrent modification issues
    Iterator i = new ArrayList(listeners).iterator();
    while (i.hasNext()) {
      ((ISessionListener) i.next()).sessionAdded(session);
    }
  }

  protected void fireSessionRemoved(ICoverageSession session) {
    // avoid concurrent modification issues
    Iterator i = new ArrayList(listeners).iterator();
    while (i.hasNext()) {
      ((ISessionListener) i.next()).sessionRemoved(session);
    }
  }

  private void fireSessionActivated(ICoverageSession session) {
    // avoid concurrent modification issues
    Iterator i = new ArrayList(listeners).iterator();
    while (i.hasNext()) {
      ((ISessionListener) i.next()).sessionActivated(session);
    }
  }

}
