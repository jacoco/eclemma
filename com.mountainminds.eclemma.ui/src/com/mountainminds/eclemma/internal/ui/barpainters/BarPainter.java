package com.mountainminds.eclemma.internal.ui.barpainters;

import static com.mountainminds.eclemma.internal.ui.barpainters.BarPainter.Paint.COVERED;
import static com.mountainminds.eclemma.internal.ui.barpainters.BarPainter.Paint.MISSED;

import java.text.DecimalFormat;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.jacoco.core.analysis.ICounter;

import com.mountainminds.eclemma.internal.ui.UIMessages;

/**
 * Base class for implementing BarPainters
 */
public abstract class BarPainter {

  private static final String MAX_PERCENTAGE_STRING = new DecimalFormat(
      UIMessages.CoverageView_columnCoverageValue).format(1.0);

  private static final int DEFAULT_BORDER_LEFT = 2;
  private static final int DEFAULT_BORDER_RIGHT = 10;

  protected static enum Paint {
    COVERED, MISSED
  }

  public void paint(Event event, int columnWith, ICounter counter, int maxTotal) {
    if (maxTotal == 0) {
      return;
    }

    // The bars on GTK will bleed to the header of the table and at the bottom
    // when rendering just part of the cell. Resetting the clipping seems to be
    // a workaround for this issue.
    Rectangle clipping = event.gc.getClipping();
    event.gc.setClipping(clipping);

    final int maxWidth = getMaxWidth(event, columnWith);
    final int missedLength = maxWidth * counter.getMissedCount() / maxTotal;
    paintBar(event, MISSED, 0, missedLength);
    final int coveredLength = maxWidth * counter.getCoveredCount() / maxTotal;
    paintBar(event, COVERED, missedLength, coveredLength);
  }

  public void dispose() {
    // Nothing to do
  }

  public void paint(Event event, int columnWith, ICounter counter) {
    paint(event, columnWith, counter, counter.getTotalCount());
  }

  protected abstract void paintBar(Event event, Paint type, int xOffset,
      int width);

  protected int getBorderLeft() {
    return DEFAULT_BORDER_LEFT;
  }

  protected int getBorderRight() {
    return DEFAULT_BORDER_RIGHT;
  }

  protected int getMaxWidth(Event event, int columnWith) {
    final int textWidth = event.gc.textExtent(MAX_PERCENTAGE_STRING).x;
    final int max = columnWith - getBorderLeft() - getBorderRight()
        - textWidth;
    return Math.max(0, max);
  }

  protected int getFontHeight(Event event) {
    GC gc = event.gc;
    return gc.getFontMetrics().getAscent()
        - gc.getFontMetrics().getDescent()
        + gc.getFontMetrics().getLeading();
  }

}