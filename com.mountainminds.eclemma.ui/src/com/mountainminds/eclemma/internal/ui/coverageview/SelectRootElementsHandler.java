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

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

/**
 * Handler to selects the root elements shown in the coverage tree.
 */
class SelectRootElementsHandler extends AbstractHandler implements
    IElementUpdater {

  public static final String ID = "com.mountainminds.eclemma.ui.selectRootElements"; //$NON-NLS-1$

  private static final String TYPE_PARAMETER = "type"; //$NON-NLS-1$

  private final ViewSettings settings;
  private final CoverageView view;

  public SelectRootElementsHandler(ViewSettings settings, CoverageView view) {
    this.settings = settings;
    this.view = view;
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final ElementType type = getType(event.getParameters());
    settings.setRootType(type);
    view.refreshViewer();
    return null;
  }

  public void updateElement(UIElement element,
      @SuppressWarnings("rawtypes") Map parameters) {
    final ElementType type = getType(parameters);
    element.setChecked(settings.getRootType().equals(type));
  }

  private ElementType getType(Map<?, ?> parameters) {
    return ElementType.valueOf((String) parameters.get(TYPE_PARAMETER));
  }

}
