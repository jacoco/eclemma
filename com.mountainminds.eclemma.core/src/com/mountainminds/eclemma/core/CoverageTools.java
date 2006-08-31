/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.core.analysis.ILineCoverage;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchConfigurationConstants;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.CoverageSession;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.launching.CoverageLaunchInfo;

/**
 * For central access to the tools provided by the coverage core plugin this
 * class offers several static methods.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public final class CoverageTools {
  
  /**
   * The launch mode used for coverage sessions.
   */
  public static final String LAUNCH_MODE = "coverage"; //$NON-NLS-1$

  /**
   * Returns the global session manager.
   * 
   * @return global session manager
   */
  public static ISessionManager getSessionManager() {
    return EclEmmaCorePlugin.getInstance().getSessionManager();
  }

  /**
   * Convenience method that tries to adapt the given object to
   * IJavaElementCoverage, i.e. find coverage information from the active
   * session.
   * 
   * @param object
   *          Object to adapt
   * @return adapter or <code>null</code>
   */
  public static IJavaElementCoverage getCoverageInfo(Object object) {
    if (object instanceof IAdaptable) {
      return (IJavaElementCoverage) ((IAdaptable) object)
          .getAdapter(IJavaElementCoverage.class);
    } else {
      return null;
    }
  }

  /**
   * Convenience method that tries to adapt the given object to ILineCoverage,
   * i.e. find line coverage information from the active session.
   * 
   * @param object
   *          Object to adapt
   * @return adapter or <code>null</code>
   */
  public static ILineCoverage getLineCoverage(Object object) {
    if (object instanceof IAdaptable) {
      return (ILineCoverage) ((IAdaptable) object)
          .getAdapter(ILineCoverage.class);
    } else {
      return null;
    }
  }

  /**
   * Returns the coverage launch info that is assoziated with the given launch.
   * If no info object is assoziated with the given launch <code>null</code>
   * is returned, i.e. the launch was probably not in coverage mode.
   * 
   * @param launch
   *          the launch object we need coverage data for
   * @return the info object or <code>null</code>
   */
  public static ICoverageLaunchInfo getLaunchInfo(ILaunch launch) {
    return CoverageLaunchInfo.getInfo(launch);
  }

  /**
   * Returns a local path to the emma.jar runtime archive.
   * 
   * @return local path to emma.jar
   * @throws CoreException
   *           if no local version can be created
   */
  public static IPath getEmmaJar() throws CoreException {
    URL url = EclEmmaCorePlugin.getInstance().find(EclEmmaCorePlugin.EMMA_JAR);
    try {
      url = Platform.asLocalURL(url);
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.NO_LOCAL_EMMAJAR_ERROR.getStatus(e));
    }
    return new Path(url.getPath());
  }

  /**
   * Returns descriptors for all class files in the workspace.
   * 
   * @return descriptors for all class files in the workspace
   *   
   * @throws CoreException
   */
  public static IClassFiles[] getClassFiles()
      throws CoreException {
    List l = new ArrayList(EclEmmaCorePlugin.getInstance()
        .getClassFiles().values());
    IClassFiles[] arr = new IClassFiles[l.size()];
    return (IClassFiles[]) l.toArray(arr);
  }

  public static IClassFiles findClassFiles(
      String location) throws CoreException {
    Map map = EclEmmaCorePlugin.getInstance().getClassFiles();
    return (IClassFiles) map.get(location);
  }
  
  /**
   * Returns descriptors for class files from the class path of the given launch
   * configuration.
   * 
   * @param configuration
   *          launch configuration to look for class files 
   * @param inplace 
   *          flag whether instrumentation will happen inplace. In this case
   *          binary libraries will be excluded
   * 
   * @return descriptors for all class for instrumentation
   *   
   * @throws CoreException
   */
  public static IClassFiles[] getClassFiles(ILaunchConfiguration configuration, boolean inplace) throws CoreException {
    List l = new ArrayList();
    IRuntimeClasspathEntry[] entries = JavaRuntime
        .computeUnresolvedRuntimeClasspath(configuration);
    entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
    for (int i = 0; i < entries.length; i++) {
      if (entries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
        IClassFiles ic = findClassFiles(entries[i].getLocation());
        if (ic != null && (!inplace || !ic.isBinary())) {
          l.add(ic);
        }
      }
    }
    IClassFiles[] arr = new IClassFiles[l.size()];
    return (IClassFiles[]) l.toArray(arr);
  }

  /**
   * Returns descriptors for class files for instrumentation as configured by
   * the given launch configuration.
   * 
   * @param configuration
   *          launch configuration to look for class files 
   * @param inplace 
   *          flag whether instrumentation will happen inplace. In this case
   *          binary libraries will be excluded
   * 
   * @return descriptors for all class for instrumentation
   *   
   * @throws CoreException
   */
  public static IClassFiles[] getClassFilesForInstrumentation(ILaunchConfiguration configuration, boolean inplace) throws CoreException {
    IClassFiles[] all = getClassFiles(configuration, inplace);
    List filtered = new ArrayList();
    List selection = 
      configuration.getAttribute(ICoverageLaunchConfigurationConstants.ATTR_INSTRUMENTATION_PATHS, (List) null);
    for (int i = 0; i < all.length; i++) {
      if (selection == null) {
        // by default we select all source based class files
        if (!all[i].isBinary()) {
          filtered.add(all[i]);
        }
      } else {
        if (selection.contains(all[i].getLocation().toString())) {
          filtered.add(all[i]);
        }
      }
    }
    return (IClassFiles[]) filtered.toArray(new IClassFiles[filtered.size()]);
  }
  
  public static ICoverageSession createCoverageSession(String description,
      IInstrumentation[] instrumentations, IPath[] coveragedatafiles,
      ILaunchConfiguration launchconfiguration) {
    return new CoverageSession(description, instrumentations, coveragedatafiles,
        launchconfiguration);
  }

  public static IJavaModelCoverage getJavaModelCoverage() {
    return EclEmmaCorePlugin.getInstance().getJavaCoverageLoader()
        .getJavaModelCoverage();
  }
  
  public static void addJavaCoverageListener(IJavaCoverageListener l) {
    EclEmmaCorePlugin.getInstance().getJavaCoverageLoader().addJavaCoverageListener(l);
  }

  public static void removeJavaCoverageListener(IJavaCoverageListener l) {
    EclEmmaCorePlugin.getInstance().getJavaCoverageLoader().removeJavaCoverageListener(l);
  }

}
