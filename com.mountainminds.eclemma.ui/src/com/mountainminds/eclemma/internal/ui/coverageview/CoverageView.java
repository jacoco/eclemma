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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Implementation of the coverage view.
 */
public class CoverageView extends ViewPart implements IShowInTarget {

  public static final String ID = "com.mountainminds.eclemma.ui.CoverageView"; //$NON-NLS-1$

  /**
   * Placeholder element for displaying "Loading..." in the coverage view.
   */
  public static final Object LOADING_ELEMENT = new Object();

  private static final DecimalFormat COVERAGE_VALUE = new DecimalFormat(
      UIMessages.CoverageView_columnCoverageValue);

  private ViewSettings settings = new ViewSettings();

  protected static final int COLUMN_ELEMENT = 0;
  protected static final int COLUMN_RATIO = 1;
  protected static final int COLUMN_COVERED = 2;
  protected static final int COLUMN_MISSED = 3;
  protected static final int COLUMN_TOTAL = 4;

  private Tree tree;
  private TreeColumn column0;
  private TreeColumn column1;
  private TreeColumn column2;
  private TreeColumn column3;
  private TreeColumn column4;
  private TreeViewer viewer;

  // Actions
  private OpenAction openAction;

  private final List<IHandler> handlers = new ArrayList<IHandler>();

  private SelectionTracker selectiontracker;
  private CoverageViewSorter sorter = new CoverageViewSorter(settings, this);

  private final ISessionListener descriptionUpdater = new ISessionListener() {
    public void sessionActivated(ICoverageSession session) {
      getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
        public void run() {
          final ICoverageSession active = CoverageTools.getSessionManager()
              .getActiveSession();
          setContentDescription(active == null ? "" : active.getDescription()); //$NON-NLS-1$
        }
      });
    }

    public void sessionAdded(ICoverageSession addedSession) {
      // Nothing to do
    }

    public void sessionRemoved(ICoverageSession removedSession) {
      // Nothing to do
    }
  };

  private final IJavaCoverageListener coverageListener = new IJavaCoverageListener() {
    public void coverageChanged() {
      tree.getDisplay().asyncExec(new Runnable() {
        public void run() {
          viewer.setInput(CoverageTools.getJavaModelCoverage());
        }
      });
    }
  };

  private final ITableLabelProvider labelprovider = new ITableLabelProvider() {

    private ILabelProvider delegate = new WorkbenchLabelProvider();

    public Image getColumnImage(Object element, int columnIndex) {
      if (element == LOADING_ELEMENT) {
        return null;
      }
      switch (columnIndex) {
      case COLUMN_ELEMENT:
        return delegate.getImage(element);
      case COLUMN_RATIO:
        ICounter counter = CoverageTools.getCoverageInfo(element).getCounter(
            settings.getCounters());
        if (counter.getTotalCount() == 0) {
          return null;
        } else {
          return EclEmmaUIPlugin.getCoverageImage(counter.getCoveredRatio());
        }
      }
      return null;
    }

    private String getSimpleTextForJavaElement(Object element) {
      if (element instanceof IPackageFragmentRoot) {
        final IPackageFragmentRoot root = (IPackageFragmentRoot) element;
        // tweak label if the package fragment root is the project itself:
        if (root.getElementName().length() == 0) {
          element = root.getJavaProject();
        }
        // shorten JAR references
        try {
          if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
            return root.getPath().lastSegment();
          }
        } catch (JavaModelException e) {
          EclEmmaUIPlugin.log(e);
        }
      }
      return delegate.getText(element);
    }

    private String getTextForJavaElement(Object element) {
      String text = getSimpleTextForJavaElement(element);
      if (element instanceof IPackageFragmentRoot
          && ElementType.BUNDLE.equals(settings.getRootType())) {
        text += " - " + getTextForJavaElement(((IPackageFragmentRoot) element).getJavaProject()); //$NON-NLS-1$
      }
      return text;
    }

    public String getColumnText(Object element, int columnIndex) {
      if (element == LOADING_ELEMENT) {
        return columnIndex == COLUMN_ELEMENT ? UIMessages.CoverageView_loadingMessage
            : ""; //$NON-NLS-1$
      }
      ICounter counter = CoverageTools.getCoverageInfo(element).getCounter(
          settings.getCounters());
      switch (columnIndex) {
      case COLUMN_ELEMENT:
        return getTextForJavaElement(element);
      case COLUMN_RATIO:
        if (counter.getTotalCount() == 0) {
          return ""; //$NON-NLS-1$
        } else {
          return COVERAGE_VALUE
              .format(Double.valueOf(counter.getCoveredRatio()));
        }
      case COLUMN_COVERED:
        return String.valueOf(counter.getCoveredCount());
      case COLUMN_MISSED:
        return String.valueOf(counter.getMissedCount());
      case COLUMN_TOTAL:
        return String.valueOf(counter.getTotalCount());
      }
      return ""; //$NON-NLS-1$
    }

    public boolean isLabelProperty(Object element, String property) {
      return delegate.isLabelProperty(element, property);
    }

    public void addListener(ILabelProviderListener listener) {
      delegate.addListener(listener);
    }

    public void removeListener(ILabelProviderListener listener) {
      delegate.removeListener(listener);
    }

    public void dispose() {
      delegate.dispose();
    }
  };

  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    settings.init(memento);
  }

  public void saveState(IMemento memento) {
    int[] widths = settings.getColumnWidths();
    widths[0] = column0.getWidth();
    widths[1] = column1.getWidth();
    widths[2] = column2.getWidth();
    widths[3] = column3.getWidth();
    widths[4] = column4.getWidth();
    settings.save(memento);
    super.saveState(memento);
  }

  public void createPartControl(Composite parent) {
    ContextHelp.setHelp(parent, ContextHelp.COVERAGE_VIEW);
    tree = new Tree(parent, SWT.MULTI);
    tree.setHeaderVisible(true);
    tree.setLinesVisible(true);
    int[] widths = settings.getColumnWidths();
    column0 = new TreeColumn(tree, SWT.NONE);
    column0.setWidth(widths[0]);
    sorter.addColumn(column0, COLUMN_ELEMENT);
    column1 = new TreeColumn(tree, SWT.RIGHT);
    column1.setWidth(widths[1]);
    sorter.addColumn(column1, COLUMN_RATIO);
    column2 = new TreeColumn(tree, SWT.RIGHT);
    column2.setWidth(widths[2]);
    sorter.addColumn(column2, COLUMN_COVERED);
    column3 = new TreeColumn(tree, SWT.RIGHT);
    column3.setWidth(widths[3]);
    sorter.addColumn(column3, COLUMN_MISSED);
    column4 = new TreeColumn(tree, SWT.RIGHT);
    column4.setWidth(widths[4]);
    sorter.addColumn(column4, COLUMN_TOTAL);
    updateColumnHeaders();

    TreeColumn sortColumn = null;
    switch (settings.getSortColumn()) {
    case COLUMN_ELEMENT:
      sortColumn = column0;
      break;
    case COLUMN_RATIO:
      sortColumn = column1;
      break;
    case COLUMN_COVERED:
      sortColumn = column2;
      break;
    case COLUMN_MISSED:
      sortColumn = column3;
      break;
    case COLUMN_TOTAL:
      sortColumn = column4;
      break;
    }

    sorter.setSortColumnAndDirection(sortColumn,
        settings.isReverseSort() ? SWT.DOWN : SWT.UP);

    viewer = new TreeViewer(tree);
    viewer.addFilter(new ViewerFilter() {
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element == LOADING_ELEMENT) {
          return true;
        } else {
          final ICoverageNode c = CoverageTools.getCoverageInfo(element);
          if (c == null) {
            return false;
          }
          final ICounter instructions = c.getInstructionCounter();
          if (instructions.getTotalCount() == 0) {
            return false;
          }
          if (settings.getHideUnusedElements()
              && instructions.getCoveredCount() == 0) {
            return false;
          }
          return true;
        }
      }
    });
    viewer.setComparator(sorter);
    viewer.setContentProvider(new CoveredElementsContentProvider(settings));
    viewer.setLabelProvider(labelprovider);
    viewer.setInput(CoverageTools.getJavaModelCoverage());
    getSite().setSelectionProvider(viewer);

    selectiontracker = new SelectionTracker(this, viewer);

    createHandlers();
    createActions();

    viewer.addOpenListener(new IOpenListener() {
      public void open(OpenEvent event) {
        openAction.run((IStructuredSelection) event.getSelection());
      }
    });

    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    tree.setMenu(menuMgr.createContextMenu(tree));
    getSite().registerContextMenu(menuMgr, viewer);

    CoverageTools.getSessionManager().addSessionListener(descriptionUpdater);
    CoverageTools.addJavaCoverageListener(coverageListener);
  }

  /**
   * Create local handlers.
   */
  private void createHandlers() {
    activateHandler(SelectRootElementsHandler.ID,
        new SelectRootElementsHandler(settings, this));
    activateHandler(SelectCountersHandler.ID, new SelectCountersHandler(
        settings, this));
    activateHandler(HideUnusedElementsHandler.ID,
        new HideUnusedElementsHandler(settings, this));
    activateHandler(IWorkbenchCommandConstants.EDIT_COPY,
        new CopyHandler(tree.getDisplay(), settings, labelprovider, viewer));
    activateHandler(IWorkbenchCommandConstants.FILE_REFRESH,
        new RefreshSessionHandler(CoverageTools.getSessionManager()));
    activateHandler(IWorkbenchCommandConstants.NAVIGATE_COLLAPSE_ALL,
        new CollapseAllHandler(viewer));
    activateHandler(LinkWithSelectionHandler.ID, new LinkWithSelectionHandler(
        settings, selectiontracker));
  }

  private void activateHandler(String id, IHandler handler) {
    final IHandlerService hs = (IHandlerService) getSite().getService(
        IHandlerService.class);
    hs.activateHandler(id, handler);
    handlers.add(handler);
  }

  private void createActions() {
    // For the following commands we use actions, as they are already available

    final IActionBars ab = getViewSite().getActionBars();

    openAction = new OpenAction(getSite());
    openAction
        .setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
    ab.setGlobalActionHandler(JdtActionConstants.OPEN, openAction);
    openAction.setEnabled(false);
    viewer.addSelectionChangedListener(openAction);

    PropertyDialogAction propertiesAction = new PropertyDialogAction(getSite(),
        viewer);
    propertiesAction
        .setActionDefinitionId(IWorkbenchCommandConstants.FILE_PROPERTIES);
    ab.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
        propertiesAction);
    propertiesAction.setEnabled(false);
    viewer.addSelectionChangedListener(propertiesAction);
  }

  public void setFocus() {
    tree.setFocus();
  }

  public void dispose() {
    for (IHandler h : handlers) {
      h.dispose();
    }
    handlers.clear();
    CoverageTools.removeJavaCoverageListener(coverageListener);
    CoverageTools.getSessionManager().removeSessionListener(descriptionUpdater);
    selectiontracker.dispose();
    super.dispose();
  }

  protected void updateColumnHeaders() {
    String[] columns = settings.getColumnHeaders();
    column0.setText(columns[0]);
    column1.setText(columns[1]);
    column2.setText(columns[2]);
    column3.setText(columns[3]);
    column4.setText(columns[4]);
  }

  protected void refreshViewer() {
    viewer.refresh();
  }

  public boolean show(ShowInContext context) {
    final ISelection selection = context.getSelection();
    if (selection instanceof IStructuredSelection) {
      viewer.setSelection(selection);
      return true;
    }
    return false;
  }

}
