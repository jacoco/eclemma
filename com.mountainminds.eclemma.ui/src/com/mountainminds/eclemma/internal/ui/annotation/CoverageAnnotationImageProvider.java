/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.annotation;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.jacoco.core.analysis.ICounter;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * The annotation image depends on the branch coverage status.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class CoverageAnnotationImageProvider implements
    IAnnotationImageProvider {

  public String getImageDescriptorId(Annotation annotation) {
    if (annotation instanceof CoverageAnnotation) {
      final ICounter branches = ((CoverageAnnotation) annotation).getLine()
          .getBranchCounter();
      switch (branches.getStatus()) {
      case ICounter.FULLY_COVERED:
        return EclEmmaUIPlugin.OBJ_MARKERFULL;
      case ICounter.PARTLY_COVERED:
        return EclEmmaUIPlugin.OBJ_MARKERPARTIAL;
      case ICounter.NOT_COVERED:
        return EclEmmaUIPlugin.OBJ_MARKERNO;
      }
    }
    return null;
  }

  public ImageDescriptor getImageDescriptor(String imageDescritporId) {
    return EclEmmaUIPlugin.getImageDescriptor(imageDescritporId);
  }

  public Image getManagedImage(Annotation annotation) {
    // we don't manage images ourself
    return null;
  }

}
