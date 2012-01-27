/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.ui.model.WorkbenchContentProvider;

import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;

/**
 * Specialized workbench content provider that selects entry elements depending
 * on the view setting (projects, package roots, packages or types).
 */
class CoveredElementsContentProvider extends WorkbenchContentProvider {

  private final ViewSettings settings;

  public CoveredElementsContentProvider(ViewSettings settings) {
    this.settings = settings;
  }

  public Object[] getElements(Object element) {
    IJavaModelCoverage coverage = (IJavaModelCoverage) element;
    if (coverage == IJavaModelCoverage.LOADING) {
      return new Object[] { CoverageView.LOADING_ELEMENT };
    }
    if (coverage != null) {
      switch (settings.getRootType()) {
      case GROUP:
        return coverage.getProjects();
      case BUNDLE:
        return coverage.getPackageFragmentRoots();
      case PACKAGE:
        return coverage.getPackageFragments();
      case CLASS:
        return coverage.getTypes();
      }
    }
    return new Object[0];
  }

}
