/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchConfigurationConstants;

/**
 * Internal utility to track selected package fragment roots.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class ClassesSelection {
  
  private boolean inplace;
  private IClassFiles[] allclasses;
  private Set selectedclasses;
  
  public void init(ILaunchConfiguration conf, boolean forceInplace) throws CoreException {
    inplace = forceInplace
    || conf.getAttribute(
            ICoverageLaunchConfigurationConstants.ATTR_INPLACE_INSTRUMENTATION, false);
    allclasses = CoverageTools.getClassFiles(conf, false);
    selectedclasses = new HashSet(Arrays.asList(CoverageTools.getClassFilesForInstrumentation(conf, inplace)));
  }

  public void setInplace(boolean inplace) {
    this.inplace = inplace;
  }
  
  public boolean getInplace() {
    return inplace;
  }
  
  public IPackageFragmentRoot[] getAllRoots() {
    return getPackageFragmentRoots(Arrays.asList(allclasses));
  }

  public IPackageFragmentRoot[] getSelectedRoots() {
    return getPackageFragmentRoots(selectedclasses);
  }
  
  public void setSelection(IPackageFragmentRoot root, boolean selected) {
    for (int i = 0; i < allclasses.length; i++) {
      IClassFiles cf = allclasses[i];
      if (Arrays.asList(cf.getPackageFragmentRoots()).contains(root)) {
        if (selected) {
          selectedclasses.add(cf);
        } else {
          selectedclasses.remove(cf);
        }
        break;
      }
    }
  }
  
  public void save(ILaunchConfigurationWorkingCopy conf) {
    conf.setAttribute(
        ICoverageLaunchConfigurationConstants.ATTR_INPLACE_INSTRUMENTATION,
        inplace);
    List l = new ArrayList();
    for (Iterator i = selectedclasses.iterator(); i.hasNext(); ) {
      IClassFiles cf = (IClassFiles) i.next();
      l.add(cf.getLocation().toString());
    }
    conf.setAttribute(ICoverageLaunchConfigurationConstants.ATTR_INSTRUMENTATION_PATHS, l);
  }
  
  private IPackageFragmentRoot[] getPackageFragmentRoots(Collection classfiles) {
    Set roots = new HashSet();
    for (Iterator i = classfiles.iterator(); i.hasNext(); ) {
      IClassFiles cf = (IClassFiles) i.next();
      roots.addAll(Arrays.asList(cf.getPackageFragmentRoots()));
    }
    return (IPackageFragmentRoot[]) roots.toArray(new IPackageFragmentRoot[roots.size()]);
  }
  
}
