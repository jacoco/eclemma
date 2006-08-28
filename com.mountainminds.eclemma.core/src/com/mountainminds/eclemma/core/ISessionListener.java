/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;


/**
 * Callback interface for changes of the session manager. This interface is 
 * intended to be implemented by clients that want to get notifications.
 *
 * @see ISessionManager#addSessionListener(ISessionListener)
 * @see ISessionManager#removeSessionListener(ISessionListener)
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ISessionListener {

  /**
   * Called when a session has been added.
   * 
   * @param addedSession
   *          added session
   */  
  public void sessionAdded(ICoverageSession addedSession);

  /**
   * Called when a session has been removed.
   * 
   * @param removedSession
   *          removes session
   */
  public void sessionRemoved(ICoverageSession removedSession);

  /**
   * Called when a new session has been activated or the last session has been
   * removed. In this case <code>null</code> is passed as a parameter.
   * 
   * @param session
   *          activated session or <code>null</code>
   */
  public void sessionActivated(ICoverageSession session);

}
