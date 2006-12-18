/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * Coverage for types elements.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class TypeCoverage extends JavaElementCoverage implements ILazyBinding {

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;
  
  public static class UnboundMethodCoverage {
    final String name;
    final String signature;
    final IJavaElementCoverage coverage;
    UnboundMethodCoverage(String name, String signature, IJavaElementCoverage coverage) {
      this.name = name;
      this.signature = signature;
      this.coverage = coverage;
    }
  }
  
  private UnboundMethodCoverage[] ubmethods;
  
  public TypeCoverage(JavaElementCoverage parent, boolean haslines, long stamp) {
    super(parent, haslines, stamp);
    ubmethods = null;
  }

  public TypeCoverage(JavaElementCoverage parent, boolean haslines, IResource resource) {
    super(parent, haslines, resource);
    ubmethods = null;
  }

  
  public void setUnboundMethods(UnboundMethodCoverage[] ubmethods) {
    this.ubmethods = ubmethods;
  }
  
  public void resolve(IJavaElement element, JavaModelCoverage modelcoverage) {
    IType type = (IType) element;
    if (ubmethods != null) {
      for (int i = 0; i < ubmethods.length; i++) {
        String name = ubmethods[i].name;
        if (name.equals("<init>")) { //$NON-NLS-1$
          name = type.getElementName();
        }
        String[] paramtypes = Signature.getParameterTypes(ubmethods[i].signature); 
        for (int j = 0; j < paramtypes.length; j++) {
          paramtypes[j] = paramtypes[j].replace('/', '.');
        }
        IMethod pattern = type.getMethod(name, paramtypes);
        IMethod[] hits = type.findMethods(pattern);
        if (hits != null && hits.length == 1) {
          modelcoverage.put(hits[0], ubmethods[i].coverage);
        } else {
          TRACER.trace("Method not found in Java model: {0}.{1}{2}", type.getElementName(), name, ubmethods[i].signature); //$NON-NLS-1$
        }
      }
      ubmethods = null;
    }    
  }

}
