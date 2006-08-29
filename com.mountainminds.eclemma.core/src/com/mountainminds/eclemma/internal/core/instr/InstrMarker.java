/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.instr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Static utilities to mark instrumented output folders. The mark created as a
 * special file in the output directory, which will be deleted during a clean
 * build.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class InstrMarker {

  private static final String MARKERFILE = ".emma_instrumented"; //$NON-NLS-1$

  /**
   * Sets a mark on the given output folder.
   * 
   * @param path
   *          workspace relative path of the output folder
   * @throws CoreException
   *          Thrown when creating the marker file fails
   */
  public static void mark(IPath path) throws CoreException {
    IFolder folder = getFolder(path);
    if (folder != null) {
      IFile marker = folder.getFile(MARKERFILE);
      if (!marker.exists()) {
        marker.create(getMarkerContent(), true, null);
        marker.setDerived(true);
      }
    }
  }

  /**
   * Checks whether the given output folder has an instrumentation mark.
   * 
   * @param path
   *          workspace relative path of the output folder
   * @return <code>true</true> if the mark exists
   */
  public static boolean isMarked(IPath path) {
    IFolder folder = getFolder(path);
    return folder == null ? false : folder.getFile(MARKERFILE).exists();
  }

  private static IFolder getFolder(IPath path) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource res = root.findMember(path);
    return res instanceof IFolder ? (IFolder) res : null;
  }

  private static InputStream getMarkerContent() {
    String text = "Class files instrumented at " + new Date(); //$NON-NLS-1$
    return new ByteArrayInputStream(text.getBytes());
  }

}
