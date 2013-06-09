/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.editors;

import org.eclipse.jface.viewers.AcceptAllFilter;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.runtime.WildcardMatcher;

/**
 * Filters for executed classes.
 */
final class ExecutedClassesFilters {

  public static ViewerFilter fromPatternString(final String pattern) {
    return new ViewerFilter() {

      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        return filterFromPatternString(pattern).select(element);
      }
    };
  }

  public static IFilter filterFromPatternString(String pattern) {
    if (pattern.length() == 0) {
      return AcceptAllFilter.getInstance();
    }

    if (pattern.startsWith("0x")) { //$NON-NLS-1$
      return new ClassIdMatcher(pattern);
    }

    return new ClassNameMatcher(pattern);
  }

  private abstract static class PatternMatchingFilter implements IFilter {
    private WildcardMatcher matcher;

    public PatternMatchingFilter(String patternString) {
      matcher = new WildcardMatcher(patternString);
    }

    public boolean select(Object toTest) {
      return matcher.matches(getMatchedValue(toTest));
    }

    abstract protected String getMatchedValue(Object toTest);

  }

  private static class ClassNameMatcher extends PatternMatchingFilter {
    public ClassNameMatcher(String patternString) {
      super(patternString);
    }

    @Override
    protected String getMatchedValue(Object toTest) {
      return ((ExecutionData) toTest).getName();
    }
  }

  private static class ClassIdMatcher extends PatternMatchingFilter {

    public ClassIdMatcher(String patternString) {
      super(patternString);
    }

    @Override
    protected String getMatchedValue(Object toTest) {
      return String.format(
          "0x%016x", Long.valueOf(((ExecutionData) toTest).getId())); //$NON-NLS-1$
    }

  }

  // No instances
  private ExecutedClassesFilters() {
  }

}
