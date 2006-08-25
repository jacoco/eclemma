/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;

/**
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JUnitCoverageTabGroup extends AbstractCoverageTabGroup {

  public JUnitCoverageTabGroup() throws CoreException {
    super("org.eclipse.jdt.junit.launchconfig");
  }

}
