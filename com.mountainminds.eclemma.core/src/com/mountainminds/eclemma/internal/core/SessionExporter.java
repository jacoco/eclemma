/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import static com.mountainminds.eclemma.core.ISessionExporter.ExportFormat.EXEC;
import static com.mountainminds.eclemma.core.ISessionExporter.ExportFormat.HTML;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.ZipMultiReportOutput;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionExporter;
import com.mountainminds.eclemma.core.analysis.IJavaModelCoverage;
import com.mountainminds.eclemma.internal.core.analysis.SessionAnalyzer;

/**
 * Implementation of ISessionExporter.
 */
public class SessionExporter implements ISessionExporter {

  private final ICoverageSession session;
  private ExportFormat format;
  private String destination;

  public SessionExporter(ICoverageSession session) {
    this.session = session;
  }

  public void setFormat(ExportFormat format) {
    this.format = format;
  }

  public void setDestination(String filename) {
    this.destination = filename;
  }

  public void export(IProgressMonitor monitor) throws CoreException {
    try {
      if (EXEC.equals(format)) {
        createExecFile(monitor);
      } else {
        createReport(monitor);
      }
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.EXPORT_ERROR.getStatus(e));
    }
  }

  private void createExecFile(IProgressMonitor monitor) throws IOException,
      CoreException {
    monitor.beginTask(
        NLS.bind(CoreMessages.ExportingSession_task, session.getDescription()),
        1);
    final OutputStream out = new BufferedOutputStream(new FileOutputStream(
        destination));
    final ExecutionDataWriter writer = new ExecutionDataWriter(out);
    session.readExecutionData(writer, writer, monitor);
    out.close();
    monitor.done();
  }

  private void createReport(IProgressMonitor monitor) throws CoreException,
      IOException {
    final int work = session.getScope().size();
    monitor.beginTask(
        NLS.bind(CoreMessages.ExportingSession_task, session.getDescription()),
        work * 2);
    final SessionAnalyzer analyzer = new SessionAnalyzer();
    final IJavaModelCoverage modelCoverage = analyzer.processSession(session,
        new SubProgressMonitor(monitor, work));
    final IReportVisitor formatter = createFormatter();
    formatter
        .visitInfo(analyzer.getSessionInfos(), analyzer.getExecutionData());
    final IReportGroupVisitor modelgroup = formatter.visitGroup(session
        .getDescription());
    for (IJavaProject project : modelCoverage.getProjects()) {
      final IReportGroupVisitor projectgroup = modelgroup.visitGroup(project
          .getElementName());
      for (IPackageFragmentRoot root : project.getPackageFragmentRoots()) {
        final IBundleCoverage coverage = (IBundleCoverage) modelCoverage
            .getCoverageFor(root);
        if (coverage != null) {
          projectgroup.visitBundle(coverage, createSourceFileLocator(root));
          monitor.worked(1);
        }
      }
    }
    formatter.visitEnd();
    monitor.done();
  }

  private IReportVisitor createFormatter() throws IOException {
    final File file = new File(destination);
    if (HTML.equals(format)) {
      HTMLFormatter htmlFormatter = new HTMLFormatter();
      htmlFormatter.setFooterText(session.getDescription());
      return htmlFormatter.createVisitor(new FileMultiReportOutput(file));
    }
    final OutputStream out = new BufferedOutputStream(
        new FileOutputStream(file));
    switch (format) {
    case HTMLZIP:
      final HTMLFormatter htmlFormatter = new HTMLFormatter();
      htmlFormatter.setFooterText(session.getDescription());
      return htmlFormatter.createVisitor(new ZipMultiReportOutput(out));
    case XML:
      final XMLFormatter xmlFormatter = new XMLFormatter();
      return xmlFormatter.createVisitor(out);
    case CSV:
      final CSVFormatter csvFormatter = new CSVFormatter();
      return csvFormatter.createVisitor(out);
    }
    throw new AssertionError("Unexpected format " + format); //$NON-NLS-1$
  }

  private ISourceFileLocator createSourceFileLocator(IPackageFragmentRoot root)
      throws JavaModelException {
    if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
      return new SourceFolderSourceFileLocator(root);
    } else {
      return new LibrarySourceFileLocator(root);
    }
  }

  private static class SourceFolderSourceFileLocator implements
      ISourceFileLocator {

    private final IPackageFragmentRoot root;

    public SourceFolderSourceFileLocator(IPackageFragmentRoot root) {
      this.root = root;
    }

    public int getTabWidth() {
      // TODO read from editor preferences
      return 4;
    }

    public Reader getSourceFile(String packagename, String sourcename)
        throws IOException {
      final IPackageFragment p = root.getPackageFragment(packagename.replace(
          '/', '.'));
      final IFile file = (IFile) p.getCompilationUnit(sourcename).getResource();
      try {
        return new InputStreamReader(file.getContents(), file.getCharset());
      } catch (CoreException e) {
        final IOException ioException = new IOException(e.getMessage());
        throw (IOException) ioException.initCause(e);
      }
    }
  }

  private static class LibrarySourceFileLocator implements ISourceFileLocator {

    public LibrarySourceFileLocator(IPackageFragmentRoot root) {
    }

    public int getTabWidth() {
      return 4;
    }

    public Reader getSourceFile(String packagename, String sourcename)
        throws IOException {
      // TODO source lookup for libraries
      return null;
    }
  }

}
