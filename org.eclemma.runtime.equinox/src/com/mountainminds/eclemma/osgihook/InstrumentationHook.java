/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package com.mountainminds.eclemma.osgihook;

import java.io.IOException;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.hooks.AdaptorHook;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.eclipse.osgi.framework.internal.core.BundleLoader;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * Implementation of the required hooks delegating to {@link ICoverageAnalyzer}.
 * 
 * @author Marc R. Hoffmann
 */
public class InstrumentationHook implements AdaptorHook, ClassLoadingHook {

	private final ICoverageAnalyzer analyzer;

	public InstrumentationHook(ICoverageAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	public void frameworkStart(BundleContext context) throws BundleException {
		analyzer.start();
	}

	public void frameworkStop(BundleContext context) throws BundleException {
		analyzer.stop();
	}

	public byte[] processClass(String name, byte[] classbytes,
			ClasspathEntry classpathEntry, BundleEntry entry,
			ClasspathManager manager) {
		return analyzer.instrument(manager.getBaseData().getSymbolicName(),
				name, classbytes);
	}

	public BaseClassLoader createClassLoader(ClassLoader parent,
			ClassLoaderDelegate delegate, BundleProtectionDomain domain,
			BaseData data, String[] bundleclasspath) {
		BundleLoader loader = (BundleLoader) delegate;
		try {
			loader.addDynamicImportPackage(ManifestElement.parseHeader(
					Constants.DYNAMICIMPORT_PACKAGE, analyzer
							.getRuntimePackages()));
		} catch (BundleException be) {
			throw new RuntimeException(be);
		}
		return null;
	}

	// Methods stubs for hooks we do not require:

	public boolean addClassPathEntry(ArrayList cpEntries, String cp,
			ClasspathManager hostmanager, BaseData sourcedata,
			ProtectionDomain sourcedomain) {
		return false;
	}

	public String findLibrary(BaseData data, String libName) {
		return null;
	}

	public ClassLoader getBundleClassLoaderParent() {
		return null;
	}

	public void initializedClassLoader(BaseClassLoader baseClassLoader,
			BaseData data) {
	}

	public void addProperties(Properties properties) {
	}

	public FrameworkLog createFrameworkLog() {
		return null;
	}

	public void frameworkStopping(BundleContext context) {
	}

	public void handleRuntimeError(Throwable error) {
	}

	public void initialize(BaseAdaptor adaptor) {
	}

	public URLConnection mapLocationToURLConnection(String location)
			throws IOException {
		return null;
	}

	public boolean matchDNChain(String pattern, String[] dnChain) {
		return false;
	}

}
