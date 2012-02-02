/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.editors;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * Wrapper for a {@link ICoverageSession} instance to serve as an
 * {@link IEditorInput}.
 */
public class CoverageSessionInput extends PlatformObject implements
    IEditorInput {

  private final ICoverageSession session;

  public CoverageSessionInput(ICoverageSession session) {
    this.session = session;
  }

  public ICoverageSession getSession() {
    return session;
  }

  public ImageDescriptor getImageDescriptor() {
    return EclEmmaUIPlugin.getImageDescriptor(EclEmmaUIPlugin.EVIEW_EXEC);
  }

  public String getName() {
    return session.getDescription();
  }

  public String getToolTipText() {
    return session.getDescription();
  }

  @Override
  public int hashCode() {
    return session.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CoverageSessionInput)) {
      return false;
    }
    final CoverageSessionInput other = (CoverageSessionInput) obj;
    return session.equals(other.session);
  }

  public boolean exists() {
    return false;
  }

  public IPersistableElement getPersistable() {
    return null;
  }

}
