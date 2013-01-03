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
package com.mountainminds.eclemma.internal.ui.handlers;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Handler to select the currently active coverage session.
 */
public class SelectActiveSessionHandler extends AbstractSessionManagerHandler {

  public SelectActiveSessionHandler() {
    super(CoverageTools.getSessionManager());
  }

  @Override
  public boolean isEnabled() {
    return !sessionManager.getSessions().isEmpty();
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final ListDialog dialog = new ListDialog(HandlerUtil.getActiveShell(event)) {
      protected void configureShell(Shell shell) {
        super.configureShell(shell);
        ContextHelp.setHelp(shell, ContextHelp.SELECT_ACTIVE_SESSION);
      }
    };
    dialog.setTitle(UIMessages.SelectActiveSessionDialog_title);
    dialog.setMessage(UIMessages.SelectActiveSessionDialog_message);
    dialog.setContentProvider(new ArrayContentProvider());
    dialog.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        return ((ICoverageSession) element).getDescription();
      }

      @Override
      public Image getImage(Object element) {
        return EclEmmaUIPlugin.getImage(EclEmmaUIPlugin.OBJ_SESSION);
      }
    });
    dialog.setInitialElementSelections(Collections.singletonList(sessionManager
        .getActiveSession()));
    dialog.setInput(sessionManager.getSessions());
    if (dialog.open() == Dialog.OK) {
      final Object[] result = dialog.getResult();
      if (result.length == 1) {
        sessionManager.activateSession((ICoverageSession) result[0]);
      }
    }
    return null;
  }
}
