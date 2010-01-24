/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 * 
 * Contributors:
 *   Brock Janiczak - link with selection option (SF #1774547)
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import java.text.DecimalFormat;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.analysis.ICounter;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.actions.ExportSessionAction;
import com.mountainminds.eclemma.internal.ui.actions.ImportSessionAction;
import com.mountainminds.eclemma.internal.ui.actions.MergeSessionsAction;
import com.mountainminds.eclemma.internal.ui.actions.RefreshSessionAction;
import com.mountainminds.eclemma.internal.ui.actions.RemoveActiveSessionAction;
import com.mountainminds.eclemma.internal.ui.actions.RemoveAllSessionsAction;

/**
 * Implementation of the coverage view.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
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
  private IAction copyAction;
  private IAction relaunchSessionAction;
  private IAction removeActiveSessionAction;
  private IAction removeAllSessionsAction;
  private IAction mergeSessionsAction;
  private IAction selectSessionAction;
  private IAction importAction;
  private IAction exportAction;
  private IAction refreshAction;
  private PropertyDialogAction propertiesAction;

  private SelectionTracker selectiontracker;
  private CoverageViewSorter sorter = new CoverageViewSorter(settings, this);

  private ISessionListener listener = new ISessionListener() {
    public void sessionAdded(ICoverageSession newSession) {
      updateActions();
    }

    public void sessionRemoved(ICoverageSession oldSession) {
      updateActions();
    }

    public void sessionActivated(ICoverageSession session) {
      updateActions();
    }
  };

  private IJavaCoverageListener coverageListener = new IJavaCoverageListener() {
    public void coverageChanged() {
      tree.getDisplay().asyncExec(new Runnable() {
        public void run() {
          viewer.setInput(CoverageTools.getJavaModelCoverage());
        }
      });
    }
  };

  private ITableLabelProvider labelprovider = new ITableLabelProvider() {

    private ILabelProvider delegate = new WorkbenchLabelProvider();

    public Image getColumnImage(Object element, int columnIndex) {
      if (element == LOADING_ELEMENT) {
        return null;
      }
      switch (columnIndex) {
      case COLUMN_ELEMENT:
        return delegate.getImage(element);
      case COLUMN_RATIO:
        ICounter counter = settings.getCounterMode().getCounter(
            CoverageTools.getCoverageInfo(element));
        if (counter.getTotalCount() == 0) {
          return null;
        } else {
          return EclEmmaUIPlugin.getCoverageImage(counter.getRatio());
        }
      }
      return null;
    }

    private String getSimpleTextForJavaElement(Object element) {
      if (element instanceof IPackageFragmentRoot) {
        // tweak label if the package fragment root is the project itself:
        IPackageFragmentRoot root = (IPackageFragmentRoot) element;
        if (root.getElementName().length() == 0) {
          element = root.getJavaProject();
        }
      }
      return delegate.getText(element);
    }

    private String getTextForJavaElement(Object element) {
      String text = getSimpleTextForJavaElement(element);
      switch (settings.getEntryMode()) {
      case ViewSettings.ENTRYMODE_PACKAGEROOTS:
        if (element instanceof IPackageFragmentRoot) {
          text += " - " + getTextForJavaElement(((IPackageFragmentRoot) element).getJavaProject()); //$NON-NLS-1$
        }
        break;
      }
      return text;
    }

    public String getColumnText(Object element, int columnIndex) {
      if (element == LOADING_ELEMENT) {
        return columnIndex == COLUMN_ELEMENT ? UIMessages.CoverageView_loadingMessage
            : ""; //$NON-NLS-1$
      }
      ICounter counter = settings.getCounterMode().getCounter(
          CoverageTools.getCoverageInfo(element));
      switch (columnIndex) {
      case COLUMN_ELEMENT:
        return getTextForJavaElement(element);
      case COLUMN_RATIO:
        if (counter.getTotalCount() == 0) {
          return ""; //$NON-NLS-1$
        } else {
          return COVERAGE_VALUE.format(new Double(counter.getRatio()));
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

    TreeSortCompatibility.setTreeSortColumnAndDirection(sortColumn, settings
        .isReverseSort() ? SWT.DOWN : SWT.UP);

    viewer = new TreeViewer(tree);
    viewer.addFilter(new ViewerFilter() {
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element == LOADING_ELEMENT) {
          return true;
        } else {
          IJavaElementCoverage c = CoverageTools.getCoverageInfo(element);
          if (c == null || c.getInstructionCounter().getTotalCount() == 0) {
            return false;
          }
          if (settings.getHideUnusedTypes()) {
            ICounter cnt = c.getTypeCounter();
            return cnt.getTotalCount() == 0 || cnt.getCoveredCount() != 0;
          }
          return true;
        }
      }
    });
    viewer.setSorter(sorter);
    viewer.setContentProvider(new CoveredElementsContentProvider(settings));
    viewer.setLabelProvider(labelprovider);
    viewer.setInput(CoverageTools.getJavaModelCoverage());
    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        openAction
            .selectionChanged((IStructuredSelection) event.getSelection());
        propertiesAction.selectionChanged(event);
      }
    });
    getSite().setSelectionProvider(viewer);

    selectiontracker = new SelectionTracker(this, viewer);

    createActions();
    updateActions();
    configureToolbar();

    viewer.addOpenListener(new IOpenListener() {
      public void open(OpenEvent event) {
        openAction.run((IStructuredSelection) event.getSelection());
      }
    });

    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    tree.setMenu(menuMgr.createContextMenu(tree));
    menuMgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager menuMgr) {
        configureContextMenu(menuMgr);
      }
    });

    CoverageTools.getSessionManager().addSessionListener(listener);
    CoverageTools.addJavaCoverageListener(coverageListener);
  }

  protected void createActions() {
    final IKeyBindingService kb = getSite().getKeyBindingService();
    final IActionBars ab = getViewSite().getActionBars();

    openAction = new OpenAction(getSite());
    openAction
        .setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
    ab.setGlobalActionHandler(JdtActionConstants.OPEN, openAction);
    openAction.setEnabled(false);

    copyAction = new CopyAction(tree.getDisplay(), settings, labelprovider,
        viewer);
    ab.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);

    relaunchSessionAction = new RelaunchSessionAction();
    kb.registerAction(relaunchSessionAction);

    removeActiveSessionAction = new RemoveActiveSessionAction();
    ab.setGlobalActionHandler(ActionFactory.DELETE.getId(),
        removeActiveSessionAction);

    removeAllSessionsAction = new RemoveAllSessionsAction();
    kb.registerAction(removeAllSessionsAction);

    mergeSessionsAction = new MergeSessionsAction(getSite()
        .getWorkbenchWindow());
    kb.registerAction(mergeSessionsAction);

    selectSessionAction = new SelectSessionAction();
    kb.registerAction(selectSessionAction);

    importAction = new ImportSessionAction(getSite().getWorkbenchWindow());
    kb.registerAction(importAction);

    exportAction = new ExportSessionAction(getSite().getWorkbenchWindow());
    kb.registerAction(exportAction);

    refreshAction = new RefreshSessionAction();
    ab.setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);

    propertiesAction = new PropertyDialogAction(getSite(), viewer);
    propertiesAction
        .setActionDefinitionId(IWorkbenchActionDefinitionIds.PROPERTIES);
    ab.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
        propertiesAction);
  }

  protected void configureToolbar() {
    IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
    tbm.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    tbm.add(new Separator());
    tbm.add(relaunchSessionAction);
    tbm.add(new Separator());
    tbm.add(removeActiveSessionAction);
    tbm.add(removeAllSessionsAction);
    tbm.add(mergeSessionsAction);
    tbm.add(selectSessionAction);
    tbm.add(new Separator());
    tbm.add(new CollapseAllAction(viewer));
    tbm.add(new LinkWithSelectionAction(settings, selectiontracker));

    IMenuManager mm = getViewSite().getActionBars().getMenuManager();
    mm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_PROJECTS, settings,
        this));
    mm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_PACKAGEROOTS,
        settings, this));
    mm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_PACKAGES, settings,
        this));
    mm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_TYPES, settings,
        this));
    mm.add(new Separator());
    mm.add(new SelectCounterModeAction(0, settings, this));
    mm.add(new SelectCounterModeAction(1, settings, this));
    mm.add(new SelectCounterModeAction(2, settings, this));
    mm.add(new SelectCounterModeAction(3, settings, this));
    mm.add(new SelectCounterModeAction(4, settings, this));
    mm.add(new Separator());
    mm.add(new HideUnusedTypesAction(settings, this));
    mm.add(new Separator());
    mm.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  public void configureContextMenu(IMenuManager mm) {
    mm.add(openAction);
    mm.add(new Separator());
    mm.add(copyAction);
    mm.add(new Separator());
    mm.add(importAction);
    mm.add(exportAction);
    mm.add(new Separator());
    mm.add(refreshAction);
    mm.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
    mm.add(propertiesAction);
  }

  public void setFocus() {
    tree.setFocus();
  }

  public void dispose() {
    CoverageTools.removeJavaCoverageListener(coverageListener);
    CoverageTools.getSessionManager().removeSessionListener(listener);
    selectiontracker.dispose();
    super.dispose();
  }

  protected void updateColumnHeaders() {
    String[] columns = settings.getCounterMode().getColumnHeaders();
    column0.setText(columns[0]);
    column1.setText(columns[1]);
    column2.setText(columns[2]);
    column3.setText(columns[3]);
    column4.setText(columns[4]);
  }

  protected void updateActions() {
    tree.getDisplay().asyncExec(new Runnable() {
      public void run() {
        ICoverageSession active = CoverageTools.getSessionManager()
            .getActiveSession();
        setContentDescription(active == null ? "" : active.getDescription()); //$NON-NLS-1$
        relaunchSessionAction.setEnabled(active != null
            && active.getLaunchConfiguration() != null);
        ICoverageSession[] sessions = CoverageTools.getSessionManager()
            .getSessions();
        boolean atLeastOne = sessions.length >= 1;
        removeActiveSessionAction.setEnabled(atLeastOne);
        removeAllSessionsAction.setEnabled(atLeastOne);
        exportAction.setEnabled(atLeastOne);
        refreshAction.setEnabled(atLeastOne);
        selectSessionAction.setEnabled(atLeastOne);
        boolean atLeastTwo = sessions.length >= 2;
        mergeSessionsAction.setEnabled(atLeastTwo);
      }
    });
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
