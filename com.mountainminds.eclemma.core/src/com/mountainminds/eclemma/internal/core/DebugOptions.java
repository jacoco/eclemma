/*******************************************************************************
 * Copyright (c) 2006 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id$
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.core.runtime.Platform;

/**
 * Access to debug options and tracing facilities for this plugin.
 * 
 * @author  Marc R. Hoffmann
 * @version $Revision$
 */
public final class DebugOptions {

  /**
   * Interface for optional trace output.
   */
  public interface ITracer {

    /**
     * Determines whether this tracer is enabled. Clients may use this method to
     * avoid expensive calculation for debug output.
     * 
     * @return <code>true</code> if the tracer is enabled
     */
    public boolean isEnabled();

    /**
     * Prints the given debug message if the tracer is enabled.
     * 
     * @param message
     *          text message for trace output
     */
    public void trace(String message);

    /**
     * Prints the given debug message if the tracer is enabled. The parameter
     * object will be inserted for the <code>{x}</code> placeholder.
     * 
     * @param message
     *          text message for trace output
     * @param param1
     *          parameter object for inserting
     */
    public void trace(String message, Object param1);

    /**
     * Prints the given debug message if the tracer is enabled. The parameter
     * object wills be inserted for the <code>{x}</code> placeholder.
     * 
     * @param message
     *          text message for trace output
     * @param param1
     *          first parameter object for inserting
     * @param param2
     *          first parameter object for inserting
     */
    public void trace(String message, Object param1, Object param2);

    /**
     * Prints the given debug message if the tracer is enabled. The parameter
     * object wills be inserted for the <code>{x}</code> placeholder.
     * 
     * @param message
     *          text message for trace output
     * @param param1
     *          first parameter object for inserting
     * @param param2
     *          first parameter object for inserting
     * @param param3
     *          third parameter object for inserting
     */
    public void trace(String message, Object param1, Object param2,
        Object param3);

    /**
     * Starts a timer for the calling thread.
     */
    public void startTimer();

    /**
     * Prints out the elapsed time since starting the timer.
     * 
     * @param message
     *          identification for the timed period
     */
    public void stopTimer(String message);

    /**
     * Start measuring heap memory usage.
     */
    public void startMemoryUsage();

    /**
     * Print out heap memory usage since starting measurement.
     * 
     * @param message
     *          identification for this memory usage output
     */
    public void stopMemoryUsage(String message);

  }

  private static final ITracer NUL_TRACER = new ITracer() {

    public boolean isEnabled() {
      return false;
    }

    public void trace(String message) {
    }

    public void trace(String message, Object param1) {
    }

    public void trace(String message, Object param1, Object param2) {
    }

    public void trace(String message, Object param1, Object param2,
        Object param3) {
    }

    public void startTimer() {
    }

    public void stopTimer(String message) {
    }

    public void startMemoryUsage() {
    }

    public void stopMemoryUsage(String message) {
    }
  };

  private static class PrintStreamTracer implements ITracer {

    private final PrintStream out;

    private final String channel;

    private final ThreadLocal starttime = new ThreadLocal();

    private final ThreadLocal heapsize = new ThreadLocal();

    PrintStreamTracer(String channel) {
      this(channel, System.out);
    }

    PrintStreamTracer(String channel, PrintStream out) {
      this.channel = channel;
      this.out = out;
    }

    public boolean isEnabled() {
      return true;
    }

    public void trace(String message) {
      StringBuffer sb = new StringBuffer();
      sb.append('[').append(channel).append("] ").append(message); //$NON-NLS-1$
      out.println(sb);
    }

    private void trace(String message, Object[] params) {
      trace(MessageFormat.format(message, params));
    }

    public void trace(String message, Object param1) {
      trace(message, new Object[] { param1 });
    }

    public void trace(String message, Object param1, Object param2) {
      trace(message, new Object[] { param1, param2 });
    }

    public void trace(String message, Object param1, Object param2,
        Object param3) {
      trace(message, new Object[] { param1, param2, param3 });
    }

    public void startTimer() {
      starttime.set(new Long(System.currentTimeMillis()));
    }

    public void stopTimer(String message) {
      Long start = (Long) starttime.get();
      if (start == null) {
        trace("Timer {0} not startet.", message); //$NON-NLS-1$
      } else {
        long time = System.currentTimeMillis() - start.longValue();
        trace("{0} ms for {1}", new Object[] { new Long(time), message }); //$NON-NLS-1$
      }
    }

    public void startMemoryUsage() {
      Runtime rt = Runtime.getRuntime();
      heapsize.set(new Long(rt.totalMemory() - rt.freeMemory()));
    }

    public void stopMemoryUsage(String message) {
      Long start = (Long) heapsize.get();
      if (start == null) {
        trace("Memory usage for {0} not started.", message); //$NON-NLS-1$
      } else {
        Runtime rt = Runtime.getRuntime();
        long bytes = rt.totalMemory() - rt.freeMemory() - start.longValue();
        trace("{0} bytes for {1}", new Object[] { new Long(bytes), message }); //$NON-NLS-1$
      }
    }
  }

  private static final String KEYPREFIX_DEBUG = EclEmmaCorePlugin.ID
      + "/debug/"; //$NON-NLS-1$

  private static final String KEY_EMMAVERBOSITYLEVEL = KEYPREFIX_DEBUG
      + "emmaVerbosityLevel"; //$NON-NLS-1$

  private static String getOption(String key, String defaultvalue) {
    String value = Platform.getDebugOption(key);
    return value == null ? defaultvalue : value;
  }

  private static ITracer getTracer(String channel) {
    String key = KEYPREFIX_DEBUG + channel;
    if (Boolean.valueOf(Platform.getDebugOption(key)).booleanValue()) {
      return new PrintStreamTracer(channel);
    } else {
      return NUL_TRACER;
    }
  }

  public static final String EMMAVERBOSITYLEVEL = getOption(
      KEY_EMMAVERBOSITYLEVEL, "silent"); //$NON-NLS-1$

  public static final ITracer PERFORMANCETRACER = getTracer("performance"); //$NON-NLS-1$
  
  public static final ITracer INSTRUMENTATIONTRACER = getTracer("instrumentation"); //$NON-NLS-1$

  public static final ITracer LAUNCHINGTRACER = getTracer("launching"); //$NON-NLS-1$

  public static final ITracer ANALYSISTRACER = getTracer("analysis"); //$NON-NLS-1$

}
