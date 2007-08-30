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
import java.util.WeakHashMap;

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
import com.mountainminds.eclemma.internal.core.StateFiles;

/**
 * Implementation of {@link ICoverageLaunchInfo}.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageLaunchInfo implements ICoverageLaunchInfo {

  private static int idcounter = (int) System.currentTimeMillis();
  private static final Map instances = new WeakHashMap();

  private final String id;
  private final ILaunchConfiguration configuration;
  private IPath coveragefile;
  private IPath propertiesjarfile;
  private final List instrumentations;
  private final Map instrumentationpaths;

  public CoverageLaunchInfo(ILaunch launch) {
      id = Integer.toHexString(idcounter++);
      instances.put(id, this);
      configuration = launch.getLaunchConfiguration();
      StateFiles statefiles = EclEmmaCorePlugin.getInstance().getStateFiles();
      IPath base = statefiles.getLaunchDataFolder().append(id);
      coveragefile = base.addFileExtension("ec"); //$NON-NLS-1$
      statefiles.registerForCleanup(coveragefile);
      propertiesjarfile = base.addFileExtension("jar"); //$NON-NLS-1$
      statefiles.registerForCleanup(propertiesjarfile);
      instrumentations = new ArrayList();
      instrumentationpaths = new HashMap();
      instances.put(launch, this);
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
    return (ICoverageLaunchInfo) instances.get(launch);
  }

  // ICoverageLaunchInfo interface

  public IPath getCoverageFile() {
    return coveragefile;
  }

  public IPath getPropertiesJARFile() {
    return propertiesjarfile;
  }

  public void instrument(IProgressMonitor monitor, boolean inplace) throws CoreException {
    instrumentations.clear();
    instrumentationpaths.clear();
    IClassFiles[] classfiles = CoverageTools.getClassFilesForInstrumentation(
        configuration, inplace);
    monitor.beginTask(CoreMessages.InstrumentingClasses_task,
        classfiles.length);
    for (int i = 0; i < classfiles.length; i++) {
      if (monitor.isCanceled()) {
        return;
      }
      monitor.subTask(NLS.bind(CoreMessages.InstrumentingClassesIn_task, classfiles[i].getLocation()));
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
    // TODO check why this is still necessary, someone seems to hold a reference
    // to the launch objects. 
    coveragefile = null;
    propertiesjarfile = null;
  }

}
