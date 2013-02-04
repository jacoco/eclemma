/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.editors;

import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorExecutedClassesPageColumnExecutedProbes_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorExecutedClassesPageColumnId_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorExecutedClassesPageColumnName_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorExecutedClassesPageColumnTotalProbes_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorExecutedClassesPageFilter_message;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorExecutedClassesPageRefreshing_task;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorExecutedClassesPage_title;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;
import org.jacoco.core.data.ExecutionData;

class ExecutedClassesPage extends FormPage {

  private final ExecutionDataContent content;
  private final Job refreshJob;

  private TableViewer dataTableViewer;
  private Text filter;

  public ExecutedClassesPage(FormEditor parent, ExecutionDataContent content) {
    super(parent, "classes", ExecutionDataEditorExecutedClassesPage_title); //$NON-NLS-1$
    this.content = content;
    this.refreshJob = new RefreshJob();
  }

  @Override
  protected void createFormContent(IManagedForm managedForm) {
    final FormToolkit toolkit = managedForm.getToolkit();

    final ScrolledForm form = managedForm.getForm();
    form.setText(ExecutionDataEditorExecutedClassesPage_title);
    toolkit.decorateFormHeading(form.getForm());

    final Composite body = form.getBody();
    body.setLayout(new org.eclipse.swt.layout.GridLayout(1, true));

    filter = toolkit.createText(body, null, SWT.SINGLE | SWT.SEARCH
        | SWT.ICON_CANCEL);
    filter.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        triggerRefresh();
      }
    });
    filter.setMessage(ExecutionDataEditorExecutedClassesPageFilter_message);
    filter.setLayoutData(new GridData(SWT.FILL, 0, true, false));

    final Table dataTable = toolkit.createTable(body, SWT.VIRTUAL
        | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
    dataTable.setHeaderVisible(true);
    dataTable.setLinesVisible(true);
    dataTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    dataTableViewer = new TableViewer(dataTable);

    final TableViewerColumn classIdColumnViewer = new TableViewerColumn(
        dataTableViewer, SWT.NONE);
    classIdColumnViewer.setLabelProvider(new ClassIdLabelProvider());
    final TableColumn classIdColumn = classIdColumnViewer.getColumn();
    classIdColumn.setText(ExecutionDataEditorExecutedClassesPageColumnId_label);
    classIdColumn.setWidth(200);
    classIdColumn.setResizable(true);

    final TableViewerColumn vmNameColumnViewer = new TableViewerColumn(
        dataTableViewer, SWT.NONE);
    vmNameColumnViewer.setLabelProvider(new VMNameLabelProvider());
    final TableColumn vmNameColumn = vmNameColumnViewer.getColumn();
    vmNameColumn
        .setText(ExecutionDataEditorExecutedClassesPageColumnName_label);
    vmNameColumn.setWidth(500);
    vmNameColumn.setResizable(true);

    final TableViewerColumn totalProbesColumnViewer = new TableViewerColumn(
        dataTableViewer, SWT.RIGHT);
    totalProbesColumnViewer.setLabelProvider(new TotalProbesLabelProvider());
    final TableColumn totalProbesColumn = totalProbesColumnViewer.getColumn();
    totalProbesColumn
        .setText(ExecutionDataEditorExecutedClassesPageColumnTotalProbes_label);
    totalProbesColumn.setWidth(100);
    totalProbesColumn.setResizable(true);

    final TableViewerColumn executedProbesColumnViewer = new TableViewerColumn(
        dataTableViewer, SWT.RIGHT);
    executedProbesColumnViewer
        .setLabelProvider(new ExecutedProbesLabelProvider());
    final TableColumn executedProbesColumn = executedProbesColumnViewer
        .getColumn();
    executedProbesColumn
        .setText(ExecutionDataEditorExecutedClassesPageColumnExecutedProbes_label);
    executedProbesColumn.setWidth(100);
    executedProbesColumn.setResizable(true);

    dataTable.setSortColumn(vmNameColumn);
    dataTable.setSortDirection(SWT.UP);
    dataTableViewer.setComparator(new ViewerComparator());
    dataTableViewer
        .setContentProvider(new AbstractExecutionDataContentProvider() {
          public Object[] getElements(ExecutionDataContent content) {
            return content.getExecutionData();
          }
        });
    dataTableViewer.setInput(content);
  }

  private abstract static class AbstractExecutionDataColumnLabelProvider extends
      ColumnLabelProvider {
    public final String getText(Object element) {
      return getText((ExecutionData) element);
    }

    public abstract String getText(ExecutionData element);
  }

  private static class VMNameLabelProvider extends
      AbstractExecutionDataColumnLabelProvider {
    @Override
    public String getText(ExecutionData element) {
      return element.getName();
    }
  }

  private static class ClassIdLabelProvider extends
      AbstractExecutionDataColumnLabelProvider {
    @Override
    public String getText(ExecutionData element) {
      return String.format("0x%016x", Long.valueOf(element.getId())); //$NON-NLS-1$
    }

    @Override
    public Font getFont(Object element) {
      return JFaceResources.getTextFont();
    }
  }

  private static class TotalProbesLabelProvider extends
      AbstractExecutionDataColumnLabelProvider {
    @Override
    public String getText(ExecutionData element) {
      return Integer.toString(element.getProbes().length);
    }
  }

  private static class ExecutedProbesLabelProvider extends
      AbstractExecutionDataColumnLabelProvider {
    @Override
    public String getText(ExecutionData element) {
      int executed = 0;
      boolean[] data = element.getProbes();
      for (int i = 0; i < data.length; i++) {
        if (data[i]) {
          executed++;
        }
      }
      return Integer.toString(executed);
    }
  }

  private void triggerRefresh() {
    refreshJob.cancel();
    refreshJob.schedule(250L);
  }

  private final class RefreshJob extends UIJob {

    public RefreshJob() {
      super(ExecutionDataEditorExecutedClassesPageRefreshing_task);
      setSystem(true);
      setPriority(Job.SHORT);
      setUser(false);
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
      dataTableViewer.setFilters(new ViewerFilter[] { ExecutedClassesFilters
          .fromPatternString(filter.getText().trim()) });
      return Status.OK_STATUS;
    }
  }

}
