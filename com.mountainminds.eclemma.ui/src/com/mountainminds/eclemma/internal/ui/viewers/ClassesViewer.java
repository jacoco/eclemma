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
package com.mountainminds.eclemma.internal.ui.viewers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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
 * Viewer for selecting <code>IClassFiles</code> objects from a given list. The
 * viewer lists the corresponding IPackageFragmentRoots. Source based class
 * files may have multiple corresponding roots, their selection status is
 * connected.
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
      @SuppressWarnings("unchecked")
      final Comparator<Object> comparator = getComparator();
      int result = comparator.compare(root1.getJavaProject().getElementName(),
          root2.getJavaProject().getElementName());
      if (result != 0)
        return result;
      if (root1.isExternal() != root2.isExternal()) {
        return root1.isExternal() ? 1 : -1;
      }
      return comparator.compare(getPathLabel(root1), getPathLabel(root2));
    }

  };

  /**
   * Calculates a label for the class path of the given package fragment root.
   * For external entries this is the full path, otherwise it is the project
   * relative path.
   * 
   * @param root
   *          package fragment root
   * @return label for the class path entry
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
  private final List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();

  private IClassFiles[] input;
  private boolean includebinaries;
  private final Set<IClassFiles> selectedclasses = new HashSet<IClassFiles>();

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
    viewer.setInput(getPackageFragmentRoots(Arrays.asList(input)));
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
      for (Iterator<IClassFiles> i = selectedclasses.iterator(); i.hasNext();) {
        if (i.next().isBinary()) {
          i.remove();
        }
      }
    }
    if (input != null) {
      viewer.setInput(getPackageFragmentRoots(Arrays.asList(input)));
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
    viewer
        .setCheckedElements(getPackageFragmentRoots(Arrays.asList(classfiles)));
  }

  /**
   * Sets the initially checked classes from the given locations.
   * 
   * @param locations
   *          location strings of the classes to select
   */
  public void setSelectedClasses(String[] locations) {
    Set<String> lset = new HashSet<String>(Arrays.asList(locations));
    selectedclasses.clear();
    for (final IClassFiles c : input) {
      if (lset.contains(c.getLocation().toString())) {
        selectedclasses.add(c);
      }
    }
    viewer.setCheckedElements(getPackageFragmentRoots(selectedclasses));
  }

  public void selectAll() {
    selectedclasses.clear();
    for (final IClassFiles cf : input) {
      if (includebinaries || !cf.isBinary()) {
        selectedclasses.add(cf);
      }
    }
    viewer.setCheckedElements(getPackageFragmentRoots(selectedclasses));
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
    return selectedclasses.toArray(new IClassFiles[selectedclasses.size()]);
  }

  /**
   * Returns the locations of the currently checked classes.
   * 
   * @return list of locations of class files that are currently checked
   */
  public String[] getSelectedClassesLocations() {
    String[] locs = new String[selectedclasses.size()];
    int idx = 0;
    for (final IClassFiles c : selectedclasses) {
      locs[idx++] = c.getLocation().toString();
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

  private IPackageFragmentRoot[] getPackageFragmentRoots(
      Collection<IClassFiles> classfiles) {
    Set<IPackageFragmentRoot> roots = new HashSet<IPackageFragmentRoot>();
    for (IClassFiles cf : classfiles) {
      if (includebinaries || !cf.isBinary()) {
        roots.addAll(Arrays.asList(cf.getPackageFragmentRoots()));
      }
    }
    return roots.toArray(new IPackageFragmentRoot[roots.size()]);
  }

  private void updateCheckedStatus(Object root, boolean checked) {
    for (IClassFiles cf : input) {
      if (Arrays.asList(cf.getPackageFragmentRoots()).contains(root)) {
        if (checked) {
          selectedclasses.add(cf);
        } else {
          selectedclasses.remove(cf);
        }
        break;
      }
    }
    viewer.setCheckedElements(getPackageFragmentRoots(selectedclasses));
    fireSelectionEvent();
  }

  private void fireSelectionEvent() {
    SelectionChangedEvent evt = new SelectionChangedEvent(this, getSelection());
    for (final ISelectionChangedListener l : listeners) {
      l.selectionChanged(evt);
    }
  }

  // ISelectionProvider interface

  public ISelection getSelection() {
    return new StructuredSelection(getSelectedClasses());
  }

  public void setSelection(ISelection selection) {
    selectedclasses.clear();
    for (Object obj : ((IStructuredSelection) selection).toArray()) {
      selectedclasses.add((IClassFiles) obj);
    }
    viewer.setCheckedElements(getPackageFragmentRoots(selectedclasses));
  }

}
