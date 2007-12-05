/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * Internal utility class for traversal of all types within a list of package
 * fragment roots.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class TypeTraverser {

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;
	
  private final IPackageFragmentRoot[] roots;

  /**
   * Creates a traverser for the given list of package fragment roots.
   * 
   * @param roots
   *          list of package fragment roots for traversal
   */
  public TypeTraverser(IPackageFragmentRoot[] roots) {
    this.roots = roots;
  }

  /**
   * Processes all types and methods reporting all types found to the given
   * {@link ITypeVisitor} instance.
   * 
   * @param visitor
   *          type visitor
   * @param monitor
   *          progress monitor to report progress and allow cancelation
   * @throws JavaModelException
   *           thrown by the underlying Java model
   */
  public void process(ITypeVisitor visitor, IProgressMonitor monitor)
      throws JavaModelException {
    monitor.beginTask("", roots.length); //$NON-NLS-1$
    for (int i = 0; i < roots.length && !monitor.isCanceled(); i++) {
      processPackageFragmentRoot(visitor, roots[i], new SubProgressMonitor(
          monitor, 1));
    }
    visitor.done();
    monitor.done();
  }

  private void processPackageFragmentRoot(ITypeVisitor visitor,
      IPackageFragmentRoot root, IProgressMonitor monitor)
      throws JavaModelException {
	if (isOnClasspath(root)) {
      IJavaElement[] fragments = root.getChildren();
      monitor.beginTask("", fragments.length); //$NON-NLS-1$
      for (int i = 0; i < fragments.length && !monitor.isCanceled(); i++) {
        IPackageFragment fragment = (IPackageFragment) fragments[i];
        IProgressMonitor submonitor = new SubProgressMonitor(monitor, 1);
        processPackageFragment(visitor, fragment, submonitor);
      }
	} else {
      TRACER.trace("Package fragment root {0} not on classpath.", //$NON-NLS-1$
    		       root.getPath());
	}
    monitor.done();
  }
  
  /**
   * This methods checks whether the given package fragment root is still on the
   * classpath. This check is required as the user might change the classpath
   * and old coverage sessions afterwards (SF #1836551).
   * 
   * @param root  package fragment root
   * @return  true, if the classpath entry still exists
   * @throws JavaModelException
   */
  private boolean isOnClasspath(IPackageFragmentRoot root)
      throws JavaModelException {
    IPath path = root.getPath();
    IJavaProject project = root.getJavaProject();
    return project.findPackageFragmentRoot(path) != null;
  }

  private void processPackageFragment(ITypeVisitor visitor,
      IPackageFragment fragment, IProgressMonitor monitor)
      throws JavaModelException {
    switch (fragment.getKind()) {
    case IPackageFragmentRoot.K_SOURCE:
      ICompilationUnit[] units = fragment.getCompilationUnits();
      monitor.beginTask("", units.length); //$NON-NLS-1$
      for (int i = 0; i < units.length && !monitor.isCanceled(); i++) {
        processCompilationUnit(visitor, units[i], monitor);
        monitor.worked(1);
      }
      break;
    case IPackageFragmentRoot.K_BINARY:
      IClassFile[] classfiles = fragment.getClassFiles();
      monitor.beginTask("", classfiles.length); //$NON-NLS-1$
      for (int i = 0; i < classfiles.length && !monitor.isCanceled(); i++) {
        processClassFile(visitor, classfiles[i], monitor);
        monitor.worked(1);
      }
      break;
    }
    monitor.done();
  }

  private void processCompilationUnit(ITypeVisitor visitor,
      ICompilationUnit unit, IProgressMonitor monitor)
      throws JavaModelException {
    IType[] types = unit.getTypes();
    for (int i = 0; i < types.length && !monitor.isCanceled(); i++) {
      IType type = types[i];
      processType(visitor, new BinaryTypeName(type), type, monitor);
    }
  }

  private void processClassFile(ITypeVisitor visitor, IClassFile file,
      IProgressMonitor monitor) throws JavaModelException {
    IType type = file.getType();
    processType(visitor, new BinaryTypeName(type), type, monitor);
  }

  private void processType(ITypeVisitor visitor, BinaryTypeName btn,
      IType type, IProgressMonitor monitor) throws JavaModelException {
    String binaryname = btn.toString();
    monitor.subTask(binaryname);
    visitor.visit(type, binaryname);
    IJavaElement[] children = type.getChildren();
    for (int i = 0; i < children.length && !monitor.isCanceled(); i++) {
      IJavaElement child = children[i];
      switch (child.getElementType()) {
        case IJavaElement.TYPE:
          IType nestedtype = (IType) child;
          processType(visitor, btn.nest(nestedtype), nestedtype, monitor);
          break;
        case IJavaElement.METHOD:
        case IJavaElement.INITIALIZER:
        case IJavaElement.FIELD:
          processAnonymousInnerTypes(visitor, btn, (IMember) child, monitor);
          break;
      }
    }
  }

  private void processAnonymousInnerTypes(ITypeVisitor visitor,
      BinaryTypeName btn, IMember member, IProgressMonitor monitor)
      throws JavaModelException {
    IJavaElement[] types = member.getChildren();
    for (int i = 0; i < types.length && !monitor.isCanceled(); i++) {
      IType type = (IType) types[i];
      processType(visitor, btn.nest(type), type, monitor);
    }
  }
  
  /**
   * Internal utility to calculate binary names of nested classes.
   */
  private static class BinaryTypeName {
    
    private static class Ctr {
      private int i = 0;
      public int inc() {
        return ++i;
      }
    }
    
    private final String rootname;
    private final String typename;
    private final Ctr ctr;

    private BinaryTypeName(String rootname, String typename, Ctr ctr) {
      this.rootname = rootname;
      this.typename = typename;
      this.ctr = ctr;
    }
    
    public BinaryTypeName(IType roottype) {
      this.rootname = roottype.getFullyQualifiedName().replace('.', '/');
      this.typename = this.rootname;
      this.ctr = new Ctr();
    }
    
    public BinaryTypeName nest(IType type) throws JavaModelException {
      if (type.isAnonymous()) {
        return new BinaryTypeName(rootname, rootname + '$' + ctr.inc(), ctr);
      } else {
        return new BinaryTypeName(rootname, typename + '$' + type.getElementName(), ctr);
      }
    }
    
    public String toString() {
      return typename;  
    }
    
  }
  
}
