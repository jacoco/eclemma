/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.instr;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.core.ISourceLocation;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;

/**
 * Implementation of {@link ISourceLocation}.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class SourceLocation implements ISourceLocation {

  public static ISourceLocation findLocation(IPackageFragmentRoot root)
      throws JavaModelException {
    if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
      IPath path = EclEmmaCorePlugin.getAbsolutePath(root.getPath());
      return new SourceLocation(path, new Path(
          IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH));
    } else {
      IPath path = root.getSourceAttachmentPath();
      if (path != null) {
        path = EclEmmaCorePlugin.getAbsolutePath(path);
        return new SourceLocation(path, root.getSourceAttachmentRootPath());
      } else {
        return null;
      }
    }
  }

  public static ISourceLocation[] findLocations(IPackageFragmentRoot[] roots)
      throws JavaModelException {
    List l = new ArrayList();
    for (int i = 0; i < roots.length; i++) {
      ISourceLocation loc = findLocation(roots[i]);
      if (loc != null) {
        l.add(loc);
      }
    }
    return (ISourceLocation[]) l.toArray(new ISourceLocation[l.size()]);
  }

  private IPath path;
  private IPath rootpath;

  /**
   * Creates a source location for the given path and root path.
   * 
   * @param path
   * @param rootpath
   */
  public SourceLocation(IPath path, IPath rootpath) {
    this.path = path;
    this.rootpath = rootpath;
  }

  public IPath getPath() {
    return path;
  }

  public IPath getRootPath() {
    return rootpath;
  }

  public boolean isArchive() {
    return path.toFile().isFile();
  }

  public void extract(IProgressMonitor monitor) throws CoreException {
    monitor.beginTask("", 1); //$NON-NLS-1$
    if (isArchive()) {
      monitor.beginTask("Extracting source archive " + path, 1); //$NON-NLS-1$
      String prefix = rootpath == null ? "" : rootpath.toString();
      byte[] buffer = new byte[0x1000];
      IPath srcfolder = EclEmmaCorePlugin.getInstance().getStateFiles().getSourceFolder(path);
      try {
        ZipInputStream zip = new ZipInputStream(new FileInputStream(path.toFile()));
        while (true) {
          ZipEntry entry = zip.getNextEntry();
          if (entry == null) break;
          if (!entry.isDirectory() && entry.getName().startsWith(prefix)) {
            IPath path = srcfolder.append(entry.getName().substring(prefix.length()));
            path.removeLastSegments(1).toFile().mkdirs();
            OutputStream out = new FileOutputStream(path.toFile());
            int len;
            while ((len = zip.read(buffer)) != -1) {
              out.write(buffer, 0, len);
            }
            out.close();
          }
          zip.closeEntry();
        }
        zip.close();
        path = srcfolder;
      } catch (IOException e) {
        // TODO core exception
        e.printStackTrace();
      }
    System.out.println("Archive " + getPath());
    System.out.println("RootPath " + getRootPath());
    // TODO find unique temporary location
    // TODO extract files from archive starting with rootpath
    // TODO modify path and rootpath
    // throw new RuntimeException("Not implemented"); //$NON-NLS-1$
    }
    monitor.done();
  }

}
