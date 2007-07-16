/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Implementation of the "Code Coverage" preferences page.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision: $
 */
public class CoveragePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public CoveragePreferencePage() {
     super(GRID);
  }
  
  protected void createFieldEditors() {
    // TODO Auto-generated method stub
  }

  public void init(IWorkbench workbench) {
  }

}
