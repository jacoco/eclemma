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
package com.mountainminds.eclemma.core.launching;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * Launcher for local Java applications.
 */
public class JavaApplicationLauncher extends CoverageLauncher {

  public Set<IPackageFragmentRoot> getOverallScope(
      ILaunchConfiguration configuration) throws CoreException {
    final Set<IPackageFragmentRoot> scope = new HashSet<IPackageFragmentRoot>();
    final IJavaProject project = JavaRuntime.getJavaProject(configuration);
    for (final IPackageFragmentRoot root : project.getAllPackageFragmentRoots()) {
      final IClasspathEntry cpentry = root.getRawClasspathEntry();
      switch (cpentry.getEntryKind()) {
      case IClasspathEntry.CPE_SOURCE:
      case IClasspathEntry.CPE_LIBRARY:
        scope.add(root);
      }
    }
    return scope;
  }

}
