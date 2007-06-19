/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
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
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
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
    ICoverageSession[] sessions = manager.getSessions();
    ICoverageSession active = manager.getActiveSession();
    for (int i = 0; i < sessions.length; i++) {
      final ICoverageSession session = sessions[i];
      MenuItem item = new MenuItem(menu, SWT.RADIO);
      Object[] labelparams = new Object[] { new Integer(i + 1),
          labelprovider.getText(session) };
      item.setText(NLS.bind(UIMessages.CoverageViewSelectSessionActionEntry_label,
          labelparams));
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
