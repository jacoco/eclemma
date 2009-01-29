/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
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
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class CoveragePreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  private static final String DECORATORS_PAGE = "org.eclipse.ui.preferencePages.Decorators"; //$NON-NLS-1$
  private static final String ANNOTATIONS_PAGE = "org.eclipse.ui.editors.preferencePages.Annotations"; //$NON-NLS-1$

  public CoveragePreferencePage() {
    super(GRID);
    setDescription(UIMessages.CoveragePreferences_description);
    setPreferenceStore(EclEmmaUIPlugin.getInstance().getPreferenceStore());
  }

  protected void createFieldEditors() {
    ContextHelp.setHelp(getControl(), ContextHelp.COVERAGE_PREFERENCES);

    final Composite parent = getFieldEditorParent();
    addField(new BooleanFieldEditor(UIPreferences.PREF_SHOW_COVERAGE_VIEW,
        UIMessages.CoveragePreferencesShowCoverageView_label, parent));
    addField(new BooleanFieldEditor(UIPreferences.PREF_ACTICATE_NEW_SESSIONS,
        UIMessages.CoveragePreferencesActivateNewSessions_label, parent));
    addField(new BooleanFieldEditor(UIPreferences.PREF_AUTO_REMOVE_SESSIONS,
        UIMessages.CoveragePreferencesAutoRemoveSessions_label, parent));
    addField(new ToggleValueFieldEditor(
        UIPreferences.PREF_ALLOW_INPLACE_INSTRUMENTATION,
        UIMessages.CoveragePreferencesShowInplaceWarning_label, parent,
        MessageDialogWithToggle.PROMPT, MessageDialogWithToggle.ALWAYS));

    // Default instrumentation:
    createSpacer(parent);
    createLabel(parent,
        UIMessages.CoveragePreferencesDefaultInstrumentation_title);
    addField(new BooleanFieldEditor(
        UIPreferences.PREF_DEFAULT_INSTRUMENTATION_SOURCE_FOLDERS_ONLY,
        UIMessages.CoveragePreferencesSourceFoldersOnly_label, parent));
    addField(new BooleanFieldEditor(
        UIPreferences.PREF_DEFAULT_INSTRUMENTATION_SAME_PROJECT_ONLY,
        UIMessages.CoveragePreferencesSameProjectOnly_label, parent));
    addField(new StringFieldEditor(
        UIPreferences.PREF_DEFAULT_INSTRUMENTATION_FILTER,
        UIMessages.CoveragePreferencesClasspathFilter_label, parent));

    // Links:
    createSpacer(parent);
    createLink(parent, UIMessages.CoveragePreferencesDecoratorsLink_label,
        DECORATORS_PAGE);
    createLink(parent, UIMessages.CoveragePreferencesAnnotationsLink_label,
        ANNOTATIONS_PAGE);
  }

  private void createSpacer(final Composite parent) {
    createLabel(parent, ""); //$NON-NLS-1$
  }

  private void createLabel(final Composite parent, final String text) {
    final Label label = new Label(parent, SWT.NONE);
    label.setText(text);
    final GridData gd = new GridData();
    gd.horizontalSpan = 2;
    label.setLayoutData(gd);
  }

  private void createLink(final Composite parent, final String text,
      String target) {
    final PreferenceLinkArea link = new PreferenceLinkArea(parent, SWT.NONE,
        target, text, (IWorkbenchPreferenceContainer) getContainer(), null);
    final GridData gd = new GridData();
    gd.horizontalSpan = 2;
    link.getControl().setLayoutData(gd);
  }

  public void init(IWorkbench workbench) {
    // nothing to do here
  }

}
