/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.mountainminds.eclemma.internal.ui.ContextHelp;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.UIPreferences;

/**
 * Implementation of the "Code Coverage" preferences page.
 */
public class CoveragePreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  private static final String DECORATORS_PAGE = "org.eclipse.ui.preferencePages.Decorators"; //$NON-NLS-1$
  private static final String ANNOTATIONS_PAGE = "org.eclipse.ui.editors.preferencePages.Annotations"; //$NON-NLS-1$

  public CoveragePreferencePage() {
    super();
    setDescription(UIMessages.CoveragePreferences_description);
    setPreferenceStore(EclEmmaUIPlugin.getInstance().getPreferenceStore());
  }

  protected Control createContents(final Composite parent) {
    ContextHelp.setHelp(parent, ContextHelp.COVERAGE_PREFERENCES);

    final Composite result = new Composite(parent, SWT.NONE);
    GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(result);

    createSessionManagementGroup(result);
    createDefaultScopeGroup(result);
    createCoverageRuntimeGroup(result);

    // Links:
    createLink(result, UIMessages.CoveragePreferencesDecoratorsLink_label,
        DECORATORS_PAGE);
    createLink(result, UIMessages.CoveragePreferencesAnnotationsLink_label,
        ANNOTATIONS_PAGE);

    initialize();
    checkState();
    return result;
  }

  private void createSessionManagementGroup(final Composite parent) {
    FieldEditor editor;
    final Group group = createGroup(parent,
        UIMessages.CoverageSessionManagement_titel);
    editor = new BooleanFieldEditor(UIPreferences.PREF_SHOW_COVERAGE_VIEW,
        UIMessages.CoveragePreferencesShowCoverageView_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    editor = new BooleanFieldEditor(UIPreferences.PREF_ACTICATE_NEW_SESSIONS,
        UIMessages.CoveragePreferencesActivateNewSessions_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    editor = new BooleanFieldEditor(UIPreferences.PREF_AUTO_REMOVE_SESSIONS,
        UIMessages.CoveragePreferencesAutoRemoveSessions_label, group);
    addField(editor);
    editor = new BooleanFieldEditor(UIPreferences.PREF_RESET_ON_DUMP,
        UIMessages.CoveragePreferencesResetOnDump_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    adjustGroupLayout(group);
  }

  private void createDefaultScopeGroup(final Composite parent) {
    FieldEditor editor;
    final Group group = createGroup(parent,
        UIMessages.CoveragePreferencesDefaultScope_title);
    editor = new BooleanFieldEditor(
        UIPreferences.PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY,
        UIMessages.CoveragePreferencesSourceFoldersOnly_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    editor = new BooleanFieldEditor(
        UIPreferences.PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY,
        UIMessages.CoveragePreferencesSameProjectOnly_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    editor = new StringFieldEditor(UIPreferences.PREF_DEFAULT_SCOPE_FILTER,
        UIMessages.CoveragePreferencesClasspathFilter_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    adjustGroupLayout(group);
  }

  private void createCoverageRuntimeGroup(final Composite parent) {
    FieldEditor editor;
    final Group group = createGroup(parent,
        UIMessages.CoveragePreferencesCoverageRuntime_title);
    editor = new StringFieldEditor(UIPreferences.PREF_AGENT_INCLUDES,
        UIMessages.CoveragePreferencesIncludes_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    editor = new StringFieldEditor(UIPreferences.PREF_AGENT_EXCLUDES,
        UIMessages.CoveragePreferencesExcludes_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    editor = new StringFieldEditor(UIPreferences.PREF_AGENT_EXCLCLASSLOADER,
        UIMessages.CoveragePreferencesExcludeClassloaders_label, group);
    addField(editor);
    editor.fillIntoGrid(group, 2);
    Label hint = new Label(group, SWT.WRAP);
    GridDataFactory.fillDefaults().span(2, 1).applyTo(hint);
    hint.setText(UIMessages.CoveragePreferencesCoverageRuntime_message);
    adjustGroupLayout(group);
  }

  private Group createGroup(final Composite parent, final String text) {
    final Group group = new Group(parent, SWT.NONE);
    group.setText(text);
    group.setFont(parent.getFont());
    GridDataFactory.fillDefaults().applyTo(group);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return group;
  }

  private void adjustGroupLayout(final Group group) {
    // Unlike the top level controls we need margins for a control in a group:
    GridLayout layout = (GridLayout) group.getLayout();
    layout.marginWidth = 5;
    layout.marginHeight = 5;
  }

  private void createLink(final Composite parent, final String text,
      String target) {
    final PreferenceLinkArea link = new PreferenceLinkArea(parent, SWT.NONE,
        target, text, (IWorkbenchPreferenceContainer) getContainer(), null);
    link.getControl().setLayoutData(new GridData());
  }

  public void init(IWorkbench workbench) {
    // nothing to do here
  }

  protected void createFieldEditors() {
    // we override createContents()
  }

}
