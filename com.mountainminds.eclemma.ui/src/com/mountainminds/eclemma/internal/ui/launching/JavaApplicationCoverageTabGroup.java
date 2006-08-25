/*
 * $Id$
 */
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
