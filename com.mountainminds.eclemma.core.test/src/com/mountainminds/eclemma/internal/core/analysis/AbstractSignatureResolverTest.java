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

import junit.framework.TestCase;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author Marc R. Hoffmann
 * @version $Revision: 171 $
 */
public abstract class AbstractSignatureResolverTest extends TestCase {

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

  public void test_NoArgs() throws Exception {
    assertSignature("noArgs", "");
  }

  public void test_int() throws Exception {
    assertSignature("method_int", "I");
  }

  public void test_int_int_int() throws Exception {
    assertSignature("method_int_int_int", "III");
  }

  public void test_short() throws Exception {
    assertSignature("method_short", "S");
  }

  public void test_charArr() throws Exception {
    assertSignature("method_charArr", "[C");
  }

  public void test_byteArr3() throws Exception {
    assertSignature("method_byteArr3", "[[[B");
  }

  public void test_String() throws Exception {
    assertSignature("method_String", "Ljava/lang/String;");
  }

  public void test_Inner() throws Exception {
    assertSignature("method_Inner", "Lsignatureresolver/Samples$Inner;");
  }

  public void test_javautilDate() throws Exception {
    assertSignature("method_javautilDate", "Ljava/util/Date;");
  }

  public void test_javautilMapEntry() throws Exception {
    assertSignature("method_javautilMapEntry", "Ljava/util/Map$Entry;");
  }

  public void test_parameterizedType() throws Exception {
    assertSignature("method_parameterizedType",
        "Lsignatureresolver/Samples$Generic;");
  }

  public void test_parameterizedParameterizedType() throws Exception {
    assertSignature("method_parameterizedParameterizedType",
        "Lsignatureresolver/Samples$Generic;");
  }

  public void test_parameterizedTypeWildcard() throws Exception {
    assertSignature("method_parameterizedTypeWildcard",
        "Lsignatureresolver/Samples$Generic;");
  }

  public void test_parameterizedTypeExtends() throws Exception {
    assertSignature("method_parameterizedTypeExtends",
        "Lsignatureresolver/Samples$Generic;");
  }

  public void test_parameterizedTypeSuper() throws Exception {
    assertSignature("method_parameterizedTypeSuper",
        "Lsignatureresolver/Samples$Generic;");
  }

  public void test_methodTypeVariable() throws Exception {
    assertSignature("method_methodTypeVariable", "Ljava/lang/Object;");
  }

  public void test_methodTypeVariableExtends() throws Exception {
    assertSignature("method_methodTypeVariableExtends", "Ljava/lang/Thread;");
  }

  public void test_methodTypeVariableExtends2() throws Exception {
    assertSignature("method_methodTypeVariableExtends2", "Ljava/lang/Number;");
  }

  public void test_classTypeVariable() throws Exception {
    assertSignature("method_classTypeVariable", "Ljava/lang/Object;");
  }

  public void test_classTypeVariableExtends() throws Exception {
    assertSignature("method_classTypeVariableExtends", "Ljava/lang/Comparable;");
  }

  public void test_classTypeVariableExtends2() throws Exception {
    assertSignature("method_classTypeVariableExtends2", "Ljava/lang/Thread;");
  }

}
