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
      switch (settings.getEntryMode()) {
      case ViewSettings.ENTRYMODE_PROJECTS:
        return coverage.getProjects();
      case ViewSettings.ENTRYMODE_PACKAGEROOTS:
        return coverage.getPackageFragmentRoots();
      case ViewSettings.ENTRYMODE_PACKAGES:
        return coverage.getPackageFragments();
      case ViewSettings.ENTRYMODE_TYPES:
        return coverage.getTypes();
      }
    }
    return new Object[0];
  }

}
