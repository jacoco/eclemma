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

  public static String ImportSessionAction_label;
  public static String ImportSessionAction_tooltip;
  public static String ExportSessionAction_label;
  public static String ExportSessionAction_tooltip;
  
  public static String SessionsView_removeActiveSessionActionLabel;
  public static String SessionsView_removeActiveSessionActionTooltip;
  public static String SessionsView_removeAllSessionsActionLabel;
  public static String SessionsView_removeAllSessionsActionTooltip;
  public static String SessionsView_relaunchCoverageSessionLabel;
  public static String SessionsView_relaunchCoverageSessionTooltip;
  public static String SessionsView_mergeSessionsLabel;
  public static String SessionsView_mergeSessionsTooltip;
  public static String SessionsView_selectSessionsActionLabel;
  public static String SessionsView_selectSessionsActionTooltip;
  public static String SessionsView_selectSessionsEntryLabel;
  public static String SessionsView_counterModeInstructionsActionLabel;
  public static String SessionsView_counterModeBlocksActionLabel;
  public static String SessionsView_counterModeLinesActionLabel;
  public static String SessionsView_showProjectsActionLabel;
  public static String SessionsView_showProjectsActionTooltip;
  public static String SessionsView_showPackageRootsActionLabel;
  public static String SessionsView_showPackageRootsActionTooltip;
  public static String SessionsView_showPackagesActionLabel;
  public static String SessionsView_showPackagesActionTooltip;
  public static String SessionsView_showTypesActionLabel;
  public static String SessionsView_showTypesActionTooltip;
  public static String SessionsView_refreshAction_label;
  public static String SessionsView_refreshAction_tooltip;
  public static String SessionsView_columnElementLabel;
  public static String SessionsView_columnCoverageLabel;
  public static String SessionsView_columnCoveredInstructionsLabel;
  public static String SessionsView_columnCoveredBlocksLabel;
  public static String SessionsView_columnCoveredLinesLabel;
  public static String SessionsView_columnTotalInstructionsLabel;
  public static String SessionsView_columnTotalBlocksLabel;
  public static String SessionsView_columnTotalLinesLabel;
  public static String SessionsView_columnCoverageValue;
  public static String SessionsView_loadingMessage;

  public static String MergeSessionsDialog_title;
  public static String MergeSessionsDialog_descriptionLabel;
  public static String MergeSessionsDialog_descriptionDefault;
  public static String MergeSessionsDialog_selectionLabel;

  
  public static String CoverageTab_title;
  public static String CoverageTab_groupInstrumentedClassesLabel;
  public static String CoverageTab_buttonInplaceIntrLabel;

  public static String InstrumentationWarning_title;
  public static String InstrumentationWarning_message;
  public static String AlreadyInstrumentedError_title;
  public static String AlreadyInstrumentedError_message;

  public static String Browse_action;
  public static String ExportReport_title;
  public static String ExportReportErrorDialog_title;
  public static String ExportReportErrorDialog_message;
  public static String ExportReportPage1_title;
  public static String ExportReportPage1_description;
  public static String ExportReportPage1NoSession_message;
  public static String ExportReportPage1MissingDestination_message;
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
