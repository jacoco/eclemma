/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.IExecutionDataSource;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;

/**
 * ISessionManager implementation.
 */
public class SessionManager implements ISessionManager {

  private final ExecutionDataFiles executiondatafiles;

  private final Object lock;
  private final List<ISessionListener> listeners;

  private final List<ICoverageSession> sessions;
  private final Map<Object, List<ICoverageSession>> launchmap;
  private ICoverageSession activeSession;

  public SessionManager(ExecutionDataFiles executiondatafiles) {
    this.executiondatafiles = executiondatafiles;
    this.lock = new Object();
    this.listeners = new ArrayList<ISessionListener>();
    this.sessions = new ArrayList<ICoverageSession>();
    this.launchmap = new HashMap<Object, List<ICoverageSession>>();
    this.activeSession = null;
  }

  public void addSession(ICoverageSession session, boolean activate,
      ILaunch launch) {
    synchronized (lock) {
      if (session == null) {
        throw new IllegalArgumentException();
      }
      if (!sessions.contains(session)) {
        sessions.add(session);
        if (launch != null) {
          List<ICoverageSession> l = launchmap.get(launch);
          if (l == null) {
            l = new ArrayList<ICoverageSession>();
            launchmap.put(launch, l);
          }
          l.add(session);
        }
        fireSessionAdded(session);
      }
      if (activate) {
        activeSession = session;
        fireSessionActivated(session);
      }
    }
  }

  public void removeSession(ICoverageSession session) {
    synchronized (lock) {
      removeSessions(Collections.singleton(session));
    }
  }

  public void removeSessionsFor(ILaunch launch) {
    synchronized (lock) {
      final List<ICoverageSession> sessionsToRemove = launchmap.get(launch);
      if (sessionsToRemove != null) {
        removeSessions(sessionsToRemove);
      }
    }
  }

  public void removeAllSessions() {
    synchronized (lock) {
      removeSessions(sessions);
    }
  }

  private void removeSessions(Collection<ICoverageSession> sessionsToRemove) {
    // Clone as in some scenarios we're modifying the caller's instance
    sessionsToRemove = new ArrayList<ICoverageSession>(sessionsToRemove);

    // Remove Sessions
    List<ICoverageSession> removedSessions = new ArrayList<ICoverageSession>();
    for (final ICoverageSession s : sessionsToRemove) {
      if (sessions.remove(s)) {
        removedSessions.add(s);
        for (final List<ICoverageSession> mappedSessions : launchmap.values()) {
          mappedSessions.remove(s);
        }
      }
    }

    // Activate other session if active session was removed:
    final boolean actived = sessionsToRemove.contains(activeSession);
    if (actived) {
      final int size = sessions.size();
      activeSession = size == 0 ? null : sessions.get(size - 1);
    }

    // Fire events:
    for (ICoverageSession s : removedSessions) {
      fireSessionRemoved(s);
    }
    if (actived) {
      fireSessionActivated(activeSession);
    }
  }

  public List<ICoverageSession> getSessions() {
    synchronized (lock) {
      return new ArrayList<ICoverageSession>(sessions);
    }
  }

  public void activateSession(ICoverageSession session) {
    synchronized (lock) {
      if (sessions.contains(session) && !session.equals(activeSession)) {
        activeSession = session;
        fireSessionActivated(session);
      }
    }
  }

  public ICoverageSession getActiveSession() {
    synchronized (lock) {
      return activeSession;
    }
  }

  public void refreshActiveSession() {
    synchronized (lock) {
      if (activeSession != null) {
        fireSessionActivated(activeSession);
      }
    }
  }

  public ICoverageSession mergeSessions(Collection<ICoverageSession> sessions,
      String description, IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(CoreMessages.MergingCoverageSessions_task,
        sessions.size());

    // Merge all sessions
    final Set<IPackageFragmentRoot> scope = new HashSet<IPackageFragmentRoot>();
    final Set<ILaunchConfiguration> launches = new HashSet<ILaunchConfiguration>();
    final MemoryExecutionDataSource memory = new MemoryExecutionDataSource();
    for (ICoverageSession session : sessions) {
      scope.addAll(session.getScope());
      if (session.getLaunchConfiguration() != null) {
        launches.add(session.getLaunchConfiguration());
      }
      session.accept(memory, memory);
      monitor.worked(1);
    }
    final IExecutionDataSource executionDataSource = executiondatafiles
        .newFile(memory);

    // Adopt launch configuration only if there is exactly one
    final ILaunchConfiguration launchconfiguration = launches.size() == 1 ? launches
        .iterator().next() : null;
    final ICoverageSession merged = new CoverageSession(description, scope,
        executionDataSource, launchconfiguration);

    // Update session list
    synchronized (lock) {
      addSession(merged, true, null);
      for (ICoverageSession session : sessions) {
        removeSession(session);
      }
    }

    monitor.done();
    return merged;
  }

  public void addSessionListener(ISessionListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException();
    }
    synchronized (lock) {
      if (!listeners.contains(listener)) {
        listeners.add(listener);
      }
    }
  }

  public void removeSessionListener(ISessionListener listener) {
    synchronized (lock) {
      listeners.remove(listener);
    }
  }

  private void fireSessionAdded(ICoverageSession session) {
    // copy to avoid concurrent modification issues
    for (ISessionListener l : new ArrayList<ISessionListener>(listeners)) {
      l.sessionAdded(session);
    }
  }

  private void fireSessionRemoved(ICoverageSession session) {
    // copy to avoid concurrent modification issues
    for (ISessionListener l : new ArrayList<ISessionListener>(listeners)) {
      l.sessionRemoved(session);
    }
  }

  private void fireSessionActivated(ICoverageSession session) {
    // copy to avoid concurrent modification issues
    for (ISessionListener l : new ArrayList<ISessionListener>(listeners)) {
      l.sessionActivated(session);
    }
  }

}
