/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;

/**
 * The IJavaModelCoverage implementation maps Java elements to its corresponding
 * coverage data objects.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaModelCoverage extends JavaElementCoverage implements
    IJavaModelCoverage {

  /** Maps Java elements to coverage objects */
  private final Map coveragemap = new HashMap();
  
  /** List of all IJavaProject objects with coverage information attached */  
  private final List projects = new ArrayList();

  /** List of all IPackageFragmentRoot objects with coverage information attached */  
  private final List fragmentroots = new ArrayList();

  /** List of all IPackageFragment objects with coverage information attached */  
  private final List fragments = new ArrayList();

  /** List of all IType objects with coverage information attached */  
  private final List types = new ArrayList();

  public JavaModelCoverage() {
    super(null, false, 0);
  }
  
  public void put(IJavaElement element, IJavaElementCoverage coverage) {
    coveragemap.put(element, coverage);
    switch (element.getElementType()) {
      case IJavaElement.JAVA_PROJECT:
        projects.add(element);
        break;
      case IJavaElement.PACKAGE_FRAGMENT_ROOT:
        fragmentroots.add(element);
        break;
      case IJavaElement.PACKAGE_FRAGMENT:
        fragments.add(element);
        break;
      case IJavaElement.TYPE:
        types.add(element);
        break;
    }
  }
  
  // IJavaModelCoverage interface

  public IJavaProject[] getInstrumentedProjects() {
    IJavaProject[] arr = new IJavaProject[projects.size()];
    return (IJavaProject[]) projects.toArray(arr);
  }

  public IPackageFragmentRoot[] getInstrumentedPackageFragmentRoots() {
    IPackageFragmentRoot[] arr = new IPackageFragmentRoot[fragmentroots.size()];
    return (IPackageFragmentRoot[]) fragmentroots.toArray(arr);
  }

  public IPackageFragment[] getInstrumentedPackageFragments() {
    IPackageFragment[] arr = new IPackageFragment[fragments.size()];
    return (IPackageFragment[]) fragments.toArray(arr);
  }

  public IType[] getInstrumentedTypes() {
    IType[] arr = new IType[types.size()];
    return (IType[]) types.toArray(arr);
  }

  public IJavaElementCoverage getCoverageFor(IJavaElement element) {
    IJavaElementCoverage c = (IJavaElementCoverage) coveragemap.get(element);
    // Currently lazy binding is for methods only:
    if (c == null && element.getElementType() == IJavaElement.METHOD) {
      IJavaElement parent = element.getParent();
      Object parentcoverage = getCoverageFor(parent);
      if (parentcoverage instanceof ILazyBinding) {
        ((ILazyBinding) parentcoverage).resolve(parent, this);
      }
      c = (IJavaElementCoverage) coveragemap.get(element);
    }
    return c;
  }

}
