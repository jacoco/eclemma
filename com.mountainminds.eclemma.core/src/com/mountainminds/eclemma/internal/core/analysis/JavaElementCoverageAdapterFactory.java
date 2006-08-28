/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.core.analysis.ILineCoverage;

/**
 * This factory adapts IResource and IJavaElement objects to the corresponding
 * coverage information of the current session. The factory is hooked into the
 * workbench through the extension point
 * <code>org.eclipse.core.runtime.adapters</code>.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaElementCoverageAdapterFactory implements IAdapterFactory {

  public Object getAdapter(Object object, Class adapterType) {
    // if the object is a IResource find the corresponding IJavaElement
    if (object instanceof IResource) {
      object = ((IResource) object).getAdapter(IJavaElement.class);
      if (object == null) {
        return null;
      }
    }
    // then find the coverage information from the current session
    IJavaModelCoverage mc = CoverageTools.getJavaModelCoverage(); 
    if (mc == null) {
      return null;
    } else {
      IJavaElementCoverage coverage = mc.getCoverageFor((IJavaElement) object);
      if (coverage != null && ILineCoverage.class.equals(adapterType)) {
        return coverage.getLineCoverage();
      } else {
        return coverage;
      }
    }
  }

  public Class[] getAdapterList() {
    return new Class[] { IJavaElementCoverage.class, ILineCoverage.class };
  }

}
