/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.IInstrumentation;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Implementation of {@link ICoverageLaunchInfo}.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageLaunchInfo implements ICoverageLaunchInfo {

  private static final String LAUNCHINFO_KEY = "com.mountainminds.eclemma.core.LAUNCHINFO";

  private static int idcounter = (int) System.currentTimeMillis();
  private static final Map instances = new HashMap();

  private final String id;
  private final ILaunchConfiguration configuration;
  private final IPath coveragefile;
  private boolean importonexit;
  private final List instrumentations;
  private final Map instrumentationpaths;

  public CoverageLaunchInfo(ILaunch launch) {
    synchronized (instances) {
      // calculate id and make an association to this object
      id = Integer.toHexString(idcounter++);
      launch.setAttribute(LAUNCHINFO_KEY, id);
      instances.put(id, this);
      configuration = launch.getLaunchConfiguration();
      coveragefile = getBase().append(id).addFileExtension("ec");
      importonexit = true;
      instrumentations = new ArrayList();
      instrumentationpaths = new HashMap();
    }
  }

  /**
   * Returns the coverage launch info that is assoziated with the given launch.
   * If no info object is assoziated with the given launch <code>null</code>
   * is returned.
   * 
   * @param launch
   *          the launch object we need coverage data for
   * @return the info data object or <code>null</code>
   */
  public static ICoverageLaunchInfo getInfo(ILaunch launch) {
    synchronized (instances) {
      String id = launch.getAttribute(LAUNCHINFO_KEY);
      return id == null ? null : (ICoverageLaunchInfo) instances.get(id);
    }
  }

  private IPath getBase() {
    return EclEmmaCorePlugin.getInstance().getStateFiles().getLaunchDataFolder();
  }

  // ICoverageLaunchInfo interface

  public IPath getCoverageFile() {
    return coveragefile;
  }

  public IPath getPropertiesJARFile() {
    return getBase().append(id).addFileExtension("jar");
  }

  public boolean getImportOnExit() {
    return importonexit;
  }

  public void instrument(IProgressMonitor monitor, boolean inplace) throws CoreException {
    instrumentations.clear();
    instrumentationpaths.clear();
    IClassFiles[] classfiles = CoverageTools.getClassFilesForInstrumentation(
        configuration, inplace);
    monitor.beginTask(CoreMessages.InstrumentingRuntimeClassesTask,
        classfiles.length);
    for (int i = 0; i < classfiles.length; i++) {
      if (monitor.isCanceled()) {
        return;
      }
      monitor.subTask(NLS.bind(CoreMessages.InstrumentingClassesInTask, classfiles[i].getLocation()));
      addInstrumentation(classfiles[i].instrument(inplace, new SubProgressMonitor(monitor, 1)));
    }
    monitor.done();
  }

  private void addInstrumentation(IInstrumentation instrumentation) {
    instrumentations.add(instrumentation);
    IPath orig = EclEmmaCorePlugin.getAbsolutePath(instrumentation.getClassFiles().getLocation());
    instrumentationpaths.put(orig.toOSString(), instrumentation);
  }

  public IInstrumentation[] getInstrumentations() {
    IInstrumentation[] a = new IInstrumentation[instrumentations.size()];
    return (IInstrumentation[]) instrumentations.toArray(a);
  }

  public IInstrumentation getInstrumentation(String originalpath) {
    return (IInstrumentation) instrumentationpaths.get(originalpath);
  }

  public void dispose() {
    synchronized (instances) {
      instances.remove(id);
      getCoverageFile().toFile().delete();
      getPropertiesJARFile().toFile().delete();
    }
  }

}
