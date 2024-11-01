/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs.FieldTitle;

/**
 * Inspired by https://stackoverflow.com/users/522444/hovercraft-full-of-eels
 * Thanks for the template!
 */
@SuppressWarnings("serial")
public class JSettingsPanel extends JPanel
{
   private static final Insets         WEST_INSETS = new Insets(5, 0, 5, 5);
   private static final Insets         EAST_INSETS = new Insets(5, 5, 5, 0);
   private Map<FieldTitle, JTextField> fieldMap    = new HashMap<FieldTitle, JTextField>();
   private HashMap<FieldTitle,String> m_values = null;

   public JSettingsPanel()
   {
      m_values = UtilPrefs.getAllValues();
      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Settings Editor"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
      GridBagConstraints gbc;
      for (int i = 0; i < FieldTitle.values().length; i++)
      {
         FieldTitle fieldTitle = FieldTitle.values()[i];
         JLabel label = new JLabel(fieldTitle.getTitle() + ":", JLabel.LEFT);
         JTextField textField = new JTextField(fieldTitle.getLength());
         textField.setText(m_values.get(fieldTitle));
         label.setDisplayedMnemonic(fieldTitle.getMnemonic());
         label.setLabelFor(textField);
         gbc = createGbc(0, i);
         add(label, gbc);
         gbc = createGbc(1, i);
         add(textField, gbc);

         fieldMap.put(fieldTitle, textField);
      }
   }

   private GridBagConstraints createGbc(int x, int y)
   {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = x;
      gbc.gridy = y;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;

      gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.WEST;
      gbc.fill = (x == 0) ? GridBagConstraints.BOTH : GridBagConstraints.NONE;

      gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
      gbc.weightx = (x == 0) ? 0.1 : 1.0;
      gbc.weighty = 1.0;
      return gbc;
   }

   public void updatePrefs()
   {
      HashMap<FieldTitle,String> values = new HashMap <FieldTitle,String>();
      for (FieldTitle fieldTitle : FieldTitle.values())
      {
         values.put(fieldTitle, getFieldText(fieldTitle));
      }
      UtilPrefs.setAllValues(values);
   }

   public String getFieldText(FieldTitle fieldTitle)
   {
      return fieldMap.get(fieldTitle).getText();
   }

}
