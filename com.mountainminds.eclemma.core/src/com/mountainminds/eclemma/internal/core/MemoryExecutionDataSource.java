/*******************************************************************************
 * Copyright (c) 2006, 2012 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import com.mountainminds.eclemma.core.IExecutionDataSource;

/**
 * In-memory {@link IExecutionDataSource} implementation.
 */
public class MemoryExecutionDataSource implements IExecutionDataSource,
    ISessionInfoVisitor, IExecutionDataVisitor {

  private final SessionInfoStore sessionInfoStore;
  private ExecutionDataStore executionDataStore;

  public MemoryExecutionDataSource() {
    sessionInfoStore = new SessionInfoStore();
    executionDataStore = new ExecutionDataStore();
  }

  public boolean isEmpty() {
    return sessionInfoStore.isEmpty();
  }

  public void accept(IExecutionDataVisitor executionDataVisitor,
      ISessionInfoVisitor sessionInfoVisitor) throws CoreException {
    sessionInfoStore.accept(sessionInfoVisitor);
    executionDataStore.accept(executionDataVisitor);
  }

  public void visitSessionInfo(SessionInfo info) {
    sessionInfoStore.visitSessionInfo(info);
  }

  public void visitClassExecution(ExecutionData data) {
    executionDataStore.visitClassExecution(data);
  }

  /**
   * Collects execution data from the given reader.
   * 
   * @param reader
   *          reader to read execution data from
   */
  public void readFrom(ExecutionDataReader reader) throws IOException {
    reader.setSessionInfoVisitor(sessionInfoStore);
    reader.setExecutionDataVisitor(executionDataStore);
    reader.read();
  }

}
