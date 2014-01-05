/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Muskalla - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.decorators;

import java.text.DecimalFormat;
import java.text.Format;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Decorator to show code coverage for Java elements.
 */
public class CoverageDecorator extends BaseLabelProvider implements
    ILightweightLabelDecorator {

  private static final Format SUFFIX_FORMAT = new DecimalFormat(
      UIMessages.CoverageDecoratorSuffix_label);

  private final IJavaCoverageListener coverageListener;

  public CoverageDecorator() {
    super();
    coverageListener = new IJavaCoverageListener() {
      public void coverageChanged() {
        final Display display = EclEmmaUIPlugin.getInstance().getWorkbench()
            .getDisplay();
        display.asyncExec(new Runnable() {
          public void run() {
            fireLabelProviderChanged(new LabelProviderChangedEvent(
                CoverageDecorator.this));
          }
        });
      }
    };
    CoverageTools.addJavaCoverageListener(coverageListener);
  }

  public void decorate(Object element, IDecoration decoration) {
    final ICoverageNode coverage = CoverageTools.getCoverageInfo(element);
    if (coverage == null) {
      // no coverage data
      return;
    }
    // TODO obtain counter from preferences
    ICounter counter = coverage.getInstructionCounter();
    ImageDescriptor overlay = EclEmmaUIPlugin.getCoverageOverlay(counter
        .getCoveredRatio());
    decoration.addOverlay(overlay, IDecoration.TOP_LEFT);
    decoration.addSuffix(SUFFIX_FORMAT.format(Double.valueOf(counter
        .getCoveredRatio())));
  }

  public boolean isLabelProperty(Object element, String property) {
    // coverage does not depend on IJavaElement properties
    return false;
  }

  public void dispose() {
    CoverageTools.removeJavaCoverageListener(coverageListener);
  }

}
