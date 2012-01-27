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

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;

/**
 * Handler to selects the counter entities shown in the coverage tree.
 */
class SelectCountersHandler extends AbstractHandler implements IElementUpdater {

  public static final String ID = "com.mountainminds.eclemma.ui.selectCounters"; //$NON-NLS-1$

  private static final String TYPE_PARAMETER = "type"; //$NON-NLS-1$

  private final ViewSettings settings;
  private final CoverageView view;

  public SelectCountersHandler(ViewSettings settings, CoverageView view) {
    this.settings = settings;
    this.view = view;
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final CounterEntity type = getType(event.getParameters());
    settings.setCounters(type);
    view.updateColumnHeaders();
    view.refreshViewer();
    return null;
  }

  public void updateElement(UIElement element,
      @SuppressWarnings("rawtypes") Map parameters) {
    final CounterEntity type = getType(parameters);
    element.setChecked(settings.getCounters().equals(type));
  }

  private CounterEntity getType(Map<?, ?> parameters) {
    return CounterEntity.valueOf((String) parameters.get(TYPE_PARAMETER));
  }

}
