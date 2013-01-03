/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Viewer for selecting {@link IPackageFragmentRoot} objects from a given list.
 */
public class ScopeViewer implements ISelectionProvider {

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
    final IPath path = root.getPath();
    try {
      if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
        return path.lastSegment();
      }
    } catch (JavaModelException e) {
      EclEmmaUIPlugin.log(e);
    }
    return path.removeFirstSegments(1).toString();
  }

  private final Table table;
  private final CheckboxTableViewer viewer;
  private final List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();

  private boolean includebinaries;

  /**
   * Creates a new viewer within the given parent.
   * 
   * @param parent
   *          composite to create the viewer's table in
   * @param style
   *          flags specifying the table's style
   */
  public ScopeViewer(Composite parent, int style) {
    this(new Table(parent, SWT.CHECK | style));
  }

  /**
   * Attaches the viewer to the given table.
   * 
   * @param table
   *          view table
   */
  public ScopeViewer(Table table) {
    this.table = table;
    viewer = new CheckboxTableViewer(table);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setLabelProvider(new PackageFragmentRootLabelProvider());
    viewer.setSorter(new PackageFragmentRootSorter());
    viewer.addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (includebinaries) {
          return true;
        }
        final IPackageFragmentRoot root = (IPackageFragmentRoot) element;
        try {
          return root.getKind() == IPackageFragmentRoot.K_SOURCE;
        } catch (JavaModelException e) {
          EclEmmaUIPlugin.log(e);
          return false;
        }
      }
    });
    viewer.addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(CheckStateChangedEvent event) {
        fireSelectionEvent();
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
   *          list of {@link IPackageFragmentRoot}s the user can select from
   */
  public void setInput(Collection<IPackageFragmentRoot> input) {
    viewer.setInput(input);
  }

  /**
   * Specifies whether binary package fragment roots should also be listed.
   * 
   * @param includebinaries
   *          <code>true</code> if binary entries should be listed
   */
  public void setIncludeBinaries(boolean includebinaries) {
    this.includebinaries = includebinaries;
    this.viewer.refresh();
  }

  /**
   * Sets the selected scope.
   * 
   * @param scope
   *          list of package fragment roots that should be checked
   */
  public void setSelectedScope(final Collection<IPackageFragmentRoot> scope) {
    viewer.setCheckedElements(scope.toArray());
  }

  public void selectAll() {
    viewer.setAllChecked(true);
    fireSelectionEvent();
  }

  public void deselectAll() {
    viewer.setAllChecked(false);
    fireSelectionEvent();
  }

  /**
   * Returns the currently selected scope.
   * 
   * @return list of package fragment roots that are currently checked
   */
  public Set<IPackageFragmentRoot> getSelectedScope() {
    Set<IPackageFragmentRoot> scope = new HashSet<IPackageFragmentRoot>();
    for (final Object element : viewer.getCheckedElements()) {
      scope.add((IPackageFragmentRoot) element);
    }
    return scope;
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

  private void fireSelectionEvent() {
    SelectionChangedEvent evt = new SelectionChangedEvent(this, getSelection());
    for (final ISelectionChangedListener l : listeners) {
      l.selectionChanged(evt);
    }
  }

  // ISelectionProvider interface

  public ISelection getSelection() {
    return new StructuredSelection(getSelectedScope().toArray());
  }

  public void setSelection(ISelection selection) {
    Collection<IPackageFragmentRoot> scope = new ArrayList<IPackageFragmentRoot>();
    for (final Object obj : ((IStructuredSelection) selection).toArray()) {
      scope.add((IPackageFragmentRoot) obj);
    }
    setSelectedScope(scope);
  }

}
