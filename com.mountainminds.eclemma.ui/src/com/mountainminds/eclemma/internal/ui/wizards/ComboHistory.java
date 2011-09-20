/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Combo;

/**
 * Utility class for saving/restoring the history of entered text strings in a
 * combo box widget.
 */
public class ComboHistory {

  /**
   * Maximum number of history items.
   */
  public static final int HISTORY_LIMIT = 10;

  /**
   * Restores the items of a combo box.
   * 
   * @param settings
   *          dialog setting used to persist the history
   * @param key
   *          key used for this combo box
   * @param combo
   *          the combo box
   */
  public static void restore(IDialogSettings settings, String key, Combo combo) {
    String[] destinations = settings.getArray(key);
    if (destinations != null) {
      combo.setItems(destinations);
      if (destinations.length > 0) {
        combo.setText(destinations[0]);
      }
    }
  }

  /**
   * Saves the items of the given combo box as its history. The current text
   * value is added as the most recent history item. The numer of history items
   * is limited.
   * 
   * @param settings
   *          dialog setting used to persist the history
   * @param key
   *          key used for this combo box
   * @param combo
   *          the combo box
   */
  public static void save(IDialogSettings settings, String key, Combo combo) {
    List history = new ArrayList(Arrays.asList(combo.getItems()));
    history.remove(combo.getText());
    history.add(0, combo.getText());
    if (history.size() > HISTORY_LIMIT) {
      history = history.subList(0, HISTORY_LIMIT);
    }
    settings.put(key, (String[]) history.toArray(new String[0]));
  }

}
