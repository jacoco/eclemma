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
package com.mountainminds.eclemma.internal.core.launching;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

import com.mountainminds.eclemma.core.EclEmmaStatus;
import com.mountainminds.eclemma.core.ICorePreferences;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.core.launching.ICoverageLaunch;
import com.mountainminds.eclemma.internal.core.CoreMessages;
import com.mountainminds.eclemma.internal.core.CoverageSession;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.ExecutionDataFiles;
import com.mountainminds.eclemma.internal.core.MemoryExecutionDataSource;

/**
 * Internal TCP/IP server for the JaCoCo agent to connect to.
 * 
 */
public class AgentServer extends Job {

  private final ICoverageLaunch launch;
  private final ISessionManager sessionManager;
  private final ExecutionDataFiles files;
  private final ICorePreferences preferences;

  private ServerSocket serverSocket;
  private RemoteControlWriter writer;
  private boolean dataReceived;

  AgentServer(ICoverageLaunch launch, ISessionManager sessionManager,
      ExecutionDataFiles files, ICorePreferences preferences) {
    super(AgentServer.class.getName());
    this.preferences = preferences;
    setSystem(true);
    this.launch = launch;
    this.sessionManager = sessionManager;
    this.files = files;
    this.dataReceived = false;
  }

  public void start() throws CoreException {
    try {
      // Bind an available port on the loopback interface:
      serverSocket = new ServerSocket(0, 0, InetAddress.getByName(null));
    } catch (IOException e) {
      throw new CoreException(
          EclEmmaStatus.AGENTSERVER_START_ERROR.getStatus(e));
    }
    schedule();
  }

  public void requestDump(boolean reset) throws CoreException {
    if (writer != null) {
      try {
        writer.visitDumpCommand(true, reset);
      } catch (IOException e) {
        throw new CoreException(EclEmmaStatus.DUMP_REQUEST_ERROR.getStatus(e));
      }
    }
  }

  public void stop() {
    writer = null;
    try {
      serverSocket.close();
    } catch (IOException e) {
      EclEmmaCorePlugin.getInstance().getLog()
          .log(EclEmmaStatus.AGENTSERVER_STOP_ERROR.getStatus(e));
    }
    cancel();
  }

  public boolean hasDataReceived() {
    return dataReceived;
  }

  public int getPort() {
    return serverSocket.getLocalPort();
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    try {
      final Socket socket = serverSocket.accept();
      writer = new RemoteControlWriter(socket.getOutputStream());
      final RemoteControlReader reader = new RemoteControlReader(
          socket.getInputStream());
      while (true) {
        final MemoryExecutionDataSource memory = new MemoryExecutionDataSource();
        memory.readFrom(reader);
        if (memory.isEmpty()) {
          return Status.OK_STATUS;
        }
        dataReceived = true;
        final CoverageSession session = new CoverageSession(
            createDescription(), launch.getScope(), files.newFile(memory),
            launch.getLaunchConfiguration());
        sessionManager.addSession(session,
            preferences.getActivateNewSessions(), launch);
      }
    } catch (IOException e) {
      return EclEmmaStatus.EXECDATA_DUMP_ERROR.getStatus(e);
    } catch (CoreException e) {
      return e.getStatus();
    }
  }

  private String createDescription() {
    final Object[] args = new Object[] {
        launch.getLaunchConfiguration().getName(), new Date() };
    return MessageFormat.format(CoreMessages.LaunchSessionDescription_value,
        args);
  }
}
