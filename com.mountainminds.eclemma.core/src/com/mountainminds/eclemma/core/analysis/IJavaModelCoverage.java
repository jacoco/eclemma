/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.core.analysis;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.internal.analysis.CounterImpl;

/**
 * The interface for coverage information attached to the Java model. It allows
 * to retrieve coverage information for any Java model element and holds lists
 * of entry points.
 */
public interface IJavaModelCoverage extends ICoverageNode {

  /**
   * This instance is used to indicate that a coverage session is currently
   * loading.
   */
  public static final IJavaModelCoverage LOADING = new IJavaModelCoverage() {

    public ElementType getElementType() {
      return ElementType.GROUP;
    }

    public String getName() {
      return "LOADING"; //$NON-NLS-1$
    }

    public ICounter getInstructionCounter() {
      return CounterImpl.COUNTER_0_0;
    }

    public ICounter getBranchCounter() {
      return CounterImpl.COUNTER_0_0;
    }

    public ICounter getLineCounter() {
      return CounterImpl.COUNTER_0_0;
    }

    public ICounter getComplexityCounter() {
      return CounterImpl.COUNTER_0_0;
    }

    public ICounter getMethodCounter() {
      return CounterImpl.COUNTER_0_0;
    }

    public ICounter getClassCounter() {
      return CounterImpl.COUNTER_0_0;
    }

    public ICounter getCounter(CounterEntity entity) {
      return CounterImpl.COUNTER_0_0;
    }

    public ICoverageNode getPlainCopy() {
      return this;
    }

    public IJavaProject[] getProjects() {
      return new IJavaProject[0];
    }

    public IPackageFragmentRoot[] getPackageFragmentRoots() {
      return new IPackageFragmentRoot[0];
    }

    public IPackageFragment[] getPackageFragments() {
      return new IPackageFragment[0];
    }

    public IType[] getTypes() {
      return new IType[0];
    }

    public ICoverageNode getCoverageFor(IJavaElement element) {
      return null;
    }

  };

  /**
   * Returns all Java projects where coverage information is available for.
   * 
   * @return list of Java projects
   */
  public IJavaProject[] getProjects();

  /**
   * Returns all package fragment roots where coverage information is available
   * for.
   * 
   * @return list of package fragment roots.
   */
  public IPackageFragmentRoot[] getPackageFragmentRoots();

  /**
   * Returns all package fragments where coverage information is available for.
   * 
   * @return list of package fragments
   */
  public IPackageFragment[] getPackageFragments();

  /**
   * Returns all Java types where coverage information is available for.
   * 
   * @return list of Java types
   */
  public IType[] getTypes();

  /**
   * Returns the coverage information associated with the given Java element. If
   * no information is available <code>null</code> is returned.
   * 
   * @param element
   *          Java element to look for coverage information
   * @return associated coverage information of null
   */
  public ICoverageNode getCoverageFor(IJavaElement element);

}
