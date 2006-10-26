/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
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
  public static String BrowseAction_label;
  public static String SelectAllAction_label;
  public static String DeselectAllAction_label;

  public static String SessionsViewRelaunchAction_label;
  public static String SessionsViewRelaunchAction_tooltip;
  public static String SessionsViewSelectSessionAction_label;
  public static String SessionsViewSelectSessionAction_looltip;
  public static String SessionsViewSelectSessionActionEntry_label;
  public static String SessionsViewCounterModeInstructionsAction_label;
  public static String SessionsViewCounterModeBlocksAction_label;
  public static String SessionsViewCounterModeLinesAction_label;
  public static String SessionsViewShowProjectsAction_label;
  public static String SessionsViewShowProjectsAction_tooltip;
  public static String SessionsViewShowPackageRootsAction_label;
  public static String SessionsViewShowPackageRootsAction_tooltip;
  public static String SessionsViewShowPackagesAction_label;
  public static String SessionsViewShowPackagesAction_tooltip;
  public static String SessionsViewShowTypesAction_label;
  public static String SessionsViewShowTypesAction_tooltip;
  public static String SessionsViewColumnElement_label;
  public static String SessionsViewColumnCoverage_label;
  public static String SessionsViewColumnCoveredInstructions_label;
  public static String SessionsViewColumnCoveredBlocks_label;
  public static String SessionsViewColumnCoveredLines_label;
  public static String SessionsViewColumnTotalInstructions_label;
  public static String SessionsViewColumnTotalBlocks_label;
  public static String SessionsViewColumnTotalLines_label;
  public static String SessionsView_columnCoverageValue;
  public static String SessionsView_loadingMessage;

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
  
  static {
    NLS.initializeMessages(BUNDLE_NAME, UIMessages.class);
  }
  
}
