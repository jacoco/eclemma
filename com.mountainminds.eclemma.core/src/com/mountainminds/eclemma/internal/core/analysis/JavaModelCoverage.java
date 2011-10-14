/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IMethodCoverage;

import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * The IJavaModelCoverage implementation maps Java elements to its corresponding
 * coverage data objects.
 */
public class JavaModelCoverage extends CoverageNodeImpl implements
    IJavaModelCoverage {

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

  /** Maps Java elements to coverage objects */
  private final Map<IJavaElement, ICoverageNode> coveragemap = new HashMap<IJavaElement, ICoverageNode>();

  /** List of all IJavaProject objects with coverage information attached */
  private final List<IJavaProject> projects = new ArrayList<IJavaProject>();

  /**
   * List of all IPackageFragmentRoot objects with coverage information attached
   */
  private final List<IPackageFragmentRoot> fragmentroots = new ArrayList<IPackageFragmentRoot>();

  /** List of all IPackageFragment objects with coverage information attached */
  private final List<IPackageFragment> fragments = new ArrayList<IPackageFragment>();

  /** List of all IType objects with coverage information attached */
  private final List<IType> types = new ArrayList<IType>();

  public JavaModelCoverage() {
    super(ElementType.GROUP, "JavaModel"); //$NON-NLS-1$
  }

  public void putFragmentRoot(IPackageFragmentRoot fragmentroot,
      IBundleCoverage coverage) {
    coveragemap.put(fragmentroot, coverage);
    fragmentroots.add(fragmentroot);
    getProjectCoverage(fragmentroot.getJavaProject()).increment(coverage);
  }

  private CoverageNodeImpl getProjectCoverage(IJavaProject project) {
    CoverageNodeImpl coverage = (CoverageNodeImpl) coveragemap.get(project);
    if (coverage == null) {
      coverage = new CoverageNodeImpl(ElementType.GROUP,
          project.getElementName());
      coveragemap.put(project, coverage);
      projects.add(project);
    }
    return coverage;
  }

  public void putFragment(IPackageFragment element, ICoverageNode coverage) {
    coveragemap.put(element, coverage);
    fragments.add(element);
  }

  public void putType(IType element, ICoverageNode coverage) {
    coveragemap.put(element, coverage);
    types.add(element);
  }

  public void putClassFile(IClassFile element, ICoverageNode coverage) {
    coveragemap.put(element, coverage);
  }

  public void putCompilationUnit(ICompilationUnit element,
      ICoverageNode coverage) {
    coveragemap.put(element, coverage);
  }

  // IJavaModelCoverage interface

  public IJavaProject[] getInstrumentedProjects() {
    IJavaProject[] arr = new IJavaProject[projects.size()];
    return projects.toArray(arr);
  }

  public IPackageFragmentRoot[] getInstrumentedPackageFragmentRoots() {
    IPackageFragmentRoot[] arr = new IPackageFragmentRoot[fragmentroots.size()];
    return fragmentroots.toArray(arr);
  }

  public IPackageFragment[] getInstrumentedPackageFragments() {
    IPackageFragment[] arr = new IPackageFragment[fragments.size()];
    return fragments.toArray(arr);
  }

  public IType[] getInstrumentedTypes() {
    IType[] arr = new IType[types.size()];
    return types.toArray(arr);
  }

  public ICoverageNode getCoverageFor(IJavaElement element) {
    final ICoverageNode coverage = coveragemap.get(element);
    if (coverage != null) {
      return coverage;
    }
    if (IJavaElement.METHOD == element.getElementType()) {
      resolveMethods((IType) element.getParent());
      return coveragemap.get(element);
    }
    return null;
  }

  private void resolveMethods(final IType type) {
    IClassCoverage classCoverage = (IClassCoverage) getCoverageFor(type);
    if (classCoverage == null) {
      return;
    }
    try {
      MethodLocator locator = new MethodLocator(type);
      for (IMethodCoverage methodCoverage : classCoverage.getMethods()) {
        final IMethod method = locator.findMethod(methodCoverage.getName(),
            methodCoverage.getDesc());
        if (method != null) {
          coveragemap.put(method, methodCoverage);
        } else {
          TRACER
              .trace(
                  "Method not found in Java model: {0}.{1}{2}", type.getFullyQualifiedName(), methodCoverage.getName(), methodCoverage.getDesc()); //$NON-NLS-1$
        }
      }
    } catch (JavaModelException e) {
      TRACER.trace("Error while creating method locator for {0}: {1}", type //$NON-NLS-1$
          .getFullyQualifiedName(), e);
    }
  }
}
