/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.annotation;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Tracks the workbench editors and to attach coverage annotation models.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class EditorTracker {

  private final IWorkbench workbench;
  
  private IWindowListener windowListener = new IWindowListener() {
    public void windowOpened(IWorkbenchWindow window) {
      window.getPartService().addPartListener(partListener);
    }
    public void windowClosed(IWorkbenchWindow window) {
      window.getPartService().removePartListener(partListener);
    }
    public void windowActivated(IWorkbenchWindow window) { }
    public void windowDeactivated(IWorkbenchWindow window) { }
  };
  
  private IPartListener2 partListener = new IPartListener2() {
    public void partOpened(IWorkbenchPartReference partref) { 
      annotateEditor(partref);
    }
    public void partActivated(IWorkbenchPartReference partref) { }
    public void partBroughtToTop(IWorkbenchPartReference partref) { }
    public void partVisible(IWorkbenchPartReference partref) { }
    public void partInputChanged(IWorkbenchPartReference partref) { }
    public void partClosed(IWorkbenchPartReference partref) { }
    public void partDeactivated(IWorkbenchPartReference partref) { }
    public void partHidden(IWorkbenchPartReference partref) { }
  };
  
  public EditorTracker(IWorkbench workbench) {
    this.workbench = workbench;
    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
    for (int i = 0; i < windows.length; i++) {
      windows[i].getPartService().addPartListener(partListener);
    }
    workbench.addWindowListener(windowListener);
    annotateAllEditors();
  }
  
  public void dispose() {
    workbench.removeWindowListener(windowListener);
    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
    for (int i = 0; i < windows.length; i++) {
      windows[i].getPartService().removePartListener(partListener);
    }
  }
  
  private void annotateAllEditors() {
    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
    for (int i = 0; i < windows.length; i++) {
      IWorkbenchPage[] pages = windows[i].getPages();
      for (int j = 0; j < pages.length; j++) {
        IEditorReference[] editors = pages[j].getEditorReferences();
        for (int k = 0; k < editors.length; k++) {
          annotateEditor(editors[k]);
        }
      }      
    }
  }
  
  private void annotateEditor(IWorkbenchPartReference partref) {
    IWorkbenchPart part = partref.getPart(false);
    if (part instanceof ITextEditor) {
      CoverageAnnotationModel.attach((ITextEditor) part);
    }
  }

}
