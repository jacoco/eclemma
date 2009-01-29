/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.launching;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.instr.InstrMarker;
import com.mountainminds.eclemma.internal.core.launching.CoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.launching.InstrumentedClasspathProvider;
import com.vladium.emma.AppLoggers;
import com.vladium.emma.EMMAProperties;

/**
 * Abstract base class for coverage mode launchers. Coverage launchers perform
 * class instrumentation and then delegate to the corresponding launcher
 * responsible for the "run" mode.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public abstract class CoverageLauncher implements ICoverageLauncher,
    IExecutableExtension {

  /**
   * Name of the file that will EMMA pick from the classpath to reads its
   * properties.
   */
  protected static final String EMMA_PROPERTIES_FILE = "emma.properties"; //$NON-NLS-1$

  /** Launch mode for the launch delegates used internally. */
  public static final String DELEGATELAUNCHMODE = ILaunchManager.RUN_MODE;

  protected String launchtype;

  protected ILaunchConfigurationDelegate launchdelegate;

  protected ILaunchConfigurationDelegate2 launchdelegate2;

  /**
   * Hook method to modify the launch configuration before it is passed on to
   * the delegate launcher.
   * 
   * @param workingcopy
   *          Configuration to modify
   * @param info
   *          Info object of this launch
   * @throws CoreException
   *           may be thrown by implementations
   */
  protected void modifyConfiguration(
      ILaunchConfigurationWorkingCopy workingcopy, ICoverageLaunchInfo info)
      throws CoreException {
    // Does nothing by default
  }

  /**
   * Returns whether in-place instrumentation should be performed. The default
   * implementation looks-up the corresponding entry in the passed launch
   * configuration. Specific launchers may modify this behavior.
   * 
   * @param configuration
   *          launch configuration for coverage run
   * @return true, if instrumentation should be performed in-place
   * @throws CoreException
   *           May be thrown when accessing the launch configuration
   */
  protected boolean hasInplaceInstrumentation(ILaunchConfiguration configuration)
      throws CoreException {
    return configuration.getAttribute(
        ICoverageLaunchConfigurationConstants.ATTR_INPLACE_INSTRUMENTATION,
        false);
  }

  /**
   * Creates the a JAR file including the <code>emma.properties</code> file that
   * will be injected in the class path.
   * 
   * @param configuration
   *          Configuration object for this launch
   * @param info
   *          Launch Info object of this launch
   * @throws CoreException
   *           Thrown when the JAR file cannot be created
   */
  private void createPropertiesJAR(ILaunchConfiguration configuration,
      ICoverageLaunchInfo info) throws CoreException {
    Properties properties = new Properties();
    properties.put(EMMAProperties.PROPERTY_COVERAGE_DATA_OUT_FILE, info
        .getCoverageFile().toOSString());
    properties.put(AppLoggers.PROPERTY_VERBOSITY_LEVEL,
        DebugOptions.EMMAVERBOSITYLEVEL);
    IPath jarfile = info.getPropertiesJARFile();
    Manifest mf = new Manifest();
    try {
      JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarfile
          .toFile()), mf);
      jar.putNextEntry(new ZipEntry(EMMA_PROPERTIES_FILE));
      properties.store(jar,
          "Created for launch configuration " + configuration.getName()); //$NON-NLS-1$
      jar.close();
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.EMMA_PROPERTIES_CREATION_ERROR
          .getStatus(jarfile, e));
    }
  }

  // IExecutableExtension interface:

  public void setInitializationData(IConfigurationElement config,
      String propertyName, Object data) throws CoreException {
    launchtype = config.getAttribute("type"); //$NON-NLS-1$
    launchdelegate = getLaunchDelegate(launchtype);
    if (launchdelegate instanceof ILaunchConfigurationDelegate2) {
      launchdelegate2 = (ILaunchConfigurationDelegate2) launchdelegate;
    }
  }

  private ILaunchConfigurationDelegate getLaunchDelegate(String launchtype)
      throws CoreException {
    ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
        .getLaunchConfigurationType(launchtype);
    if (type == null) {
      throw new CoreException(EclEmmaStatus.UNKOWN_LAUNCH_TYPE_ERROR
          .getStatus(launchtype));
    }
    return type.getDelegate(DELEGATELAUNCHMODE);
  }

  // ILaunchConfigurationDelegate interface:

  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(NLS.bind(CoreMessages.Launching_task, configuration
        .getName()), 2);
    IRuntimeClasspathProvider provider = JavaRuntime
        .getClasspathProvider(configuration);
    ICoverageLaunchInfo info = CoverageTools.getLaunchInfo(launch);
    if (info == null) {
      // Must not happen as we should have created the launch
      throw new CoreException(EclEmmaStatus.MISSING_LAUNCH_INFO_ERROR
          .getStatus(null));
    }
    info.instrument(new SubProgressMonitor(monitor, 1),
        hasInplaceInstrumentation(configuration));
    if (monitor.isCanceled()) {
      return;
    }
    createPropertiesJAR(configuration, info);
    ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
    modifyConfiguration(wc, info);
    InstrumentedClasspathProvider.enable(provider, info);
    try {
      launchdelegate.launch(wc, DELEGATELAUNCHMODE, launch,
          new SubProgressMonitor(monitor, 1));
    } finally {
      InstrumentedClasspathProvider.disable();
    }
    monitor.done();
  }

  // ILaunchConfigurationDelegate2 interface:

  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
      throws CoreException {
    ILaunch launch = new Launch(configuration, CoverageTools.LAUNCH_MODE, null);
    new CoverageLaunchInfo(launch);
    return launch;
  }

  public boolean buildForLaunch(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) throws CoreException {
    if (launchdelegate2 == null) {
      return true;
    } else {
      return launchdelegate2.buildForLaunch(configuration, DELEGATELAUNCHMODE,
          monitor);
    }
  }

  public boolean preLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) throws CoreException {
    boolean inplace = hasInplaceInstrumentation(configuration);
    if (CoverageTools.getClassFilesForInstrumentation(configuration, inplace).length == 0) {
      IStatus status = EclEmmaStatus.NO_INSTRUMENTED_CLASSES.getStatus();
      EclEmmaCorePlugin.getInstance().showPrompt(status, configuration);
      return false;
    }
    if (inplace) {
      // Issue an inplace instrumentation warning:
      IStatus status = EclEmmaStatus.INPLACE_INSTRUMENTATION_INFO.getStatus();
      if (!EclEmmaCorePlugin.getInstance().showPrompt(status, configuration)) {
        return false;
      }
    } else {
      // check whether inpace instrumentation has been performed before
      if (checkForPreviousInplace(configuration)) {
        IStatus status = EclEmmaStatus.ALREADY_INSTRUMENTED_ERROR.getStatus();
        EclEmmaCorePlugin.getInstance().showPrompt(status, configuration);
        return false;
      }
    }
    // Then allow the delegate's veto:
    if (launchdelegate2 == null) {
      return true;
    } else {
      return launchdelegate2.preLaunchCheck(configuration, DELEGATELAUNCHMODE,
          monitor);
    }
  }

  private boolean checkForPreviousInplace(ILaunchConfiguration config)
      throws CoreException {
    IClassFiles[] classfiles = CoverageTools.getClassFilesForInstrumentation(
        config, false);
    for (int i = 0; i < classfiles.length; i++) {
      if (InstrMarker.isMarked(classfiles[i].getLocation())) {
        return true;
      }
    }
    return false;
  }

  public boolean finalLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) throws CoreException {
    if (launchdelegate2 == null) {
      return true;
    } else {
      return launchdelegate2.finalLaunchCheck(configuration,
          DELEGATELAUNCHMODE, monitor);
    }
  }

  // ICoverageLauncher interface:

  /*
   * The default implemenation delegates to the classpath provider.
   */
  public IClassFiles[] getClassFiles(ILaunchConfiguration configuration,
      boolean includebinaries) throws CoreException {
    List l = new ArrayList();
    IRuntimeClasspathEntry[] entries = JavaRuntime
        .computeUnresolvedRuntimeClasspath(configuration);
    entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
    for (int i = 0; i < entries.length; i++) {
      if (entries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
        IClassFiles ic = CoverageTools
            .getClassFilesAtAbsoluteLocation(entries[i].getLocation());
        if (ic != null && (includebinaries || !ic.isBinary())) {
          l.add(ic);
        }
      }
    }
    IClassFiles[] arr = new IClassFiles[l.size()];
    return (IClassFiles[]) l.toArray(arr);
  }

  /**
   * Internal utility to find the {@link IClassFiles} descriptor for the given
   * class path location.
   * 
   * @param location
   *          class path location
   * @return descriptor or <code>null</code>
   * @throws CoreException
   *           in case of internal inconsistencies
   * @deprecated please user
   *             {@link CoverageTools#getClassFilesAtAbsoluteLocation(String)}
   *             instead
   */
  protected IClassFiles findClassFiles(String location) throws CoreException {
    return CoverageTools.getClassFilesAtAbsoluteLocation(location);
  }

}
