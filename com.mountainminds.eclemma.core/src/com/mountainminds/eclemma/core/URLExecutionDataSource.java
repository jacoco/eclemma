/*******************************************************************************
 * Copyright (c) 2006, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;


/**
 * {@link IExecutionDataSource} implementation based on a *.exec file obtained
 * from a URL.
 */
public class URLExecutionDataSource implements IExecutionDataSource {

  private final URL url;

  public URLExecutionDataSource(final URL url) {
    this.url = url;
  }

  public void accept(IExecutionDataVisitor executionDataVisitor,
      ISessionInfoVisitor sessionInfoVisitor) throws CoreException {
    try {
      final InputStream in = new BufferedInputStream(url.openStream());
      final ExecutionDataReader reader = new ExecutionDataReader(in);
      reader.setExecutionDataVisitor(executionDataVisitor);
      reader.setSessionInfoVisitor(sessionInfoVisitor);
      reader.read();
      in.close();
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.EXEC_FILE_READ_ERROR.getStatus(url,
          e));
    }
  }

}
