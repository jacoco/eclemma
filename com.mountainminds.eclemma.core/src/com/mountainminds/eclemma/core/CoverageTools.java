/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.core.analysis.ILineCoverage;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchConfigurationConstants;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.core.launching.ICoverageLauncher;
import com.mountainminds.eclemma.internal.core.CoverageSession;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.SessionExporter;
import com.mountainminds.eclemma.internal.core.SessionImporter;
import com.mountainminds.eclemma.internal.core.instr.DefaultInstrumentationFilter;
import com.mountainminds.eclemma.internal.core.launching.CoverageLaunchInfo;

/**
 * For central access to the tools provided by the coverage core plugin this
 * class offers several static methods.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public final class CoverageTools {

  /**
   * The launch mode used for coverage sessions.
   */
  public static final String LAUNCH_MODE = "coverage"; //$NON-NLS-1$

  public static final int EXPORT_HTML = 0;
  public static final int EXPORT_XML = 1;
  public static final int EXPORT_TEXT = 2;
  public static final int EXPORT_ES = 3;

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
   * If no info object is assoziated with the given launch <code>null</code> is
   * returned, i.e. the launch was probably not in coverage mode.
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
   * @throws CoreException
   */
  public static IClassFiles[] getClassFiles() throws CoreException {
    return EclEmmaCorePlugin.getInstance().getAllClassFiles().getClassFiles();
  }

  /**
   * Returns descriptors the location at the given absolute location on the
   * local system.
   * 
   * @return {@link IClassFiles} at the given location or <code>null</code> if
   *         not found
   * @throws CoreException
   */
  public static IClassFiles getClassFilesAtAbsoluteLocation(String location)
      throws CoreException {
    return EclEmmaCorePlugin.getInstance().getAllClassFiles()
        .getAtAbsoluteLocation(location);
  }

  /**
   * Returns descriptors for class files for the given launch configuration.
   * 
   * @param configuration
   *          launch configuration to look for class files
   * @param includebinaries
   *          flag whether binary classpath entries should be included
   * 
   * @return descriptors for all class files
   * 
   * @throws CoreException
   */
  public static IClassFiles[] getClassFiles(ILaunchConfiguration configuration,
      boolean includebinaries) throws CoreException {
    ICoverageLauncher launcher = (ICoverageLauncher) configuration.getType()
        .getDelegate(LAUNCH_MODE);
    return launcher.getClassFiles(configuration, includebinaries);
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
  public static IClassFiles[] getClassFilesForInstrumentation(
      final ILaunchConfiguration configuration, final boolean inplace)
      throws CoreException {
    final IClassFiles[] all = getClassFiles(configuration, !inplace);
    final List selection = configuration.getAttribute(
        ICoverageLaunchConfigurationConstants.ATTR_INSTRUMENTATION_PATHS,
        (List) null);
    if (selection != null && selection.size() > 0) {
      // Classes for instrumentation are already configured:
      final List filtered = new ArrayList();
      for (int i = 0; i < all.length; i++) {
        if (selection.contains(all[i].getLocation().toString())) {
          filtered.add(all[i]);
        }
      }
      return (IClassFiles[]) filtered.toArray(new IClassFiles[filtered.size()]);
    }
    // Otherwise we use a default selection:
    final DefaultInstrumentationFilter filter = EclEmmaCorePlugin.getInstance()
        .createDefaultIntrumentationFilter();
    return filter.filter(all, configuration);
  }

  public static ICoverageSession createCoverageSession(String description,
      IInstrumentation[] instrumentations, IPath[] coveragedatafiles,
      ILaunchConfiguration launchconfiguration) {
    return new CoverageSession(description, instrumentations,
        coveragedatafiles, launchconfiguration);
  }

  public static IJavaModelCoverage getJavaModelCoverage() {
    return EclEmmaCorePlugin.getInstance().getJavaCoverageLoader()
        .getJavaModelCoverage();
  }

  public static void addJavaCoverageListener(IJavaCoverageListener l) {
    EclEmmaCorePlugin.getInstance().getJavaCoverageLoader()
        .addJavaCoverageListener(l);
  }

  public static void removeJavaCoverageListener(IJavaCoverageListener l) {
    EclEmmaCorePlugin.getInstance().getJavaCoverageLoader()
        .removeJavaCoverageListener(l);
  }

  public static ISessionExporter getExporter(ICoverageSession session) {
    return new SessionExporter(session);
  }

  public static ISessionImporter getImporter() {
    return new SessionImporter();
  }

  /**
   * Sets a {@link ICorePreferences} instance which will be used by the EclEmma
   * core to query preference settings if required.
   * 
   * @param preferences
   *          callback object for preference settings
   */
  public static void setPreferences(ICorePreferences preferences) {
    EclEmmaCorePlugin.getInstance().setPreferences(preferences);
  }

}
