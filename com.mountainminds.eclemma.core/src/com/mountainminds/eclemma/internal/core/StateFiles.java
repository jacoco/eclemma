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
package com.mountainminds.eclemma.internal.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.mountainminds.eclemma.core.EclEmmaStatus;

/**
 * Internal utility to manage files in the plugin's state locations.
 */
public class StateFiles {

  private static final String LAUNCHDATA_FOLDER = ".launch/"; //$NON-NLS-1$

  private static final String IMPORTDATA_FOLDER = ".import/"; //$NON-NLS-1$

  private static final String SOURCE_FOLDER = ".src/"; //$NON-NLS-1$

  private static final ReferenceQueue<IPath> CLEANUPQUEUE = new ReferenceQueue<IPath>();

  private static final Set<CleanupFile> CLEANUPFILES = new HashSet<CleanupFile>();

  private final IPath stateLocation;

  public StateFiles(IPath stateLocation) {
    this.stateLocation = stateLocation;
    this.stateLocation.toFile().mkdirs();
    getLaunchDataFolder().toFile().mkdirs();
    getSourceDataFolder().toFile().mkdirs();
    getImportDataFolder().toFile().mkdirs();
  }

  public void deleteTemporaryFiles() {
    deleteFiles(getLaunchDataFolder().toFile(), false);
    deleteFiles(getSourceDataFolder().toFile(), false);
    deleteFiles(getImportDataFolder().toFile(), false);
  }

  private static void deleteFiles(File file, boolean deleteparent) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (int i = 0; files != null && i < files.length; i++) {
        deleteFiles(files[i], true);
      }
    }
    if (deleteparent)
      file.delete();
  }

  public IPath getLaunchDataFolder() {
    return stateLocation.append(LAUNCHDATA_FOLDER);
  }

  private IPath getSourceDataFolder() {
    return stateLocation.append(SOURCE_FOLDER);
  }

  public IPath getSourceFolder(IPath location) throws CoreException {
    return getSourceDataFolder().append(getInternalId(location, true));
  }

  private IPath getImportDataFolder() {
    return stateLocation.append(IMPORTDATA_FOLDER);
  }

  public IPath getImportSessionFile(IPath original) throws CoreException {
    IPath p = getImportDataFolder().append(getInternalId(original, true));
    registerForCleanup(p);
    return p;
  }

  private static String getInternalId(IPath location, boolean withtimestamp)
      throws CoreException {
    long timestamp = 0;
    if (withtimestamp) {
      File f = location.toFile();
      if (f.exists()) {
        timestamp = f.lastModified();
      }
    }
    StringBuffer sb = new StringBuffer();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
      md.update(location.toString().getBytes("UTF8")); //$NON-NLS-1$
      md.update(Long.toHexString(timestamp).getBytes("UTF8")); //$NON-NLS-1$
      byte[] sig = md.digest();
      for (int i = 0; i < sig.length; i++) {
        sb.append(Character.forDigit((sig[i] >> 4) & 0xf, 0x10));
        sb.append(Character.forDigit(sig[i] & 0xf, 0x10));
      }
    } catch (NoSuchAlgorithmException e) {
      throw new CoreException(EclEmmaStatus.ID_CREATION_ERROR.getStatus(e));
    } catch (UnsupportedEncodingException e) {
      throw new CoreException(EclEmmaStatus.ID_CREATION_ERROR.getStatus(e));
    }
    return sb.toString();
  }

  /**
   * Registers the file the given path points to for deletion as soon as the
   * reference to the path objects gets garbage collected. The caller must
   * ensure to hold a reference to the given path object as long as the file is
   * required. The file is not required to (jet) actually exist.
   * 
   * @param file
   *          path object that points to the file
   */
  public void registerForCleanup(IPath file) {
    cleanupObsoleteFiles();
    CLEANUPFILES.add(new CleanupFile(file));
  }

  private void cleanupObsoleteFiles() {
    while (true) {
      CleanupFile f = (CleanupFile) CLEANUPQUEUE.poll();
      if (f == null) {
        break;
      }
      CLEANUPFILES.remove(f);
      f.delete();
    }
  }

  private static class CleanupFile extends PhantomReference<IPath> {

    private final File file;

    public CleanupFile(IPath path) {
      super(path, CLEANUPQUEUE);
      this.file = path.toFile();
    }

    public void delete() {
      deleteFiles(file, true);
      clear();
    }

  }

}
