/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * Utility class for saving/restoring the history of widget state in dialogs.
 */
public final class WidgetHistory {

  /**
   * Maximum number of history items.
   */
  private static final int HISTORY_LIMIT = 10;

  /**
   * Restores the value of a text field. If no value was persisted before the
   * preset value is used.
   * 
   * @param settings
   *          dialog setting used to persist the value
   * @param key
   *          key used for this text field
   * @param text
   *          text field
   * @param preset
   *          value used if no value was persisted before
   */
  public static void restoreText(IDialogSettings settings, String key,
      Text text, String preset) {
    final String value = settings.get(key);
    text.setText((value == null || value.length() == 0) ? preset : value);
  }

  /**
   * Saves the current value of a text field.
   * 
   * @param settings
   *          dialog setting used to persist the value
   * @param key
   *          key used for this text field
   * @param text
   *          text field
   */
  public static void saveText(IDialogSettings settings, String key, Text text) {
    settings.put(key, text.getText());
  }

  /**
   * Restores the selection state of a radio group. If no value was persisted
   * the first button is selected.
   * 
   * @param settings
   *          dialog setting used to persist the status
   * @param key
   *          key used for this radio group
   * @param radios
   *          buttons of the radio group
   */
  public static void restoreRadio(IDialogSettings settings, String key,
      Button... radios) {
    int idx;
    try {
      idx = settings.getInt(key);
    } catch (NumberFormatException e) {
      idx = 0;
    }
    if (idx < 0 || idx >= radios.length) {
      idx = 0;
    }
    radios[idx].setSelection(true);
  }

  /**
   * Saves the selection status of a radio group.
   * 
   * @param settings
   *          dialog setting used to persist the status
   * @param key
   *          key used for this radio group
   * @param radios
   *          buttons of the radio group
   */
  public static void saveRadio(IDialogSettings settings, String key,
      Button... radios) {
    for (int idx = 0; idx < radios.length; idx++) {
      if (radios[idx].getSelection()) {
        settings.put(key, idx);
        break;
      }
    }
  }

  /**
   * Restores the selection state of a check box.
   * 
   * @param settings
   *          dialog setting used to persist the status
   * @param check
   *          check box
   */
  public static void restoreCheck(IDialogSettings settings, String key,
      Button check) {
    check.setSelection(settings.getBoolean(key));
  }

  /**
   * Saves the selection status of a check box.
   * 
   * @param settings
   *          dialog setting used to persist the status
   * @param key
   *          key used for this check box
   * @param check
   *          check box
   */
  public static void saveCheck(IDialogSettings settings, String key,
      Button check) {
    settings.put(key, check.getSelection());
  }

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
  public static void restoreCombo(IDialogSettings settings, String key,
      Combo combo) {
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
   * value is added as the most recent history item. The number of history items
   * is limited.
   * 
   * @param settings
   *          dialog setting used to persist the history
   * @param key
   *          key used for this combo box
   * @param combo
   *          the combo box
   */
  public static void saveCombo(IDialogSettings settings, String key, Combo combo) {
    List<String> history = new ArrayList<String>(
        Arrays.asList(combo.getItems()));
    history.remove(combo.getText());
    history.add(0, combo.getText());
    if (history.size() > HISTORY_LIMIT) {
      history = history.subList(0, HISTORY_LIMIT);
    }
    settings.put(key, history.toArray(new String[history.size()]));
  }

  private WidgetHistory() {
    // no instances
  }

}
