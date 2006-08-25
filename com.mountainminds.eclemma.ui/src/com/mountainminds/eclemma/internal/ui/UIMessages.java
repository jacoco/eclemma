/*
 * $Id$
 */
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

  public static String SessionsView_removeActiveSessionActionLabel;
  public static String SessionsView_removeActiveSessionActionTooltip;
  public static String SessionsView_removeAllSessionsActionLabel;
  public static String SessionsView_removeAllSessionsActionTooltip;
  public static String SessionsView_relaunchCoverageSessionLabel;
  public static String SessionsView_relaunchCoverageSessionTooltip;
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

  public static String InstrumentationWarning_title;
  public static String InstrumentationWarning_message;
  
  static {
    NLS.initializeMessages(BUNDLE_NAME, UIMessages.class);
  }
  
}
