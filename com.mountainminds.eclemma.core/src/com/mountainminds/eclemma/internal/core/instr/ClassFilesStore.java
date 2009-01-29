/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: ClassFiles.java 519 2009-01-28 20:09:45Z mtnminds $
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
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class ClassFilesStore {

  private final Map locationIndex = new HashMap();

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
    ClassFiles classfiles = (ClassFiles) locationIndex.get(absolute);
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
    for (int i = 0; i < roots.length; i++) {
      add(roots[i]);
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
    final IJavaProject[] javaProjects = javaModel.getJavaProjects();
    for (int i = 0; i < javaProjects.length; i++) {
      add(javaProjects[i]);
    }
  }

  /**
   * Returns all contained {@link IClassFiles} objects.
   * 
   * @return
   */
  public IClassFiles[] getClassFiles() {
    final List l = new ArrayList(locationIndex.values());
    return (IClassFiles[]) l.toArray(new IClassFiles[l.size()]);
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
    return (IClassFiles) locationIndex.get(location);
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
