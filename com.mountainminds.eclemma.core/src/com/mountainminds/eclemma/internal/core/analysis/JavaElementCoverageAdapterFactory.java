/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ISourceNode;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;

/**
 * This factory adapts IResource and IJavaElement objects to the corresponding
 * coverage information of the current session. The factory is hooked into the
 * workbench through the extension point
 * <code>org.eclipse.core.runtime.adapters</code>.
 */
public class JavaElementCoverageAdapterFactory implements IAdapterFactory {

  public Object getAdapter(Object object,
      @SuppressWarnings("rawtypes") Class adapterType) {
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
      ICoverageNode coverage = mc.getCoverageFor((IJavaElement) object);
      if (adapterType.isInstance(coverage)) {
        return coverage;
      } else {
        return null;
      }
    }
  }

  public Class<?>[] getAdapterList() {
    return new Class[] { ICoverageNode.class, ISourceNode.class };
  }

}
