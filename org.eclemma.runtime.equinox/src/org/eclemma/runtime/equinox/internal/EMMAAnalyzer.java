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
import java.util.List;
import java.util.Properties;

import org.eclemma.runtime.equinox.ICoverageAnalyzer;

import com.vladium.emma.EMMAProperties;
import com.vladium.emma.data.CoverageOptions;
import com.vladium.emma.data.CoverageOptionsFactory;
import com.vladium.emma.data.DataFactory;
import com.vladium.emma.data.ICoverageData;
import com.vladium.emma.data.IMetaData;
import com.vladium.emma.data.SessionData;
import com.vladium.emma.instr.InstrVisitor;
import com.vladium.emma.report.AbstractReportGenerator;
import com.vladium.emma.report.IReportGenerator;
import com.vladium.emma.report.IReportProperties;
import com.vladium.emma.report.SourcePathCache;
import com.vladium.emma.rt.RT;
import com.vladium.emma.rt.RTSettings;
import com.vladium.jcd.cls.ClassDef;
import com.vladium.jcd.compiler.ClassWriter;
import com.vladium.jcd.parser.ClassDefParser;
import com.vladium.util.IProperties;
import com.vladium.util.Property;

/**
 * This EMMA based coverage analyzer dumps a *.es file and a HTML report when
 * the OSGI framework exits.
 * 
 * @author Marc R. Hoffmann, Mikkel T Andersen
 */
public class EMMAAnalyzer implements ICoverageAnalyzer {
	private final String FILE_SEPARATOR = Property
			.getSystemProperty("file.separator");
	private static final String SRC_PATHS = "srcPaths";

	private static final String SRC_FOLDERS = "srcFolders";

	private static final String OUTPUT = "output";

	private static final String BUNDLE_ROOT_PATHS = "bundleRootPaths";

	private static final String INCLUDED_BUNDLES = "includedBundles";

	private CoverageOptions options;

	private IMetaData metadata;

	private boolean started;

	private List includedBundles;

	public void start() {
		System.out
				.println("Running Equinox with code coverage. (Add -DemmaHelp for help)");
		printOptions();

		RTSettings.setStandaloneMode(false);
		RT.reset(true, false);

		options = CoverageOptionsFactory.create(System.getProperties());
		metadata = DataFactory.newMetaData(options);
		System.out.println("Covering the bundles with symbolic name(s): "
				+ System.getProperty(INCLUDED_BUNDLES));
		started = true;
	}

	public void stop() {
		started = false;
		File folder = createOutputFolder();
		System.out.println("Saving coverage data to " + folder);
		ICoverageData coveragedata = RT.getCoverageData();
		try {
			writeSessionData(metadata, coveragedata, folder);
			writeHTMLReport(metadata, coveragedata, folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] instrument(String bundleid, String classname, byte[] bytes) {
		if (started
				&& (getIncludedBundles().contains(bundleid) || getIncludedBundles()
						.isEmpty())) {
			try {
				ClassDef classdef = ClassDefParser.parseClass(bytes);
				InstrVisitor.InstrResult result = new InstrVisitor.InstrResult();
				new InstrVisitor(options).process(classdef, false, true, true,
						result);
				if (result.m_instrumented) {
					metadata.add(result.m_descriptor, true);
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
		return bytes;
	}

	public String getRuntimePackages() {
		return "com.vladium.emma.rt";
	}

	private File createOutputFolder() {
		String folderName = System.getProperty(OUTPUT);
		File folder = null;
		if (folderName != null) {
			folder = new File(folderName);
		} else {
			folder = new File("coverage-" + System.currentTimeMillis());
		}
		folder.mkdirs();
		return folder;
	}

	private void writeSessionData(IMetaData metadata,
			ICoverageData coveragedata, File folder) throws IOException {
		File f = new File(folder, "coverage.es");
		DataFactory.persist(new SessionData(metadata, coveragedata), f, false);
	}

	private void writeHTMLReport(IMetaData metadata,
			ICoverageData coveragedata, File folder) throws IOException {
		File f = new File(folder, "coverage.html");
		Properties props = new Properties();
		props.setProperty(
				IReportProperties.PREFIX + IReportProperties.OUT_FILE, f
						.getAbsolutePath());
		props.setProperty(IReportProperties.PREFIX
				+ IReportProperties.OUT_ENCODING, "UTF-8");
		IReportGenerator generator = AbstractReportGenerator.create("html");

		List list = getSourcePaths();
		SourcePathCache cache = list.isEmpty() ? null : new SourcePathCache(
				(String[]) list.toArray(new String[list.size()]), true);

		final IProperties appProperties = EMMAProperties.getAppProperties();

		generator.process(metadata, coveragedata, cache, IProperties.Factory
				.combine(EMMAProperties.wrap(props), appProperties));
	}

	private List getSourcePaths() {
		List list = PropertyUtils.toList(System.getProperty(SRC_PATHS));

		List rootList = PropertyUtils.toList(System
				.getProperty(BUNDLE_ROOT_PATHS));
		for (int i = 0; i < rootList.size(); i++) {
			String rootDir = (String) rootList.get(i);
			List bundleNames = getIncludedBundles();
			for (int j = 0; j < bundleNames.size(); j++) {
				String bundleName = (String) bundleNames.get(j);
				list.add(rootDir + FILE_SEPARATOR + bundleName + FILE_SEPARATOR
						+ "src");
				list.add(rootDir + FILE_SEPARATOR + bundleName + FILE_SEPARATOR
						+ "test");

				List srcFolderlist = PropertyUtils.toList(System
						.getProperty(SRC_FOLDERS));
				for (int k = 0; k < srcFolderlist.size(); k++) {
					list.add(rootDir + FILE_SEPARATOR + bundleName
							+ FILE_SEPARATOR + srcFolderlist.get(k));
				}
			}
		}

		return list;
	}

	public List getIncludedBundles() {
		if (includedBundles == null) {
			includedBundles = PropertyUtils.toList(System
					.getProperty(INCLUDED_BUNDLES));
		}
		return includedBundles;
	}

	private void printOptions() {
		if (System.getProperty("emmaHelp") != null) {
			System.out
					.println("Options: includedBundles, (bundleRootPaths and/or srcPaths), output, (srcFolders)");
			System.out
					.println("    - includedBundles: list all bundle symbolic names separated with , (comma)");
			System.out
					.println("    - bundleRootPaths: if you dont want to list all srcPaths just list the root of your code, project name is expected to be the bundle symbolic name/'srcFolders'. Paths are separated with , (comma)");
			System.out
					.println("    - srcPaths: list all bundle src folders (full path) separated with , (comma)");
			System.out
					.println("    - output: the folder to put the output in (coverage.es and coverage.html)");
			System.out
					.println("    - (optional)srcFolders: list the folders to look for under your project, if not 'src' or 'test' separated with , (comma)");
			System.out
					.println("    Example 1: -DincludedBundles=org.eclipse.swt -DsrcPaths=C:/code/org.eclipse.swt/src");
			System.out
					.println("    Example 2: -DincludedBundles=org.eclipse.swt,org.eclipse.jface -DbundleRootPaths=C:/code -Doutput=c:/emmaOutput");
			System.out
					.println("    Example 3: -DincludedBundles=org.eclipse.swt,org.eclipse.jface -DbundleRootPaths=C:/code -DsrcFolders=src2,source");
			System.out
					.println("----------------------------------------------------------------------------------------------------------------");
		}
	}
}
