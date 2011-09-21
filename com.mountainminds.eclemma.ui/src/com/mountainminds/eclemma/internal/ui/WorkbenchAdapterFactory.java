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
package com.mountainminds.eclemma.internal.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * Factory for <code>IWorkbenchAdapter</code>s for coverage model elements.
 */
public class WorkbenchAdapterFactory implements IAdapterFactory {

  private static final IWorkbenchAdapter SESSIONADAPTER = new IWorkbenchAdapter() {

    public ImageDescriptor getImageDescriptor(Object object) {
      return EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.OBJ_SESSION);
    }

    public String getLabel(Object o) {
      return ((ICoverageSession) o).getDescription();
    }

    public Object[] getChildren(Object o) {
      return new Object[0];
    }

    public Object getParent(Object o) {
      return null;
    }

  };

  public Object getAdapter(Object adaptableObject,
      @SuppressWarnings("rawtypes") Class adapterType) {
    if (adaptableObject instanceof ICoverageSession) {
      return SESSIONADAPTER;
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Class[] getAdapterList() {
    return new Class[] { IWorkbenchAdapter.class };
  }

}
