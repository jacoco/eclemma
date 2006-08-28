/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.annotation;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import com.mountainminds.eclemma.core.analysis.ILineCoverage;

/**
 * Annotation object that includes its position information to avoid internal
 * mappings.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageAnnotation extends Annotation {
  
  private static final String FULL_COVERAGE = "com.mountainminds.eclemma.ui.fullCoverageAnnotation"; //$NON-NLS-1$
  private static final String PARTIAL_COVERAGE = "com.mountainminds.eclemma.ui.partialCoverageAnnotation"; //$NON-NLS-1$
  private static final String NO_COVERAGE = "com.mountainminds.eclemma.ui.noCoverageAnnotation"; //$NON-NLS-1$

  private final Position position;
  
  public CoverageAnnotation(int offset, int length, int status) {
    super(getAnnotationID(status), false, null);
    position = new Position(offset, length);
  }
  
  public Position getPosition() {
    return position;
  }
  
  private static String getAnnotationID(int status) {
    switch (status) {
      case ILineCoverage.FULLY_COVERED: return FULL_COVERAGE;
      case ILineCoverage.PARTLY_COVERED: return PARTIAL_COVERAGE;
      case ILineCoverage.NOT_COVERED: return NO_COVERAGE;
    }
    throw new RuntimeException("Invalid status: " + status); //$NON-NLS-1$
  }

}
