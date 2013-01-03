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
package com.mountainminds.eclemma.internal.ui.editors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

/**
 * Internal editor model for execution data.
 */
class ExecutionDataContent {

  private ExecutionDataStore executionData;
  private SessionInfoStore sessionData;
  private final List<IPropertyListener> listeners;

  public ExecutionDataContent() {
    clear();
    listeners = new ArrayList<IPropertyListener>();
  }

  public void load(IEditorInput input) {
    clear();
    try {
      if (input instanceof CoverageSessionInput) {
        final CoverageSessionInput csi = (CoverageSessionInput) input;
        csi.getSession().accept(executionData, sessionData);
      } else {
        final InputStream stream = openStream(input);
        final ExecutionDataReader reader = new ExecutionDataReader(stream);
        reader.setExecutionDataVisitor(executionData);
        reader.setSessionInfoVisitor(sessionData);
        while (reader.read()) {
          // Do nothing
        }
      }
    } catch (CoreException e) {
      EclEmmaUIPlugin.log(e);
    } catch (IOException e) {
      EclEmmaUIPlugin.log(e);
    }
    fireChangedEvent();
  }

  private InputStream openStream(IEditorInput input) throws CoreException,
      IOException {
    if (input instanceof IStorageEditorInput) {
      final IStorage storage = ((IStorageEditorInput) input).getStorage();
      return storage.getContents();
    }
    if (input instanceof IURIEditorInput) {
      final URI uri = ((IURIEditorInput) input).getURI();
      return uri.toURL().openStream();
    }
    throw new IOException("Unsupported input type: " + input.getClass()); //$NON-NLS-1$
  }

  public void addPropertyListener(IPropertyListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removePropertyListener(IPropertyListener listener) {
    listeners.remove(listener);
  }

  private void fireChangedEvent() {
    for (final IPropertyListener l : listeners) {
      l.propertyChanged(this, 0);
    }
  }

  public ExecutionData[] getExecutionData() {
    final Collection<ExecutionData> data = executionData.getContents();
    return data.toArray(new ExecutionData[data.size()]);
  }

  public SessionInfo[] getSessionInfos() {
    final Collection<SessionInfo> infos = sessionData.getInfos();
    return infos.toArray(new SessionInfo[infos.size()]);
  }

  private void clear() {
    executionData = new ExecutionDataStore();
    sessionData = new SessionInfoStore();
  }

}
