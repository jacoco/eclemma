/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import org.eclipse.ui.model.WorkbenchContentProvider;

import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;

/**
 * Specialized workbench content provider that selects entry elements depending
 * on the view setting (projects, package roots, packages or types).
 *  
 * @author  Marc R. Hoffmann
 * @version $Revision$
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
          return coverage.getInstrumentedProjects();
        case ViewSettings.ENTRYMODE_PACKAGEROOTS:
          return coverage.getInstrumentedPackageFragmentRoots();
        case ViewSettings.ENTRYMODE_PACKAGES:
          return coverage.getInstrumentedPackageFragments();
        case ViewSettings.ENTRYMODE_TYPES:
          return coverage.getInstrumentedTypes();
      }
    }
    return new Object[0];
  }

}
