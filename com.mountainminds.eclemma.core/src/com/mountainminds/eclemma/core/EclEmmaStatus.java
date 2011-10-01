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
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Status objects used by the core plug-in.
 */
public final class EclEmmaStatus {

  public final int code;

  public final int severity;

  public final String message;

  private EclEmmaStatus(int code, int severity, String message) {
    this.code = code;
    this.severity = severity;
    this.message = message;
  }

  public IStatus getStatus() {
    String m = NLS.bind(message, Integer.valueOf(code));
    return new Status(severity, EclEmmaCorePlugin.ID, code, m, null);
  }

  public IStatus getStatus(Throwable t) {
    String m = NLS.bind(message, Integer.valueOf(code));
    return new Status(severity, EclEmmaCorePlugin.ID, code, m, t);
  }

  public IStatus getStatus(Object param1, Throwable t) {
    String m = NLS.bind(message, Integer.valueOf(code), param1);
    return new Status(severity, EclEmmaCorePlugin.ID, code, m, t);
  }

  public IStatus getStatus(Object param1) {
    String m = NLS.bind(message, Integer.valueOf(code), param1);
    return new Status(severity, EclEmmaCorePlugin.ID, code, m, null);
  }

  /**
   * Status indicating that it was not possible to obtain a local version of the
   * runtime agent file.
   */
  public static final EclEmmaStatus NO_LOCAL_AGENTJAR_ERROR = new EclEmmaStatus(
      5000, IStatus.ERROR, CoreMessages.StatusNO_LOCAL_AGENTJAR_ERROR_message);

  /**
   * Status indication that it was not possible to generate a internal id for a
   * resource.
   */
  public static final EclEmmaStatus ID_CREATION_ERROR = new EclEmmaStatus(5001,
      IStatus.ERROR, CoreMessages.StatusID_CREATION_ERROR_message);

  /**
   * The requested launch type is not known.
   */
  public static final EclEmmaStatus UNKOWN_LAUNCH_TYPE_ERROR = new EclEmmaStatus(
      5002, IStatus.ERROR, CoreMessages.StatusUNKOWN_LAUNCH_TYPE_ERROR_message);

  /**
   * The coverage runtime classpath provider has been called in an invalid
   * execution context, i.e. outside of <code>CoverageLauncher.launch()</code>.
   */
  public static final EclEmmaStatus INVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR = new EclEmmaStatus(
      5003, IStatus.ERROR,
      CoreMessages.StatusINVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR_message);

  /**
   * The coverage launch info object is missing unexpectedly.
   */
  public static final EclEmmaStatus MISSING_LAUNCH_INFO_ERROR = new EclEmmaStatus(
      5004, IStatus.ERROR, CoreMessages.StatusMISSING_LAUNCH_INFO_ERROR_message);

  /**
   * The execution data file can not be created.
   */
  public static final EclEmmaStatus EXECFILE_ERROR = new EclEmmaStatus(5004,
      IStatus.ERROR, CoreMessages.StatusEXECFILE_ERROR_message);

  /**
   * Error while reading coverage data file.
   */
  public static final EclEmmaStatus COVERAGEDATA_FILE_READ_ERROR = new EclEmmaStatus(
      5006, IStatus.ERROR,
      CoreMessages.StatusCOVERAGEDATA_FILE_READ_ERROR_message);

  /**
   * Error while reading meta data file.
   */
  public static final EclEmmaStatus METADATA_FILE_READ_ERROR = new EclEmmaStatus(
      5007, IStatus.ERROR, CoreMessages.StatusMETADATA_FILE_READ_ERROR_message);

  /**
   * Error while extracting source files.
   */
  public static final EclEmmaStatus SOURCE_EXTRACTION_ERROR = new EclEmmaStatus(
      5008, IStatus.ERROR, CoreMessages.StatusSOURCE_EXTRACTION_ERROR_message);

  /**
   * Error while importing external coverage session.
   */
  public static final EclEmmaStatus IMPORT_ERROR = new EclEmmaStatus(5009,
      IStatus.ERROR, CoreMessages.StatusIMPORT_ERROR_message);

  /**
   * Error while importing external coverage session.
   */
  public static final EclEmmaStatus FILE_CONTAINS_NO_METADATA = new EclEmmaStatus(
      5010, IStatus.ERROR, CoreMessages.StatusFILE_CONTAINS_NO_METADATA_message);

  /**
   * No coverage data file has been created during a coverage launch. This
   * status is used to issue an error prompt.
   */
  public static final EclEmmaStatus NO_COVERAGE_DATA_ERROR = new EclEmmaStatus(
      5101, IStatus.ERROR, CoreMessages.StatusNO_COVERAGE_DATA_ERROR_message);

  /**
   * No classes are selected for instrumentation. This status is used to issue
   * an error prompt during launching.
   */
  public static final EclEmmaStatus NO_INSTRUMENTED_CLASSES = new EclEmmaStatus(
      5102, IStatus.ERROR, CoreMessages.StatusNO_INSTRUMENTED_CLASSES_message);

}
