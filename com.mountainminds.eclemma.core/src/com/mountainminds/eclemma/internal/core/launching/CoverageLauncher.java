/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core.launching;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchConfigurationConstants;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.instr.InstrMarker;
import com.vladium.emma.AppLoggers;
import com.vladium.emma.EMMAProperties;

/**
 * Abstract base class for coverage mode launchers. Coverage launchers
 * perform class instrumentation and then delegate to the corresponding
 * launcher responsible for the "run" mode.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public abstract class CoverageLauncher implements ILaunchConfigurationDelegate2 {

  /** Status used to trigger user prompts */
  protected static final IStatus PROMPT_STATUS = new Status(IStatus.INFO,
      "org.eclipse.debug.ui", 200, "", null); //$NON-NLS-1$//$NON-NLS-2$

  /** Launch mode for the launch delegates used internally. */
  public static final String DELEGATELAUNCHMODE = ILaunchManager.RUN_MODE;

  protected final String launchtypeid;

  protected final ILaunchConfigurationDelegate launchdelegate;

  protected final ILaunchConfigurationDelegate2 launchdelegate2;

  protected CoverageLauncher(String launchtypeid) throws CoreException {
    this.launchtypeid = launchtypeid;
    launchdelegate = getLaunchDelegate(launchtypeid);
    if (launchdelegate instanceof ILaunchConfigurationDelegate2) {
      launchdelegate2 = (ILaunchConfigurationDelegate2) launchdelegate;
    } else {
      launchdelegate2 = null;
    }
  }

  private ILaunchConfigurationDelegate getLaunchDelegate(String launchtypeid)
      throws CoreException {
    ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
        .getLaunchConfigurationType(launchtypeid);
    if (type == null) {
      throw new CoreException(EclEmmaStatus.UNKOWN_LAUNCH_TYPE_ERROR.getStatus(
          launchtypeid, null));
    }
    return type.getDelegate(DELEGATELAUNCHMODE);
  }

  /**
   * Hook method to modify the launch configuration before it is passed on to
   * the delegate launcher.
   *  
   * @param workingcopy
   *          Configuration to modify
   * @param info
   *          Info object of this launch
   * @throws CoreException
   *          may be thrown by implementations
   */
  protected void modifyConfiguration(ILaunchConfigurationWorkingCopy workingcopy,
                                     ICoverageLaunchInfo info) throws CoreException {
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
   *   May be thrown when accessing the launch configuration
   */
  protected boolean hasInplaceInstrumentation(ILaunchConfiguration configuration)
      throws CoreException {
    return configuration.getAttribute(
        ICoverageLaunchConfigurationConstants.ATTR_INPLACE_INSTRUMENTATION,
        false);
  }

  /**
   * Creates the a JAR file includung the emma.properties file that will be
   * injected in the class path.
   * 
   * @param configuration
   *   Configuration object for this launch
   * @param info
   *   Launch Info object of this launch
   * @throws CoreException
   *   Thrown when the JAR file cannot be created
   */
  private void createPropertiesJAR(ILaunchConfiguration configuration, ICoverageLaunchInfo info) throws CoreException {
    Properties properties = new Properties();
    properties.put(EMMAProperties.PROPERTY_COVERAGE_DATA_OUT_FILE, info.getCoverageFile().toOSString());
    properties.put(AppLoggers.PROPERTY_VERBOSITY_LEVEL, DebugOptions.EMMAVERBOSITYLEVEL);
    IPath jarfile = info.getPropertiesJARFile();
    Manifest mf = new Manifest();
    try {
      JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarfile
          .toFile()), mf);
      jar.putNextEntry(new ZipEntry("emma.properties"));
      properties.store(jar, "Created for launch configuration " + configuration.getName());
      jar.close();
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.EMMA_PROPERTIES_CREATION_ERROR.getStatus(jarfile, e));
    }
  }
  
  // ILaunchConfigurationDelegate interface:

  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(NLS.bind(CoreMessages.LaunchingTask, configuration
        .getName()), 2);
    IRuntimeClasspathProvider provider = JavaRuntime
        .getClasspathProvider(configuration);
    ICoverageLaunchInfo info = CoverageTools.getLaunchInfo(launch);
    if (info == null) {
      // Must not happen as we should have created the launch
      throw new CoreException(EclEmmaStatus.MISSING_LAUNCH_INFO_ERROR.getStatus(null));
    }
    info.instrument(new SubProgressMonitor(monitor, 1), hasInplaceInstrumentation(configuration));
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
    if (hasInplaceInstrumentation(configuration)) {
      // Issue an inplace instrumentation warning:
      IStatus status = EclEmmaStatus.INPLACE_INSTRUMENTATION_INFO.getStatus();
      if (!showPrompt(status, configuration)) {
          return false;
      }
    } else {
      // check whether inpace instrumentation has been performed before
      if (checkForPreviousInplace(configuration)) {
        IStatus status = EclEmmaStatus.ALREADY_INSTRUMENTED_ERROR.getStatus();
        showPrompt(status, configuration);
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
  
  private boolean showPrompt(IStatus status, Object info) throws CoreException {
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
  
  private boolean checkForPreviousInplace(ILaunchConfiguration conig) throws CoreException {
    IClassFiles[] classfiles = CoverageTools.getClassFilesForInstrumentation(conig, false);
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

}
