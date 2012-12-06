package com.mountainminds.eclemma.internal.ui.barpainters;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;
import com.mountainminds.eclemma.internal.ui.UIPreferences;

/**
 * Factory that creates the painters based on the preference page
 */
public class BarPainterFactory {

  private BarPainterFactory() {
    // Factory
  }

  /**
   * Returns a new instance of the configured painter. If for any reason the
   * configured painter cannot be obtained it will log the exception and return
   * an instance of the default painter.
   */
  public static BarPainter newBarPainter() {
    String barPainterClassName = EclEmmaUIPlugin.getInstance()
        .getPreferenceStore()
        .getString(UIPreferences.PREF_BAR_PAINTER);
    BarPainter barPainter;
    try {
      barPainter = (BarPainter) Class.forName(barPainterClassName)
          .newInstance();
    } catch (Exception e) {
      EclEmmaUIPlugin.log(e);
      // Use the default painter
      barPainter = new JaCoCoBarPainter();
    }
    return barPainter;
  }

}
