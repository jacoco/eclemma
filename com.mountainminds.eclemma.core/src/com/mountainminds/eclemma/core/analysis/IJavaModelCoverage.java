/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core.analysis;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import com.mountainminds.eclemma.internal.core.analysis.Counter;

/**
 * TODO
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface IJavaModelCoverage extends IJavaElementCoverage {
  
  public static final IJavaModelCoverage LOADING = new IJavaModelCoverage() {

    public ILineCoverage getLineCoverage() {
      return null;
    }

    public ICounter getBlockCounter() {
      return new Counter();
    }

    public ICounter getLineCounter() {
      return new Counter();
    }

    public ICounter getInstructionCounter() {
      return new Counter();
    }

    public long getResourceModificationStamp() {
      return 0;
    }

    public IJavaProject[] getInstrumentedProjects() {
      return new IJavaProject[0];
    }

    public IPackageFragmentRoot[] getInstrumentedPackageFragmentRoots() {
      return new IPackageFragmentRoot[0];
    }

    public IPackageFragment[] getInstrumentedPackageFragments() {
      return new IPackageFragment[0];
    }

    public IType[] getInstrumentedTypes() {
      return new IType[0];
    }

    public IJavaElementCoverage getCoverageFor(IJavaElement element) {
      return null;
    }
    
  };
  
  public IJavaProject[] getInstrumentedProjects();

  public IPackageFragmentRoot[] getInstrumentedPackageFragmentRoots();

  public IPackageFragment[] getInstrumentedPackageFragments();

  public IType[] getInstrumentedTypes();
  
  public IJavaElementCoverage getCoverageFor(IJavaElement element);
  
}
