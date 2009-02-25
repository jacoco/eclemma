/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Internal utility to select methods by their binary signature. For performance
 * optimization matching is performed in two steps, where the first step should
 * quickly identify methods in most situations: Identification by name and
 * parameter count. Only if the first step does fails to identify a method
 * unambiguously the parameter types are resolved in a second step.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class MethodLocator {

  /** Special value to identify ambiguous entries in {@link #indexParamCount} */
  private static final Object AMBIGUOUS = new Object();

  /** Index on methods by name and parameter count. */
  private final Map indexParamCount = new HashMap();

  /** Index on methods by name and parameter signature. */
  private final Map indexParamSignature = new HashMap();

  private final IType type;

  /**
   * Initializes a new locator for method search within the given type.
   * 
   * @param type
   *          type to search methods in
   * @throws JavaModelException
   */
  public MethodLocator(final IType type) throws JavaModelException {
    this.type = type;
    final IMethod[] methods = type.getMethods();
    for (int i = 0; i < methods.length; i++) {
      addToIndex(methods[i]);
    }
  }

  /**
   * Searches for the method with the given binary name.
   * 
   * @param name
   *          binary method name
   * @param signature
   *          binary method signature
   * @return method or <code>null</code>
   */
  public IMethod findMethod(String name, String signature) {
    if (name.equals("<init>")) { //$NON-NLS-1$
      name = type.getElementName();
    }
    final Object value = indexParamCount.get(createParamCountKey(name,
        signature));
    if (value == AMBIGUOUS) {
      return (IMethod) indexParamSignature.get(createParamSignatureKey(name,
          signature));
    }
    return (IMethod) value;
  }

  private void addToIndex(final IMethod method) throws JavaModelException {
    final String paramCountKey = createParamCountKey(method);
    final Object existing = indexParamCount.get(paramCountKey);
    if (existing == null) {
      indexParamCount.put(paramCountKey, method);
      return;
    }
    if (existing != AMBIGUOUS) {
      indexParamCount.put(paramCountKey, AMBIGUOUS);
      final IMethod m = (IMethod) existing;
      indexParamSignature.put(createParamSignatureKey(m), m);
    }
    indexParamSignature.put(createParamSignatureKey(method), method);
  }

  private String createParamCountKey(final IMethod method) {
    return method.getElementName() + "@" + method.getParameterTypes().length; //$NON-NLS-1$
  }

  private String createParamCountKey(final String name,
      final String fullSignature) {
    return name + "@" + Signature.getParameterCount(fullSignature); //$NON-NLS-1$
  }

  private String createParamSignatureKey(final IMethod method)
      throws JavaModelException {
    return method.getElementName() + "#" //$NON-NLS-1$
        + SignatureResolver.getParameters(method);
  }

  private String createParamSignatureKey(final String name,
      final String fullSignature) {
    return name + "#" + SignatureResolver.getParameters(fullSignature); //$NON-NLS-1$
  }

}
