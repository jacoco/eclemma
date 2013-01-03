/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.ISourceNode;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * IAnnotationModel implementation for efficient coverage highlighting.
 */
public final class CoverageAnnotationModel implements IAnnotationModel {

  /** Key used to piggyback our model to the editor's model. */
  private static final Object KEY = new Object();

  /** List of current CoverageAnnotation objects */
  private List<CoverageAnnotation> annotations = new ArrayList<CoverageAnnotation>(
      32);

  /** List of registered IAnnotationModelListener */
  private List<IAnnotationModelListener> annotationModelListeners = new ArrayList<IAnnotationModelListener>(
      2);

  private final ITextEditor editor;
  private final IDocument document;
  private int openConnections = 0;
  private boolean annotated = false;

  private IJavaCoverageListener coverageListener = new IJavaCoverageListener() {
    public void coverageChanged() {
      updateAnnotations(true);
    }
  };

  private IDocumentListener documentListener = new IDocumentListener() {
    public void documentChanged(DocumentEvent event) {
      updateAnnotations(false);
    }

    public void documentAboutToBeChanged(DocumentEvent event) {
    }
  };

  private CoverageAnnotationModel(ITextEditor editor, IDocument document) {
    this.editor = editor;
    this.document = document;
    updateAnnotations(true);
  }

  /**
   * Attaches a coverage annotation model for the given editor if the editor can
   * be annotated. Does nothing if the model is already attached.
   * 
   * @param editor
   *          Editor to attach a annotation model to
   */
  public static void attach(ITextEditor editor) {
    IDocumentProvider provider = editor.getDocumentProvider();
    // there may be text editors without document providers (SF #1725100)
    if (provider == null)
      return;
    IAnnotationModel model = provider.getAnnotationModel(editor
        .getEditorInput());
    if (!(model instanceof IAnnotationModelExtension))
      return;
    IAnnotationModelExtension modelex = (IAnnotationModelExtension) model;

    IDocument document = provider.getDocument(editor.getEditorInput());

    CoverageAnnotationModel coveragemodel = (CoverageAnnotationModel) modelex
        .getAnnotationModel(KEY);
    if (coveragemodel == null) {
      coveragemodel = new CoverageAnnotationModel(editor, document);
      modelex.addAnnotationModel(KEY, coveragemodel);
    }
  }

  /**
   * Detaches the coverage annotation model from the given editor. If the editor
   * does not have a model attached, this method does nothing.
   * 
   * @param editor
   *          Editor to detach the annotation model from
   */
  public static void detach(ITextEditor editor) {
    IDocumentProvider provider = editor.getDocumentProvider();
    // there may be text editors without document providers (SF #1725100)
    if (provider == null)
      return;
    IAnnotationModel model = provider.getAnnotationModel(editor
        .getEditorInput());
    if (!(model instanceof IAnnotationModelExtension))
      return;
    IAnnotationModelExtension modelex = (IAnnotationModelExtension) model;
    modelex.removeAnnotationModel(KEY);
  }

  private void updateAnnotations(boolean force) {
    final ISourceNode coverage = findSourceCoverageForEditor();
    if (coverage != null) {
      if (!annotated || force) {
        createAnnotations(coverage);
        annotated = true;
      }
    } else {
      if (annotated) {
        clear();
        annotated = false;
      }
    }
  }

  private ISourceNode findSourceCoverageForEditor() {
    if (editor.isDirty()) {
      return null;
    }
    final IEditorInput input = editor.getEditorInput();
    if (input == null) {
      return null;
    }
    final Object element = input.getAdapter(IJavaElement.class);
    if (!hasSource((IJavaElement) element)) {
      return null;
    }
    return findSourceCoverageForElement(element);
  }

  private boolean hasSource(IJavaElement element) {
    if (element instanceof ISourceReference) {
      try {
        return ((ISourceReference) element).getSourceRange() != null;
      } catch (JavaModelException ex) {
        // we ignore this, the resource seems to have problems
      }
    }
    return false;
  }

  private ISourceNode findSourceCoverageForElement(Object element) {
    // Do we have a coverage info for the editor input?
    ICoverageNode coverage = CoverageTools.getCoverageInfo(element);
    if (coverage == null) {
      return null;
    }

    // TODO check resource timestamp
    // Does the resource version (if any) corresponds to the coverage data?
    // IResource resource = (IResource) ((IAdaptable) element)
    // .getAdapter(IResource.class);
    // if (resource != null) {
    // if (resource.getModificationStamp() != coverage
    // .getResourceModificationStamp())
    // return null;
    // }

    if (coverage instanceof ISourceNode) {
      return (ISourceNode) coverage;
    }
    return null;
  }

  private void clear() {
    AnnotationModelEvent event = new AnnotationModelEvent(this);
    clear(event);
    fireModelChanged(event);
  }

  private void clear(AnnotationModelEvent event) {
    for (final CoverageAnnotation ca : annotations) {
      event.annotationRemoved(ca, ca.getPosition());
    }
    annotations.clear();
  }

  private void createAnnotations(ISourceNode linecoverage) {
    AnnotationModelEvent event = new AnnotationModelEvent(this);
    clear(event);
    int firstline = linecoverage.getFirstLine();
    int lastline = linecoverage.getLastLine();
    try {
      for (int l = firstline; l <= lastline; l++) {
        final ILine line = linecoverage.getLine(l);
        if (line.getStatus() != ICounter.EMPTY) {
          IRegion region = document.getLineInformation(l - 1);
          int docoffset = region.getOffset();
          int doclength = region.getLength();
          CoverageAnnotation ca = new CoverageAnnotation(docoffset, doclength,
              line);
          annotations.add(ca);
          event.annotationAdded(ca);
        }
      }
    } catch (BadLocationException ex) {
      EclEmmaUIPlugin.log(ex);
    }
    fireModelChanged(event);
  }

  public void addAnnotationModelListener(IAnnotationModelListener listener) {
    if (!annotationModelListeners.contains(listener)) {
      annotationModelListeners.add(listener);
      fireModelChanged(new AnnotationModelEvent(this, true));
    }
  }

  public void removeAnnotationModelListener(IAnnotationModelListener listener) {
    annotationModelListeners.remove(listener);
  }

  private void fireModelChanged(AnnotationModelEvent event) {
    event.markSealed();
    if (!event.isEmpty()) {
      for (final IAnnotationModelListener l : annotationModelListeners) {
        if (l instanceof IAnnotationModelListenerExtension) {
          ((IAnnotationModelListenerExtension) l).modelChanged(event);
        } else {
          l.modelChanged(this);
        }
      }
    }
  }

  public void connect(IDocument document) {
    if (this.document != document)
      throw new RuntimeException("Can't connect to different document."); //$NON-NLS-1$
    for (final CoverageAnnotation ca : annotations) {
      try {
        document.addPosition(ca.getPosition());
      } catch (BadLocationException ex) {
        EclEmmaUIPlugin.log(ex);
      }
    }
    if (openConnections++ == 0) {
      CoverageTools.addJavaCoverageListener(coverageListener);
      document.addDocumentListener(documentListener);
    }
  }

  public void disconnect(IDocument document) {
    if (this.document != document)
      throw new RuntimeException("Can't disconnect from different document."); //$NON-NLS-1$
    for (final CoverageAnnotation ca : annotations) {
      document.removePosition(ca.getPosition());
    }
    if (--openConnections == 0) {
      CoverageTools.removeJavaCoverageListener(coverageListener);
      document.removeDocumentListener(documentListener);
    }
  }

  /**
   * External modification is not supported.
   */
  public void addAnnotation(Annotation annotation, Position position) {
    throw new UnsupportedOperationException();
  }

  /**
   * External modification is not supported.
   */
  public void removeAnnotation(Annotation annotation) {
    throw new UnsupportedOperationException();
  }

  public Iterator<?> getAnnotationIterator() {
    return annotations.iterator();
  }

  public Position getPosition(Annotation annotation) {
    if (annotation instanceof CoverageAnnotation) {
      return ((CoverageAnnotation) annotation).getPosition();
    } else {
      return null;
    }
  }

}
