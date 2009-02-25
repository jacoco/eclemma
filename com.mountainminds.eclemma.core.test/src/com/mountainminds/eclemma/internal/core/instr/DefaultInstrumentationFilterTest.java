/*******************************************************************************
 * Copyright (c) 2006 ,2009 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.instr;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.ICorePreferences;
import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class DefaultInstrumentationFilterTest extends TestCase {

  private JavaProjectKit javaProject1;

  private JavaProjectKit javaProject2;

  private TestPreferences preferences;

  private DefaultInstrumentationFilter filter;

  private ILaunchConfigurationWorkingCopy configuration;

  private IClassFiles classFilesSrc1;

  private IClassFiles classFilesSrc2;

  private IClassFiles classFilesBin1;

  protected void setUp() throws Exception {
    javaProject1 = new JavaProjectKit("project1");
    javaProject2 = new JavaProjectKit("project2");
    preferences = new TestPreferences();
    filter = new DefaultInstrumentationFilter(preferences);

    configuration = DebugPlugin.getDefault().getLaunchManager()
        .getLaunchConfigurationType(
            "org.eclipse.jdt.launching.localJavaApplication").newInstance(
            javaProject1.project, "test.launch");

    final IPackageFragmentRoot rootSrc1 = javaProject1
        .createSourceFolder("src1");
    final IPath location1 = new Path("bin");
    classFilesSrc1 = new ClassFiles(rootSrc1, location1);

    final IPackageFragmentRoot rootSrc2 = javaProject2
        .createSourceFolder("testsrc");
    final IPath location2 = new Path("bin");
    classFilesSrc2 = new ClassFiles(rootSrc2, location2);

    final IPackageFragmentRoot rootBin = javaProject1.createJAR(
        "testdata/bin/signatureresolver.jar", "/sample.jar", new Path(
            "/UnitTestProject/sample.jar"), null);
    final IPath location3 = new Path("/sample.jar");
    classFilesBin1 = new ClassFiles(rootBin, location3);
    JavaProjectKit.waitForBuild();
  }

  protected void tearDown() throws Exception {
    javaProject1.destroy();
    javaProject2.destroy();
  }

  public void testNoFilters() throws CoreException {
    preferences.sourceFoldersOnly = false;
    final IClassFiles[] input = new IClassFiles[] { classFilesSrc1,
        classFilesSrc2, classFilesBin1 };
    final IClassFiles[] output = filter.filter(input, configuration);
    assertEquals(Arrays.asList(input), Arrays.asList(output));
  }

  public void testSourceFoldersOnly() throws CoreException {
    preferences.sourceFoldersOnly = true;
    final IClassFiles[] input = new IClassFiles[] { classFilesSrc1,
        classFilesBin1 };
    final IClassFiles[] output = filter.filter(input, configuration);
    assertEquals(Arrays.asList(new IClassFiles[] { classFilesSrc1 }), Arrays
        .asList(output));
  }

  public void testSameProjectOnly() throws CoreException {
    preferences.sameProjectOnly = true;
    configuration.setAttribute(
        IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "project1");
    final IClassFiles[] input = new IClassFiles[] { classFilesSrc1,
        classFilesSrc2 };
    final IClassFiles[] output = filter.filter(input, configuration);
    assertEquals(Arrays.asList(new IClassFiles[] { classFilesSrc1 }), Arrays
        .asList(output));
  }

  public void testFilter() throws CoreException {
    preferences.filter = "testsrc,abc";
    final IClassFiles[] input = new IClassFiles[] { classFilesSrc1,
        classFilesSrc2 };
    final IClassFiles[] output = filter.filter(input, configuration);
    assertEquals(Arrays.asList(new IClassFiles[] { classFilesSrc2 }), Arrays
        .asList(output));
  }

  private static class TestPreferences implements ICorePreferences {

    String filter;

    boolean sameProjectOnly;

    boolean sourceFoldersOnly;

    public String getDefaultInstrumentationFilter() {
      return filter;
    }

    public boolean getDefaultInstrumentationSameProjectOnly() {
      return sameProjectOnly;
    }

    public boolean getDefaultInstrumentationSourceFoldersOnly() {
      return sourceFoldersOnly;
    }

    public boolean getActivateNewSessions() {
      return false;
    }

    public boolean getAutoRemoveSessions() {
      return false;
    }

  }

}
