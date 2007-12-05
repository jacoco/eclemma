/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.viewers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Viewer for selecting <code>IClassFiles</code> objects from a given list.
 * The viewer lists the corresponding IPackageFragmentRoots. Source based class
 * files may have multiple corresponding roots, their selection status is
 * connected.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class ClassesViewer implements ISelectionProvider {

  private static class PackageFragmentRootLabelProvider extends LabelProvider {

    private ILabelProvider delegate = new WorkbenchLabelProvider();

    public Image getImage(Object element) {
      return delegate.getImage(element);
    }

    public String getText(Object element) {
      IPackageFragmentRoot root = (IPackageFragmentRoot) element;
      String projectname = root.getJavaProject().getElementName();
      String path = getPathLabel(root);
      if (path.length() > 0) {
        String fmt = UIMessages.ClassesViewerEntry_label;
        return NLS.bind(fmt, projectname, getPathLabel(root));
      } else {
        return projectname;
      }
    }

    public void dispose() {
      delegate.dispose();
    }

  }

  /**
   * The entries will be sorted by project name, type and path name. 
   */
  private static class PackageFragmentRootSorter extends ViewerSorter {

    public int compare(Viewer viewer, Object e1, Object e2) {
      IPackageFragmentRoot root1 = (IPackageFragmentRoot) e1;
      IPackageFragmentRoot root2 = (IPackageFragmentRoot) e2;
      int result = getCollator().compare(root1.getJavaProject().getElementName(), 
    	                                 root2.getJavaProject().getElementName());
      if (result != 0) return result;
      if (root1.isExternal() != root2.isExternal()) {
        return root1.isExternal() ? 1 : -1;
      }
      return getCollator().compare(getPathLabel(root1), getPathLabel(root2));
    }
	  
  };
  
  /**
   * Calculates a label for the class path of the given package fragment root.
   * For external entries this is the full path, otherwise it is the project
   * relative path.
   * 
   * @param root  package fragement root
   * @return  label for the class path entry
   */
  private static String getPathLabel(IPackageFragmentRoot root) {
    IPath path = root.getPath();
    if (!root.isExternal()) {
      path = path.removeFirstSegments(1);
    }
    return path.toString();
  }
  
  private final Table table;
  private final CheckboxTableViewer viewer;
  private final List listeners = new ArrayList();

  private IClassFiles[] input;
  private boolean includebinaries;
  private final Set selectedclasses = new HashSet();

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
    viewer.setSorter(new PackageFragmentRootSorter());
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
      for (Iterator i = selectedclasses.iterator(); i.hasNext();) {
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
  public void setSelectedClasses(IClassFiles[] classfiles) {
    selectedclasses.clear();
    selectedclasses.addAll(Arrays.asList(classfiles));
    viewer.setCheckedElements(getPackageFragmentRoots(classfiles));
  }

  /**
   * Sets the initially checked classes from the given locations.
   * 
   * @param locations
   *          location strings of the classes to select
   */
  public void setSelectedClasses(String[] locations) {
    Set lset = new HashSet(Arrays.asList(locations));
    selectedclasses.clear();
    for (int i = 0; i < input.length; i++) {
      if (lset.contains(input[i].getLocation().toString())) {
        selectedclasses.add(input[i]);
      }
    }
    viewer.setCheckedElements(getPackageFragmentRoots(selectedclasses.toArray()));
  }
  
  public void selectAll() {
    selectedclasses.clear();
    for (int i = 0; i < input.length; i++) {
      if (includebinaries || !input[i].isBinary()) {
        selectedclasses.add(input[i]);
      }
    }
    viewer.setCheckedElements(getPackageFragmentRoots(selectedclasses.toArray()));
  }

  public void deselectAll() {
    selectedclasses.clear();
    viewer.setCheckedElements(new Object[0]);
  }
  
  /**
   * Returns the currently checked classes.
   * 
   * @return list of class files that are currently checked
   */
  public IClassFiles[] getSelectedClasses() {
    return (IClassFiles[]) selectedclasses.toArray(new IClassFiles[0]);
  }

  /**
   * Returns the locations of the currently checked classes.
   * 
   * @return list of locations of class files that are currently checked
   */
  public String[] getSelectedClassesLocations() {
    String[] locs = new String[selectedclasses.size()];
    int idx = 0;
    for (Iterator i = selectedclasses.iterator(); i.hasNext();) {
      locs[idx++] = ((IClassFiles) i.next()).getLocation().toString();
    }
    return locs;
  }

  /**
   * Registers the given selection listener if not already registered.
   * 
   * @param listener
   *          listener to add
   */
  public void addSelectionChangedListener(ISelectionChangedListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Removes the given selection listener.
   * 
   * @param listener
   *          listener to remove
   */
  public void removeSelectionChangedListener(ISelectionChangedListener listener) {
    listeners.remove(listener);
  }
  
  private IPackageFragmentRoot[] getPackageFragmentRoots(Object[] classfiles) {
    Set roots = new HashSet();
    for (int i = 0; i < classfiles.length; i++) {
      IClassFiles cf = (IClassFiles) classfiles[i];
      if (includebinaries || !cf.isBinary()) {
        roots.addAll(Arrays.asList(cf.getPackageFragmentRoots()));
      }
    }
    return (IPackageFragmentRoot[]) roots
        .toArray(new IPackageFragmentRoot[roots.size()]);
  }

  private void updateCheckedStatus(Object root, boolean checked) {
    for (int i = 0; i < input.length; i++) {
      IClassFiles cf = input[i];
      if (Arrays.asList(cf.getPackageFragmentRoots()).contains(root)) {
        if (checked) {
          selectedclasses.add(cf);
        } else {
          selectedclasses.remove(cf);
        }
        break;
      }
    }
    viewer.setCheckedElements(getPackageFragmentRoots(selectedclasses.toArray()));
    fireSelectionEvent();
  }

  private void fireSelectionEvent() {
    SelectionChangedEvent evt = new SelectionChangedEvent(this, getSelection());
    for (Iterator i = listeners.iterator(); i.hasNext();) {
      ISelectionChangedListener l = (ISelectionChangedListener) i.next();
      l.selectionChanged(evt);
    }
  }
  
  // ISelectionProvider interface

  public ISelection getSelection() {
    return new StructuredSelection(getSelectedClasses());
  }

  public void setSelection(ISelection selection) {
    Object[] classfiles = ((IStructuredSelection) selection).toArray();
    selectedclasses.clear();
    selectedclasses.addAll(Arrays.asList(classfiles));
    viewer.setCheckedElements(getPackageFragmentRoots(classfiles));
  }
  
}
