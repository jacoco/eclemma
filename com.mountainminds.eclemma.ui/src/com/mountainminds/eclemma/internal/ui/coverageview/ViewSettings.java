/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    Brock Janiczak - link with selection option (SF #1774547)
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IMemento;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * All setting for the coverage view that will become persisted in the view's
 * memento.
 */
public class ViewSettings {

  private static final String KEY_SORTCOLUMN = "sortcolumn"; //$NON-NLS-1$
  private static final String KEY_REVERSESORT = "reversesort"; //$NON-NLS-1$
  private static final String KEY_COUNTERS = "counters"; //$NON-NLS-1$
  private static final String KEY_HIDEUNUSEDTYPES = "hideunusedtypes"; //$NON-NLS-1$
  private static final String KEY_ROOTTYPE = "roottype"; //$NON-NLS-1$
  private static final String KEY_COLUMN0 = "column0"; //$NON-NLS-1$
  private static final String KEY_COLUMN1 = "column1"; //$NON-NLS-1$
  private static final String KEY_COLUMN2 = "column2"; //$NON-NLS-1$
  private static final String KEY_COLUMN3 = "column3"; //$NON-NLS-1$
  private static final String KEY_COLUMN4 = "column4"; //$NON-NLS-1$
  private static final String KEY_LINKED = "linked"; //$NON-NLS-1$

  private static final Map<CounterEntity, String[]> COLUMNS_HEADERS = new HashMap<ICoverageNode.CounterEntity, String[]>();

  static {
    COLUMNS_HEADERS.put(CounterEntity.INSTRUCTION, new String[] {
        UIMessages.CoverageViewColumnElement_label,
        UIMessages.CoverageViewColumnCoverage_label,
        UIMessages.CoverageViewColumnCoveredInstructions_label,
        UIMessages.CoverageViewColumnMissedInstructions_label,
        UIMessages.CoverageViewColumnTotalInstructions_label });
    COLUMNS_HEADERS.put(CounterEntity.BRANCH, new String[] {
        UIMessages.CoverageViewColumnElement_label,
        UIMessages.CoverageViewColumnCoverage_label,
        UIMessages.CoverageViewColumnCoveredBranches_label,
        UIMessages.CoverageViewColumnMissedBranches_label,
        UIMessages.CoverageViewColumnTotalBranches_label });
    COLUMNS_HEADERS.put(CounterEntity.LINE, new String[] {
        UIMessages.CoverageViewColumnElement_label,
        UIMessages.CoverageViewColumnCoverage_label,
        UIMessages.CoverageViewColumnCoveredLines_label,
        UIMessages.CoverageViewColumnMissedLines_label,
        UIMessages.CoverageViewColumnTotalLines_label });
    COLUMNS_HEADERS.put(CounterEntity.METHOD, new String[] {
        UIMessages.CoverageViewColumnElement_label,
        UIMessages.CoverageViewColumnCoverage_label,
        UIMessages.CoverageViewColumnCoveredMethods_label,
        UIMessages.CoverageViewColumnMissedMethods_label,
        UIMessages.CoverageViewColumnTotalMethods_label });
    COLUMNS_HEADERS.put(CounterEntity.CLASS, new String[] {
        UIMessages.CoverageViewColumnElement_label,
        UIMessages.CoverageViewColumnCoverage_label,
        UIMessages.CoverageViewColumnCoveredTypes_label,
        UIMessages.CoverageViewColumnMissedTypes_label,
        UIMessages.CoverageViewColumnTotalTypes_label });
    COLUMNS_HEADERS.put(CounterEntity.COMPLEXITY, new String[] {
        UIMessages.CoverageViewColumnElement_label,
        UIMessages.CoverageViewColumnCoverage_label,
        UIMessages.CoverageViewColumnCoveredComplexity_label,
        UIMessages.CoverageViewColumnMissedComplexity_label,
        UIMessages.CoverageViewColumnTotalComplexity_label });
  }

  private static final int[] DEFAULT_COLUMNWIDTH = new int[] { 300, 80, 120,
      120, 120 };

  private int sortcolumn;
  private boolean reversesort;
  private CounterEntity counters;
  private ElementType roottype;
  private boolean hideunusedtypes;
  private int[] columnwidths = new int[5];
  private boolean linked;

  public int getSortColumn() {
    return sortcolumn;
  }

  public boolean isReverseSort() {
    return reversesort;
  }

  public void toggleSortColumn(int column) {
    if (sortcolumn == column) {
      reversesort = !reversesort;
    } else {
      reversesort = false;
      sortcolumn = column;
    }
  }

  public String[] getColumnHeaders() {
    return COLUMNS_HEADERS.get(counters);
  }

  public CounterEntity getCounters() {
    return counters;
  }

  public void setCounters(CounterEntity counters) {
    this.counters = counters;
  }

  public ElementType getRootType() {
    return roottype;
  }

  public void setRootType(ElementType roottype) {
    this.roottype = roottype;
  }

  public boolean getHideUnusedTypes() {
    return hideunusedtypes;
  }

  public void setHideUnusedTypes(boolean flag) {
    hideunusedtypes = flag;
  }

  public int[] getColumnWidths() {
    return columnwidths;
  }

  public boolean getLinked() {
    return linked;
  }

  public void setLinked(boolean linked) {
    this.linked = linked;
  }

  public void init(IMemento memento) {
    sortcolumn = getInt(memento, KEY_SORTCOLUMN, CoverageView.COLUMN_MISSED);
    reversesort = getBoolean(memento, KEY_REVERSESORT, true);
    counters = getEnum(memento, KEY_COUNTERS, CounterEntity.class,
        CounterEntity.INSTRUCTION);
    roottype = getEnum(memento, KEY_ROOTTYPE, ElementType.class,
        ElementType.GROUP);
    hideunusedtypes = getBoolean(memento, KEY_HIDEUNUSEDTYPES, false);
    columnwidths[0] = getInt(memento, KEY_COLUMN0, DEFAULT_COLUMNWIDTH[0]);
    columnwidths[1] = getInt(memento, KEY_COLUMN1, DEFAULT_COLUMNWIDTH[1]);
    columnwidths[2] = getInt(memento, KEY_COLUMN2, DEFAULT_COLUMNWIDTH[2]);
    columnwidths[3] = getInt(memento, KEY_COLUMN3, DEFAULT_COLUMNWIDTH[3]);
    columnwidths[4] = getInt(memento, KEY_COLUMN4, DEFAULT_COLUMNWIDTH[4]);
    linked = getBoolean(memento, KEY_LINKED, false);
  }

  public void save(IMemento memento) {
    memento.putInteger(KEY_SORTCOLUMN, sortcolumn);
    memento.putInteger(KEY_REVERSESORT, reversesort ? 1 : 0);
    memento.putString(KEY_COUNTERS, counters.name());
    memento.putString(KEY_ROOTTYPE, roottype.name());
    memento.putInteger(KEY_HIDEUNUSEDTYPES, hideunusedtypes ? 1 : 0);
    memento.putInteger(KEY_COLUMN0, columnwidths[0]);
    memento.putInteger(KEY_COLUMN1, columnwidths[1]);
    memento.putInteger(KEY_COLUMN2, columnwidths[2]);
    memento.putInteger(KEY_COLUMN3, columnwidths[3]);
    memento.putInteger(KEY_COLUMN4, columnwidths[4]);
    memento.putInteger(KEY_LINKED, linked ? 1 : 0);
  }

  private int getInt(IMemento memento, String key, int preset) {
    if (memento == null) {
      return preset;
    } else {
      Integer i = memento.getInteger(key);
      return i == null ? preset : i.intValue();
    }
  }

  private <T extends Enum<T>> T getEnum(IMemento memento, String key,
      Class<T> type, T preset) {
    if (memento == null) {
      return preset;
    }
    final String s = memento.getString(key);
    if (s == null) {
      return preset;
    }
    try {
      return Enum.valueOf(type, s);
    } catch (IllegalArgumentException e) {
      return preset;
    }
  }

  private boolean getBoolean(IMemento memento, String key, boolean preset) {
    return getInt(memento, key, preset ? 1 : 0) == 1;
  }

}
