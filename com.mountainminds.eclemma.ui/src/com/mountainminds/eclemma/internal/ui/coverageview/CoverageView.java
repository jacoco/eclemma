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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
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

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.RedGreenBar;
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

  private final ViewSettings settings = new ViewSettings();

  private final CellTextConverter cellTextConverter = new CellTextConverter(
      settings);

  private final MaxTotalCache maxTotalCache = new MaxTotalCache(settings);

  protected static final int COLUMN_ELEMENT = 0;
  protected static final int COLUMN_RATIO = 1;
  protected static final int COLUMN_COVERED = 2;
  protected static final int COLUMN_MISSED = 3;
  protected static final int COLUMN_TOTAL = 4;

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
      getSite().getShell().getDisplay().asyncExec(new Runnable() {
        public void run() {
          maxTotalCache.reset();
          viewer.setInput(CoverageTools.getJavaModelCoverage());
        }
      });
    }
  };

  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    settings.init(memento);
  }

  public void saveState(IMemento memento) {
    settings.storeColumnWidth(viewer);
    settings.save(memento);
    super.saveState(memento);
  }

  public void createPartControl(Composite parent) {
    ContextHelp.setHelp(parent, ContextHelp.COVERAGE_VIEW);
    Tree tree = new Tree(parent, SWT.MULTI);
    tree.setHeaderVisible(true);
    tree.setLinesVisible(true);

    viewer = new TreeViewer(tree);
    final TreeViewerColumn column0 = new TreeViewerColumn(viewer, SWT.LEFT);
    column0.setLabelProvider(new CellLabelProvider() {

      private final ILabelProvider delegate = new WorkbenchLabelProvider();

      @Override
      public void update(ViewerCell cell) {
        if (cell.getElement() == LOADING_ELEMENT) {
          cell.setText(UIMessages.CoverageView_loadingMessage);
          cell.setImage(null);
        } else {
          cell.setText(cellTextConverter.getElementName(cell.getElement()));
          cell.setImage(delegate.getImage(cell.getElement()));
        }
      }
    });
    sorter.addColumn(column0, COLUMN_ELEMENT);

    final TreeViewerColumn column1 = new TreeViewerColumn(viewer, SWT.RIGHT);
    column1.setLabelProvider(new OwnerDrawLabelProvider() {

      @Override
      public void update(ViewerCell cell) {
        if (cell.getElement() == LOADING_ELEMENT) {
          cell.setText(""); //$NON-NLS-1$
        } else {
          cell.setText(cellTextConverter.getRatio(cell.getElement()));
        }
      }

      @Override
      protected void erase(Event event, Object element) {
      }

      @Override
      protected void measure(Event event, Object element) {
      }

      @Override
      protected void paint(Event event, Object element) {
        if (element != LOADING_ELEMENT) {
          ICounter counter = CoverageTools.getCoverageInfo(element).getCounter(
              settings.getCounters());
          RedGreenBar.draw(event, column1.getColumn().getWidth(), counter,
              maxTotalCache.getMaxTotal(element));
        }
      }
    });
    sorter.addColumn(column1, COLUMN_RATIO);

    final TreeViewerColumn column2 = new TreeViewerColumn(viewer, SWT.RIGHT);
    column2.setLabelProvider(new CellLabelProvider() {

      @Override
      public void update(ViewerCell cell) {
        if (cell.getElement() == LOADING_ELEMENT) {
          cell.setText(""); //$NON-NLS-1$
        } else {
          cell.setText(cellTextConverter.getCovered(cell.getElement()));
        }
      }
    });
    sorter.addColumn(column2, COLUMN_COVERED);

    final TreeViewerColumn column3 = new TreeViewerColumn(viewer, SWT.RIGHT);
    column3.setLabelProvider(new CellLabelProvider() {

      @Override
      public void update(ViewerCell cell) {
        if (cell.getElement() == LOADING_ELEMENT) {
          cell.setText(""); //$NON-NLS-1$
        } else {
          cell.setText(cellTextConverter.getMissed(cell.getElement()));
        }
      }
    });
    sorter.addColumn(column3, COLUMN_MISSED);

    final TreeViewerColumn column4 = new TreeViewerColumn(viewer, SWT.RIGHT);
    column4.setLabelProvider(new CellLabelProvider() {

      @Override
      public void update(ViewerCell cell) {
        if (cell.getElement() == LOADING_ELEMENT) {
          cell.setText(""); //$NON-NLS-1$
        } else {
          cell.setText(cellTextConverter.getTotal(cell.getElement()));
        }
      }
    });
    sorter.addColumn(column4, COLUMN_TOTAL);

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
    settings.updateColumnHeaders(viewer);
    settings.restoreColumnWidth(viewer);
    viewer.setComparator(sorter);
    viewer.setContentProvider(new CoveredElementsContentProvider(settings));
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
    activateHandler(IWorkbenchCommandConstants.EDIT_COPY, new CopyHandler(
        settings, getSite().getShell().getDisplay(), viewer));
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
    viewer.getTree().setFocus();
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

  protected void refreshViewer() {
    maxTotalCache.reset();
    settings.updateColumnHeaders(viewer);
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
