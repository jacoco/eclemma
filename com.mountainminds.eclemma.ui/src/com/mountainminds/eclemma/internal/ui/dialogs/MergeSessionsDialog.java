/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * TODO
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class MergeSessionsDialog extends ListSelectionDialog {

  /**
   * TODO
   *
   * @param parent
   * @param sessions 
   */
  public MergeSessionsDialog(Shell parent, ICoverageSession[] sessions) {
    super(parent, sessions, new ArrayContentProvider(), new WorkbenchLabelProvider(),
          "Select at least two sessions to merge");
    setTitle("Merge Sessions");
  }

}
