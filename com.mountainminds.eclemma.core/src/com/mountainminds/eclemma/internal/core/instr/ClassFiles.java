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
package com.mountainminds.eclemma.internal.core.instr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ISourceLocation;

/**
 * Implementation of IClassFiles.
 */
public class ClassFiles implements IClassFiles {

  private final IPackageFragmentRoot[] roots;

  private final IPath location;

  private final boolean binary;

  /**
   * Create a new instance containing a single package fragment root with the
   * given class file location.
   * 
   * @param root
   *          package fragment root
   * @param location
   *          location of the class files
   * @throws JavaModelException
   *           thrown when a problem with the underlying Java model occures
   */
  public ClassFiles(IPackageFragmentRoot root, IPath location)
      throws JavaModelException {
    this(new IPackageFragmentRoot[] { root }, location,
        root.getKind() == IPackageFragmentRoot.K_BINARY);
  }

  private ClassFiles(IPackageFragmentRoot[] roots, IPath location,
      boolean binary) {
    this.roots = roots;
    this.location = location;
    this.binary = binary;
  }

  /**
   * Creates a new ClassFiles instance with the given package fragment root
   * added. Mixing source and binary package fragment roots will result in an
   * exception.
   * 
   * @param root
   *          the package fragment root to add
   * @return new instance
   * @throws JavaModelException
   *           thrown when a problem with the underlying Java model occures
   */
  public ClassFiles addRoot(IPackageFragmentRoot root)
      throws JavaModelException {
    IPackageFragmentRoot[] newroots = new IPackageFragmentRoot[roots.length + 1];
    System.arraycopy(roots, 0, newroots, 0, roots.length);
    newroots[roots.length] = root;
    return new ClassFiles(newroots, location, binary
        && root.getKind() == IPackageFragmentRoot.K_BINARY);
  }

  public String toString() {
    final StringBuffer sb = new StringBuffer(getClass().getName());
    sb.append("[").append(location).append("]"); //$NON-NLS-1$//$NON-NLS-2$
    return sb.toString();
  }

  // IClassFiles implementation

  public boolean isBinary() {
    return binary;
  }

  public IPackageFragmentRoot[] getPackageFragmentRoots() {
    return roots;
  }

  public IPath getLocation() {
    return location;
  }

  public ISourceLocation[] getSourceLocations() throws JavaModelException {
    List<ISourceLocation> l = new ArrayList<ISourceLocation>();
    for (int i = 0; i < roots.length; i++) {
      ISourceLocation location = SourceLocation.findLocation(roots[i]);
      if (location != null) {
        l.add(location);
      }
    }
    ISourceLocation[] array = new ISourceLocation[l.size()];
    return l.toArray(array);
  }

}
