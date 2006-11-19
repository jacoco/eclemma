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
 * The interface for coverage information attached to the Java model. It allows
 * to retrieve coverage information for any Java model element and holds lists
 * of entry points.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface IJavaModelCoverage extends IJavaElementCoverage {
  
  /**
   * This instance is used to indicate that a coverage session is currently
   * loading.
   */
  public static final IJavaModelCoverage LOADING = new IJavaModelCoverage() {

    public ILineCoverage getLineCoverage() {
      return null;
    }

    public ICounter getBlockCounter() {
      return Counter.COUNTER_0_0;
    }

    public ICounter getLineCounter() {
      return Counter.COUNTER_0_0;
    }

    public ICounter getInstructionCounter() {
      return Counter.COUNTER_0_0;
    }

    public ICounter getMethodCounter() {
      return Counter.COUNTER_0_0;
    }

    public ICounter getTypeCounter() {
      return Counter.COUNTER_0_0;
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
  
  /**
   * Returns all Java projects where coverage information is available for.
   * 
   * @return  list of Java projects
   */
  public IJavaProject[] getInstrumentedProjects();

  /**
   * Returns all package fragment roots where coverage information is available
   * for.
   * 
   * @return  list of package fragment roots.
   */
  public IPackageFragmentRoot[] getInstrumentedPackageFragmentRoots();

  /**
   * Returns all package fragments where coverage information is available for.
   * 
   * @return  list of package fragments
   */
  public IPackageFragment[] getInstrumentedPackageFragments();

  /**
   * Returns all Java types where coverage information is available for.
   * 
   * @return  list of Java types
   */
  public IType[] getInstrumentedTypes();
  
  /**
   * Returns the coverage information associated with the given Java element. If
   * no information is available <code>null</code> is returned.
   * 
   * @param element  Java element to look for coverage information
   * @return  associated coverage information of null
   */
  public IJavaElementCoverage getCoverageFor(IJavaElement element);
  
}
