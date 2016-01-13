/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.jacoco.core.analysis.ICoverageNode;

import com.mountainminds.eclemma.core.CoverageTools;

/**
 * Internal cache to calculate and keep the maximum total amount within a group.
 */
class MaxTotalCache {

  private final ViewSettings settings;
  private final ITreeContentProvider contentProvider;

  private Map<IJavaElement, Integer> maxTotals;

  MaxTotalCache(ViewSettings settings) {
    this.settings = settings;
    this.contentProvider = new WorkbenchContentProvider();
    this.maxTotals = new HashMap<IJavaElement, Integer>();
  }

  int getMaxTotal(Object element) {
    final IJavaElement parent = ((IJavaElement) element).getParent();
    Integer max = maxTotals.get(parent);
    if (max == null) {
      max = Integer.valueOf(calculateMaxTotal(parent));
      maxTotals.put(parent, max);
    }
    return max.intValue();
  }

  private int calculateMaxTotal(IJavaElement parent) {
    int max = 0;
    for (Object sibling : contentProvider.getChildren(parent)) {
      final ICoverageNode coverage = CoverageTools.getCoverageInfo(sibling);
      if (coverage != null) {
        max = Math.max(max, coverage.getCounter(settings.getCounters())
            .getTotalCount());
      }
    }
    return max;
  }

  void reset() {
    maxTotals.clear();
  }

}
