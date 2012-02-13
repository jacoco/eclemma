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
package com.mountainminds.eclemma.internal.ui.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIPreferences;

/**
 * Handler to toggle the "reset on dump" option.
 */
public class ResetOnDumpHandler extends AbstractHandler implements
    IElementUpdater {

  private final IPreferenceStore preferenceStore;

  public ResetOnDumpHandler() {
    preferenceStore = EclEmmaUIPlugin.getInstance().getPreferenceStore();
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final boolean flag = preferenceStore
        .getBoolean(UIPreferences.PREF_RESET_ON_DUMP);
    preferenceStore.setValue(UIPreferences.PREF_RESET_ON_DUMP, !flag);
    return null;
  }

  public void updateElement(UIElement element,
      @SuppressWarnings("rawtypes") Map parameters) {
    element.setChecked(preferenceStore
        .getBoolean(UIPreferences.PREF_RESET_ON_DUMP));
  }

}
