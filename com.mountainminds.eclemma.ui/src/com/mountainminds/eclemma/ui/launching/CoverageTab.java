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
package com.mountainminds.eclemma.ui.launching;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.mountainminds.eclemma.core.ScopeUtils;
import com.mountainminds.eclemma.core.launching.ICoverageLaunchConfigurationConstants;
import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.ScopeViewer;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * The "Coverage" tab of the launch configuration dialog.
 */
public class CoverageTab extends AbstractLaunchConfigurationTab {

  private ScopeViewer classesviewer;

  public CoverageTab() {
  }

  public void createControl(Composite parent) {
    parent = new Composite(parent, SWT.NONE);
    ContextHelp.setHelp(parent, ContextHelp.COVERAGE_LAUNCH_TAB);
    GridLayout layout = new GridLayout();
    layout.verticalSpacing = 0;
    parent.setLayout(layout);
    setControl(parent);
    createAnalysisScope(parent);
  }

  private void createAnalysisScope(Composite parent) {
    Group group = new Group(parent, SWT.NONE);
    group.setLayoutData(new GridData(GridData.FILL_BOTH));
    group.setText(UIMessages.CoverageTabAnalysisScopeGroup_label);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    group.setLayout(layout);
    classesviewer = new ScopeViewer(group, SWT.BORDER);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 2;
    classesviewer.getTable().setLayoutData(gd);
    classesviewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        setDirty(true);
        updateErrorStatus();
        updateLaunchConfigurationDialog();
      }
    });

    Button buttonSelectAll = createPushButton(group,
        UIMessages.SelectAllAction_label, null);
    buttonSelectAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        classesviewer.selectAll();
        setDirty(true);
        updateLaunchConfigurationDialog();
      }
    });
    buttonSelectAll.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
        | GridData.HORIZONTAL_ALIGN_END));
    Button buttonDeselectAll = createPushButton(group,
        UIMessages.DeselectAllAction_label, null);
    buttonDeselectAll.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        classesviewer.deselectAll();
        setDirty(true);
        updateLaunchConfigurationDialog();
      }
    });
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    // nothing to do
  }

  public void initializeFrom(ILaunchConfiguration configuration) {
    try {
      classesviewer.setIncludeBinaries(true);
      classesviewer.setInput(ScopeUtils.getOverallScope(configuration));
      classesviewer.setSelectedScope(ScopeUtils
          .getConfiguredScope(configuration));
    } catch (CoreException e) {
      EclEmmaUIPlugin.log(e);
    }
    updateErrorStatus();
    setDirty(false);
  }

  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    if (isDirty()) {
      final List<String> ids = ScopeUtils.writeScope(classesviewer
          .getSelectedScope());
      configuration.setAttribute(
          ICoverageLaunchConfigurationConstants.ATTR_SCOPE_IDS, ids);
    }
  }

  public boolean isValid(ILaunchConfiguration launchConfig) {
    return !classesviewer.getSelection().isEmpty();
  }

  public String getName() {
    return UIMessages.CoverageTab_title;
  }

  public Image getImage() {
    return EclEmmaUIPlugin.getImage(EclEmmaUIPlugin.EVIEW_COVERAGE);
  }

  private void updateErrorStatus() {
    if (classesviewer.getSelection().isEmpty()) {
      setErrorMessage(UIMessages.CoverageTabEmptyAnalysisScope_message);
    } else {
      setErrorMessage(null);
    }
  }

}
