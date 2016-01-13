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
package com.mountainminds.eclemma.internal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * Tests for {@link SessionManager}.
 */
public class SessionManagerTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  protected ISessionManager manager;
  protected RecordingListener listener;
  protected RecordingListener reflistener;

  @Before
  public void setup() throws Exception {
    final IPath path = Path.fromOSString(folder.getRoot().getAbsolutePath());
    manager = new SessionManager(new ExecutionDataFiles(path));
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

    assertEquals(Arrays.asList(s0, s1, s2), manager.getSessions());
    assertNull(manager.getActiveSession());
    reflistener.sessionAdded(s0);
    reflistener.sessionAdded(s1);
    reflistener.sessionAdded(s2);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testAddSession2() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    ICoverageSession s2 = new DummySession();

    manager.addSession(s0, false, null);
    manager.addSession(s1, true, null);
    manager.addSession(s2, false, null);

    assertEquals(Arrays.asList(s0, s1, s2), manager.getSessions());
    assertSame(s1, manager.getActiveSession());
    reflistener.sessionAdded(s0);
    reflistener.sessionAdded(s1);
    reflistener.sessionActivated(s1);
    reflistener.sessionAdded(s2);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testAddSession3() {
    ICoverageSession s0 = new DummySession();

    manager.addSession(s0, false, null);
    manager.addSession(s0, false, null);

    assertEquals(Arrays.asList(s0), manager.getSessions());
    assertNull(manager.getActiveSession());
    reflistener.sessionAdded(s0);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testAddSession4() {
    ICoverageSession s0 = new DummySession();

    manager.addSession(s0, false, null);
    manager.addSession(s0, true, null);

    assertEquals(Arrays.asList(s0), manager.getSessions());
    assertSame(s0, manager.getActiveSession());
    reflistener.sessionAdded(s0);
    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSession5() {
    manager.addSession(null, false, null);
  }

  @Test
  public void testRemoveSession1() {
    ICoverageSession s0 = new DummySession();
    manager.addSession(s0, true, null);
    listener.clear();

    manager.removeSession(s0);

    assertEquals(Arrays.asList(), manager.getSessions());
    assertNull(manager.getActiveSession());
    reflistener.sessionRemoved(s0);
    reflistener.sessionActivated(null);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveSession2() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, true, null);
    manager.addSession(s1, false, null);
    listener.clear();

    manager.removeSession(s1);

    assertEquals(Arrays.asList(s0), manager.getSessions());
    assertSame(s0, manager.getActiveSession());
    reflistener.sessionRemoved(s1);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveSession3() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, true, null);
    listener.clear();

    manager.removeSession(s1);

    assertEquals(Arrays.asList(s0), manager.getSessions());
    assertSame(s0, manager.getActiveSession());
    reflistener.sessionRemoved(s1);
    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveSession4() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, true, null);
    listener.clear();

    manager.removeSession(s1);

    assertEquals(Arrays.asList(s0), manager.getSessions());
    assertSame(s0, manager.getActiveSession());
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveSessionsFor1() {
    ILaunch launch0 = new Launch(null, null, null);
    ILaunch launch1 = new Launch(null, null, null);
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, launch0);
    manager.addSession(s1, true, launch1);
    listener.clear();

    manager.removeSessionsFor(launch1);

    assertEquals(Arrays.asList(s0), manager.getSessions());
    assertSame(s0, manager.getActiveSession());
    reflistener.sessionRemoved(s1);
    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveSessionsFor2() {
    ILaunch launch0 = new Launch(null, null, null);
    ILaunch launch1 = new Launch(null, null, null);
    ILaunch launch2 = new Launch(null, null, null);
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, launch0);
    manager.addSession(s1, true, launch1);
    listener.clear();

    manager.removeSessionsFor(launch2);

    assertEquals(Arrays.asList(s0, s1), manager.getSessions());
    assertSame(s1, manager.getActiveSession());
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveSessionsFor3() {
    ILaunch launch0 = new Launch(null, null, null);
    ILaunch launch1 = new Launch(null, null, null);
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    ICoverageSession s2 = new DummySession();
    manager.addSession(s0, true, launch0);
    manager.addSession(s1, true, launch1);
    manager.addSession(s2, true, launch1);
    listener.clear();

    manager.removeSessionsFor(launch1);

    assertEquals(Arrays.asList(s0), manager.getSessions());
    assertSame(s0, manager.getActiveSession());
    reflistener.sessionRemoved(s1);
    reflistener.sessionRemoved(s2);
    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveAllSessions1() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, false, null);
    listener.clear();

    manager.removeAllSessions();

    assertEquals(Arrays.asList(), manager.getSessions());
    assertNull(manager.getActiveSession());
    reflistener.sessionRemoved(s0);
    reflistener.sessionRemoved(s1);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRemoveAllSessions2() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, true, null);
    listener.clear();

    manager.removeAllSessions();

    assertEquals(Arrays.asList(), manager.getSessions());
    assertNull(manager.getActiveSession());
    reflistener.sessionRemoved(s0);
    reflistener.sessionRemoved(s1);
    reflistener.sessionActivated(null);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testGetSessions1() {
    assertEquals(Arrays.asList(), manager.getSessions());
  }

  @Test
  public void testActivateSession1() {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    manager.addSession(s0, false, null);
    listener.clear();

    manager.activateSession(s1);

    assertNull(manager.getActiveSession());
    assertEquals(reflistener, listener);
  }

  @Test
  public void testActivateSession2() {
    ICoverageSession s0 = new DummySession();
    manager.addSession(s0, true, null);
    listener.clear();

    manager.activateSession(s0);

    assertSame(s0, manager.getActiveSession());
    assertEquals(reflistener, listener);
  }

  @Test
  public void testActivateSession3() {
    ICoverageSession s0 = new DummySession();
    manager.addSession(s0, false, null);
    listener.clear();

    manager.activateSession(s0);

    assertSame(s0, manager.getActiveSession());
    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testRefreshActivateSession1() {
    manager.refreshActiveSession();

    assertEquals(reflistener, listener);
  }

  @Test
  public void testRefreshActivateSession2() {
    ICoverageSession s0 = new DummySession();
    manager.addSession(s0, true, null);
    listener.clear();

    manager.refreshActiveSession();

    reflistener.sessionActivated(s0);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testAddSessionListener1() {
    // Add listener a second time
    manager.addSessionListener(listener);

    ICoverageSession s0 = new DummySession();
    manager.addSession(s0, false, null);
    // Events are only sent once to the listener
    reflistener.sessionAdded(s0);
    assertEquals(reflistener, listener);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSessionListener2() {
    manager.addSessionListener(null);
  }

  @Test
  public void testRemoveSessionListener1() {
    manager.removeSessionListener(listener);

    ICoverageSession s0 = new DummySession();
    manager.addSession(s0, false, null);
    // No events recorded any more
    assertEquals(reflistener, listener);
  }

  @Test
  public void testMergeSession1() throws Exception {
    ICoverageSession s0 = new DummySession();
    ICoverageSession s1 = new DummySession();
    ICoverageSession s2 = new DummySession();
    manager.addSession(s0, false, null);
    manager.addSession(s1, false, null);
    manager.addSession(s2, false, null);
    listener.clear();

    final ICoverageSession m0 = manager.mergeSessions(Arrays.asList(s0, s1),
        "Merged", new NullProgressMonitor());

    assertEquals("Merged", m0.getDescription());
    assertNull(m0.getLaunchConfiguration());
    assertEquals(Arrays.asList(s2, m0), manager.getSessions());
    assertEquals(m0, manager.getActiveSession());
    reflistener.sessionAdded(m0);
    reflistener.sessionActivated(m0);
    reflistener.sessionRemoved(s0);
    reflistener.sessionRemoved(s1);
    assertEquals(reflistener, listener);
  }

  @Test
  public void testMergeSession2() throws Exception {
    ILaunchConfiguration launch = new DummyLaunchConfiguration();
    ICoverageSession s0 = new DummySession(launch);
    ICoverageSession s1 = new DummySession(launch);
    manager.addSession(s0, false, null);
    manager.addSession(s1, false, null);
    listener.clear();

    final ICoverageSession m0 = manager.mergeSessions(Arrays.asList(s0, s1),
        "Merged", new NullProgressMonitor());

    assertEquals("Merged", m0.getDescription());
    assertSame(launch, m0.getLaunchConfiguration());
    assertEquals(Arrays.asList(m0), manager.getSessions());
    assertEquals(m0, manager.getActiveSession());
    reflistener.sessionAdded(m0);
    reflistener.sessionActivated(m0);
    reflistener.sessionRemoved(s0);
    reflistener.sessionRemoved(s1);
    assertEquals(reflistener, listener);
  }

  private static class DummySession implements ICoverageSession {

    private final ILaunchConfiguration launch;

    DummySession(ILaunchConfiguration launch) {
      this.launch = launch;
    }

    DummySession() {
      this(null);
    }

    public String getDescription() {
      return toString();
    }

    public ILaunchConfiguration getLaunchConfiguration() {
      return launch;
    }

    public Set<IPackageFragmentRoot> getScope() {
      return Collections.emptySet();
    }

    public void accept(IExecutionDataVisitor executionDataVisitor,
        ISessionInfoVisitor sessionInfoVisitor) {
    }

    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
      return null;
    }

    public String toString() {
      return "Session@" + System.identityHashCode(this);
    }

  }

  private static class DummyLaunchConfiguration implements ILaunchConfiguration {

    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
      return null;
    }

    public boolean contentsEqual(ILaunchConfiguration configuration) {
      return false;
    }

    public ILaunchConfigurationWorkingCopy copy(String name)
        throws CoreException {
      return null;
    }

    public void delete() throws CoreException {
    }

    public boolean exists() {
      return false;
    }

    public boolean getAttribute(String attributeName, boolean defaultValue)
        throws CoreException {
      return false;
    }

    public int getAttribute(String attributeName, int defaultValue)
        throws CoreException {
      return 0;
    }

    public List<?> getAttribute(String attributeName,
        @SuppressWarnings("rawtypes") List defaultValue) throws CoreException {
      return null;
    }

    public Set<?> getAttribute(String attributeName,
        @SuppressWarnings("rawtypes") Set defaultValue) throws CoreException {
      return null;
    }

    public Map<?, ?> getAttribute(String attributeName,
        @SuppressWarnings("rawtypes") Map defaultValue) throws CoreException {
      return null;
    }

    public String getAttribute(String attributeName, String defaultValue)
        throws CoreException {
      return null;
    }

    public Map<?, ?> getAttributes() throws CoreException {
      return null;
    }

    public String getCategory() throws CoreException {
      return null;
    }

    public IFile getFile() {
      return null;
    }

    public IPath getLocation() {
      return null;
    }

    public IResource[] getMappedResources() throws CoreException {
      return null;
    }

    public String getMemento() throws CoreException {
      return null;
    }

    public String getName() {
      return null;
    }

    public Set<?> getModes() throws CoreException {
      return null;
    }

    public ILaunchDelegate getPreferredDelegate(
        @SuppressWarnings("rawtypes") Set modes) throws CoreException {
      return null;
    }

    public ILaunchConfigurationType getType() throws CoreException {
      return null;
    }

    public ILaunchConfigurationWorkingCopy getWorkingCopy()
        throws CoreException {
      return null;
    }

    public boolean hasAttribute(String attributeName) throws CoreException {
      return false;
    }

    public boolean isLocal() {
      return false;
    }

    public boolean isMigrationCandidate() throws CoreException {
      return false;
    }

    public boolean isWorkingCopy() {
      return false;
    }

    public ILaunch launch(String mode, IProgressMonitor monitor)
        throws CoreException {
      return null;
    }

    public ILaunch launch(String mode, IProgressMonitor monitor, boolean build)
        throws CoreException {
      return null;
    }

    public ILaunch launch(String mode, IProgressMonitor monitor, boolean build,
        boolean register) throws CoreException {
      return null;
    }

    public void migrate() throws CoreException {
    }

    public boolean supportsMode(String mode) throws CoreException {
      return false;
    }

    public boolean isReadOnly() {
      return false;
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

    public void clear() {
      l.clear();
    }

  }

}
