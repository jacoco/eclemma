/*
 * $Id$
 */
package com.mountainminds.eclemma.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Status objects used by the core plugin.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
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
    String m = NLS.bind(message, new Integer(code));
    return new Status(severity, EclEmmaCorePlugin.ID, code, m, null);
  }

  public IStatus getStatus(Throwable t) {
    String m = NLS.bind(message, new Integer(code));
    return new Status(severity, EclEmmaCorePlugin.ID, code, m, t);
  }

  public IStatus getStatus(Object param1, Throwable t) {
    String m = NLS.bind(message, new Integer(code), param1);
    return new Status(severity, EclEmmaCorePlugin.ID, code, m, t);
  }

  /**
   * Info before inplace instrumentation happens.
   */
  public static final EclEmmaStatus INPLACE_INSTRUMENTATION_INFO = new EclEmmaStatus(
      2000, IStatus.INFO,
      CoreMessages.StatusMessage_INPLACE_INSTRUMENTATION_INFO);

  /**
   * Status indicating that it was not possible to obtain a local version of the
   * emma.jar file.
   */
  public static final EclEmmaStatus NO_LOCAL_EMMAJAR_ERROR = new EclEmmaStatus(
      5000, IStatus.ERROR, CoreMessages.StatusMessage_NO_LOCAL_EMMAJAR_ERROR);

  /**
   * Status indication that it was not possible to generate a internal id for a
   * resource.
   */
  public static final EclEmmaStatus ID_CREATION_ERROR = new EclEmmaStatus(5001,
      IStatus.ERROR, CoreMessages.StatusMessage_ID_CREATION_ERROR);

  /**
   * The requested launch type is not known.
   */
  public static final EclEmmaStatus UNKOWN_LAUNCH_TYPE_ERROR = new EclEmmaStatus(
      5002, IStatus.ERROR, CoreMessages.StatusMessage_UNKOWN_LAUNCH_TYPE_ERROR);

  /**
   * The coverage runtime classpath provider has been called in an invalid
   * execution context, i.e. outside of <code>CoverageLauncher.launch()</code>.
   */
  public static final EclEmmaStatus INVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR = new EclEmmaStatus(
      5003, IStatus.ERROR,
      CoreMessages.StatusMessage_INVALID_CLASSPATH_PROVIDER_CONTEXT_ERROR);

  /**
   * The coverage launch info object is missing unexpectedly.
   */
  public static final EclEmmaStatus MISSING_LAUNCH_INFO_ERROR = new EclEmmaStatus(
      5004, IStatus.ERROR, CoreMessages.StatusMessage_MISSING_LAUNCH_INFO_ERROR);

  /**
   * Error while creating the JAR containing emma runtime properties.
   */
  public static final EclEmmaStatus EMMA_PROPERTIES_CREATION_ERROR = new EclEmmaStatus(
      5005, IStatus.ERROR,
      CoreMessages.StatusMessage_EMMA_PROPERTIES_CREATION_ERROR);

  /**
   * Error while reading coverage data file.
   */
  public static final EclEmmaStatus COVERAGEDATA_FILE_READ_ERROR = new EclEmmaStatus(
      5006, IStatus.ERROR,
      CoreMessages.StatusMessage_COVERAGEDATA_FILE_READ_ERROR);

  /**
   * Error while reading meta data file.
   */
  public static final EclEmmaStatus METADATA_FILE_READ_ERROR = new EclEmmaStatus(
      5007, IStatus.ERROR, CoreMessages.StatusMessage_METADATA_FILE_READ_ERROR);

}
