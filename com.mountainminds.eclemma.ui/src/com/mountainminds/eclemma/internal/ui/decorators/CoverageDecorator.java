/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.IWorkbench;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Decorator to show code coverage for Java elements.
 */
public class CoverageDecorator implements ILightweightLabelDecorator {

  private static final Format SUFFIX_FORMAT = new DecimalFormat(
      UIMessages.CoverageDecoratorSuffix_label);

  private List listeners = new ArrayList();
  private IJavaCoverageListener coverageListener = null;

  public CoverageDecorator() {
    super();
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
    decoration.addSuffix(SUFFIX_FORMAT.format(new Double(counter
        .getCoveredRatio())));
  }

  protected void fireEvent() {
    final IWorkbench workbench = EclEmmaUIPlugin.getInstance().getWorkbench();
    if (workbench == null)
      return;
    final LabelProviderChangedEvent event = new LabelProviderChangedEvent(this);
    workbench.getDisplay().asyncExec(new Runnable() {
      public void run() {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
          ((ILabelProviderListener) i.next()).labelProviderChanged(event);
        }
      }
    });
  }

  public void addListener(ILabelProviderListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
    if (coverageListener == null) {
      coverageListener = new IJavaCoverageListener() {
        public void coverageChanged() {
          fireEvent();
        }
      };
      CoverageTools.addJavaCoverageListener(coverageListener);
    }
  }

  public void removeListener(ILabelProviderListener listener) {
    listeners.remove(listener);
  }

  public boolean isLabelProperty(Object element, String property) {
    // coverage does not depend on IJavaElement properties
    return false;
  }

  public void dispose() {
    if (coverageListener != null) {
      CoverageTools.removeJavaCoverageListener(coverageListener);
      coverageListener = null;
    }
  }

}
