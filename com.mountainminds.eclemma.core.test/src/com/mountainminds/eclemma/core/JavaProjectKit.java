/*******************************************************************************
 * Copyright (c) 2006, 2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Utility class to setup Java projects programatically.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaProjectKit {

  private static final String DEFAULT_PROJECT_NAME = "UnitTestProject";

  public final IWorkspace workspace;

  public final IProject project;

  public final IJavaProject javaProject;

  public JavaProjectKit() throws CoreException {
    this(DEFAULT_PROJECT_NAME);
  }

  public JavaProjectKit(String name) throws CoreException {
    workspace = ResourcesPlugin.getWorkspace();
    IWorkspaceRoot root = workspace.getRoot();
    project = root.getProject(name);
    project.create(null);
    project.open(null);
    IProjectDescription description = project.getDescription();
    description.setNatureIds(new String[] { JavaCore.NATURE_ID });
    project.setDescription(description, null);
    javaProject = JavaCore.create(project);
    javaProject.setRawClasspath(new IClasspathEntry[0], null);
    addClassPathEntry(JavaRuntime.getDefaultJREContainerEntry());
  }

  public void enableJava5() {
    javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
    javaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
  }

  public IFolder setDefaultOutputLocation(String foldername)
      throws CoreException {
    IFolder folder = project.getFolder(foldername);
    folder.create(false, true, null);
    javaProject.setOutputLocation(folder.getFullPath(), null);
    return folder;
  }

  public IPackageFragmentRoot createSourceFolder(String foldername)
      throws CoreException {
    IFolder folder = project.getFolder(foldername);
    folder.create(false, true, null);
    IPackageFragmentRoot packageRoot = javaProject
        .getPackageFragmentRoot(folder);
    addClassPathEntry(JavaCore.newSourceEntry(packageRoot.getPath()));
    return packageRoot;
  }

  public IPackageFragmentRoot createJAR(String jarsrc, String jarpath,
      IPath sourceAttachmentPath, IPath sourceAttachmentRootPath)
      throws CoreException, IOException {
    IFile jarfile = project.getFile(jarpath);
    InputStream source = openTestResource(new Path(jarsrc));
    jarfile.create(source, true, null);
    IPackageFragmentRoot packageRoot = javaProject
        .getPackageFragmentRoot(jarfile);
    addClassPathEntry(JavaCore.newLibraryEntry(packageRoot.getPath(),
        sourceAttachmentPath, sourceAttachmentRootPath));
    return packageRoot;
  }

  public IPackageFragment createPackage(IPackageFragmentRoot fragmentRoot,
      String name) throws CoreException {
    return fragmentRoot.createPackageFragment(name, false, null);
  }

  public ICompilationUnit createCompilationUnit(IPackageFragment fragment,
      String name, String content) throws JavaModelException {
    return fragment.createCompilationUnit(name, content, false, null);
  }

  public ICompilationUnit createCompilationUnit(
      IPackageFragmentRoot fragmentRoot, String testsrc, String path)
      throws CoreException, IOException {
    IPath typepath = new Path(path);
    String pkgname = typepath.removeLastSegments(1).toString()
        .replace('/', '.');
    IPackageFragment fragment = createPackage(fragmentRoot, pkgname);
    StringBuffer sb = new StringBuffer();
    InputStream source = openTestResource(new Path(testsrc).append(typepath));
    Reader r = new InputStreamReader(source);
    int c;
    while ((c = r.read()) != -1)
      sb.append((char) c);
    r.close();
    return createCompilationUnit(fragment, typepath.lastSegment(), sb
        .toString());
  }

  public void addClassPathEntry(IClasspathEntry entry) throws CoreException {
    IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
    System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
    newEntries[oldEntries.length] = entry;
    javaProject.setRawClasspath(newEntries, null);
  }

  public void destroy() throws CoreException {
    project.delete(true, true, null);
  }

  public InputStream openTestResource(IPath path) throws IOException {
    return Platform.find(Platform.getBundle(EclEmmaCorePlugin.ID), path)
        .openStream();
  }

  public void assertNoErrors() throws CoreException {
    final IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true,
        IResource.DEPTH_INFINITE);
    if (markers.length > 0) {
      for (int i = 0; i < markers.length; i++) {
        Integer severity = (Integer) markers[i].getAttribute(IMarker.SEVERITY);
        if (severity != null) {
          Assert.assertTrue(String.valueOf(markers[i]
              .getAttribute(IMarker.MESSAGE)),
              severity.intValue() < IMarker.SEVERITY_ERROR);
        }
      }
    }
  }

  public static void waitForBuild() throws OperationCanceledException,
      InterruptedException {
    Platform.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
  }

}
