/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchInfo;
import com.mountainminds.eclemma.internal.core.PlatformVersion;

/**
 * Laucher for the Eclipse runtime workbench.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: 32 $
 */
public class EclipseLauncher extends CoverageLauncher {

  /** Pre-Eclipse 3.2.0 VM arguments key */ 
  protected static final String PRE320VMARGS = "vmargs"; //$NON-NLS-1$
  protected static final String VMARGS = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
  
  public static final String BOOTPATHARG = "-Xbootclasspath/a:"; //$NON-NLS-1$

  /**
   * Returns the proper key for VM arguments depending on the Eclipse version
   * 
   * @return  launch configuration key for VM arguments
   */
  protected String getVMArgsKey() {
    boolean is320 = PlatformVersion.CURRENT.isGreaterOrEqualTo(PlatformVersion.V320);
    return is320 ? VMARGS : PRE320VMARGS;
  }
  
  protected boolean hasInplaceInstrumentation(ILaunchConfiguration configuration) {
    // Inplace instrumentation is required for plugin launches, as we can't
    // modify the classpath
    return true;
  }

  protected void modifyConfiguration(ILaunchConfigurationWorkingCopy workingcopy,
      ICoverageLaunchInfo info) throws CoreException {
    String vmargskey = getVMArgsKey(); 
    StringBuffer sb = new StringBuffer(workingcopy.getAttribute(vmargskey, "")); //$NON-NLS-1$
    sb.append(" ").append(BOOTPATHARG); //$NON-NLS-1$
    sb.append(info.getPropertiesJARFile());
    sb.append(";").append(CoverageTools.getEmmaJar().toOSString()); //$NON-NLS-1$
    workingcopy.setAttribute(vmargskey, sb.toString());
  }

}
