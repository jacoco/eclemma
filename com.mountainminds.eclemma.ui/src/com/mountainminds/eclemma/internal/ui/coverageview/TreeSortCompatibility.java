/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Attempts to set the sort order/direction of a tree using reflection to
 * maintain compatibility with older versions of SWT
 * 
 * @author  Brock Janiczak
 * @version $Revision$
 */
public final class TreeSortCompatibility {
  private TreeSortCompatibility() {
  }
  
  private static boolean sortingAvailable = true;

  public static void setTreeSortColumnAndDirection(TreeColumn sortColumn, int direction) {
    if (sortingAvailable) {
      try {
        Method setSortColumn = Tree.class.getMethod("setSortColumn", new Class[] {TreeColumn.class}); //$NON-NLS-1$
        setSortColumn.invoke(sortColumn.getParent(), new Object[] {sortColumn});
        
        Method setSortDirection = Tree.class.getMethod("setSortDirection", new Class[] {int.class}); //$NON-NLS-1$
        setSortDirection.invoke(sortColumn.getParent(), new Object[] {new Integer(direction)});
        
      } catch (Exception e) {
        sortingAvailable = false;
      }
    }
  }
}
