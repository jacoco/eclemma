/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.internal.ui.annotation.EditorTracker;
import com.mountainminds.eclemma.internal.ui.coverageview.CoverageView;

/**
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class EclEmmaUIPlugin extends AbstractUIPlugin {
  
  public static final String ID = "com.mountainminds.eclemma.ui"; //$NON-NLS-1$
  
  /** Identifier for the 'coverage' launch group. */
  public static final String ID_COVERAGE_LAUNCH_GROUP = ID + ".launchGroup.coverage"; //$NON-NLS-1$  

  
  // Icons used by the Plugin
  
  public static final String ELCL_REFRESH       = "icons/full/elcl16/refresh.gif"; //$NON-NLS-1$
  public static final String DLCL_REFRESH       = "icons/full/dlcl16/refresh.gif"; //$NON-NLS-1$
  public static final String ELCL_IMPORT        = "icons/full/elcl16/import.gif"; //$NON-NLS-1$
  public static final String DLCL_IMPORT        = "icons/full/dlcl16/import.gif"; //$NON-NLS-1$
  public static final String ELCL_EXPORT        = "icons/full/elcl16/export.gif"; //$NON-NLS-1$
  public static final String DLCL_EXPORT        = "icons/full/dlcl16/export.gif"; //$NON-NLS-1$
  public static final String ELCL_REMOVE        = "icons/full/elcl16/remove.gif"; //$NON-NLS-1$
  public static final String DLCL_REMOVE        = "icons/full/dlcl16/remove.gif"; //$NON-NLS-1$
  public static final String ELCL_REMOVEALL     = "icons/full/elcl16/removeall.gif"; //$NON-NLS-1$
  public static final String DLCL_REMOVEALL     = "icons/full/dlcl16/removeall.gif"; //$NON-NLS-1$
  public static final String ELCL_MERGESESSIONS = "icons/full/elcl16/mergesessions.gif"; //$NON-NLS-1$
  public static final String DLCL_MERGESESSIONS = "icons/full/dlcl16/mergesessions.gif"; //$NON-NLS-1$
  public static final String ELCL_RELAUNCH      = "icons/full/elcl16/relaunch.gif"; //$NON-NLS-1$
  public static final String DLCL_RELAUNCH      = "icons/full/dlcl16/relaunch.gif"; //$NON-NLS-1$
  public static final String ELCL_SESSION       = "icons/full/elcl16/session.gif"; //$NON-NLS-1$
  public static final String DLCL_SESSION       = "icons/full/dlcl16/session.gif"; //$NON-NLS-1$
  public static final String ELCL_SHOWPROJECTS  = "icons/full/elcl16/showprojects.gif"; //$NON-NLS-1$
  public static final String ELCL_SHOWPACKAGEROOTS = "icons/full/elcl16/showpackageroots.gif"; //$NON-NLS-1$
  public static final String ELCL_SHOWPACKAGES  = "icons/full/elcl16/showpackages.gif"; //$NON-NLS-1$
  public static final String ELCL_SHOWTYPES     = "icons/full/elcl16/showtypes.gif"; //$NON-NLS-1$
  public static final String ELCL_COLLAPSEALL   = "icons/full/elcl16/collapseall.gif"; //$NON-NLS-1$
  public static final String ELCL_LINKED        = "icons/full/elcl16/linked.gif"; //$NON-NLS-1$

  public static final String EVIEW_COVERAGE     = "icons/full/eview16/coverage.gif"; //$NON-NLS-1$

  public static final String OBJ_SESSION        = "icons/full/obj16/session.gif"; //$NON-NLS-1$

  private static final String[] OBJ_COVERAGE = new String[] { 
    "icons/full/obj16/coverage00.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage01.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage02.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage03.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage04.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage05.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage06.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage07.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage08.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage09.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage10.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage11.gif", //$NON-NLS-1$
    "icons/full/obj16/coverage12.gif"  //$NON-NLS-1$
  };
  
  private static final String[] OBJ_COVERAGE_OVERLAY = new String[] { 
    "icons/full/ovr16/coverage00.gif", //$NON-NLS-1$
    "icons/full/ovr16/coverage01.gif", //$NON-NLS-1$
    "icons/full/ovr16/coverage02.gif", //$NON-NLS-1$
    "icons/full/ovr16/coverage03.gif", //$NON-NLS-1$
    "icons/full/ovr16/coverage04.gif", //$NON-NLS-1$
    "icons/full/ovr16/coverage05.gif", //$NON-NLS-1$
    "icons/full/ovr16/coverage06.gif", //$NON-NLS-1$
    "icons/full/ovr16/coverage07.gif"  //$NON-NLS-1$
  };

  public static final String WIZBAN_EXPORT_SESSION  = "icons/full/wizban/export_session.gif"; //$NON-NLS-1$
  public static final String WIZBAN_IMPORT_SESSION  = "icons/full/wizban/import_session.gif"; //$NON-NLS-1$

  
  private static EclEmmaUIPlugin instance;

  private EditorTracker editorTracker;
  
  private ISessionListener sessionListener = new ISessionListener() {
    public void sessionAdded(ICoverageSession addedSession) {
      if (getPreferenceStore().getBoolean(UIPreferences.PREF_SHOW_COVERAGE_VIEW)) {
        getWorkbench().getDisplay().asyncExec(new Runnable() {
          public void run() {
            showCoverageView();
          }
        });
      }
    }
    public void sessionRemoved(ICoverageSession removedSession) {
    }
    public void sessionActivated(ICoverageSession session) {
    }
  };
  
  public void start(BundleContext context) throws Exception {
    super.start(context);
    CoverageTools.setPreferences(UIPreferences.CORE_PREFERENCES);
    CoverageTools.getSessionManager().addSessionListener(sessionListener);
    editorTracker = new EditorTracker(getWorkbench());
    instance = this;
  }

  public void stop(BundleContext context) throws Exception {
    instance = null;
    editorTracker.dispose();
    CoverageTools.getSessionManager().removeSessionListener(sessionListener);
    super.stop(context);
  }
  
  public static EclEmmaUIPlugin getInstance() {
    return instance;
  }
  
  private void showCoverageView() {
    IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
    if (window == null) return;
    IWorkbenchPage page = window.getActivePage();
    if (page != null) {
      try {
        IViewPart view = page.showView(CoverageView.ID, null, IWorkbenchPage.VIEW_CREATE);
        page.bringToTop(view);
      } catch (PartInitException e) {
        log(e);
      }
    }
  }
  
  public Shell getShell() {
    return getWorkbench().getActiveWorkbenchWindow().getShell();
  }
  
  public static IStatus errorStatus(String message, Throwable t) {
    return new Status(IStatus.ERROR, ID, IStatus.ERROR, message, t);
  }

  public static void log(Throwable t) {
    String message = t.getMessage();
    if (message == null) {
      message = "Internal Error"; //$NON-NLS-1$
    }
    instance.getLog().log(errorStatus(message, t));
  }

  public static void log(CoreException t) {
    instance.getLog().log(t.getStatus());
  }
  
  public static ImageDescriptor getImageDescriptor(String key) {
    return loadImage(key).getDescriptor(key);
  }

  public static Image getImage(String key) {
    return loadImage(key).get(key);
  }
  
  public static Image getCoverageImage(double ratio) {
    int idx = (int) Math.round(ratio * OBJ_COVERAGE.length);
    if (idx < 0) idx = 0;
    if (idx >= OBJ_COVERAGE.length) idx = OBJ_COVERAGE.length - 1;
    return getImage(OBJ_COVERAGE[idx]);
  }
  
  public static ImageDescriptor getCoverageOverlay(double ratio) {
	  int idx = (int) Math.round(ratio * OBJ_COVERAGE_OVERLAY.length);
	  if (idx < 0) idx = 0;
	  if (idx >= OBJ_COVERAGE_OVERLAY.length) idx = OBJ_COVERAGE_OVERLAY.length - 1;
	  return getImageDescriptor(OBJ_COVERAGE_OVERLAY[idx]);
  }
  
  private static ImageRegistry loadImage(String path) {
    ImageRegistry reg = getInstance().getImageRegistry();
    if (reg.getDescriptor(path) == null) {
      URL url = instance.getBundle().getEntry(path);
      reg.put(path, ImageDescriptor.createFromURL(url));
    }
    return reg;
  }
   
}
