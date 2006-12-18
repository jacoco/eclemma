/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.internal.core.analysis.SessionAnalyzer;

/**
 * Internal utility class that loads the coverage data asynchronously, holds the
 * current {@link IJavaModelCoverage} object and sends out events in case of
 * changed coverage information.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaCoverageLoader {

  private final ISessionManager sessionManager;

  private ICoverageSession activeSession;

  private IJavaModelCoverage coverage;

  private final List listeners = new ArrayList();

  private ISessionListener sessionListener = new ISessionListener() {

    public void sessionActivated(ICoverageSession session) {
      activeSession = session;
      // TODO Looks like this has no effect
      Platform.getJobManager().cancel(LOADJOB);
      if (session == null) {
        coverage = null;
        fireCoverageChanged();
      } else {
        coverage = IJavaModelCoverage.LOADING;
        fireCoverageChanged();
        new LoadSessionJob(activeSession).schedule();
      }
    }

    public void sessionAdded(ICoverageSession addedSession) {
    }

    public void sessionRemoved(ICoverageSession removedSession) {
    }

  };

  private static final Object LOADJOB = new Object();
  
  private class LoadSessionJob extends Job {

    private final ICoverageSession session;

    public LoadSessionJob(ICoverageSession session) {
      super(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()));
      this.session = session;
    }

    protected IStatus run(IProgressMonitor monitor) {
      IJavaModelCoverage c;
      try {
        c = new SessionAnalyzer().processSession(session, monitor);
      } catch (CoreException e) {
        return e.getStatus();
      }
      coverage = monitor.isCanceled() ? null : c;
      fireCoverageChanged();
      return Status.OK_STATUS;
    }

    public boolean belongsTo(Object family) {
      return family == LOADJOB;
    }

  };

  public JavaCoverageLoader(ISessionManager sessionManager) {
    this.sessionManager = sessionManager;
    sessionManager.addSessionListener(sessionListener);
  }

  public void addJavaCoverageListener(IJavaCoverageListener l) {
    if (l == null)
      throw new NullPointerException();
    if (!listeners.contains(l)) {
      listeners.add(l);
    }
  }

  public void removeJavaCoverageListener(IJavaCoverageListener l) {
    listeners.remove(l);
  }

  protected void fireCoverageChanged() {
    // avoid concurrent modification issues
    Iterator i = new ArrayList(listeners).iterator();
    while (i.hasNext()) {
      ((IJavaCoverageListener) i.next()).coverageChanged();
    }
  }

  public IJavaModelCoverage getJavaModelCoverage() {
    return coverage;
  }

  public void dispose() {
    sessionManager.removeSessionListener(sessionListener);
  }

}
