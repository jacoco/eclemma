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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * Tests for {@link SessionManager}.
 */
public class SessionManagerTest {

  protected ISessionManager manager;
  protected ISessionListener listener;
  protected ISessionListener reflistener;

  @Before
  public void setup() throws Exception {
    manager = new SessionManager();
    listener = new RecordingListener();
    manager.addSessionListener(listener);
    reflistener = new RecordingListener();
  }

  @Test
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

  @Test
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

  @Test
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

  @Test(expected = NullPointerException.class)
  public void testAddSession4() {
    manager.addSession(null, false, null);
    fail("NullPointerException expected.");
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void testGetSessions1() {
    ICoverageSession[] sessions = manager.getSessions();
    assertNotNull(sessions);
    assertEquals(0, sessions.length);
  }

  @Test
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

    public ILaunchConfiguration getLaunchConfiguration() {
      return null;
    }

    public Collection<IPackageFragmentRoot> getScope() {
      return Collections.emptyList();
    }

    public Collection<IPath> getExecutionDataFiles() {
      return Collections.emptyList();
    }

    public ICoverageSession merge(ICoverageSession other, String description) {
      return new DummySession();
    }

    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
      return null;
    }

    public String toString() {
      return "Session@" + System.identityHashCode(this);
    }

  }

  private static class RecordingListener implements ISessionListener {

    private List<Object> l = new ArrayList<Object>();

    public void sessionAdded(ICoverageSession addedSession) {
      l.add("ADDED");
      l.add(addedSession);
    }

    public void sessionRemoved(ICoverageSession removedSession) {
      l.add("REMOVED");
      l.add(removedSession);
    }

    public void sessionActivated(ICoverageSession session) {
      l.add("ACTIVATED");
      l.add(session);
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
