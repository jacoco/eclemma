/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.IInstrumentation;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class SessionManagerTest extends TestCase {
  
  protected ISessionManager manager;
  protected ISessionListener listener;
  protected ISessionListener reflistener;

  protected void setUp() throws Exception {
    manager = new SessionManager();
    listener = new RecordingListener();
    manager.addSessionListener(listener);
    reflistener = new RecordingListener();
  }
  
  public void testAddSession1() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    ICoverageSession s2 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, false, null);
    manager.addSession(s2, false, null);
    ICoverageSession[] sessions = manager.getSessions();
    assertEquals(3, sessions.length);
    assertSame(s0, sessions[0]);
    assertSame(s1, sessions[1]);
    assertSame(s2, sessions[2]);
    assertNull(manager.getActiveSession());
  }

  public void testAddSession2() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    ICoverageSession s2 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, true, null);
    manager.addSession(s2, false, null);
    ICoverageSession[] sessions = manager.getSessions();
    assertEquals(3, sessions.length);
    assertSame(s0, sessions[0]);
    assertSame(s1, sessions[1]);
    assertSame(s2, sessions[2]);
    assertSame(s1, manager.getActiveSession());
  }
  
  public void testAddSession3() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, true, null);
    reflistener.sessionAdded(s0);
    reflistener.sessionAdded(s1);
    reflistener.sessionActivated(s1);
    assertEquals(reflistener, listener);
  }

  public void testAddSession4() {
    try {
      manager.addSession(null, false, null);
      fail("NullPointerException expected.");
    } catch (NullPointerException npe) {
    }
  }
  
  public void testRemoveSession1() {
    ICoverageSession s0 = new DummySession();
    manager.addSession(s0, true, null);
    manager.removeSession(s0);
    assertEquals(0, manager.getSessions().length);
    assertNull(manager.getActiveSession());
    reflistener.sessionAdded(s0);
    reflistener.sessionActivated(s0);
    reflistener.sessionRemoved(s0);
    reflistener.sessionActivated(null);
    assertEquals(reflistener, listener);
  }

  public void testRemoveSession2() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, true, null);
    manager.removeSession(s1);
    ICoverageSession[] sessions = manager.getSessions();
    assertEquals(1, sessions.length);
    assertSame(s0, sessions[0]);
    assertSame(s0, manager.getActiveSession());
    reflistener.sessionAdded(s0);
    reflistener.sessionAdded(s1);
    reflistener.sessionActivated(s1);
    reflistener.sessionRemoved(s1);
    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }
  
  public void testRemoveSession3() {
    Object key0 = new Object();
    Object key1 = new Object();
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, key0);
    manager.addSession(s1, true, key1);
    manager.removeSession(key1);
    ICoverageSession[] sessions = manager.getSessions();
    assertEquals(1, sessions.length);
    assertSame(s0, sessions[0]);
    assertSame(s0, manager.getActiveSession());
    reflistener.sessionAdded(s0);
    reflistener.sessionAdded(s1);
    reflistener.sessionActivated(s1);
    reflistener.sessionRemoved(s1);
    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }
  
  public void testRemoveSession4() {
    Object key0 = new Object();
    Object key1 = new Object();
    Object key2 = new Object();
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, key0);
    manager.addSession(s1, true, key1);
    manager.removeSession(key2);
    assertEquals(2, manager.getSessions().length);
  }
  
  public void testRemoveAllSessions1() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, true, null);
    manager.removeAllSessions();
    assertEquals(0, manager.getSessions().length);
    assertNull(manager.getActiveSession());
    reflistener.sessionAdded(s0);
    reflistener.sessionAdded(s1);
    reflistener.sessionActivated(s1);
    reflistener.sessionRemoved(s0);
    reflistener.sessionRemoved(s1);
    reflistener.sessionActivated(null);
    assertEquals(reflistener, listener);
  }
  
  public void testGetSessions1() {
    ICoverageSession[] sessions = manager.getSessions();
    assertNotNull(sessions);
    assertEquals(0, sessions.length);
  }

  public void testGetSession1() {
    Object key0 = new Object();
    Object key1 = new Object();
    Object key2 = new Object();
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, key0);
    manager.addSession(s1, false, key1);
    assertEquals(s0, manager.getSession(key0));
    assertEquals(s1, manager.getSession(key1));
    assertNull(manager.getSession(key2));
  }
  
  
  private static class DummySession implements ICoverageSession {

    public String getDescription() {
      return toString();
    }
  
    public IInstrumentation[] getInstrumentations() {
      return new IInstrumentation[0];
    }
  
    public IPath[] getCoverageDataFiles() {
      return new IPath[0];
    }
  
    public ILaunchConfiguration getLaunchConfiguration() {
      return null;
    }
  
    public ICoverageSession merge(ICoverageSession other, String description) {
      return new DummySession();
    }
  
    public Object getAdapter(Class adapter) {
      return null;
    }

    public String toString() {
      return "Session@" + System.identityHashCode(this);
    }

  }
  
  private static class RecordingListener implements ISessionListener {
    
    private List l = new ArrayList();

    public void sessionAdded(ICoverageSession addedSession) {
      l.add("ADDED"); l.add(addedSession);
    }

    public void sessionRemoved(ICoverageSession removedSession) {
      l.add("REMOVED"); l.add(removedSession);
    }

    public void sessionActivated(ICoverageSession session) {
      l.add("ACTIVATED"); l.add(session);
    }

    public boolean equals(Object obj) {
      if (obj instanceof RecordingListener) {
        return l.equals(((RecordingListener) obj).l);
      } else {
        return false;
      }
    }

    public int hashCode() {
      return l.hashCode();
    }

    public String toString() {
      return l.toString();
    }
    
  }
  
}
