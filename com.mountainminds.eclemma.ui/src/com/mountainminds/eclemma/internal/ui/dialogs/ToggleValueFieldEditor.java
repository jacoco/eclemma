/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Modified BooleanFieldEditor to toggle between two given string values. 
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
class ToggleValueFieldEditor extends BooleanFieldEditor {
  
  private final String onvalue;
  private final String offvalue;
  private Button checkbox;
  
  public ToggleValueFieldEditor(String name, String label, Composite parent,
                                String onvalue, String offvalue) {
    super(name, label, parent);
    this.onvalue = onvalue;
    this.offvalue = offvalue;
  }
  
  public Button getChangeControl(Composite parent) {
    // we need to grap the control here, as the superclass declares it private
    return checkbox = super.getChangeControl(parent);
  }
  
  protected void doLoad() {
    if (checkbox != null) {
      String value = getPreferenceStore().getString(getPreferenceName());
      checkbox.setSelection(onvalue.equals(value));
    }
  }
  
  protected void doLoadDefault() {
    String value = getPreferenceStore().getDefaultString(getPreferenceName());
    checkbox.setSelection(onvalue.equals(value));
  }
  
  protected void doStore() {
    if (checkbox != null) {
      String value = checkbox.getSelection() ? onvalue : offvalue;
      getPreferenceStore().setValue(getPreferenceName(), value);
    }
  }
    
}

  
