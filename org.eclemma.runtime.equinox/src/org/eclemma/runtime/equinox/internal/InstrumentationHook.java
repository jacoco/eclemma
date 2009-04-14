/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package org.eclemma.runtime.equinox.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.eclemma.runtime.equinox.ICoverageAnalyzer;
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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * Implementation of the required hooks delegating to {@link ICoverageAnalyzer}.
 * 
 * @author Marc R. Hoffmann, Mikkel T Andersen
 */
public class InstrumentationHook implements AdaptorHook, ClassLoadingHook {
	private final String FILE_SEPARATOR = "/";
	private static final String BIN_FOLDERS = "binFolders";
	private final ICoverageAnalyzer analyzer;
	private BundleContext bundleContext;

	public InstrumentationHook(ICoverageAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	public void frameworkStart(BundleContext context) throws BundleException {
		this.bundleContext = context;
		analyzer.start();
	}

	public void frameworkStop(BundleContext context) throws BundleException {
		instrumentAllClassesInBundles();
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
		if (shouldInstrumentClassesInBundle(data.getSymbolicName())) {
			BundleLoader loader = (BundleLoader) delegate;
			try {
				loader.addDynamicImportPackage(ManifestElement.parseHeader(
						Constants.DYNAMICIMPORT_PACKAGE, analyzer
								.getRuntimePackages()));
			} catch (BundleException be) {
				throw new RuntimeException(be);
			}
		}
		return null;
	}

	/**
	 * Instruments all classes in bundles to make sure they are all in the
	 * report.
	 */
	private void instrumentAllClassesInBundles() {
		List includedBundles = analyzer.getIncludedBundles();
		while (!includedBundles.isEmpty()) {
			String symbolicName = (String) includedBundles.remove(0);
			Bundle bundle = getBundle(symbolicName);
			if (shouldInstrumentClassesInBundle(symbolicName)) {
				Enumeration entryPaths = bundle.findEntries("/", "*.class",
						true);
				loadClassFiles(bundle, entryPaths);
			}
		}
	}

	private Bundle getBundle(String symbolicName) {
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getSymbolicName().equals(symbolicName)) {
				return bundles[i];
			}
		}
		throw new RuntimeException("Bundle with symbolicname: " + symbolicName
				+ " was not found in the bundle context.");
	}

	private boolean shouldInstrumentClassesInBundle(String symbolicName) {
		return analyzer.getIncludedBundles().contains(symbolicName)
				|| analyzer.getIncludedBundles().isEmpty();
	}

	private void loadClassFiles(Bundle bundle, Enumeration entryPaths) {
		while (entryPaths.hasMoreElements()) {
			URL nextElement = (URL) entryPaths.nextElement();
			String element = nextElement.getPath();
			try {
				// an element looks like :
				// /org/eclemma/runtime/equinox/ICoverageAnalyzer.class
				// and we need to convert that to:
				// org.eclemma.runtime.equinox.ICoverageAnalyzer
				element = element.replaceAll(".class", "").replace(
						FILE_SEPARATOR, ".").substring(1);
				element = removeBinFolders(element);
				bundle.loadClass(element);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String removeBinFolders(String element) {
		List list = PropertyUtils.toList(System.getProperty(BIN_FOLDERS));
		for (int i = 0; i < list.size(); i++) {
			String binFolder = (String) list.get(i);
			element = removeIfStartsWith(element, binFolder);
		}
		element = removeIfStartsWith(element, "bin.");
		element = removeIfStartsWith(element, "output.");
		return element;
	}

	private String removeIfStartsWith(String element, String string) {
		if (element.startsWith(string)) {
			element = element.substring(string.length());
		}
		return element;
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
