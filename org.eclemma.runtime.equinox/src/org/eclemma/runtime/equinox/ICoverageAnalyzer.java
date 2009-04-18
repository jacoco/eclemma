/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package org.eclemma.runtime.equinox;

import org.osgi.framework.BundleContext;

/**
 * Abstraction of a code coverage system based on class file instrumentation.
 * 
 * @author Marc R. Hoffmann, Mikkel T Andersen
 */
public interface ICoverageAnalyzer {

	/**
	 * Called when the OSGi framework is started. Can be used for initialization
	 * tasks.
	 * 
	 * @param context
	 *            context of the framework bundle
	 */
	public void start(BundleContext context);

	/**
	 * Called when the OSGi framework shuts down.
	 */
	public void stop();

	/**
	 * For each class definition loaded from a bundle this method is called, if
	 * shouldInstrumentClassesInBundle(bundleid) returns true. The method may
	 * return an instrumented version of the class or null, if the class should
	 * not be modified.
	 * 
	 * @param bundleid
	 *            symbolic name of the bundle
	 * @param classname
	 *            full qualified VM class name
	 * @param bytes
	 *            original class file bytes
	 * @return instrumented bytes or null if the original bytes should be used.
	 */
	public byte[] instrument(String bundleid, String classname, byte[] bytes);

	/**
	 * Check to see if the bundle id should allow instrumentation of classes.
	 * 
	 * @param bundleid
	 *            symbolic name of the bundle
	 * @return if instrumentation is allowed for given bundle id
	 */
	public boolean shouldInstrumentClassesInBundle(String bundleid);

	/**
	 * Class file instrumentation might introduce dependencies on a vendor
	 * specific runtime library. The list of Java packages returned by this
	 * method will be made available to the instrumented plug-ins.
	 * 
	 * @return comma separated list of Java package names
	 */
	public String getRuntimePackages();

}
