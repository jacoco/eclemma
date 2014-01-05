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
package com.mountainminds.eclemma.internal.ui.handlers;

import java.util.Collections;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.wizards.SessionExportWizard;

/**
 * Handler to export a JaCoCo coverage session.
 * 
 * Unlike the default handler for the export command, this implementation does
 * not overwrite menu icons and labels.
 */
public class ExportSessionHandler extends AbstractSessionManagerHandler {

  public ExportSessionHandler() {
    super(CoverageTools.getSessionManager());
  }

  @Override
  public boolean isEnabled() {
    return !sessionManager.getSessions().isEmpty();
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final ICommandService cs = (ICommandService) HandlerUtil.getActiveSite(
        event).getService(ICommandService.class);
    final Command command = cs
        .getCommand(IWorkbenchCommandConstants.FILE_EXPORT);
    final ExecutionEvent importEvent = new ExecutionEvent(command,
        Collections.singletonMap("exportWizardId", SessionExportWizard.ID), //$NON-NLS-1$
        event.getTrigger(), event.getApplicationContext());
    try {
      command.executeWithChecks(importEvent);
    } catch (CommandException e) {
      EclEmmaUIPlugin.log(e);
    }
    return null;
  }
}
