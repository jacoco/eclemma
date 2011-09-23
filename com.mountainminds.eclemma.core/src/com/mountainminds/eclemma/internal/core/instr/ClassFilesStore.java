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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Support for creation and access for {@link IClassFiles} objects.
 */
public class ClassFilesStore {

  private final Map<String, ClassFiles> locationIndex = new HashMap<String, ClassFiles>();

  /**
   * Adds the given package fragment root.
   * 
   * @param root
   *          package fragment root to add
   * @throws JavaModelException
   *           might be thrown by the underlying Java model
   */
  public void add(IPackageFragmentRoot root) throws JavaModelException {
    final IPath location = getClassFileLocation(root);
    final String absolute = EclEmmaCorePlugin.getAbsolutePath(location)
        .toOSString();
    ClassFiles classfiles = locationIndex.get(absolute);
    if (classfiles == null) {
      classfiles = new ClassFiles(root, location);
    } else {
      classfiles = classfiles.addRoot(root);
    }
    locationIndex.put(absolute, classfiles);
  }

  /**
   * Adds all class files of the given Java project.
   * 
   * @param javaProject
   *          Java project to add
   * @throws JavaModelException
   *           might be thrown by the underlying Java model
   */
  public void add(IJavaProject javaProject) throws JavaModelException {
    final IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
    for (final IPackageFragmentRoot root : roots) {
      add(root);
    }
  }

  /**
   * Adds all class files of all projects of the Java model.
   * 
   * @param javaModel
   *          Java model to add
   * @throws JavaModelException
   *           might be thrown by the underlying Java model
   */
  public void add(IJavaModel javaModel) throws JavaModelException {
    for (final IJavaProject project : javaModel.getJavaProjects()) {
      add(project);
    }
  }

  /**
   * Returns all contained {@link IClassFiles} objects.
   * 
   * @return
   */
  public IClassFiles[] getClassFiles() {
    final List<IClassFiles> l = new ArrayList<IClassFiles>(
        locationIndex.values());
    return l.toArray(new IClassFiles[l.size()]);
  }

  /**
   * Returns the {@link IClassFiles} object with the given absolute location on
   * the local file system.
   * 
   * @param location
   *          absolute location
   * @return {@link IClassFiles} object or <code>null</code>
   * 
   */
  public IClassFiles getAtAbsoluteLocation(String location) {
    return locationIndex.get(location);
  }

  private static IPath getClassFileLocation(IPackageFragmentRoot root)
      throws JavaModelException {
    IPath path;
    if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
      IClasspathEntry entry = root.getRawClasspathEntry();
      path = entry.getOutputLocation();
      if (path == null) {
        path = root.getJavaProject().getOutputLocation();
      }
    } else {
      path = root.getPath();
    }
    return path;
  }

}
