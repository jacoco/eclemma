/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;

import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Utility class to setup Java projects programatically.
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

    IVMInstall vmInstall = getExecutionEnvironment("J2SE-1.5").getCompatibleVMs()[0];
    IPath containerPath = new Path(JavaRuntime.JRE_CONTAINER);
    IPath vmPath = containerPath.append(vmInstall.getVMInstallType().getId()).append(vmInstall.getName());
    addClassPathEntry(JavaCore.newContainerEntry(vmPath));
  }

  private IExecutionEnvironment getExecutionEnvironment(String environmentId) {
    IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
    for (IExecutionEnvironment environment : manager.getExecutionEnvironments()) {
      if (environment.getId().equals(environmentId)) {
        return environment;
      }
    }
    return null;
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

  public IPackageFragmentRoot createSourceFolder() throws CoreException {
    IPackageFragmentRoot packageRoot = javaProject
        .getPackageFragmentRoot(javaProject.getResource());
    addClassPathEntry(JavaCore.newSourceEntry(packageRoot.getPath()));
    return packageRoot;
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

  public IPackageFragmentRoot createSourceFolder(String foldername,
      String output) throws CoreException {
    IFolder folder = project.getFolder(foldername);
    folder.create(false, true, null);
    IPackageFragmentRoot packageRoot = javaProject
        .getPackageFragmentRoot(folder);
    IFolder outputFolder = project.getFolder(output);
    outputFolder.create(false, true, null);
    addClassPathEntry(JavaCore.newSourceEntry(packageRoot.getPath(), null,
        outputFolder.getFullPath()));
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

  public IPackageFragmentRoot createExternalJAR(String jarsrc,
      IPath sourceAttachmentPath, IPath sourceAttachmentRootPath)
      throws CoreException, IOException {
    File jarfile = File.createTempFile("test", ".jar");
    InputStream source = openTestResource(new Path(jarsrc));
    OutputStream out = new FileOutputStream(jarfile);
    byte[] buffer = new byte[1024];
    int len;
    while ((len = source.read(buffer)) != -1) {
      out.write(buffer, 0, len);
    }
    out.close();
    IPackageFragmentRoot packageRoot = javaProject
        .getPackageFragmentRoot(jarfile.getAbsolutePath());
    addClassPathEntry(JavaCore.newLibraryEntry(packageRoot.getPath(),
        sourceAttachmentPath, sourceAttachmentRootPath));
    return packageRoot;
  }

  public void addProjectReference(JavaProjectKit otherProject)
      throws CoreException {
    addClassPathEntry(JavaCore.newProjectEntry(otherProject.project
        .getFullPath()));
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
    StringBuilder sb = new StringBuilder();
    InputStream source = openTestResource(new Path(testsrc).append(typepath));
    Reader r = new InputStreamReader(source);
    int c;
    while ((c = r.read()) != -1)
      sb.append((char) c);
    r.close();
    return createCompilationUnit(fragment, typepath.lastSegment(),
        sb.toString());
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
    return FileLocator.openStream(Platform.getBundle(EclEmmaCorePlugin.ID),
        path, false);
  }

  public void assertNoErrors() throws CoreException {
    final IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true,
        IResource.DEPTH_INFINITE);
    if (markers.length > 0) {
      for (int i = 0; i < markers.length; i++) {
        Integer severity = (Integer) markers[i].getAttribute(IMarker.SEVERITY);
        Assert.assertTrue(
            String.valueOf(markers[i].getAttribute(IMarker.MESSAGE)),
            severity.intValue() < IMarker.SEVERITY_ERROR);
      }
    }
  }

  public static void waitForBuild() throws OperationCanceledException,
      InterruptedException {
    Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
  }

}
