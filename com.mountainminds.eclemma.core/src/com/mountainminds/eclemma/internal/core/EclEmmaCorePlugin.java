/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.BundleContext;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ICorePreferences;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.instr.ClassFilesStore;
import com.mountainminds.eclemma.internal.core.instr.DefaultInstrumentationFilter;

/**
 * Bundle activator for the EclEmma core.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class EclEmmaCorePlugin extends Plugin {

  public static final String ID = "com.mountainminds.eclemma.core"; //$NON-NLS-1$

  /** Status used to trigger user prompts */
  private static final IStatus PROMPT_STATUS = new Status(IStatus.INFO,
      "org.eclipse.debug.ui", 200, "", null); //$NON-NLS-1$//$NON-NLS-2$

  public static final IPath EMMA_JAR = new Path("/emma.jar"); //$NON-NLS-1$

  private static EclEmmaCorePlugin instance;

  private ICorePreferences preferences = ICorePreferences.DEFAULT;

  private ISessionManager sessionManager;

  private JavaCoverageLoader coverageLoader;

  private StateFiles stateFiles;

  private ClassFilesStore allClassFiles = null;

  private ILaunchListener launchListener = new ILaunchListener() {
    public void launchRemoved(ILaunch launch) {
      if (preferences.getAutoRemoveSessions()) {
        sessionManager.removeSession(launch);
      }
    }

    public void launchAdded(ILaunch launch) {
    }

    public void launchChanged(ILaunch launch) {
    }
  };

  private IDebugEventSetListener debugListener = new IDebugEventSetListener() {
    public void handleDebugEvents(DebugEvent[] events) {
      for (int i = 0; i < events.length; i++) {
        DebugEvent e = events[i];
        if (e.getSource() instanceof IProcess
            && e.getKind() == DebugEvent.TERMINATE) {
          IProcess proc = (IProcess) e.getSource();
          final ILaunch launch = proc.getLaunch();
          ICoverageLaunchInfo info = CoverageTools.getLaunchInfo(launch);
          if (info != null) {
            IPath coveragedatafile = info.getCoverageFile();
            if (checkCoverageDataFile(coveragedatafile)) {
              Object[] args = new Object[] {
                  launch.getLaunchConfiguration().getName(), new Date() };
              String description = MessageFormat.format(
                  CoreMessages.LaunchSessionDescription_value, args);
              ICoverageSession session = new CoverageSession(description, info
                  .getInstrumentations(), new IPath[] { coveragedatafile },
                  launch.getLaunchConfiguration());
              sessionManager.addSession(session, preferences
                  .getActivateNewSessions(), launch);
            }
            info.dispose();
          }
        }
      }
    }

    private boolean checkCoverageDataFile(IPath path) {
      boolean ok = path.toFile().exists();
      if (!ok) {
        try {
          showPrompt(EclEmmaStatus.NO_COVERAGE_DATA_ERROR.getStatus(), path);
        } catch (CoreException e) {
          getLog().log(e.getStatus());
        }
      }
      return ok;
    }
  };

  private IElementChangedListener elementListener = new IElementChangedListener() {
    public void elementChanged(ElementChangedEvent event) {
      synchronized (EclEmmaCorePlugin.this) {
        allClassFiles = null;
      }
    }
  };

  public void start(BundleContext context) throws Exception {
    super.start(context);
    sessionManager = new SessionManager();
    coverageLoader = new JavaCoverageLoader(sessionManager);
    stateFiles = new StateFiles(getStateLocation());
    stateFiles.deleteTemporaryFiles();
    DebugPlugin.getDefault().getLaunchManager().addLaunchListener(
        launchListener);
    DebugPlugin.getDefault().addDebugEventListener(debugListener);
    JavaCore.addElementChangedListener(elementListener);
    instance = this;
  }

  public void stop(BundleContext context) throws Exception {
    instance = null;
    stateFiles.deleteTemporaryFiles();
    JavaCore.removeElementChangedListener(elementListener);
    DebugPlugin.getDefault().removeDebugEventListener(debugListener);
    DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(
        launchListener);
    stateFiles = null;
    coverageLoader.dispose();
    coverageLoader = null;
    sessionManager = null;
    super.stop(context);
  }

  public static EclEmmaCorePlugin getInstance() {
    return instance;
  }

  public void setPreferences(ICorePreferences preferences) {
    this.preferences = preferences;
  }

  /**
   * Returns a new filter instance for default selection of instrumemted
   * classes.
   * 
   * @return new filter
   */
  public DefaultInstrumentationFilter createDefaultIntrumentationFilter() {
    return new DefaultInstrumentationFilter(this.preferences);
  }

  public ISessionManager getSessionManager() {
    return sessionManager;
  }

  public JavaCoverageLoader getJavaCoverageLoader() {
    return coverageLoader;
  }

  public StateFiles getStateFiles() {
    return stateFiles;
  }

  /**
   * Issues an user prompt using the statis handler registered for the given
   * status.
   * 
   * @param status
   *          IStatus object to find prompter for
   * @param info
   *          addional information passed to the handler
   * @return boolean result returned by the status handler
   * @throws CoreException
   *           if the status has severity error and no handler is available
   */
  public boolean showPrompt(IStatus status, Object info) throws CoreException {
    IStatusHandler prompter = DebugPlugin.getDefault().getStatusHandler(
        PROMPT_STATUS);
    if (prompter == null) {
      if (status.getSeverity() == IStatus.ERROR) {
        throw new CoreException(status);
      } else {
        return true;
      }
    } else {
      return ((Boolean) prompter.handleStatus(status, info)).booleanValue();
    }
  }

  /**
   * Tries to find the absolute path for the given workspace relative path.
   * 
   * @param path
   *          workspace relative path to resolve
   * @return absolute path
   */
  public static IPath getAbsolutePath(IPath path) {
    if (path.getDevice() == null) {
      IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
      if (res != null) {
        return res.getLocation();
      }
    }
    return path;
  }

  /**
   * Calculates the list of IClassFiles for the given Java project. Basically
   * for every separate output location a entry will be returned.
   * 
   * @param project
   *          Java project to calculate IClassFiles for
   * @return aArray of IClassFiles objects
   * @throws JavaModelException
   *           thrown when a problem with the underlying Java model occures
   */
  public static IClassFiles[] getClassFiles(IJavaProject project)
      throws JavaModelException {
    final ClassFilesStore store = new ClassFilesStore();
    store.add(project);
    return store.getClassFiles();
  }

  public ClassFilesStore getAllClassFiles() throws CoreException {
    ClassFilesStore store = allClassFiles;
    if (store == null) {
      store = new ClassFilesStore();
      store.add(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
      allClassFiles = store;
    }
    return store;
  }

}
