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
package com.mountainminds.eclemma.internal.ui.annotation;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;

import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Annotation object that includes its position information to avoid internal
 * mappings.
 */
public class CoverageAnnotation extends Annotation {

  private static final String FULL_COVERAGE = "com.mountainminds.eclemma.ui.fullCoverageAnnotation"; //$NON-NLS-1$
  private static final String PARTIAL_COVERAGE = "com.mountainminds.eclemma.ui.partialCoverageAnnotation"; //$NON-NLS-1$
  private static final String NO_COVERAGE = "com.mountainminds.eclemma.ui.noCoverageAnnotation"; //$NON-NLS-1$

  private final Position position;
  private final ILine line;

  public CoverageAnnotation(int offset, int length, ILine line) {
    super(getAnnotationID(line), false, null);
    this.line = line;
    position = new Position(offset, length);
  }

  public Position getPosition() {
    return position;
  }

  public ILine getLine() {
    return line;
  }

  public String getText() {
    final StringBuilder text = new StringBuilder();
    final ICounter branches = line.getBranchCounter();
    switch (branches.getStatus()) {
    case ICounter.NOT_COVERED:
      text.append(NLS.bind(UIMessages.AnnotationTextAllBranchesMissed_message,
          Integer.valueOf(branches.getMissedCount())));
      break;
    case ICounter.FULLY_COVERED:
      text.append(NLS.bind(UIMessages.AnnotationTextAllBranchesCovered_message,
          Integer.valueOf(branches.getTotalCount())));
      break;
    case ICounter.PARTLY_COVERED:
      text.append(NLS.bind(UIMessages.AnnotationTextSomeBranchesMissed_message,
          Integer.valueOf(branches.getMissedCount()),
          Integer.valueOf(branches.getTotalCount())));
      break;
    }

    final ICounter instructions = line.getInstructionCounter();
    text.append("\n").append(instructions.getCoveredCount()).append(" of ")
        .append(instructions.getTotalCount()).append(" covered");

    return text.toString();
  }

  private static String getAnnotationID(ILine line) {
    switch (line.getStatus()) {
    case ICounter.FULLY_COVERED:
      return FULL_COVERAGE;
    case ICounter.PARTLY_COVERED:
      return PARTIAL_COVERAGE;
    case ICounter.NOT_COVERED:
      return NO_COVERAGE;
    }
    throw new AssertionError(line.getStatus());
  }

}
