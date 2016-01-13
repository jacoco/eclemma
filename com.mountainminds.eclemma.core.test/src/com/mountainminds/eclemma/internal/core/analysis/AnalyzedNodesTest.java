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
package com.mountainminds.eclemma.internal.core.analysis;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.jacoco.core.internal.analysis.SourceFileCoverageImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link AnalyzedNodes}.
 */
public class AnalyzedNodesTest {

  private List<IClassCoverage> classes;
  private List<ISourceFileCoverage> sourcefiles;

  @Before
  public void setup() {
    classes = new ArrayList<IClassCoverage>();
    sourcefiles = new ArrayList<ISourceFileCoverage>();
  }

  @Test
  public void testGetClassCoverage() {
    final ClassCoverageImpl c = new ClassCoverageImpl("package/MyClass", 0,
        false, null, "java/lang/Object", new String[0]);
    classes.add(c);

    final AnalyzedNodes nodes = new AnalyzedNodes(classes, sourcefiles);

    assertSame(c, nodes.getClassCoverage("package/MyClass"));
  }

  @Test
  public void testGetClassCoverageNegative() {
    final AnalyzedNodes nodes = new AnalyzedNodes(classes, sourcefiles);

    assertNull(nodes.getClassCoverage("somewhere/NotExist"));
  }

  @Test
  public void testGetSourceFileCoverage() {
    final SourceFileCoverageImpl c = new SourceFileCoverageImpl("Example.java",
        "com/example");
    sourcefiles.add(c);

    final AnalyzedNodes nodes = new AnalyzedNodes(classes, sourcefiles);

    assertSame(c, nodes.getSourceFileCoverage("com/example", "Example.java"));
  }

  @Test
  public void testGetSourceFileCoverageNegative() {
    final AnalyzedNodes nodes = new AnalyzedNodes(classes, sourcefiles);

    assertNull(nodes.getSourceFileCoverage("com/example", "NotExist.java"));
  }

}
