/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.BundleContext;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.instr.ClassFiles;

/**
 * Bundle activator for the EclEmma core.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class EclEmmaCorePlugin extends Plugin {

  public static final String ID = "com.mountainminds.eclemma.core"; //$NON-NLS-1$

  public static final IPath EMMA_JAR = new Path("/emma.jar"); //$NON-NLS-1$

  private static EclEmmaCorePlugin instance;

  private ISessionManager sessionManager;

  private JavaCoverageLoader coverageLoader;

  private StateFiles stateFiles;

  // TODO synchronize access!
  private Map instrumentedClasses = null;

  private ILaunchListener launchListener = new ILaunchListener() {
    public void launchRemoved(ILaunch launch) {
      // TODO this behaviour will be optional
      /*
      ICoverageSession session = sessionManager.getSession(launch);
      if (session != null) {
        session.dispose();
        sessionManager.removeSession(session);
      }
      */
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
            Object[] args = new Object[] {
                launch.getLaunchConfiguration().getName(), new Date() };
            String description = MessageFormat.format(
                CoreMessages.LaunchSessionLabel, args);
            ICoverageSession session = new CoverageSession(description, info
                .getInstrumentations(), new IPath[] { info.getCoverageFile() },
                launch.getLaunchConfiguration(), true);
            // TODO it will be optional, whether the new session is activated
            sessionManager.addSession(session, true, launch);
            info.dispose();
          }
        }
      }
    }
  };

  private IElementChangedListener elementListener = new IElementChangedListener() {
    public void elementChanged(ElementChangedEvent event) {
      synchronized (EclEmmaCorePlugin.this) {
        instrumentedClasses = null;
      }
    }
  };

  public void start(BundleContext context) throws Exception {
    super.start(context);
    sessionManager = new SessionManager();
    coverageLoader = new JavaCoverageLoader(sessionManager);
    stateFiles = new StateFiles(getStateLocation());
    stateFiles.removeObsoleteFiles();
    DebugPlugin.getDefault().getLaunchManager().addLaunchListener(
        launchListener);
    DebugPlugin.getDefault().addDebugEventListener(debugListener);
    JavaCore.addElementChangedListener(elementListener);
    instance = this;
  }

  public void stop(BundleContext context) throws Exception {
    instance = null;
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

  public synchronized Map getClassFiles() throws CoreException {
    if (instrumentedClasses == null) {
      instrumentedClasses = new HashMap();
      IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
          .getRoot());
      IJavaProject[] projects = model.getJavaProjects();
      for (int i = 0; i < projects.length; i++) {
        {
          IPackageFragmentRoot[] roots = projects[i].getPackageFragmentRoots();
          for (int j = 0; j < roots.length; j++) {
            IPath location = getClassFileLocation(roots[j]);
            String ospath = getAbsolutePath(location).toOSString();
            ClassFiles classfiles = (ClassFiles) instrumentedClasses
                .get(ospath);
            if (classfiles == null) {
              instrumentedClasses.put(ospath, new ClassFiles(
                  new IPackageFragmentRoot[] { roots[j] }, location));
            } else {
              instrumentedClasses.put(ospath, classfiles.addRoot(roots[j]));
            }
          }
        }
      }
    }
    return instrumentedClasses;
  }

  private static IPath getClassFileLocation(IPackageFragmentRoot root)
      throws JavaModelException {
    IPath path;
    if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
      IClasspathEntry entry = root.getRawClasspathEntry();
      path = entry.getOutputLocation();
      if (path == null) {
        path = root.getJavaProject().getOutputLocation();
      }
    } else {
      path = root.getPath();
    }
    return path;
  }

}
