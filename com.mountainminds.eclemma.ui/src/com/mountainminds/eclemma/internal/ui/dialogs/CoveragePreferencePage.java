/*******************************************************************************
 * Copyright (c) 2006, 2007 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.dialogs;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;
import com.mountainminds.eclemma.internal.ui.UIPreferences;

/**
 * Implementation of the "Code Coverage" preferences page.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoveragePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  
  private static final String DECORATORS_PAGE = "org.eclipse.ui.preferencePages.Decorators"; //$NON-NLS-1$
  private static final String ANNOTATIONS_PAGE = "org.eclipse.ui.editors.preferencePages.Annotations"; //$NON-NLS-1$

  public CoveragePreferencePage() {
     super(GRID);
     setDescription(UIMessages.CoveragePreferences_description);
     setPreferenceStore(EclEmmaUIPlugin.getInstance().getPreferenceStore());
  }
  
  protected Control createContents(Composite parent) {
    parent = (Composite) super.createContents(parent);
    new Label(parent, SWT.NONE);
    new PreferenceLinkArea(parent, SWT.NONE, DECORATORS_PAGE, 
        UIMessages.CoveragePreferencesDecoratorsLink_label,
        (IWorkbenchPreferenceContainer) getContainer(), null);
    new PreferenceLinkArea(parent, SWT.NONE, ANNOTATIONS_PAGE, 
        UIMessages.CoveragePreferencesAnnotationsLink_label,
        (IWorkbenchPreferenceContainer) getContainer(), null);
    return parent;
  }

  protected void createFieldEditors() {
    addField(new BooleanFieldEditor(UIPreferences.PREF_SHOW_COVERAGE_VIEW,
                                    UIMessages.CoveragePreferencesShowCoverageView_label,
                                    getFieldEditorParent()));
    addField(new BooleanFieldEditor(UIPreferences.PREF_ACTICATE_NEW_SESSIONS,
                                    UIMessages.CoveragePreferencesActivateNewSessions_label,
                                    getFieldEditorParent()));
    addField(new BooleanFieldEditor(UIPreferences.PREF_AUTO_REMOVE_SESSIONS,
                                    UIMessages.CoveragePreferencesAutoRemoveSessions_label,
                                    getFieldEditorParent()));
    addField(new ToggleValueFieldEditor(UIPreferences.PREF_ALLOW_INPLACE_INSTRUMENTATION,
                                    UIMessages.CoveragePreferencesShowInplaceWarning_label,
                                    getFieldEditorParent(),
                                    MessageDialogWithToggle.PROMPT, MessageDialogWithToggle.ALWAYS));
  }

  public void init(IWorkbench workbench) {
    // nothing to do here
  }

}
