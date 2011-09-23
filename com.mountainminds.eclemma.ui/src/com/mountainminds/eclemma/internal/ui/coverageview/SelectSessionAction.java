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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Drop-down action to select the active session. Internally used by the
 * coverage view.
 */
class SelectSessionAction extends Action implements IMenuCreator {

  private Menu menu;

  SelectSessionAction() {
    setText(UIMessages.CoverageViewSelectSessionAction_label);
    setToolTipText(UIMessages.CoverageViewSelectSessionAction_looltip);
    setImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.ELCL_SESSION));
    setDisabledImageDescriptor(EclEmmaUIPlugin
        .getImageDescriptor(EclEmmaUIPlugin.DLCL_SESSION));
    setMenuCreator(this);
    setEnabled(false);
  }

  public Menu getMenu(Control parent) {
    if (menu != null) {
      menu.dispose();
    }
    menu = new Menu(parent);

    ILabelProvider labelprovider = new WorkbenchLabelProvider();
    final ISessionManager manager = CoverageTools.getSessionManager();
    ICoverageSession active = manager.getActiveSession();
    int count = 0;
    for (final ICoverageSession session : manager.getSessions()) {
      MenuItem item = new MenuItem(menu, SWT.RADIO);
      Object[] labelparams = new Object[] { new Integer(++count),
          labelprovider.getText(session) };
      item.setText(NLS.bind(
          UIMessages.CoverageViewSelectSessionActionEntry_label, labelparams));
      item.setImage(labelprovider.getImage(session));
      item.setSelection(session == active);
      item.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          manager.activateSession(session);
        }
      });
    }
    return menu;
  }

  public Menu getMenu(Menu parent) {
    return null;
  }

  public void dispose() {
  }

}
