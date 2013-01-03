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
package com.mountainminds.eclemma.internal.ui.coverageview;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Internal converter to create textual representations for table cells.
 */
class CellTextConverter {

  private static final NumberFormat COVERAGE_VALUE = new DecimalFormat(
      UIMessages.CoverageView_columnCoverageValue);

  private static final NumberFormat COUNTER_VALUE = DecimalFormat
      .getIntegerInstance();

  private final ViewSettings settings;
  private final ILabelProvider workbenchLabelProvider;

  CellTextConverter(ViewSettings settings) {
    this.settings = settings;
    this.workbenchLabelProvider = new WorkbenchLabelProvider();
  }

  String getElementName(Object element) {
    String text = getSimpleTextForJavaElement(element);
    if (element instanceof IPackageFragmentRoot
        && ElementType.BUNDLE.equals(settings.getRootType())) {
      text += " - " + getElementName(((IPackageFragmentRoot) element).getJavaProject()); //$NON-NLS-1$
    }
    return text;
  }

  private String getSimpleTextForJavaElement(Object element) {
    if (element instanceof IPackageFragmentRoot) {
      final IPackageFragmentRoot root = (IPackageFragmentRoot) element;
      // tweak label if the package fragment root is the project itself:
      if (root.getElementName().length() == 0) {
        element = root.getJavaProject();
      }
      // shorten JAR references
      try {
        if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
          return root.getPath().lastSegment();
        }
      } catch (JavaModelException e) {
        EclEmmaUIPlugin.log(e);
      }
    }
    return workbenchLabelProvider.getText(element);
  }

  String getRatio(Object element) {
    ICounter counter = getCounter(element);
    if (counter.getTotalCount() == 0) {
      return ""; //$NON-NLS-1$
    } else {
      return COVERAGE_VALUE.format(counter.getCoveredRatio());
    }
  }

  String getCovered(Object element) {
    return COUNTER_VALUE.format(getCounter(element).getCoveredCount());
  }

  String getMissed(Object element) {
    return COUNTER_VALUE.format(getCounter(element).getMissedCount());
  }

  String getTotal(Object element) {
    return COUNTER_VALUE.format(getCounter(element).getTotalCount());
  }

  private ICounter getCounter(Object element) {
    return CoverageTools.getCoverageInfo(element).getCounter(
        settings.getCounters());
  }

}
