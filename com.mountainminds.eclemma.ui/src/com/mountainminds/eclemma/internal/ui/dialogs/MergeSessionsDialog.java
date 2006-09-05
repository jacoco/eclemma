/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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

  private String description;
  
  private Text textdescr;
  
  /**
   * TODO
   *
   * @param parent
   * @param sessions 
   * @param description 
   */
  public MergeSessionsDialog(Shell parent, ICoverageSession[] sessions, String description) {
    super(parent, sessions, new ArrayContentProvider(), new WorkbenchLabelProvider(),
          "Select at least two sessions to merge:");
    setTitle("Merge Sessions");
    setInitialSelections(sessions);
    this.description = description;
  }

  protected Label createMessageArea(Composite composite) {
    Label l = new Label(composite, SWT.NONE);
    l.setFont(composite.getFont());
    l.setText("Description of merged session:");
    
    textdescr = new Text(composite, SWT.BORDER);
    textdescr.setFont(composite.getFont());
    textdescr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    textdescr.setText(description);
    
    return super.createMessageArea(composite);
  }

  protected void okPressed() {
    description = textdescr.getText();
    super.okPressed();
  }
  
  
  
  

}
