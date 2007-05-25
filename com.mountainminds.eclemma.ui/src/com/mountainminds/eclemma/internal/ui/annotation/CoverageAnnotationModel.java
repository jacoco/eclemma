/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
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

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.analysis.IJavaCoverageListener;
import com.mountainminds.eclemma.core.analysis.IJavaElementCoverage;
import com.mountainminds.eclemma.core.analysis.ILineCoverage;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * IAnnotationModel implementation for efficient coverage highlighting.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public class CoverageAnnotationModel implements IAnnotationModel {
  
  /** Key used to piggyback our model to the editor's model. */
  private static final Object KEY = new Object();
  
  /** List of current CoverageAnnotation objects */
  private List annotations = new ArrayList(32);

  /** List of registered IAnnotationModelListener */
  private List annotationModelListeners = new ArrayList(2);
  
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
   * Attaches a coverage annotation model for the given editor if the editor
   * can be annotated. Does nothing if the model is already attached.
   * 
   * @param editor Editor to attach a annotation model to
   */
  public static void attach(ITextEditor editor) {
    IDocumentProvider provider = editor.getDocumentProvider();
    // there may be text editors without document providers (SF #1725100)
    if (provider == null) return;
    IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
    if (!(model instanceof IAnnotationModelExtension)) return;
    IAnnotationModelExtension modelex = (IAnnotationModelExtension) model;
    
    IDocument document = provider.getDocument(editor.getEditorInput());
    
    CoverageAnnotationModel coveragemodel = (CoverageAnnotationModel) modelex.getAnnotationModel(KEY);
    if (coveragemodel == null) {
      coveragemodel = new CoverageAnnotationModel(editor, document);
      modelex.addAnnotationModel(KEY, coveragemodel);
    }
  }
  
  /**
   * Detaches the coverage annotation model from the given editor. If the editor
   * does not have a model attached, this method does nothing.
   *
   * @param editor Editor to detach the annotation model from
   */
  public static void detach(ITextEditor editor) {
    IDocumentProvider provider = editor.getDocumentProvider();
    // there may be text editors without document providers (SF #1725100)
    if (provider == null) return;
    IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
    if (!(model instanceof IAnnotationModelExtension)) return;
    IAnnotationModelExtension modelex = (IAnnotationModelExtension) model;
    modelex.removeAnnotationModel(KEY);
  }
  
  protected void updateAnnotations(boolean force) {
    ILineCoverage lineCoverage = null;
    boolean annotate = false;
    preconditions: {
      if (editor.isDirty()) break preconditions;
      IEditorInput input = editor.getEditorInput();
      if (input == null) break preconditions;
      Object element = input.getAdapter(IJavaElement.class);
      lineCoverage = findLineCoverage(element);
      if (lineCoverage == null || !hasSource((IJavaElement) element))
        break preconditions;
      annotate = true;
    }
    if (annotate) {
      if (!annotated || force) {
        createAnnotations(lineCoverage);
        annotated = true; 
      }
    } else {
      if (annotated) {
        clear();
        annotated = false; 
      }
    }
  }
  
  protected boolean hasSource(IJavaElement element) {
    if (element instanceof ISourceReference) {
      try {
        return ((ISourceReference) element).getSourceRange() != null;
      } catch (JavaModelException ex) {
        // we ignore this, the resource seems to have problems
      }
    }
    return false;
  }
  
  protected ILineCoverage findLineCoverage(Object element) {
    // Do we have a coverage info for the editor input?
    IJavaElementCoverage coverage = CoverageTools.getCoverageInfo(element);
    if (coverage == null) return null;
    
    // Does the resource version (if any) corresponds to the coverage data?
    IResource resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
    if (resource != null) {
      if (resource.getModificationStamp() != coverage.getResourceModificationStamp()) return null;
    }
    
    return coverage.getLineCoverage();
  }
  
  protected void clear() {
    AnnotationModelEvent event = new AnnotationModelEvent(this);
    clear(event);
    fireModelChanged(event);
  }

  protected void clear(AnnotationModelEvent event) {
    for (Iterator i = annotations.iterator(); i.hasNext();) {
      CoverageAnnotation ca = (CoverageAnnotation) i.next();
      event.annotationRemoved(ca, ca.getPosition());
    }
    annotations.clear();
  }

  protected void createAnnotations(ILineCoverage linecoverage) {
    AnnotationModelEvent event = new AnnotationModelEvent(this);
    clear(event);
    int firstline = linecoverage.getFirstLine();
    int lastline = linecoverage.getLastLine();
    int offset = linecoverage.getOffset();
    byte[] coverage = linecoverage.getCoverage();
    try {
      for (int l = firstline ; l <= lastline; l++) {
        int status = coverage[l - offset];
        if (status != ILineCoverage.NO_CODE) {
          IRegion region = document.getLineInformation(l - 1);
          int docoffset = region.getOffset();
          int doclength = region.getLength();
          // Extend annotation for subsequent lines with same status:
          while (l < lastline && coverage[l + 1 - offset] == status) {
            l++;
            region = document.getLineInformation(l - 1);
            doclength = region.getOffset() - docoffset + region.getLength();
          }
          CoverageAnnotation ca = new CoverageAnnotation(docoffset, doclength, status);
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
  
  protected void fireModelChanged(AnnotationModelEvent event) {
    event.markSealed();
    if (!event.isEmpty()) {
      for (Iterator i = annotationModelListeners.iterator(); i.hasNext(); ) {
        IAnnotationModelListener l = (IAnnotationModelListener) i.next();
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
    for (Iterator i = annotations.iterator(); i.hasNext();) {
      CoverageAnnotation ca = (CoverageAnnotation) i.next();
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
    for (Iterator i = annotations.iterator(); i.hasNext();) {
      CoverageAnnotation ca = (CoverageAnnotation) i.next();
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

  public Iterator getAnnotationIterator() {
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
