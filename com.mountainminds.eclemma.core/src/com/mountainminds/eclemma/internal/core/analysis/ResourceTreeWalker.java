/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jacoco.core.analysis.Analyzer;

/**
 * Internal utility to walk through a resource tree and identify all class
 * files.
 */
class ResourceTreeWalker {

  private final Analyzer analyzer;

  public ResourceTreeWalker(Analyzer analyzer) {
    this.analyzer = analyzer;
  }

  public void walk(IResource resource) throws CoreException, IOException {
    if (resource.getType() == IResource.FILE) {
      final IFile file = (IFile) resource;
      final InputStream in = file.getContents(true);
      try {
        analyzer.analyzeArchive(in);
      } finally {
        in.close();
      }
    } else {
      walkResource(resource, true);
    }
  }

  private void walkResource(IResource resource, boolean root)
      throws CoreException, IOException {
    switch (resource.getType()) {
    case IResource.FILE:
      if (resource.getName().endsWith(".class")) { //$NON-NLS-1$
        final IFile file = (IFile) resource;
        final InputStream in = file.getContents(true);
        try {
          analyzer.analyzeAll(in);
        } finally {
          in.close();
        }
      }
      break;
    case IResource.FOLDER:
    case IResource.PROJECT:
      // Do not traverse into sub-folders like ".svn"
      if (root || isJavaIdentifier(resource.getName())) {
        final IContainer container = (IContainer) resource;
        for (final IResource child : container.members()) {
          walkResource(child, false);
        }
      }
      break;
    }
  }

  public void walk(IPath path) throws IOException {
    final File file = path.toFile();
    if (file.isFile()) {
      final InputStream in = open(file);
      try {
        analyzer.analyzeArchive(in);
      } finally {
        in.close();
      }
    } else {
      walkFile(file, true);
    }
  }

  private void walkFile(File file, boolean root) throws IOException {
    if (file.isFile()) {
      if (file.getName().endsWith(".class")) { //$NON-NLS-1$
        final InputStream in = open(file);
        try {
          analyzer.analyzeAll(in);
        } finally {
          in.close();
        }
      }
    } else {
      // Do not traverse into folders like ".svn"
      if (root || isJavaIdentifier(file.getName())) {
        for (final File child : file.listFiles()) {
          walkFile(child, false);
        }
      }
    }
  }

  private BufferedInputStream open(final File file)
      throws FileNotFoundException {
    return new BufferedInputStream(new FileInputStream(file));
  }

  private boolean isJavaIdentifier(String name) {
    for (int i = 0; i < name.length(); i++) {
      final char c = name.charAt(i);
      if (i == 0) {
        if (!Character.isJavaIdentifierStart(c)) {
          return false;
        }
      } else {
        if (!Character.isJavaIdentifierPart(c)) {
          return false;
        }
      }
    }
    return true;
  }

}
