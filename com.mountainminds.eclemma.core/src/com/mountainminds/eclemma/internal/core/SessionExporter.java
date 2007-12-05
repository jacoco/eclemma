/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.IInstrumentation;
import com.mountainminds.eclemma.core.ISessionExporter;
import com.mountainminds.eclemma.core.ISourceLocation;
import com.vladium.emma.data.DataFactory;
import com.vladium.emma.data.ICoverageData;
import com.vladium.emma.data.IMergeable;
import com.vladium.emma.data.IMetaData;
import com.vladium.emma.data.ISessionData;
import com.vladium.emma.data.SessionData;
import com.vladium.emma.report.IReportProperties;
import com.vladium.emma.report.ReportProcessor;

/**
 * Implementation of ISessionExporter.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class SessionExporter implements ISessionExporter {

  private static final String PROP_OUT_FILE = IReportProperties.PREFIX + IReportProperties.OUT_FILE;
  private static final String PROP_OUT_ENCODING = IReportProperties.PREFIX + IReportProperties.OUT_ENCODING;
  
  private static final String OUTPUT_ENCODING = "UTF-8"; //$NON-NLS-1$
  
  private final ICoverageSession session;
  private int format;
  private String destination;
  private Properties options;

  public SessionExporter(ICoverageSession session) {
    this.session = session;
  }

  public void setFormat(int format) {
    this.format = format;
  }

  public void setDestination(String filename) {
    this.destination = filename;
  }

  public void setReportOptions(Properties options) {
    this.options = options;
  }

  public void export(IProgressMonitor monitor) throws CoreException {
    if (format == EMMA_FORMAT) {
      createSessionFile(monitor);
    } else {
      createReport(monitor);
    }
  }

  private void createReport(IProgressMonitor monitor) throws CoreException {
    IInstrumentation[] instrs = session.getInstrumentations();
    IPath[] coveragefiles = session.getCoverageDataFiles();
    monitor.beginTask(NLS.bind(CoreMessages.ExportingSession_task, session.getDescription()),
       instrs.length + coveragefiles.length + 1);
    List datapath = new ArrayList();
    List sourcepath = new ArrayList();
    for (int i = 0; i < instrs.length; i++) {
      if (instrs[i].getMetaDataFile().toFile().exists()) {
        datapath.add(instrs[i].getMetaDataFile().toOSString());
        if (format == HTML_FORMAT) {
          ISourceLocation[] srcs = instrs[i].getClassFiles().getSourceLocations();
          IProgressMonitor srcmonitor = new SubProgressMonitor(monitor, 1);
          srcmonitor.beginTask("", srcs.length); //$NON-NLS-1$
          for (int j = 0; j < srcs.length; j++) {
            srcs[j].extract(new SubProgressMonitor(srcmonitor, 1));
            sourcepath.add(srcs[j].getPath().toOSString());
          }
          srcmonitor.done();
        } else {
          monitor.worked(1);
        }
      } else {
        monitor.worked(1);
      }
    }
    for (int i = 0; i < coveragefiles.length; i++) {
      datapath.add(coveragefiles[i].toOSString());
      monitor.worked(1);
    }
    ReportProcessor processor = ReportProcessor.create();
    processor.setDataPath((String[]) datapath.toArray(new String[0]));
    processor.setSourcePath((String[]) sourcepath.toArray(new String[0]));
    processor.setReportTypes(new String[] { DEFAULT_EXTENSIONS[format] });
    Properties props = new Properties(options);
    props.setProperty(PROP_OUT_FILE, destination);
    props.setProperty(PROP_OUT_ENCODING, OUTPUT_ENCODING);
    processor.setPropertyOverrides(props);
    processor.run();
    monitor.done();
  }

  private void createSessionFile(IProgressMonitor monitor) throws CoreException {
    IInstrumentation[] instrs = session.getInstrumentations();
    IPath[] coveragefiles = session.getCoverageDataFiles();
    monitor.beginTask(NLS.bind(CoreMessages.ExportingSession_task, session.getDescription()),
      instrs.length + coveragefiles.length + 1);
    IMergeable metadata = null;
    for (int i = 0; i < instrs.length; i++) {
      metadata = loadDataFile(metadata, DataFactory.TYPE_METADATA, instrs[i].getMetaDataFile());
      monitor.worked(1);
    }
    IMergeable coveragedata = null;
    for (int i = 0; i < coveragefiles.length; i++) {
      coveragedata = loadDataFile(coveragedata, DataFactory.TYPE_COVERAGEDATA, coveragefiles[i]);
      monitor.worked(1);
    }
    ISessionData sessiondata = new SessionData((IMetaData) metadata, (ICoverageData) coveragedata);
    try {
      DataFactory.persist(sessiondata, new File(destination), false);
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.COVERAGEDATA_FILE_READ_ERROR
          .getStatus(destination, e));
    }
    monitor.done();
  }

  private IMergeable loadDataFile(IMergeable data, int type, IPath path)
      throws CoreException {
    try {
      File f = path.toFile();
      if (f.exists()) {
        IMergeable newdata = DataFactory.load(f)[type];
        data = data == null ? newdata : data.merge(newdata);
      }
      return data;
    } catch (IOException e) {
      if (type == DataFactory.TYPE_COVERAGEDATA) {
        throw new CoreException(EclEmmaStatus.COVERAGEDATA_FILE_READ_ERROR
          .getStatus(path, e));
      } else {
        throw new CoreException(EclEmmaStatus.METADATA_FILE_READ_ERROR
          .getStatus(path, e));
      }
    }
  }

}
