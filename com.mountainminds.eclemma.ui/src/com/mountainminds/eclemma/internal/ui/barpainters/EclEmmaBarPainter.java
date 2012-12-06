package com.mountainminds.eclemma.internal.ui.barpainters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * Paints using EclEmma schema colors without gradients
 */
public class EclEmmaBarPainter extends BarPainter {

  final private Color missedColorBright;
  final private Color missedColorDark;
  final private Color coveredColorBright;
  final private Color coveredColorDark;

  public EclEmmaBarPainter() {
    final Display display = Display.getCurrent();
    missedColorBright = new Color(display, 186, 0, 0);
    missedColorDark = new Color(display, 161, 0, 0);
    coveredColorBright = new Color(display, 20, 210, 0);
    coveredColorDark = new Color(display, 17, 174, 1);
  }

  @Override
  protected void paintBar(Event event, Paint type, int xOffset, int width) {
    // Calculate positions
    final int cellHeight = event.getBounds().height;
    final int fontHeight = getFontHeight(event);
    final int margin = (int) Math.round((cellHeight - fontHeight) / 2d);
    final int textBase = cellHeight - margin;

    final int height = (int) (fontHeight * 0.75);
    final int halfHeight = height / 2;
    final int yBar = event.getBounds().y + textBase - height - 1;
    final int xBar = event.x + xOffset + getBorderLeft();

    // Colors
    Color colorBright = null;
    Color colorDark = null;
    if (type.equals(Paint.MISSED)) {
      colorBright = missedColorBright;
      colorDark = missedColorDark;
    } else {
      colorBright = coveredColorBright;
      colorDark = coveredColorDark;
    }

    final GC gc = event.gc;
    // Fill top
    gc.setForeground(colorBright);
    gc.setBackground(colorDark);
    gc.fillGradientRectangle(xBar, yBar, width, halfHeight + 1, true);
    // Fill bottom
    gc.setForeground(colorDark);
    gc.setBackground(colorBright);
    gc.fillGradientRectangle(xBar, yBar + halfHeight + 1, width, halfHeight,
        true);
    // Border
    final Color borderColor = Display.getCurrent()
        .getSystemColor(SWT.COLOR_DARK_GRAY);
    gc.setForeground(borderColor);
    gc.drawRectangle(xBar, yBar, width, height);
  }

  @Override
  public void dispose() {
    super.dispose();
    missedColorBright.dispose();
    missedColorDark.dispose();
    coveredColorBright.dispose();
    coveredColorDark.dispose();
  }

}
