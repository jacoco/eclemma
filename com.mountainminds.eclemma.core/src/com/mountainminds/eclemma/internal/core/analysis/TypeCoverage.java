/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
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
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * Coverage for types elements.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class TypeCoverage extends JavaElementCoverage implements ILazyBinding {

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

  public static class UnboundMethodCoverage {
    final String name;
    final String signature;
    final IJavaElementCoverage coverage;

    UnboundMethodCoverage(String name, String signature,
        IJavaElementCoverage coverage) {
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

  public TypeCoverage(JavaElementCoverage parent, boolean haslines,
      IResource resource) {
    super(parent, haslines, resource);
    ubmethods = null;
  }

  public void setUnboundMethods(UnboundMethodCoverage[] ubmethods) {
    this.ubmethods = ubmethods;
  }

  public void resolve(IJavaElement element, JavaModelCoverage modelcoverage) {
    if (ubmethods != null) {
      final IType type = (IType) element;
      final MethodLocator locator;
      try {
        locator = new MethodLocator(type);
        for (int i = 0; i < ubmethods.length; i++) {
          final String name = ubmethods[i].name;
          final String signature = ubmethods[i].signature;
          final IMethod method = locator.findMethod(name, signature);
          if (method != null) {
            modelcoverage.put(method, ubmethods[i].coverage);
          } else {
            TRACER
                .trace(
                    "Method not found in Java model: {0}.{1}{2}", type.getFullyQualifiedName(), name, signature); //$NON-NLS-1$
          }
        }
      } catch (JavaModelException e) {
        TRACER.trace("Error while creating method locator for {0}: {1}", type //$NON-NLS-1$
            .getFullyQualifiedName(), e);
      }
      ubmethods = null;
    }
  }

}
