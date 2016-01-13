/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Stack;

import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Mock support for {@link ILaunchConfiguration}.
 */
class ConfigurationMock implements InvocationHandler {

  private ILaunchConfiguration mock;

  private Stack<Object> mockResult;

  ConfigurationMock() {
    mockResult = new Stack<Object>();
    mock = (ILaunchConfiguration) Proxy.newProxyInstance(getClass()
        .getClassLoader(), new Class<?>[] { ILaunchConfiguration.class }, this);
  }

  ILaunchConfiguration getMock() {
    return mock;
  }

  void pushResult(Object value) {
    mockResult.push(value);
  }

  // InvocationHandler implementation

  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    return mockResult.pop();
  }

}
