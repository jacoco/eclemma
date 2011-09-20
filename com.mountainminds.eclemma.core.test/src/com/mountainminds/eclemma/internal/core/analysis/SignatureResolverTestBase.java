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
package com.mountainminds.eclemma.internal.core.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;

/**
 * Base class for {@link SignatureResolver} tests.
 */
public abstract class SignatureResolverTestBase {

  private Map methodsByName;

  protected IType type;

  protected void createMethodIndex() throws JavaModelException {
    methodsByName = new HashMap();
    final IMethod[] methods = type.getMethods();
    for (int i = 0; i < methods.length; i++) {
      methodsByName.put(methods[i].getElementName(), methods[i]);
    }
  }

  private IMethod getMethod(final String name) {
    final IMethod m = (IMethod) methodsByName.get(name);
    assertNotNull(m);
    return m;
  }

  private void assertSignature(final String methodName, final String signature)
      throws Exception {
    final IMethod method = getMethod(methodName);
    assertEquals(signature, SignatureResolver.getParameters(method));
  }

  @Test
  public void test_NoArgs() throws Exception {
    assertSignature("noArgs", "");
  }

  @Test
  public void test_int() throws Exception {
    assertSignature("method_int", "I");
  }

  @Test
  public void test_int_int_int() throws Exception {
    assertSignature("method_int_int_int", "III");
  }

  @Test
  public void test_short() throws Exception {
    assertSignature("method_short", "S");
  }

  @Test
  public void test_charArr() throws Exception {
    assertSignature("method_charArr", "[C");
  }

  @Test
  public void test_byteArr3() throws Exception {
    assertSignature("method_byteArr3", "[[[B");
  }

  @Test
  public void test_String() throws Exception {
    assertSignature("method_String", "Ljava/lang/String;");
  }

  @Test
  public void test_Inner() throws Exception {
    assertSignature("method_Inner", "Lsignatureresolver/Samples$Inner;");
  }

  @Test
  public void test_javautilDate() throws Exception {
    assertSignature("method_javautilDate", "Ljava/util/Date;");
  }

  @Test
  public void test_javautilMapEntry() throws Exception {
    assertSignature("method_javautilMapEntry", "Ljava/util/Map$Entry;");
  }

  @Test
  public void test_parameterizedType() throws Exception {
    assertSignature("method_parameterizedType",
        "Lsignatureresolver/Samples$Generic;");
  }

  @Test
  public void test_parameterizedParameterizedType() throws Exception {
    assertSignature("method_parameterizedParameterizedType",
        "Lsignatureresolver/Samples$Generic;");
  }

  @Test
  public void test_parameterizedTypeWildcard() throws Exception {
    assertSignature("method_parameterizedTypeWildcard",
        "Lsignatureresolver/Samples$Generic;");
  }

  @Test
  public void test_parameterizedTypeExtends() throws Exception {
    assertSignature("method_parameterizedTypeExtends",
        "Lsignatureresolver/Samples$Generic;");
  }

  @Test
  public void test_parameterizedTypeSuper() throws Exception {
    assertSignature("method_parameterizedTypeSuper",
        "Lsignatureresolver/Samples$Generic;");
  }

  @Test
  public void test_methodTypeVariable() throws Exception {
    assertSignature("method_methodTypeVariable", "Ljava/lang/Object;");
  }

  @Test
  public void test_methodTypeVariableExtends() throws Exception {
    assertSignature("method_methodTypeVariableExtends", "Ljava/lang/Thread;");
  }

  @Test
  public void test_methodTypeVariableExtends2() throws Exception {
    assertSignature("method_methodTypeVariableExtends2", "Ljava/lang/Number;");
  }

  @Test
  public void test_classTypeVariable() throws Exception {
    assertSignature("method_classTypeVariable", "Ljava/lang/Object;");
  }

  @Test
  public void test_classTypeVariableExtends() throws Exception {
    assertSignature("method_classTypeVariableExtends", "Ljava/lang/Comparable;");
  }

  @Test
  public void test_classTypeVariableExtends2() throws Exception {
    assertSignature("method_classTypeVariableExtends2", "Ljava/lang/Thread;");
  }

}
