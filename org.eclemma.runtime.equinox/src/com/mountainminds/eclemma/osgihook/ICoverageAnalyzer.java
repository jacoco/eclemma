/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package com.mountainminds.eclemma.osgihook;

/**
 * Abstraction of a code coverage system based on class file instrumentation.
 * 
 * @author Marc R. Hoffmann
 */
public interface ICoverageAnalyzer {

	/**
	 * Called when the OSGi framework is started. Can be used for initialization
	 * tasks.
	 */
	public void start();

	/**
	 * Called when the OSGi framework shuts down. Here we can e.g. write a
	 * coverage report.
	 */
	public void stop();

	/**
	 * For each class definition loaded from a bundle this method is called. The
	 * method may return a instrumented version of the class or null, if the
	 * class should not be modified.
	 * 
	 * @param bundleid
	 *            symbolic name of the bundle
	 * @param classname
	 *            full qualified VM class name
	 * @param bytes
	 *            original class file bytes
	 * @return instrumented class file bytes or null
	 */
	public byte[] instrument(String bundleid, String classname, byte[] bytes);

	/**
	 * Class file instrumentation might introduce dependencies on a vendor
	 * specific runtime library. The list of Java packages returned by this
	 * method will be made available to the instrumented plug-ins.
	 * 
	 * @return comma separated list of Java package names
	 */
	public String getRuntimePackages();

}
