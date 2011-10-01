/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
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

  public static String InstrumentingClasses_task;
  public static String InstrumentingClassesIn_task;
  public static String Launching_task;
  public static String AnalyzingCoverageSession_task;
  public static String ExportingSession_task;
  public static String ImportingSession_task;
  public static String ExtractingSourceArchive_task;

  public static String StatusNO_LOCAL_AGENTJAR_ERROR_message;
  public static String StatusID_CREATION_ERROR_message;
  public static String StatusUNKOWN_LAUNCH_TYPE_ERROR_message;
  public static String StatusINVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR_message;
  public static String StatusMISSING_LAUNCH_INFO_ERROR_message;
  public static String StatusEXECFILE_ERROR_message;
  public static String StatusCOVERAGEDATA_FILE_READ_ERROR_message;
  public static String StatusMETADATA_FILE_READ_ERROR_message;
  public static String StatusSOURCE_EXTRACTION_ERROR_message;
  public static String StatusIMPORT_ERROR_message;
  public static String StatusFILE_CONTAINS_NO_METADATA_message;
  public static String StatusNO_COVERAGE_DATA_ERROR_message;
  public static String StatusNO_INSTRUMENTED_CLASSES_message;

  static {
    NLS.initializeMessages(BUNDLE_NAME, CoreMessages.class);
  }

}
