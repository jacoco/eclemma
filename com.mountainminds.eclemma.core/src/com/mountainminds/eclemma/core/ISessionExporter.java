/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * API for exporting sessions. This interface is not intended to be implemented
 * by clients. Use {@link CoverageTools#getExporter(ICoverageSession)} to get an
 * instance.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public interface ISessionExporter {

  /** Constant for export as HTML files (value is 0). */
  public static final int HTML_FORMAT = 0;

  /** Constant for XML file export (value is 1). */
  public static final int XML_FORMAT = 1;

  /** Constant for plain text export (value is 2). */
  public static final int TXT_FORMAT = 2;

  /** Constant for EMMA session file export (value is 3). */
  public static final int EMMA_FORMAT = 3;

  /**
   * Default file extensions for the different file formats. The array index
   * corresponds to the format constant.
   */
  public static final String[] DEFAULT_EXTENSIONS = new String[] { "html", //$NON-NLS-1$
      "xml", "txt", "es" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  /**
   * Sets the export format.
   * 
   * @param format
   *          export format constant
   */
  public void setFormat(int format);

  /**
   * Sets the export file name. Note that in case of HTML export this is only
   * the main file, while more files are created as siblings.
   * 
   * @param filename
   *          file name of export destination
   */
  public void setDestination(String filename);

  /**
   * Sets EMMA specific report options that overwrite the default setting.
   * 
   * @param options
   *          EMMA specific report options
   */
  public void setReportOptions(Properties options);

  /**
   * A call to this method triggers the actual export process.
   * 
   * @param monitor
   *          progress monitor
   * @throws CoreException
   *           if something goes wrong during export
   */
  public void export(IProgressMonitor monitor) throws CoreException;

}
