/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for the core plug-in.
 */
public class CoreMessages extends NLS {

  private static final String BUNDLE_NAME = "com.mountainminds.eclemma.internal.core.coremessages";//$NON-NLS-1$

  public static String LaunchSessionDescription_value;

  public static String Launching_task;
  public static String AnalyzingCoverageSession_task;
  public static String ExportingSession_task;
  public static String ImportingSession_task;
  public static String MergingCoverageSessions_task;

  public static String StatusNO_LOCAL_AGENTJAR_ERROR_message;
  public static String StatusSESSION_LOAD_ERROR_message;
  public static String StatusUNKOWN_LAUNCH_TYPE_ERROR_message;
  public static String StatusMERGE_SESSIONS_ERROR_message;
  public static String StatusEXEC_FILE_CREATE_ERROR_message;
  public static String StatusEXEC_FILE_READ_ERROR_message;
  public static String StatusAGENT_CONNECT_ERROR_message;
  public static String StatusBUNDLE_ANALYSIS_ERROR_message;
  public static String StatusEXPORT_ERROR_message;
  public static String StatusAGENTSERVER_START_ERROR_message;
  public static String StatusAGENTSERVER_STOP_ERROR_message;
  public static String StatusEXECDATA_DUMP_ERROR_message;
  public static String StatusDUMP_REQUEST_ERROR_message;

  public static String StatusNO_COVERAGE_DATA_ERROR_message;

  public static String ExportFormatHTML_value;
  public static String ExportFormatHTMLZIP_value;
  public static String ExportFormatXML_value;
  public static String ExportFormatCSV_value;
  public static String ExportFormatEXEC_value;

  static {
    NLS.initializeMessages(BUNDLE_NAME, CoreMessages.class);
  }

}
