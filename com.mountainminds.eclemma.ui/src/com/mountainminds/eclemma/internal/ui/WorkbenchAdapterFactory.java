/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.mountainminds.eclemma.core.ICoverageSession;

/**
 * Factory for <code>IWorkbenchAdapter</code>s for coverage model
 * elements.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
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

  public Object getAdapter(Object adaptableObject, Class adapterType) {
    if (adaptableObject instanceof ICoverageSession) {
      return SESSIONADAPTER;
    }
    return null;
  }

  public Class[] getAdapterList() {
    return new Class[] { IWorkbenchAdapter.class };
  }

}
