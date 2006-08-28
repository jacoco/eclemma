/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for the core plugin.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoreMessages extends NLS {
  
  private static final String BUNDLE_NAME = "com.mountainminds.eclemma.internal.core.coremessages";//$NON-NLS-1$

  public static String LaunchSessionLabel;
  
  public static String InstrumentingRuntimeClassesTask;
  public static String InstrumentingClassesInTask;
  public static String LaunchingTask;
  public static String AnalyzingCoverageSessionTask;
  
  public static String StatusMessage_INPLACE_INSTRUMENTATION_INFO;
  public static String StatusMessage_NO_LOCAL_EMMAJAR_ERROR;
  public static String StatusMessage_ID_CREATION_ERROR;
  public static String StatusMessage_UNKOWN_LAUNCH_TYPE_ERROR;
  public static String StatusMessage_INVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR;
  public static String StatusMessage_MISSING_LAUNCH_INFO_ERROR;
  public static String StatusMessage_EMMA_PROPERTIES_CREATION_ERROR;
  public static String StatusMessage_COVERAGEDATA_FILE_READ_ERROR;
  public static String StatusMessage_METADATA_FILE_READ_ERROR;
  public static String StatusMessage_ALREADY_INSTRUMENTED_ERROR;

  static {
    NLS.initializeMessages(BUNDLE_NAME, CoreMessages.class);
  }

}
