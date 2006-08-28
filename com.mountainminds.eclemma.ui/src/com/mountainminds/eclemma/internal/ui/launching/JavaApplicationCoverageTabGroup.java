/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaApplicationCoverageTabGroup extends AbstractCoverageTabGroup {

  public JavaApplicationCoverageTabGroup() throws CoreException {
    super("org.eclipse.jdt.launching.localJavaApplication");
  }

}
