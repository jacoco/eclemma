/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
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

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.AcceptAllFilter;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jacoco.core.data.ExecutionData;

/**
 * Filters for executed classes.
 */
class ExecutedClassesFilters {

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
    private Pattern pattern;

    public PatternMatchingFilter(String patternString) {
      String regex = patternString.replace("*", ".*").replace("?", "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      pattern = Pattern.compile(regex);
    }

    public boolean select(Object toTest) {
      return pattern.matcher(getMatchedValue(toTest)).matches();
    }

    abstract protected CharSequence getMatchedValue(Object toTest);

  }

  private static class ClassNameMatcher extends PatternMatchingFilter {
    public ClassNameMatcher(String patternString) {
      super(patternString);
    }

    @Override
    protected CharSequence getMatchedValue(Object toTest) {
      return ((ExecutionData) toTest).getName();
    }
  }

  private static class ClassIdMatcher extends PatternMatchingFilter {

    public ClassIdMatcher(String patternString) {
      super(patternString);
    }

    @Override
    protected CharSequence getMatchedValue(Object toTest) {
      return String.format(
          "0x%016x", Long.valueOf(((ExecutionData) toTest).getId())); //$NON-NLS-1$
    }

  }
}
