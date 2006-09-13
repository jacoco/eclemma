/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.core;


/**
 * The session manager holds a list of currently available sessions. One of the
 * sessions in the list may be the active session, which is the one that is used
 * to attach coverage information to Java elements.
 * 
 * This interface is not intended to be implemented or extended by clients.
 * 
 * @see com.mountainminds.eclemma.core.CoverageTools#getSessionManager()
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public interface ISessionManager {

  /**
   * Adds the given session to this session manager. If the session is already
   * part of this session manager the method has no effect. If the key parameter
   * is not <code>null</code> the key is internally assigned to this session
   * for later access.
   * 
   * @param session
   *          the new session
   * @param activate
   *          if <code>true</code> the session will also be activated
   * @param key
   *          key object this session will be assigned to
   */
  public void addSession(ICoverageSession session, boolean activate, Object key);

  /**
   * Removes the given session. If the session is not in included in this
   * session manager this method has no effect. If the removed session was the
   * active session, the most recently added session becomes active.
   * 
   * @param session
   *          session to remove
   */
  public void removeSession(ICoverageSession session);

  /**
   * Removes the session that has been assigned to the given key. If there is no
   * session for the key this method has no effect. If the removed session was
   * the active session, the most recently added session becomes active.
   * 
   * @see #addSession(ICoverageSession, boolean, Object)
   * @param key
   *          key object for the session to remove
   */
  public void removeSession(Object key);

  /**
   * Removes all registered sessions.
   */
  public void removeAllSessions();

  /**
   * Returns all sessions that have been registered with this session manager.
   * 
   * @see #addSession(ICoverageSession, boolean, Object)
   * @return list of registered session
   */
  public ICoverageSession[] getSessions();

  /**
   * Returns the session that has been assigned to the given key. If there is no
   * session for the key this method returns <code>null</code>.
   * 
   * @see #addSession(ICoverageSession, boolean, Object)
   * @param key
   *          key object for the session
   * @return session object or <code>null</code>
   */
  public ICoverageSession getSession(Object key);

  /**
   * Activates the given session. If the session is not in included in this
   * session manager this method has no effect.
   * 
   * @param session
   *          session to activate
   */
  public void activateSession(ICoverageSession session);

  /**
   * Activates the session that has been assigned to the given key. If there is
   * no session for the key this method has no effect.
   * 
   * @see #addSession(ICoverageSession, boolean, Object)
   * @param key
   *          key object for the session to activate
   */
  public void activateSession(Object key);

  /**
   * Returns the active session or <code>null</code> if there is no session.
   * 
   * @return active session or <code>null</null>
   */
  public ICoverageSession getActiveSession();

  /**
   * Triggers a reload of the active session. If there is no active session
   * this method has no effect.
   */
  public void refreshActiveSession();
  
  /**
   * Adds the given session listener unless it has been added before.
   * 
   * @param listener
   *          session listener to add
   */
  public void addSessionListener(ISessionListener listener);

  /**
   * Removes the given session listener. If the listener has not been added
   * before this method has no effect.
   * 
   * @param listener
   *          session listener to remove
   */
  public void removeSessionListener(ISessionListener listener);

}
