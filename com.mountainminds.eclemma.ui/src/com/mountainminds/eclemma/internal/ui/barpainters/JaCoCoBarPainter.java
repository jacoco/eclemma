package com.mountainminds.eclemma.internal.ui.barpainters;

import org.eclipse.swt.widgets.Event;

import com.mountainminds.eclemma.internal.ui.EclEmmaUIPlugin;

public class JaCoCoBarPainter extends BarPainter {

  @Override
  protected void paintBar(Event event, Paint type, int xOffset, int width) {
    // Calculate positions
    final int cellHeight = event.getBounds().height;
    final int fontHeight = getFontHeight(event);
    final int margin = (cellHeight - fontHeight) / 2;
    final int textBase = cellHeight - margin;

    final int yBar = event.getBounds().y + textBase - fontHeight;

    final String imageKey = type.equals(Paint.MISSED) ?
        EclEmmaUIPlugin.DGM_REDBAR : EclEmmaUIPlugin.DGM_GREENBAR;

    event.gc.drawImage(EclEmmaUIPlugin.getImage(imageKey), 0, 0, 1, 10,
        event.x + xOffset + getBorderLeft(), yBar, width, fontHeight);
  }
}
