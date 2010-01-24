/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for the UI plugin.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class UIMessages extends NLS {

  private static final String BUNDLE_NAME = "com.mountainminds.eclemma.internal.ui.uimessages";//$NON-NLS-1$

  public static String RemoveActiveSessionAction_label;
  public static String RemoveActiveSessionAction_tooltip;
  public static String RemoveAllSessionsAction_label;
  public static String RemoveAllSessionsAction_tooltip;
  public static String RefreshSessionAction_label;
  public static String RefreshSessionAction_tooltip;
  public static String MergeSessionsAction_label;
  public static String MergeSessionsAction_tooltip;
  public static String ImportSessionAction_label;
  public static String ImportSessionAction_tooltip;
  public static String ExportSessionAction_label;
  public static String ExportSessionAction_tooltip;
  public static String CopyAction_label;
  public static String BrowseAction_label;
  public static String SelectAllAction_label;
  public static String DeselectAllAction_label;
  public static String CoverageLastAction_label;

  public static String CoverageViewRelaunchAction_label;
  public static String CoverageViewRelaunchAction_tooltip;
  public static String CoverageViewSelectSessionAction_label;
  public static String CoverageViewSelectSessionAction_looltip;
  public static String CoverageViewSelectSessionActionEntry_label;
  public static String CoverageViewCounterModeInstructionsAction_label;
  public static String CoverageViewCounterModeBlocksAction_label;
  public static String CoverageViewCounterModeLinesAction_label;
  public static String CoverageViewCounterModeMethodsAction_label;
  public static String CoverageViewCounterModeTypesAction_label;
  public static String CoverageViewShowProjectsAction_label;
  public static String CoverageViewShowProjectsAction_tooltip;
  public static String CoverageViewShowPackageRootsAction_label;
  public static String CoverageViewShowPackageRootsAction_tooltip;
  public static String CoverageViewShowPackagesAction_label;
  public static String CoverageViewShowPackagesAction_tooltip;
  public static String CoverageViewShowTypesAction_label;
  public static String CoverageViewShowTypesAction_tooltip;
  public static String CoverageViewHideUnusedTypesAction_label;
  public static String CoverageViewCollapseAllAction_label;
  public static String CoverageViewCollapseAllAction_tooltip;
  public static String CoverageViewLinkWithSelectionAction_label;
  public static String CoverageViewLinkWithSelectionAction_tooltip;
  public static String CoverageViewColumnElement_label;
  public static String CoverageViewColumnCoverage_label;
  public static String CoverageViewColumnCoveredInstructions_label;
  public static String CoverageViewColumnCoveredBlocks_label;
  public static String CoverageViewColumnCoveredLines_label;
  public static String CoverageViewColumnCoveredMethods_label;
  public static String CoverageViewColumnCoveredTypes_label;
  public static String CoverageViewColumnMissedInstructions_label;
  public static String CoverageViewColumnMissedBlocks_label;
  public static String CoverageViewColumnMissedLines_label;
  public static String CoverageViewColumnMissedMethods_label;
  public static String CoverageViewColumnMissedTypes_label;
  public static String CoverageViewColumnTotalInstructions_label;
  public static String CoverageViewColumnTotalBlocks_label;
  public static String CoverageViewColumnTotalLines_label;
  public static String CoverageViewColumnTotalMethods_label;
  public static String CoverageViewColumnTotalTypes_label;
  public static String CoverageView_columnCoverageValue;
  public static String CoverageView_loadingMessage;

  public static String MergeSessionsDialog_title;
  public static String MergeSessionsDialogDescription_label;
  public static String MergeSessionsDialogDescriptionDefault_value;
  public static String MergeSessionsDialogSelection_label;

  public static String CoverageTab_title;
  public static String CoverageTabInstrumentedClassesGroup_label;
  public static String CoverageTabInplaceInstrumentation_label;
  public static String CoverageTabNoClassesSelected_message;

  public static String InstrumentationWarning_title;
  public static String InstrumentationWarning_message;
  public static String AlreadyInstrumentedError_title;
  public static String AlreadyInstrumentedError_message;
  public static String NoCoverageDataError_title;
  public static String NoCoverageDataError_message;
  public static String NoInstrumentedClassesError_title;
  public static String NoInstrumentedClassesError_message;

  public static String ExportReport_title;
  public static String ExportReportErrorDialog_title;
  public static String ExportReportErrorDialog_message;
  public static String ExportReportPage1_title;
  public static String ExportReportPage1_description;
  public static String ExportReportPage1NoSession_message;
  public static String ExportReportPage1MissingDestination_message;
  public static String ExportReportPage1InvalidDestination_message;
  public static String ExportReportPage1WrongExtension_message;
  public static String ExportReportPage1Sessions_label;
  public static String ExportReportPage1DestinationGroup_label;
  public static String ExportReportPage1Format_label;
  public static String ExportReportPage1Destination_label;
  public static String ExportReportPage1BrowseDialog_title;
  public static String ExportReportPage1HTMLFormat_value;
  public static String ExportReportPage1XMLFormat_value;
  public static String ExportReportPage1TextFormat_value;
  public static String ExportReportPage1EMMAFormat_value;
  public static String ExportReportOpenReport_label;

  public static String ImportSession_title;
  public static String ImportSessionPage1_title;
  public static String ImportReportErrorDialog_title;
  public static String ImportReportErrorDialog_message;
  public static String ImportSessionPage1_description;
  public static String ImportReportPage1NoDescription_message;
  public static String ImportReportPage1NoCoverageFile_message;
  public static String ImportReportPage1NoClassFiles_message;
  public static String ImportSessionPage1Description_label;
  public static String ImportSessionPage1Description_value;
  public static String ImportSessionPage1CoverageFile_label;
  public static String ImportSessionPage1BrowseDialog_title;
  public static String ImportSessionPage1Binaries_label;
  public static String ImportSessionPage1ModeGroup_label;
  public static String ImportSessionPage1Reference_label;
  public static String ImportSessionPage1Copy_label;
  public static String ImportSessionPage1MetadataGroup_label;
  public static String ImportSessionPage1IDEClasses_label;
  public static String ImportSessionPage1ImportMetaData_label;

  public static String CoveragePropertyPageSession_label;
  public static String CoveragePropertyPageNoSession_value;
  public static String CoveragePropertyPageColumnCounter_label;
  public static String CoveragePropertyPageColumnCoverage_label;
  public static String CoveragePropertyPageColumnCoverage_value;
  public static String CoveragePropertyPageColumnCovered_label;
  public static String CoveragePropertyPageColumnMissed_label;
  public static String CoveragePropertyPageColumnTotal_label;
  public static String CoveragePropertyPageInstructions_label;
  public static String CoveragePropertyPageBlocks_label;
  public static String CoveragePropertyPageLines_label;
  public static String CoveragePropertyPageMethods_label;
  public static String CoveragePropertyPageTypes_label;

  public static String CoverageDecoratorSuffix_label;

  public static String CoveragePreferences_description;
  public static String CoveragePreferencesShowCoverageView_label;
  public static String CoveragePreferencesActivateNewSessions_label;
  public static String CoveragePreferencesAutoRemoveSessions_label;
  public static String CoveragePreferencesShowInplaceWarning_label;
  public static String CoveragePreferencesDefaultInstrumentation_title;
  public static String CoveragePreferencesSourceFoldersOnly_label;
  public static String CoveragePreferencesSameProjectOnly_label;
  public static String CoveragePreferencesClasspathFilter_label;
  public static String CoveragePreferencesDecoratorsLink_label;
  public static String CoveragePreferencesAnnotationsLink_label;

  public static String ClassesViewerEntry_label;

  static {
    NLS.initializeMessages(BUNDLE_NAME, UIMessages.class);
  }

}
