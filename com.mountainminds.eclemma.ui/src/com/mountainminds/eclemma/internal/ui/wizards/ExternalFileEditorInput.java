/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.editors.text.ILocationProvider;

/**
 * Editor input for external files.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class ExternalFileEditorInput extends PlatformObject implements
    IPathEditorInput, ILocationProvider {

  private File file;

  public ExternalFileEditorInput(File file) {
    this.file = file;
  }

  public boolean equals(Object o) {
    if (o instanceof IPathEditorInput) {
      IPathEditorInput input = (IPathEditorInput) o;
      return getPath().equals(input.getPath());
    }
    return false;
  }

  public int hashCode() {
    return file.hashCode();
  }

  // IEditorInput implementation

  public boolean exists() {
    return file.exists();
  }

  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  public String getName() {
    return file.getName();
  }

  public IPersistableElement getPersistable() {
    return null;
  }

  public String getToolTipText() {
    return file.getAbsolutePath();
  }

  // IPathEditorInput implementation

  public IPath getPath() {
    return Path.fromOSString(file.getAbsolutePath());
  }

  // ILocationProvider implementation

  public IPath getPath(Object element) {
    if (element instanceof ExternalFileEditorInput) {
      return ((ExternalFileEditorInput) element).getPath();
    } else {
      return null;
    }
  }

}
