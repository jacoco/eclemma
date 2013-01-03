/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * Editor implementation for JaCoCo execution data files.
 */
public class ExecutionDataEditor extends FormEditor {

  public static final String ID = "com.mountainminds.eclemma.ui.editors.executiondata"; //$NON-NLS-1$

  private final ExecutionDataContent content = new ExecutionDataContent();

  @Override
  protected void addPages() {
    try {
      addPage(new ExecutedClassesPage(this, content));
      addPage(new SessionDataPage(this, content));
    } catch (PartInitException e) {
      EclEmmaUIPlugin.log(e);
    }
  }

  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);
    setPartName(getEditorInput().getName());
    content.load(getEditorInput());
    firePropertyChange(PROP_INPUT);
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
  }

  @Override
  public void doSaveAs() {
  }

}
