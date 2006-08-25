/*
 * $Id$
 */
package com.mountainminds.eclemma.internal.core.instr;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;

import com.mountainminds.eclemma.core.IClassFiles;
import com.mountainminds.eclemma.core.IInstrumentation;
import com.mountainminds.eclemma.core.ISourceLocation;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;
import com.vladium.emma.AppLoggers;
import com.vladium.emma.instr.InstrProcessor;
import com.vladium.emma.instr.InstrProcessor.OutMode;
import com.vladium.util.XProperties;

/**
 * 
 * @author Marc R. Hoffmann
 * @version $Revision$
 */
public class ClassFiles implements IClassFiles {

  private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

  private static final String METADATAFILE_EXT = "em"; //$NON-NLS-1$

  private final IPackageFragmentRoot[] roots;
  private final IPath location;

  public ClassFiles(IPackageFragmentRoot[] roots, IPath location) {
    this.roots = roots;
    this.location = location;
  }

  public ClassFiles addRoot(IPackageFragmentRoot root) {
    IPackageFragmentRoot[] newroots = new IPackageFragmentRoot[roots.length + 1];
    System.arraycopy(roots, 0, newroots, 0, roots.length);
    newroots[roots.length] = root;
    return new ClassFiles(newroots, location);
  }

  public boolean isBinary() throws JavaModelException {
    for (int i = 0; i < roots.length; i++) {
      if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
        return false;
      }
    }
    return true;
  }

  public IPackageFragmentRoot[] getPackageFragmentRoots() {
    return roots;
  }

  public IPath getLocation() {
    return location;
  }

  public ISourceLocation[] getSourceLocations() throws JavaModelException {
    List l = new ArrayList();
    for (int i = 0; i < roots.length; i++) {
      ISourceLocation location = SourceLocation.findLocation(roots[i]);
      if (location != null) {
        l.add(location);
      }
    }
    ISourceLocation[] array = new ISourceLocation[l.size()];
    return (ISourceLocation[]) l.toArray(array);
  }

  public IInstrumentation instrument(boolean inplace, IProgressMonitor monitor)
      throws CoreException {
    PERFORMANCE.startTimer();
    monitor.beginTask(NLS.bind(CoreMessages.InstrumentingClassesInTask,
        location), 1);
    IPath outputlocation = EclEmmaCorePlugin.getInstance().getStateFiles()
        .getInstrDataFolder(location);
    outputlocation.toFile().mkdirs();
    IPath metadatafile = outputlocation.addFileExtension(METADATAFILE_EXT);
    if (inplace) {
      outputlocation = EclEmmaCorePlugin.getAbsolutePath(location);
    }
    InstrProcessor processor = InstrProcessor.create();
    processor.setInstrPath(new String[] { EclEmmaCorePlugin.getAbsolutePath(
        location).toOSString() }, true);
    processor.setInstrOutDir(outputlocation.toOSString());
    processor.setMetaOutFile(metadatafile.toOSString());
    processor.setMetaOutMerge(Boolean.TRUE);
    processor.setOutMode(inplace ? OutMode.OUT_MODE_OVERWRITE
        : OutMode.OUT_MODE_COPY);
    Properties props = new XProperties();
    props.put(AppLoggers.PROPERTY_VERBOSITY_LEVEL,
        DebugOptions.EMMAVERBOSITYLEVEL);
    processor.setPropertyOverrides(props);
    processor.run();
    monitor.done();
    PERFORMANCE.stopTimer("instrumenting " + location); //$NON-NLS-1$
    return new Instrumentation(this, inplace, outputlocation, metadatafile);
  }

}
