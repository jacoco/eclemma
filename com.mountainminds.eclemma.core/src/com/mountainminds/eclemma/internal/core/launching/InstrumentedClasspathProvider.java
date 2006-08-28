/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;
import org.eclipse.jdt.launching.JavaRuntime;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IInstrumentation;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * Class path provider used internally to inject instrumented classes and the
 * Emma runtime.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class InstrumentedClasspathProvider implements IRuntimeClasspathProvider {

  public static final String ID = "com.mountainminds.eclemma.core.instrumentedClasspathProvider"; //$NON-NLS-1$

  private static final ITracer TRACER = DebugOptions.LAUNCHINGTRACER;

  private static final ThreadLocal originalClasspathProvider = new ThreadLocal();

  private static final ThreadLocal launchInfo = new ThreadLocal();

  public static void enable(
      IRuntimeClasspathProvider originalClasspathProvider,
      ICoverageLaunchInfo launchInfo) {
    InstrumentedClasspathProvider.originalClasspathProvider.set(originalClasspathProvider);
    InstrumentedClasspathProvider.launchInfo.set(launchInfo);
  }

  public static void disable() {
    InstrumentedClasspathProvider.originalClasspathProvider.set(null);
    InstrumentedClasspathProvider.launchInfo.set(null);
  }

  private static IRuntimeClasspathProvider getOriginalClasspathProvider()
      throws CoreException {
    Object obj = originalClasspathProvider.get();
    if (obj == null) {
      throw new CoreException(EclEmmaStatus.INVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR.getStatus(null));
    }
    return (IRuntimeClasspathProvider) obj;
  }

  private static ICoverageLaunchInfo getLaunchInfo() throws CoreException {
    Object obj = launchInfo.get();
    if (obj == null) {
      throw new CoreException(EclEmmaStatus.INVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR.getStatus(null));
    }
    return (ICoverageLaunchInfo) obj;
  }

  // IRuntimeClasspathProvider implementation

  public IRuntimeClasspathEntry[] computeUnresolvedClasspath(
      ILaunchConfiguration configuration) throws CoreException {
    IRuntimeClasspathEntry[] entries = getOriginalClasspathProvider().computeUnresolvedClasspath(
        configuration);
    TRACER.trace("computeUnresolvedClasspath() -> {0}", Arrays.asList(entries)); //$NON-NLS-1$
    return entries;
  }

  public IRuntimeClasspathEntry[] resolveClasspath(
      IRuntimeClasspathEntry[] entries, ILaunchConfiguration configuration)
      throws CoreException {
    TRACER.trace("resolveClasspath()"); //$NON-NLS-1$
    ICoverageLaunchInfo info = getLaunchInfo();
    entries = getOriginalClasspathProvider().resolveClasspath(entries, configuration);
    List newentries = new ArrayList();
    boolean emmartinserted = false;
    for (int i = 0; i < entries.length; i++) {
      if (entries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
        TRACER.trace("Resolved classpath entry: {0}", entries[i].getLocation()); //$NON-NLS-1$
        IInstrumentation instr = info.getInstrumentation(entries[i].getLocation());
        if (instr != null) {
          TRACER.trace("Found instrumented classes for {0}", entries[i].getLocation()); //$NON-NLS-1$
          if (!emmartinserted) {
            addEmmaRuntime(info, newentries);
            emmartinserted = true;
          }
          if (!instr.isInplace()) {
            newentries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(instr.getOutputLocation()));
          }
        }
      }
      newentries.add(entries[i]);
    }
    IRuntimeClasspathEntry[] arr = new IRuntimeClasspathEntry[newentries.size()];
    return (IRuntimeClasspathEntry[]) newentries.toArray(arr);
  }
  
  protected void addEmmaRuntime(ICoverageLaunchInfo info, List entries) throws CoreException {
    IPath propertiesjarpath = info.getPropertiesJARFile();
    entries.add(0, JavaRuntime.newArchiveRuntimeClasspathEntry(propertiesjarpath));
    entries.add(0, JavaRuntime.newArchiveRuntimeClasspathEntry(CoverageTools.getEmmaJar()));
  }

}
