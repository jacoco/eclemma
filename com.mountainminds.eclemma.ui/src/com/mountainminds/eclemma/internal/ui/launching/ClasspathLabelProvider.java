/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class ClasspathLabelProvider extends LabelProvider {

  private ILabelProvider delegate = new WorkbenchLabelProvider();
  
  public Image getImage(Object element) {
    return delegate.getImage(element);
  }

  public String getText(Object element) {
    StringBuffer sb = new StringBuffer(delegate.getText(element));
    sb.append(" - ");
    sb.append(delegate.getText(((IPackageFragmentRoot) element).getJavaProject()));
    return sb.toString();
  }

  
}
