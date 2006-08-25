/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.mountainminds.eclemma.core.EclEmmaStatus;

/**
 * Internal utility to manage files in the plugin's state locations.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class StateFiles {

  private static final String LAUNCHDATA_FOLDER = ".launch/"; //$NON-NLS-1$
  private static final String INSTRDATA_FOLDER = ".instr/"; //$NON-NLS-1$

  private final IPath stateLocation;
  
  public StateFiles(IPath stateLocation) {
    this.stateLocation = stateLocation;
    this.stateLocation.toFile().mkdirs();
    getLaunchDataFolder().toFile().mkdirs();
  }
  
  public void removeObsoleteFiles() {
    File[] files = getLaunchDataFolder().toFile().listFiles();
    for (int i = 0; i < files.length; i++) {
      files[i].delete();
    }
  }

  public IPath getLaunchDataFolder() {
    return stateLocation.append(LAUNCHDATA_FOLDER);
  }

  public IPath getInstrDataFolder(IPath location) throws CoreException {
    IPath path = stateLocation.append(INSTRDATA_FOLDER).append(getInternalId(location.toString()));
    return path;
  }
  
  private static String getInternalId(String location) throws CoreException {
    StringBuffer sb = new StringBuffer();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
      md.update(location.getBytes("UTF8")); //$NON-NLS-1$
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
  
}
