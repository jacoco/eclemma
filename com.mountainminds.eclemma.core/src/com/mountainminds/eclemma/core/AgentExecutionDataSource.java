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

import java.io.IOException;
import java.net.Socket;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;


/**
 * {@link IExecutionDataSource} that receives execution data from a JaCoCo agent
 * via a TCP/IP connection.
 */
public class AgentExecutionDataSource implements IExecutionDataSource {

  private String address;
  private int port;
  private boolean reset;

  public AgentExecutionDataSource(final String address, final int port,
      final boolean reset) {
    this.address = address;
    this.port = port;
    this.reset = reset;
  }

  public void accept(IExecutionDataVisitor executionDataVisitor,
      ISessionInfoVisitor sessionInfoVisitor) throws CoreException {
    try {
      final Socket socket = new Socket(address, port);
      final RemoteControlWriter writer = new RemoteControlWriter(
          socket.getOutputStream());
      final RemoteControlReader reader = new RemoteControlReader(
          socket.getInputStream());
      reader.setExecutionDataVisitor(executionDataVisitor);
      reader.setSessionInfoVisitor(sessionInfoVisitor);
      writer.visitDumpCommand(true, reset);
      reader.read();
      socket.close();
    } catch (IOException e) {
      throw new CoreException(EclEmmaStatus.AGENT_CONNECT_ERROR.getStatus(
          address, Integer.valueOf(port), e));
    }
  }

}
