/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.coverageview;

import java.text.DecimalFormat;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.core.analysis.ICounter;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Implementation of the coverage view.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageView extends ViewPart {
  
  public static final String ID = "com.mountainminds.eclemma.ui.CoverageView"; //$NON-NLS-1$
  
  /**
   * Placeholder element for displaying "Loading..." in the coverage view. 
   */
  public static final Object LOADING_ELEMENT = new Object();

  private static final DecimalFormat COVERAGE_VALUE = new DecimalFormat(UIMessages.SessionsView_columnCoverageValue);
  
  private ViewSettings settings = new ViewSettings();
  
  protected static final int COLUMN_ELEMENT = 0;
  protected static final int COLUMN_RATIO   = 1;
  protected static final int COLUMN_COVERED = 2;
  protected static final int COLUMN_TOTAL   = 3;
  
  private Tree tree;
  private TreeColumn column0;
  private TreeColumn column1;
  private TreeColumn column2;
  private TreeColumn column3;
  private TreeViewer viewer;
  
  
  // Actions
  private OpenAction openAction;
  private IAction relaunchSessionAction;
  private IAction removeActiveSessionAction;
  private IAction removeAllSessionsAction;
  private IAction mergeSessionsAction;
  private IAction selectSessionAction;
  private IAction refreshAction;
  
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
          ICounter counter = settings.getCounterMode().getCounter(CoverageTools.getCoverageInfo(element));
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
        return columnIndex == COLUMN_ELEMENT ? UIMessages.SessionsView_loadingMessage : ""; //$NON-NLS-1$
      }
      ICounter counter = settings.getCounterMode().getCounter(CoverageTools.getCoverageInfo(element));
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
    settings.save(memento);
    super.saveState(memento);
  }

  public void createPartControl(Composite parent) {
    tree = new Tree(parent, SWT.NONE);
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
    sorter.addColumn(column3, COLUMN_TOTAL);
    updateColumnHeaders();
    
    viewer = new TreeViewer(tree);
    viewer.addFilter(new ViewerFilter() {
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element == LOADING_ELEMENT) {
          return true;
        } else {
          IJavaElementCoverage c = CoverageTools.getCoverageInfo(element);
          return c != null && c.getInstructionCounter().getTotalCount() != 0;
        }
      }
    });
    viewer.setSorter(sorter);
    viewer.setContentProvider(new CoveredElementsContentProvider(settings));
    viewer.setLabelProvider(labelprovider);
    viewer.setInput(CoverageTools.getJavaModelCoverage());
    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        openAction.selectionChanged((IStructuredSelection) event.getSelection());
      }
    });
    getSite().setSelectionProvider(viewer);
    
    createActions();
    updateActions();
    configureToolbar();
    
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      public void doubleClick(DoubleClickEvent event) {
        openAction.run((IStructuredSelection) event.getSelection());
      }
    });
    
    MenuManager menuMgr= new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    viewer.getTree().setMenu(menuMgr.createContextMenu(viewer.getTree()));
    menuMgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager menuMgr) {
        configureContextMenu(menuMgr);
      }
    });
    
    CoverageTools.getSessionManager().addSessionListener(listener);
    CoverageTools.addJavaCoverageListener(coverageListener);
  }
  
  protected void createActions() {
    IKeyBindingService kb = getSite().getKeyBindingService();
    openAction = new OpenAction(getSite());
    openAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
    getViewSite().getActionBars().setGlobalActionHandler(JdtActionConstants.OPEN, openAction);
    openAction.setEnabled(false);
    kb.registerAction(openAction);
    relaunchSessionAction = new RelaunchSessionAction();
    kb.registerAction(relaunchSessionAction);
    removeActiveSessionAction = new RemoveActiveSessionAction();
    kb.registerAction(removeActiveSessionAction);
    removeAllSessionsAction = new RemoveAllSessionsAction();
    kb.registerAction(removeAllSessionsAction);
    mergeSessionsAction = new MergeSessionsAction(getSite().getWorkbenchWindow());
    kb.registerAction(mergeSessionsAction);
    selectSessionAction = new SelectSessionAction();
    kb.registerAction(selectSessionAction);
    refreshAction = new RefreshAction();
    kb.registerAction(refreshAction);
    getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
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
    tbm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_PROJECTS, settings, this));
    tbm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_PACKAGEROOTS, settings, this));
    tbm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_PACKAGES, settings, this));
    tbm.add(new SelectEntryModeAction(ViewSettings.ENTRYMODE_TYPES, settings, this));
    
    IMenuManager mm = getViewSite().getActionBars().getMenuManager();
    mm.add(new SelectCounterModeAction(0, settings, this));
    mm.add(new SelectCounterModeAction(1, settings, this));
    mm.add(new SelectCounterModeAction(2, settings, this));
    mm.add(new Separator());
    mm.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }
  
  public void configureContextMenu(IMenuManager menuMgr) {
    menuMgr.add(openAction);
    menuMgr.add(refreshAction);
  }

  
  public void setFocus() {
    tree.setFocus();
  }

  public void dispose() {
    CoverageTools.removeJavaCoverageListener(coverageListener);
    CoverageTools.getSessionManager().removeSessionListener(listener);
    super.dispose();
  }
  
  protected void updateColumnHeaders() {
    String[] columns = settings.getCounterMode().getColumnHeaders();
    column0.setText(columns[0]);
    column1.setText(columns[1]);
    column2.setText(columns[2]);
    column3.setText(columns[3]);
  }
  
  protected void updateActions() {
    tree.getDisplay().asyncExec(new Runnable() {
      public void run() {
        ICoverageSession active = CoverageTools.getSessionManager().getActiveSession();
        setContentDescription(active == null ? "" : active.getDescription()); //$NON-NLS-1$
        relaunchSessionAction.setEnabled(active != null && active.getLaunchConfiguration() != null);
        ICoverageSession[] sessions = CoverageTools.getSessionManager().getSessions();
        boolean atLeastOne = sessions.length >= 1;
        removeActiveSessionAction.setEnabled(atLeastOne);
        removeAllSessionsAction.setEnabled(atLeastOne);
        refreshAction.setEnabled(atLeastOne);
        boolean atLeastTwo = sessions.length >= 2;
        mergeSessionsAction.setEnabled(atLeastTwo);
        selectSessionAction.setEnabled(atLeastTwo);
      }
    });
  }

  protected void refreshViewer() {
    viewer.refresh();
  }
  
}
