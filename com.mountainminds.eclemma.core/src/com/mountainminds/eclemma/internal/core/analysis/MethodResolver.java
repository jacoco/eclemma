/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.CharOperation;

import com.mountainminds.eclemma.internal.core.DebugOptions;
import com.mountainminds.eclemma.internal.core.DebugOptions.ITracer;

/**
 * Internal utility class that resolves local method signatures into absolute VM
 * type identifierts.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class MethodResolver {
  
  private static final String CONSTRUCTOR_VMNAME = "<init>"; //$NON-NLS-1$

  private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

  private final IType type;

  private final Map cache = new HashMap();

  /**
   * Creates a new resolver that works relative to the given type.
   * 
   * @param type
   *          context type to resolve local declarations in
   */
  public MethodResolver(IType type) {
    this.type = type;
  }

  /**
   * Resolves the given signature type, e.g. <code>QMap.Entry;</code>.
   * Absolute types and primitve types will not be resolved. The returned string
   * is a VM signature type, e.g. <code>Ljava/util/Map$Entry;</code>.
   * 
   * @param localName
   *          the local dot-based signature type
   * @return resolved absolute VM signature
   * @throws JavaModelException
   *           if thrown by the underlying JavaModel
   */
  public char[] resolve(final String localName) throws JavaModelException {
    char[] cresolved = (char[]) cache.get(localName);
    if (cresolved != null)
      return cresolved;

    char[] clocalName = localName.toCharArray();

    int arraynesting = Signature.getArrayCount(clocalName);
    if (arraynesting > 0) {
      cresolved = Signature.getElementType(clocalName);
    } else {
      cresolved = clocalName;
    }
    cresolved = Signature.getTypeErasure(cresolved);
    if (Signature.getTypeSignatureKind(cresolved) == Signature.CLASS_TYPE_SIGNATURE) {
      String[][] hits = type.resolveType(new String(cresolved, 1,
          cresolved.length - 2));
      if (hits == null) {
        // some guessing
        cresolved[0] = Signature.C_RESOLVED;
        CharOperation.replace(cresolved, '.', '/');
      } else {
        if (hits.length > 1) {
          TRACER.trace("Ambigous type resolved for {0} in {1}", localName, type //$NON-NLS-1$
              .getElementName());
        }
        char[] pack = hits[0][0].toCharArray();
        CharOperation.replace(pack, '.', '/');
        char[] type = hits[0][1].toCharArray();
        CharOperation.replace(type, '.', '$');
        cresolved = new char[pack.length + type.length + 3];
        cresolved[0] = Signature.C_RESOLVED;
        System.arraycopy(pack, 0, cresolved, 1, pack.length);
        cresolved[pack.length + 1] = '/';
        System.arraycopy(type, 0, cresolved, pack.length + 2, type.length);
        cresolved[pack.length + type.length + 2] = ';';
      }
    }
    if (arraynesting > 0) {
      cresolved = Signature.createArraySignature(cresolved, arraynesting);
    }
    cache.put(localName, cresolved);
    return cresolved;
  }
  
  public String resolve(IMethod method) throws JavaModelException {
    String[] params = method.getParameterTypes();
    char[][] resparams = new char[params.length][];
    for (int j = 0; j < params.length; j++) {
      resparams[j] = resolve(params[j]);
    }
    char[] resreturn = resolve(method.getReturnType());
    char[] signature = Signature.createMethodSignature(resparams, resreturn);
    String name = method.getElementName();
    name = replaceSpecialMethodNames(name);
    StringBuffer sb = new StringBuffer(name).append(signature);
    return sb.toString();
  }
  
  private String replaceSpecialMethodNames(String name) {
    if (name.equals(type.getElementName())) {
      name = CONSTRUCTOR_VMNAME;
    }
    return name;
  }
  
}
