/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
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

import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorSessionsPageColumnDumpTime_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorSessionsPageColumnSessionId_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorSessionsPageColumnStartTime_label;
import static com.mountainminds.eclemma.internal.ui.UIMessages.ExecutionDataEditorSessionsPage_title;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jacoco.core.data.SessionInfo;

/**
 * Page to list session information.
 */
class SessionDataPage extends FormPage {

  private final ExecutionDataContent content;
  private final DateFormat dateTimeFormat;

  public SessionDataPage(FormEditor parent, ExecutionDataContent content) {
    super(parent, "sessions", ExecutionDataEditorSessionsPage_title); //$NON-NLS-1$
    this.content = content;
    this.dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
        DateFormat.MEDIUM);
  }

  @Override
  protected void createFormContent(IManagedForm managedForm) {
    final FormToolkit toolkit = managedForm.getToolkit();

    final ScrolledForm form = managedForm.getForm();
    form.setText(ExecutionDataEditorSessionsPage_title);
    toolkit.decorateFormHeading(form.getForm());

    final Composite body = form.getBody();
    GridLayoutFactory.swtDefaults().applyTo(body);

    final Table sessionTable = toolkit.createTable(body, SWT.FULL_SELECTION
        | SWT.V_SCROLL | SWT.BORDER);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(sessionTable);
    sessionTable.setHeaderVisible(true);
    sessionTable.setLinesVisible(true);

    final TableViewer sessionTableViewer = new TableViewer(sessionTable);

    final TableViewerColumn sessionIdColumnViewer = new TableViewerColumn(
        sessionTableViewer, SWT.NONE);
    sessionIdColumnViewer.setLabelProvider(new SessionIdColumnLabelProvider());
    final TableColumn sessionIdColumn = sessionIdColumnViewer.getColumn();
    sessionIdColumn
        .setText(ExecutionDataEditorSessionsPageColumnSessionId_label);
    sessionIdColumn.setWidth(300);

    final TableViewerColumn startTimeColumnViewer = new TableViewerColumn(
        sessionTableViewer, SWT.NONE);
    startTimeColumnViewer.setLabelProvider(new StartTimeColumnLabelProvider());
    final TableColumn startTimeColumn = startTimeColumnViewer.getColumn();
    startTimeColumn
        .setText(ExecutionDataEditorSessionsPageColumnStartTime_label);
    startTimeColumn.setWidth(200);

    final TableViewerColumn dumpTimeColumnViewer = new TableViewerColumn(
        sessionTableViewer, SWT.NONE);
    dumpTimeColumnViewer.setLabelProvider(new DumpTimeColumnLabelProvider());
    final TableColumn dumpTimeColumn = dumpTimeColumnViewer.getColumn();
    dumpTimeColumn.setText(ExecutionDataEditorSessionsPageColumnDumpTime_label);
    dumpTimeColumn.setWidth(200);

    sessionTable.setSortColumn(startTimeColumn);
    sessionTable.setSortDirection(SWT.UP);

    sessionTableViewer
        .setContentProvider(new AbstractExecutionDataContentProvider() {
          public Object[] getElements(ExecutionDataContent content) {
            return content.getSessionInfos();
          }
        });
    sessionTableViewer.setInput(content);
  }

  private static final class SessionIdColumnLabelProvider extends
      ColumnLabelProvider {

    @Override
    public String getText(Object element) {
      return ((SessionInfo) element).getId();
    }
  }

  private final class StartTimeColumnLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
      return dateTimeFormat.format(new Date(((SessionInfo) element)
          .getStartTimeStamp()));
    }
  }

  private final class DumpTimeColumnLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
      return dateTimeFormat.format(new Date(((SessionInfo) element)
          .getDumpTimeStamp()));
    }
  }

}
