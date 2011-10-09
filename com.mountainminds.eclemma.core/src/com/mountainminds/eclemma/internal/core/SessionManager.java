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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.jacoco.core.data.ExecutionDataWriter;

import com.mountainminds.eclemma.core.EclEmmaStatus;
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
  private final ExecutionDataFiles executiondatafiles;

  public SessionManager(ExecutionDataFiles executiondatafiles) {
    this.executiondatafiles = executiondatafiles;
  }

  public void addSession(ICoverageSession session, boolean activate, Object key) {
    if (session == null) {
      throw new IllegalArgumentException();
    }
    if (!sessions.contains(session)) {
      sessions.add(session);
      if (key != null) {
        keymap.put(key, session);
      }
      fireSessionAdded(session);
    }
    if (activate) {
      activeSession = session;
      fireSessionActivated(session);
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

  public List<ICoverageSession> getSessions() {
    return new ArrayList<ICoverageSession>(sessions);
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

  public ICoverageSession getActiveSession() {
    return activeSession;
  }

  public void refreshActiveSession() {
    if (activeSession != null) {
      fireSessionActivated(activeSession);
    }
  }

  public ICoverageSession mergeSessions(Collection<ICoverageSession> sessions,
      String description, IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(CoreMessages.MergingCoverageSessions_task,
        sessions.size());
    final Set<IPackageFragmentRoot> scope = new HashSet<IPackageFragmentRoot>();
    final Set<ILaunchConfiguration> launches = new HashSet<ILaunchConfiguration>();
    final IPath execfile = executiondatafiles.newFile();
    try {
      final OutputStream out = new BufferedOutputStream(new FileOutputStream(
          execfile.toFile()));
      final ExecutionDataWriter writer = new ExecutionDataWriter(out);

      // Merge all sessions
      for (ICoverageSession session : sessions) {
        scope.addAll(session.getScope());
        if (session.getLaunchConfiguration() != null) {
          launches.add(session.getLaunchConfiguration());
        }
        final SubProgressMonitor submonitor = new SubProgressMonitor(monitor, 1);
        session.readExecutionData(writer, writer, submonitor);
      }
      out.close();
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.MERGE_ERROR.getStatus(e));
    }

    // Adopt launch configuration only if there is exactly one
    final ILaunchConfiguration launchconfiguration = launches.size() == 1 ? launches
        .iterator().next() : null;
    final ICoverageSession merged = new CoverageSession(description, scope,
        execfile, launchconfiguration);

    // Update session list
    addSession(merged, true, null);
    for (ICoverageSession session : sessions) {
      removeSession(session);
    }

    monitor.done();
    return merged;
  }

  public void addSessionListener(ISessionListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException();
    }
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeSessionListener(ISessionListener listener) {
    listeners.remove(listener);
  }

  protected void fireSessionAdded(ICoverageSession session) {
    // copy to avoid concurrent modification issues
    for (ISessionListener l : new ArrayList<ISessionListener>(listeners)) {
      l.sessionAdded(session);
    }
  }

  protected void fireSessionRemoved(ICoverageSession session) {
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
