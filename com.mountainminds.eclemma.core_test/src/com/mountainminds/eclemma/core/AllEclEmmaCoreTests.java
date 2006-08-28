/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import com.mountainminds.eclemma.internal.core.analysis.CounterTest;
import com.mountainminds.eclemma.internal.core.analysis.JavaElementCoverageTest;
import com.mountainminds.eclemma.internal.core.analysis.JavaElementsTraverserTest;
import com.mountainminds.eclemma.internal.core.analysis.LinesTest;
import com.mountainminds.eclemma.internal.core.analysis.MethodResolverTest1;
import com.mountainminds.eclemma.internal.core.analysis.MethodResolverTest2;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite of all EclEmma core tests.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class AllEclEmmaCoreTests {
  
  public static Test suite() {
    TestSuite suite = new TestSuite();
    
    // com.mountainminds.eclemma.internal.core.analysis.*
    suite.addTestSuite(CounterTest.class);
    suite.addTestSuite(JavaElementCoverageTest.class);
    suite.addTestSuite(JavaElementsTraverserTest.class);
    suite.addTestSuite(LinesTest.class);
    suite.addTest(MethodResolverTest1.suite());
    suite.addTest(MethodResolverTest2.suite());
    
    return suite;
  }

}
