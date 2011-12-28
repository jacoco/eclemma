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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link ExecutionDataFiles}.
 */
public class ExecutionDataFilesTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private ExecutionDataFiles files;

  @Before
  public void setup() {
    final IPath path = Path.fromOSString(folder.getRoot().getAbsolutePath());
    files = new ExecutionDataFiles(path);
  }

  @Test
  public void testNewFile() throws Exception {
    final IPath path = files.newFile();

    final File file = new File(path.toOSString());
    assertTrue(file.exists());
    assertTrue(file.isFile());
    assertEquals(0, file.length());
  }

  @Test(expected = CoreException.class)
  public void testNewFileNegative() throws Exception {
    folder.delete();
    files.newFile();
  }

  @Test
  public void testDeleteTemporaryFiles() throws Exception {
    final File file1 = new File(files.newFile().toOSString());
    final File file2 = new File(files.newFile().toOSString());

    files.deleteTemporaryFiles();

    assertFalse(file1.exists());
    assertFalse(file2.exists());
  }

}
