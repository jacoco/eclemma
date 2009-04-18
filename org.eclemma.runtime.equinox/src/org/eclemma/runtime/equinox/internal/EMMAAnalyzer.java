/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package org.eclemma.runtime.equinox.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclemma.runtime.equinox.ICoverageAnalyzer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.vladium.emma.data.CoverageOptions;
import com.vladium.emma.data.CoverageOptionsFactory;
import com.vladium.emma.data.DataFactory;
import com.vladium.emma.data.ICoverageData;
import com.vladium.emma.data.IMetaData;
import com.vladium.emma.data.SessionData;
import com.vladium.emma.instr.InstrVisitor;
import com.vladium.emma.rt.RT;
import com.vladium.emma.rt.RTSettings;
import com.vladium.jcd.cls.ClassDef;
import com.vladium.jcd.compiler.ClassWriter;
import com.vladium.jcd.parser.ClassDefParser;

/**
 * This EMMA based coverage analyzer dumps a *.es file to file in system
 * property emma.session.out.file or if that is not defined it will create a
 * coverage-'timestamp' folder with coverage.es in.
 * 
 * @author Marc R. Hoffmann, Mikkel T Andersen
 */
public class EMMAAnalyzer implements ICoverageAnalyzer {

	private static final String SESSION_OUT_FILE = "emma.session.out.file";

	private static final String SESSION_OUT_MERGE = "emma.session.out.merge";

	private static final String INSTRUMENT_BUNDLES = "eclemma.instrument.bundles";

	private BundleContext bundleContext;

	private CoverageOptions options;

	private IMetaData metadata;

	private boolean started;

	private List bundlesToInstrument;

	public void start(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		System.out
				.println("Running Equinox with emma code coverage. (Add -Declemma.help for help)");
		printHelpOptions();

		RTSettings.setStandaloneMode(false);
		RT.reset(true, false);

		options = CoverageOptionsFactory.create(System.getProperties());
		metadata = DataFactory.newMetaData(options);

		final String instrumentBundles = System.getProperty(INSTRUMENT_BUNDLES);
		bundlesToInstrument = PropertyUtils.toList(instrumentBundles);
		System.out
				.println("Covering the bundles with symbolic name(s): "
						+ (instrumentBundles != null ? instrumentBundles
								: " no bundles specified (-Declemma.instrument.bundles=org.eclipse.swt), instrumenting all then"));
		started = true;
	}

	public void stop() {
		collectMetaData();
		started = false;

		final ICoverageData coveragedata = RT.getCoverageData();
		try {
			writeSessionData(metadata, coveragedata);
		} catch (IOException e) {
			System.out.println("Error while writing the session file");
			e.printStackTrace();
		}
	}

	public byte[] instrument(final String bundleid, final String classname,
			final byte[] bytes) {
		if (started) {
			try {
				final ClassDef classdef = ClassDefParser.parseClass(bytes);
				final InstrVisitor.InstrResult result = process(classdef, true);
				if (result.m_instrumented) {
					ByteArrayOutputStream out = new ByteArrayOutputStream(
							bytes.length * 2);
					ClassWriter.writeClassTable(classdef, out);
					return out.toByteArray();
				}
			} catch (Exception ex) {
				System.out.println("Error while instrumenting " + classname
						+ " in bundle " + bundleid);
				ex.printStackTrace();
			}
		}
		return null;
	}

	public String getRuntimePackages() {
		return "com.vladium.emma.rt";
	}

	public boolean shouldInstrumentClassesInBundle(String symbolicName) {
		return getBundlesToInstrument().contains(symbolicName)
				|| getBundlesToInstrument().isEmpty();
	}

	/**
	 * Collect Meta data of all classes that have not yet been loaded.
	 */
	private void collectMetaData() {
		final Bundle[] allBundles = bundleContext.getBundles();
		List instrumentBundles = new ArrayList(getBundlesToInstrument());

		for (int i = 0; i < allBundles.length; i++) {
			final Bundle bundle = allBundles[i];
			if (shouldInstrumentClassesInBundle(bundle.getSymbolicName())) {
				instrumentBundles.remove(bundle.getSymbolicName());
				collectMetaData(bundle);
			}
		}
		if (!instrumentBundles.isEmpty()) {
			throw new RuntimeException(
					"Could not instrument all bundles as they were not in the bundle context: "
							+ PropertyUtils.listToString(instrumentBundles));
		}
	}

	/**
	 * Processes all Java *.class files contained in the given bundle and
	 * extracts coverage Meta data information. For accurate statistics this is
	 * required to obtain information about classes that have not been loaded at
	 * all.
	 * 
	 * TODO: Respect bundle class path and also process included JARs.
	 * 
	 * @param bundle
	 *            bundle to collect coverage Meta data for
	 */
	private void collectMetaData(final Bundle bundle) {
		System.out.println("Collecting coverage Meta data for bundle "
				+ bundle.getSymbolicName());
		final Enumeration entries = bundle.findEntries("/", "*.class", true);
		while (entries != null && entries.hasMoreElements()) {
			final URL url = (URL) entries.nextElement();
			try {
				final ClassDef classdef = ClassDefParser.parseClass(url
						.openStream());
				if (!metadata.hasDescriptor(classdef.getName())) {
					process(classdef, false);
				}
			} catch (final IOException e) {
				System.out.println("Error while opening resource " + url
						+ " in bundle " + bundle.getSymbolicName());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Applies EMMA's instrumentation process to the given class definition.
	 * 
	 * @param classdef
	 *            class definition
	 * @param instrument
	 *            flag whether the class should actually become instrumented,
	 *            otherwise only Meta data is collected
	 * @return instrumentation result
	 */
	private InstrVisitor.InstrResult process(final ClassDef classdef,
			final boolean instrument) {
		final InstrVisitor.InstrResult result = new InstrVisitor.InstrResult();
		final InstrVisitor visitor = new InstrVisitor(options);
		visitor.process(classdef, false, instrument, true, result);
		if (result.m_descriptor != null) {
			metadata.add(result.m_descriptor, true);
		}
		return result;
	}

	/**
	 * Writing the combined metadata and coveragedata into a session file.
	 * 
	 * @param metadata
	 *            metadata for the session
	 * @param coveragedata
	 *            coveragedata for the session
	 * @throws IOException
	 *             if it could not persist it will throw an IOException.
	 */
	private void writeSessionData(IMetaData metadata, ICoverageData coveragedata)
			throws IOException {
		String fileName = System.getProperty(SESSION_OUT_FILE,
				getDefaultSessionFileName());
		System.out.println("Saving session data to: " + fileName);

		File file = new File(fileName);
		new File(file.getParent()).mkdirs();
		DataFactory.persist(new SessionData(metadata, coveragedata), file,
				shouldMerge());
	}

	/**
	 * Checks to see if it should merge. Reads the System property
	 * emma.session.out.merge. If it is not set the default value is true, like
	 * in the emma documentation.
	 * 
	 * @see {@link http://emma.sourceforge.net/reference/ch03.html}
	 * 
	 * @return
	 */
	private boolean shouldMerge() {
		return new Boolean(System.getProperty(SESSION_OUT_MERGE, "true"))
				.booleanValue();
	}

	/**
	 * Gets the default session name according to the documentation.
	 * 
	 * @see {@link http://emma.sourceforge.net/reference/ch03.html}
	 * 
	 * @return the default name.
	 */
	private String getDefaultSessionFileName() {
		return System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "coverage.es";
	}

	/**
	 * @return a list of symbolic names for bundles it needs to instrument.
	 */
	private List getBundlesToInstrument() {
		return bundlesToInstrument;
	}

	private void printHelpOptions() {
		if (System.getProperty("eclemma.help") != null) {
			System.out
					.println("---------------------------------------------------------------------------------------------------------------------------------------");
			System.out
					.println("Options: eclemma.instrument.bundles, emma.session.out.file");
			System.out
					.println("    - eclemma.instrument.bundles: list all bundle symbolic names separated with , (comma)");
			System.out
					.println("    - emma.session.out.file: the file to put the output of the session in (c:/myCoverage/coverage.es)");
			System.out
					.println("    - emma.session.out.merge: true if it should merge and false if it should not merge with existing session.out.file (default is true)");
			System.out
					.println("    Example 1: -Declemma.instrument.bundles=org.eclipse.swt -Demma.session.out.merge=false");
			System.out
					.println("    Example 2: -Declemma.instrument.bundles=org.eclipse.swt,org.eclipse.jface -Doutput=c:/swt-jface-coverage.es");
			System.out
					.println("---------------------------------------------------------------------------------------------------------------------------------------");
		}
	}
}
