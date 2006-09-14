/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.viewers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.IClassFiles;

/**
 * Viewer for selecting <code>IClassFiles</code> objects from a given list.
 * The viewer lists the corresponding package fragment roots.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class ClassesViewer {

  private static class PackageFragmentRootLabelProvider extends LabelProvider {

    private ILabelProvider delegate = new WorkbenchLabelProvider();

    public Image getImage(Object element) {
      return delegate.getImage(element);
    }

    public String getText(Object element) {
      IPackageFragmentRoot root = (IPackageFragmentRoot) element;
      return root.getPath().toString();
    }

    public void dispose() {
      delegate.dispose();
    }

  }

  private final Table table;
  private final CheckboxTableViewer viewer;
  
  private IClassFiles[] input;
  private boolean includebinaries;
  private final Set checkedclasses = new HashSet();

  /**
   * Creates a new viewer within the given parent.
   * 
   * @param parent
   *          composite to create the viewer's table in
   * @param style
   *          flags specifying the table's style
   */
  public ClassesViewer(Composite parent, int style) {
    this(new Table(parent, SWT.CHECK | style));
  }

  /**
   * Attaches the viewer to the given table.
   * 
   * @param table
   *          view table
   */
  public ClassesViewer(Table table) {
    this.table = table;
    viewer = new CheckboxTableViewer(table);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setLabelProvider(new PackageFragmentRootLabelProvider());
    viewer.addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(CheckStateChangedEvent event) {
        updateCheckedStatus(event.getElement(), event.getChecked());
      }
    });
  }

  /**
   * Returns the table used by the viewer.
   * 
   * @return table used by the viewer
   */
  public Table getTable() {
    return table;
  }

  /**
   * Sets the input for this viewer.
   * 
   * @param input
   *          list of classfiles objects the user can select from
   */
  public void setInput(IClassFiles[] input) {
    this.input = input;
    viewer.setInput(getPackageFragmentRoots(input));
  }

  /**
   * Specifies whether binary classpath entries should also be listed.
   * 
   * @param includebinaries
   *          <code>true</code> if binary entries should be listed
   */
  public void setIncludeBinaries(boolean includebinaries) {
    this.includebinaries = includebinaries;
    if (!includebinaries) {
      for (Iterator i = checkedclasses.iterator(); i.hasNext(); ) {
        if (((IClassFiles) i.next()).isBinary()) {
          i.remove();
        }
      }
    }
    if (input != null) {
      viewer.setInput(getPackageFragmentRoots(input));
    }
  }

  /**
   * Sets the initially checked classes.
   * 
   * @param classfiles
   *          list of classfiles that should be checked
   */
  public void setCheckedClasses(IClassFiles[] classfiles) {
    checkedclasses.clear();
    checkedclasses.addAll(Arrays.asList(classfiles));
    viewer.setCheckedElements(getPackageFragmentRoots(classfiles));
  }
  
  /**
   * Sets the initially checked classes from the given locations. 
   *
   * @param locations
   *   location strings of the classes to select
   */
  public void setCheckedClasses(String[] locations) {
    Set lset = new HashSet(Arrays.asList(locations));
    checkedclasses.clear();
    for (int i = 0; i < input.length; i++) {
      if (lset.contains(input[i].getLocation().toString())) {
        checkedclasses.add(input[i]);
      }
    }
    IClassFiles[] ccs = (IClassFiles[]) checkedclasses.toArray(new IClassFiles[0]);
    viewer.setCheckedElements(getPackageFragmentRoots(ccs));
  }

  /**
   * Returns the currently checked classes.
   * 
   * @return list of class files that are currently checked
   */
  public IClassFiles[] getCheckedClasses() {
    return (IClassFiles[]) checkedclasses.toArray(new IClassFiles[0]);
  }
  
  /**
   * Returns the locations of the currently checked classes.
   * 
   * @return list of locations of class files that are currently checked
   */
  public String[] getCheckedClassesLocations() {
    String[] locs = new String[checkedclasses.size()];
    int idx = 0;
    for (Iterator i = checkedclasses.iterator(); i.hasNext(); ) {
      locs[idx++] = ((IClassFiles) i.next()).getLocation().toString();
    }
    return locs;
  }
  
  private IPackageFragmentRoot[] getPackageFragmentRoots(Object[] classfiles) {
    Set roots = new HashSet();
    for (int i = 0; i < classfiles.length; i++) {
      IClassFiles cf = (IClassFiles) classfiles[i];
      if (includebinaries || !cf.isBinary()) {
        roots.addAll(Arrays.asList(cf.getPackageFragmentRoots()));
      }
    }
    return (IPackageFragmentRoot[]) roots.toArray(new IPackageFragmentRoot[roots.size()]);
  }

  private void updateCheckedStatus(Object root, boolean checked) {
    for (int i = 0; i < input.length; i++) {
      IClassFiles cf = input[i];
      if (Arrays.asList(cf.getPackageFragmentRoots()).contains(root)) {
        if (checked) {
          checkedclasses.add(cf);
        } else {
          checkedclasses.remove(cf);
        }
        break;
      }
    }
    viewer.setCheckedElements(getPackageFragmentRoots(checkedclasses.toArray()));
  }
  
}
