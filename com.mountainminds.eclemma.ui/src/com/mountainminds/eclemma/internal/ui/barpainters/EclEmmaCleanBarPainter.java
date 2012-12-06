package com.mountainminds.eclemma.internal.ui.barpainters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * Paints using EclEmma schema colors without gradients
 */
public class EclEmmaCleanBarPainter extends BarPainter {

  final Color missedColor;
  final Color coveredColor;

  public EclEmmaCleanBarPainter() {
    Display display = Display.getCurrent();
    missedColor = new Color(display, 161, 0, 0);
    coveredColor = new Color(display, 17, 174, 1);
  }

  @Override
  protected void paintBar(Event event, Paint type, int xOffset, int width) {
    // Calculate positions
    final int cellHeight = event.getBounds().height;
    final int fontHeight = getFontHeight(event);
    final int margin = (cellHeight - fontHeight) / 2;
    final int textBase = cellHeight - margin;

    final int height = (int) (fontHeight * 0.75);
    final int yBar = event.getBounds().y + textBase - height - 1;
    final int xBar = event.x + xOffset + getBorderLeft();

    // Fill
    final Color color = type.equals(Paint.MISSED) ? missedColor : coveredColor;
    GC gc = event.gc;
    gc.setBackground(color);
    gc.fillRectangle(xBar, yBar, width, height);

    // Border
    gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
    gc.drawRectangle(xBar, yBar, width, height);
  }

  @Override
  public void dispose() {
    super.dispose();
    missedColor.dispose();
    coveredColor.dispose();
  }
}
