/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Internal utility class for traversal of all types and methods with a list of
 * package fragment roots.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class JavaElementsTraverser {

  private final IPackageFragmentRoot[] roots;

  /**
   * Creates a traverser for the given list of package fragment roots.
   * 
   * @param roots
   *          list of package fragment roots for traversal
   */
  public JavaElementsTraverser(IPackageFragmentRoot[] roots) {
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
    IJavaElement[] fragments = root.getChildren();
    monitor.beginTask("", fragments.length); //$NON-NLS-1$
    for (int i = 0; i < fragments.length && !monitor.isCanceled(); i++) {
      IPackageFragment fragment = (IPackageFragment) fragments[i];
      IProgressMonitor submonitor = new SubProgressMonitor(monitor, 1);
      processPackageFragment(visitor, fragment, submonitor);
    }
    monitor.done();
  }

  private void processPackageFragment(ITypeVisitor visitor,
      IPackageFragment fragment, IProgressMonitor monitor)
      throws JavaModelException {
    switch (fragment.getKind()) {
    case IPackageFragmentRoot.K_SOURCE:
      ICompilationUnit[] units = fragment.getCompilationUnits();
      monitor.beginTask("", units.length); //$NON-NLS-1$
      for (int i = 0; i < units.length && !monitor.isCanceled(); i++) {
        IProgressMonitor submonitor = new SubProgressMonitor(monitor, 1);
        processCompilationUnit(visitor, units[i], submonitor);
      }
      break;
    case IPackageFragmentRoot.K_BINARY:
      IClassFile[] classfiles = fragment.getClassFiles();
      monitor.beginTask("", classfiles.length); //$NON-NLS-1$
      for (int i = 0; i < classfiles.length && !monitor.isCanceled(); i++) {
        IProgressMonitor submonitor = new SubProgressMonitor(monitor, 1);
        processClassFile(visitor, classfiles[i], submonitor);
      }
      break;
    }
    monitor.done();
  }

  private void processCompilationUnit(ITypeVisitor visitor,
      ICompilationUnit unit, IProgressMonitor monitor)
      throws JavaModelException {
    processTypes(visitor, unit.getAllTypes(), monitor);
  }

  private void processClassFile(ITypeVisitor visitor, IClassFile file,
      IProgressMonitor monitor) throws JavaModelException {
    IType type = file.getType();
    processType(visitor, new AnonymousTypeEnumerator(type), type, monitor);
  }

  private void processTypes(ITypeVisitor visitor, IType[] types,
      IProgressMonitor monitor) throws JavaModelException {
    for (int i = 0; i < types.length && !monitor.isCanceled(); i++) {
      IType type = types[i];
      processType(visitor, new AnonymousTypeEnumerator(type), type, monitor);
    }
  }

  private void processType(ITypeVisitor visitor, AnonymousTypeEnumerator aenum,
      IType type, IProgressMonitor monitor) throws JavaModelException {
    String vmname = aenum.getVMName(type);
    monitor.subTask(vmname);
    IMethodVisitor methodvisitor = visitor.visit(type, vmname);
    MethodResolver resolver = new MethodResolver(type);
    IJavaElement[] children = type.getChildren();
    for (int i = 0; i < children.length && !monitor.isCanceled(); i++) {
      IJavaElement child = children[i];
      switch (child.getElementType()) {
      case IJavaElement.METHOD:
        IMethod method = (IMethod) child;
        if (methodvisitor != null) {
          methodvisitor.visit(method, resolver.resolve(method));
        }
        processAnonymousInnerTypes(visitor, aenum, method, monitor);
        break;
      case IJavaElement.INITIALIZER:
        IInitializer init = (IInitializer) child;
        processAnonymousInnerTypes(visitor, aenum, init, monitor);
        break;
      case IJavaElement.FIELD:
        IField field = (IField) child;
        processAnonymousInnerTypes(visitor, aenum, field, monitor);
        break;
      }
    }
    if (methodvisitor != null) {
      methodvisitor.done();
    }
  }

  private void processAnonymousInnerTypes(ITypeVisitor visitor,
      AnonymousTypeEnumerator aenum, IMember member, IProgressMonitor monitor)
      throws JavaModelException {
    IJavaElement[] types = member.getChildren();
    for (int i = 0; i < types.length && !monitor.isCanceled(); i++) {
      IType type = (IType) types[i];
      processType(visitor, aenum, type, monitor);
    }
  }

  private static class AnonymousTypeEnumerator {

    private static final int FIRST_INDEX = 1;

    private final IType enclosingtype;

    private int index;

    AnonymousTypeEnumerator(IType enclosingtype) {
      this.enclosingtype = enclosingtype;
      index = FIRST_INDEX;
    }

    public String getVMName(IType type) throws JavaModelException {
      if (!type.isBinary() && type.isAnonymous()) {
        StringBuffer sb = new StringBuffer(64);
        sb.append(enclosingtype.getFullyQualifiedName('$').replace('.', '/'));
        sb.append('$');
        sb.append(index++);
        return sb.toString();
      } else {
        return type.getFullyQualifiedName('$').replace('.', '/');
      }
    }

  }

}
