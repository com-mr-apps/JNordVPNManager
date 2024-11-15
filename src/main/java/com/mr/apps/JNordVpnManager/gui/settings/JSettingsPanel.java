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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JIntegerStepValField;
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
   private Map<FieldTitle, Object> fieldMap    = new HashMap<FieldTitle, Object>();
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
         JLabel label = new JLabel(fieldTitle.getLabel() + ":", JLabel.LEFT);
         gbc = createGbc(0, i);
         add(label, gbc);
         gbc = createGbc(1, i);
         if (fieldTitle.getElementType().startsWith("T"))
         {
            JTextField textField = new JTextField(fieldTitle.getLength());
            textField.setText(m_values.get(fieldTitle));
            if (fieldTitle.getMnemonic() > 0) label.setDisplayedMnemonic(fieldTitle.getMnemonic());
            label.setLabelFor(textField);
            add(textField, gbc);
            fieldMap.put(fieldTitle, textField);
         }
         else if (fieldTitle.getElementType().startsWith("B"))
         {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(m_values.get(fieldTitle).equals("1"));
            if (fieldTitle.getMnemonic() > 0) label.setDisplayedMnemonic(fieldTitle.getMnemonic());
            label.setLabelFor(checkBox);
            add(checkBox, gbc);
            fieldMap.put(fieldTitle, checkBox);
         }
         else if (fieldTitle.getElementType().startsWith("N"))
         {
            int minMax[] = getMinMax(fieldTitle.getElementType());
            JIntegerStepValField textField = new JIntegerStepValField(null, null, minMax[0], minMax[1], 1);
            textField.setText(m_values.get(fieldTitle));
            textField.setEditable(false);
            if (fieldTitle.getMnemonic() > 0) label.setDisplayedMnemonic(fieldTitle.getMnemonic());
            label.setLabelFor(textField);
            add(textField.getJPanel(), gbc);
            fieldMap.put(fieldTitle, textField);
         }
         else
         {
            Starter._m_logError.TranslatorAbend(10997,
                  "Invalid Element Type.",
                  "The element type '" + fieldTitle.getElementType() + "' is not implemented. Software implementation error, Program is terminating!");
         }
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
      if (fieldTitle.getElementType().startsWith("T"))
      {
         return ((JTextField) (fieldMap.get(fieldTitle))).getText();
      }
      else if (fieldTitle.getElementType().startsWith("B"))
      {
         return (((JCheckBox) (fieldMap.get(fieldTitle))).isSelected()) ? "1" : "0";
      }
      else if (fieldTitle.getElementType().startsWith("N"))
      {
         return ((JTextField) (fieldMap.get(fieldTitle))).getText();
      }

      // should not happen
      return null;
   }

   private int[] getMinMax(String def)
   {
      int minMax[] = {0,0};
      
      Pattern pattern = Pattern.compile("N\\[([+-]?\\d+),([+-]?\\d+)\\]", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(def);
      boolean matchFound = matcher.find();
      if (matchFound)
      {
         minMax[0] = Integer.valueOf(matcher.group(1));
         minMax[1] = Integer.valueOf(matcher.group(2));
      }

      return minMax;
   }
}
