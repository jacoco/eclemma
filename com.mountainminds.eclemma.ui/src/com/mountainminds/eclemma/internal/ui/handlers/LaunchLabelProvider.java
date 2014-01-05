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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Internal label provider for {@link ILaunch} objects.
 */
class LaunchLabelProvider extends LabelProvider {

  @Override
  public String getText(Object element) {
    return getLaunchText((ILaunch) element);
  }

  @Override
  public Image getImage(Object element) {
    return EclEmmaUIPlugin.getImage(EclEmmaUIPlugin.ELCL_DUMP);
  }

  public static String getLaunchText(ILaunch launch) {
    // new launch configuration
    final ILaunchConfiguration config = launch.getLaunchConfiguration();
    if (config == null) {
      return UIMessages.DumpExecutionDataUnknownLaunch_value;
    }
    StringBuilder sb = new StringBuilder(config.getName());
    sb.append(" ["); //$NON-NLS-1$
    try {
      sb.append(config.getType().getName());
    } catch (CoreException e) {
      EclEmmaUIPlugin.log(e);
    }
    sb.append("]"); //$NON-NLS-1$
    return sb.toString();
  }

}
