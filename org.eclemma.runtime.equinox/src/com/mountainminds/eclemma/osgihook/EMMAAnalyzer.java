/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package com.mountainminds.eclemma.osgihook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

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
import com.vladium.emma.rt.RT;
import com.vladium.emma.rt.RTSettings;
import com.vladium.jcd.cls.ClassDef;
import com.vladium.jcd.compiler.ClassWriter;
import com.vladium.jcd.parser.ClassDefParser;

/**
 * This EMMA based coverage analyzer dumps a *.es file and a HTML report when
 * the OSGI framework exits.
 * 
 * @author Marc R. Hoffmann
 */
public class EMMAAnalyzer implements ICoverageAnalyzer {

	private CoverageOptions options;

	private IMetaData metadata;

	private boolean started;

	public void start() {
		System.out.println("Running Equinox with code coverage.");
		RTSettings.setStandaloneMode(false);
		RT.reset(true, false);
		options = CoverageOptionsFactory.create(System.getProperties());
		metadata = DataFactory.newMetaData(options);
		started = true;
	}

	public void stop() {
		started = false;
		File folder = createOutputFolder();
		System.out.println("Dumping coverage data to " + folder);
		ICoverageData coveragedata = RT.getCoverageData();
		try {
			writeSessionData(metadata, coveragedata, folder);
			writeHTMLReport(metadata, coveragedata, folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] instrument(String bundleid, String classname, byte[] bytes) {
		if (started) {
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
		File folder = new File("coverage-" + System.currentTimeMillis());
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
		generator.process(metadata, coveragedata, null, EMMAProperties
				.wrap(props));
	}

}
