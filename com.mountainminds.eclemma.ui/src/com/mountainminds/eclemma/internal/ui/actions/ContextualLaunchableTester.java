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
package com.mountainminds.eclemma.internal.ui.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchManager;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * This property tester checks whether the "coverage" launch mode is possible
 * for the current selection. The expression defined for the shortcut enablement
 * has changed for different Eclipse versions. Therefore the implementation of
 * the property tester delegates to the expression defined for the corresponding
 * launch shortcut expression for the "run" mode.
 */
public class ContextualLaunchableTester extends PropertyTester {

  /** Cache for expressions maps launch shortcut ids to Expression objects. */
  private Map<String, Expression> expressions = new HashMap<String, Expression>();

  public boolean test(Object receiver, String property, Object[] args,
      Object expectedValue) {
    String delegateShortcutID = (String) args[0];
    Expression expr = expressions.get(delegateShortcutID);
    if (expr == null) {
      expr = createEnablementExpression(delegateShortcutID);
      expressions.put(delegateShortcutID, expr);
    }
    try {
      return expr.evaluate(createContext(receiver)) != EvaluationResult.FALSE;
    } catch (CoreException ce) {
      EclEmmaUIPlugin.log(ce);
      return false;
    }
  }

  private IEvaluationContext createContext(Object selection) {
    IEvaluationContext context = new EvaluationContext(null, selection);
    context.addVariable("selection", selection); //$NON-NLS-1$
    return context;
  }

  private Expression createEnablementExpression(String delegateShortcutID) {
    IConfigurationElement element = findEnablementConfiguration(delegateShortcutID);
    if (element != null) {
      try {
        return ExpressionConverter.getDefault().perform(element);
      } catch (CoreException ce) {
        EclEmmaUIPlugin.log(ce);
      }
    }
    return Expression.FALSE;
  }

  private IConfigurationElement findEnablementConfiguration(
      String delegateShortcutID) {
    IConfigurationElement[] configs = Platform.getExtensionRegistry()
        .getConfigurationElementsFor("org.eclipse.debug.ui.launchShortcuts"); //$NON-NLS-1$
    for (final IConfigurationElement config : configs) {
      if (!delegateShortcutID.equals(config.getAttribute("id")))continue; //$NON-NLS-1$
      String modes = config.getAttribute("modes"); //$NON-NLS-1$
      if (modes == null)
        continue;
      if (!Arrays.asList(modes.split("\\W")).contains(ILaunchManager.RUN_MODE))continue; //$NON-NLS-1$
      IConfigurationElement[] launch = config.getChildren("contextualLaunch"); //$NON-NLS-1$
      if (launch.length != 1)
        continue;
      IConfigurationElement[] enablement = launch[0]
          .getChildren(ExpressionTagNames.ENABLEMENT);
      if (enablement.length == 1)
        return enablement[0];
    }
    return null;
  }

}
