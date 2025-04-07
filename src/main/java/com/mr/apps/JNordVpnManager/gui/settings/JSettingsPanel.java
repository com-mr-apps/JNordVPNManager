/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JIntegerStepValField;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;

/**
 * Common class to create a settings panel<p>
 * Inspired by https://stackoverflow.com/users/522444/hovercraft-full-of-eels<br>
 * Thanks for the template!
 */
@SuppressWarnings("serial")
public class JSettingsPanel extends JPanel
{
   private static final Insets              WEST_INSETS        = new Insets(5, 0, 5, 5);
   private static final Insets              EAST_INSETS        = new Insets(5, 5, 5, 0);
   private Map<String, JSettingsPanelField> m_hmSettingsFields = null;
   private HashMap<String, String>          m_hmSettingsValues = null;
   private String                           m_title;

   public JSettingsPanel(String title, Map<String, JSettingsPanelField> hmSettingsFields, HashMap<String,String> hmSettingsValues)
   {
      m_title = title;
      m_hmSettingsFields = hmSettingsFields;
      m_hmSettingsValues = hmSettingsValues;
      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Settings Editor"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
      GridBagConstraints gbc;

      int iFieldNb = 0;
      SortedSet<String> keys = new TreeSet<>(hmSettingsFields.keySet());
      for (String key : keys)
      {
         JSettingsPanelField settingsField = hmSettingsFields.get(key);
         if (null == settingsField)
         {
            // skip (internal) dataset value w/o panel field
//            Starter._m_logError.TraceDebug("Skip internal data (no Panel Field): " + key);
            continue;
         }

         String valueField = hmSettingsValues.get(key);
         if (null == valueField)
         {
            // Skip Panel field with no value
//            Starter._m_logError.TraceDebug("Skip Panel Field (no value): " + settingsField.toString());
            continue;
         }

//         Starter._m_logError.TraceDebug("Create Panel Field: " + settingsField.toString());

         JLabel label = new JLabel(settingsField.getLabel() + ":", JLabel.LEFT);
         gbc = createGbc(0, iFieldNb);
         add(label, gbc);
         gbc = createGbc(1, iFieldNb);
         if (settingsField.getElementType().startsWith("T"))
         {
            JTextField textField = new JTextField(settingsField.getLength());
            textField.setText(valueField);
            if (settingsField.getMnemonic() > 0) label.setDisplayedMnemonic(settingsField.getMnemonic());
            label.setLabelFor(textField);
            add(textField, gbc);
            settingsField.setJPanelComponent(textField);
         }
         else if (settingsField.getElementType().startsWith("B"))
         {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(valueField.matches("1|true|enable|on|enabled"));
            if (settingsField.getMnemonic() > 0) label.setDisplayedMnemonic(settingsField.getMnemonic());
            label.setLabelFor(checkBox);
            add(checkBox, gbc);
            settingsField.setJPanelComponent(checkBox);
         }
         else if (settingsField.getElementType().startsWith("N"))
         {
            JIntegerStepValField textField = new JIntegerStepValField(null, null, settingsField.getElementType());
            textField.setText(valueField);
            if (textField.hasMinMaxValues()) textField.setEditable(false);
            if (settingsField.getMnemonic() > 0) label.setDisplayedMnemonic(settingsField.getMnemonic());
            label.setLabelFor(textField);
            add(textField.getJPanel(), gbc);
            settingsField.setJPanelComponent(textField);
         }
         else if (settingsField.getElementType().startsWith("L"))
         {
            String[] saList = getList(settingsField.getElementType());
            if (null == saList)
            {
               Starter._m_logError.LoggingError(10500, "Error in List definition", "List values cannot be parsed. Check definition: " + settingsField.getElementType());
            }
            else
            {
               JList<String> listField = new JList<String>(saList);
               listField.setLayoutOrientation(JList.HORIZONTAL_WRAP);
               listField.setVisibleRowCount(1);
               listField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
               int idx = getListIndex(saList, valueField);
               listField.setSelectedIndex(idx);
               if (settingsField.getMnemonic() > 0) label.setDisplayedMnemonic(settingsField.getMnemonic());
               label.setLabelFor(listField);
               add(listField, gbc);
               settingsField.setJPanelComponent(listField);
            }
         }
         else
         {
            Starter._m_logError.LoggingAbend(10997,
                  "Invalid Field Type",
                  "The field type '" + settingsField.getElementType() + "' is not implemented yet. Please open a Issue/Bug report.");
         }

         // Reset button
         JButton jbReset = new JButton(JResizedIcon.getIcon(JResizedIcon.IconUrls.ICON_SETTINGS_RESET, JResizedIcon.IconSize.SMALL));
         jbReset.setToolTipText("Reset the field to its default value.");
         jbReset.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               setSettingValue(key, settingsField.getJPanelComponent(), null);
            }
         });
         gbc = createGbc(2, iFieldNb);
         add(jbReset, gbc);

         iFieldNb++;
      }
   }

   private GridBagConstraints createGbc(int x, int y)
   {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = x;
      gbc.gridy = y;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;

      gbc.anchor = (x != 1) ? GridBagConstraints.WEST : GridBagConstraints.WEST;
      gbc.fill = (x != 1) ? GridBagConstraints.BOTH : GridBagConstraints.NONE;

      gbc.insets = (x != 1) ? WEST_INSETS : EAST_INSETS;
      gbc.weightx = (x != 1) ? 0.1 : 1.0;
      gbc.weighty = 1.0;
      return gbc;
   }

   /**
    * Get all current Dataset values
    * @return the complete current dataset values
    */
   public HashMap<String, String> getAllValues()
   {
      HashMap<String,String> values = new HashMap <String,String>();
      for (HashMap.Entry<String, JSettingsPanelField> entry : m_hmSettingsFields.entrySet())
      {
         String key = entry.getKey();
         JSettingsPanelField fieldClass = entry.getValue();
         if (null == fieldClass)
         {
            // internal dataset value w/o panel field
            values.put(key, m_hmSettingsValues.get(key));
         }
         else
         {
            // get the current panel field value
            values.put(key, getFieldText(fieldClass));
         }
      }
      return values;
   }

   /**
    * Set a panel field with a value
    * 
    * @param key
    *           is the value key
    * @param field
    *           is the panel field component
    * @param value
    *           is the value. If null - the data set default value is used
    */
   private void setSettingValue(String key, Object field, String value)
   {
      if (null == field) return;

      JSettingsPanelField fieldClass = m_hmSettingsFields.get(key);
      if (value == null) value = fieldClass.getDefaultValue();

      if (fieldClass.getElementType().startsWith("T"))
      {
         ((JTextField) field).setText(value);
      }
      else if (fieldClass.getElementType().startsWith("B"))
      {
         ((JCheckBox) field).setSelected(value.matches("1|true|enable|on|enabled"));
      }
      else if (fieldClass.getElementType().startsWith("N"))
      {
         ((JIntegerStepValField) field).setText(value);
      }
      else if (fieldClass.getElementType().startsWith("L"))
      {
         int idx = getListIndex(getList(fieldClass.getElementType()), value);
         ((JList<?>) field).setSelectedIndex(idx);
      }
      else
      {
         // should not happen
         Starter._m_logError.LoggingAbend(10997,
               "Invalid Field Type",
               "The field type '" + fieldClass.getElementType() + "' is not implemented yet. Please open a Issue/Bug report.");
         return;
      }
      
   }

   /**
    * Set all panel fields with values from a data set
    * 
    * @param hm
    *           is the data set
    */
   public void setAllSettingValues(HashMap<String, String> hm)
   {
      Starter._m_logError.TraceDebug("Refresh " + m_title + " settings panel values.");
      for (HashMap.Entry<String, JSettingsPanelField> entry : m_hmSettingsFields.entrySet())
      {
         String key = entry.getKey();
         JSettingsPanelField entryValue = entry.getValue();
         if (null == entryValue)
         {
            // set internal data set value with current value (e.g. from import)
            m_hmSettingsValues.put(key, hm.get(key));
         }
         else
         {
            Object textField = entryValue.getJPanelComponent();
            String value = null;
            if (null != hm)
            {
               value = hm.get(key);
            }
            setSettingValue(key, textField, value);
         }
      }
   }

   /**
    * Helper to get a component specific field value
    * 
    * @param fieldClass
    *           is the panel field class
    * @return the panel field text value
    */
   private String getFieldText(JSettingsPanelField fieldClass)
   {
      Object field = fieldClass.getJPanelComponent();
      if (null == field) return null;

      if (fieldClass.getElementType().startsWith("T"))
      {
         return ((JTextField) field).getText();
      }
      else if (fieldClass.getElementType().startsWith("B"))
      {
         return (((JCheckBox) field).isSelected()) ? "1" : "0";
      }
      else if (fieldClass.getElementType().startsWith("N"))
      {
         return ((JIntegerStepValField) field).getText();
      }
      else if (fieldClass.getElementType().startsWith("L"))
      {
         return ((JList<?>) field).getSelectedValue().toString();
      }
      else
      {
         // should not happen
         Starter._m_logError.LoggingAbend(10997,
               "Invalid Field Type",
               "The field type '" + fieldClass.getElementType() + "' is not implemented yet. Please open a Issue/Bug report.");
         return null;
      }
   }
   
   /**
    * Helper to get the list options from the list definition
    * 
    * @param def
    *           is the list definition in form L[l1,l2,l3,...]
    * @return the list in form {"l1","l2","l3",...}
    */
   private String[] getList(String def)
   {
      Pattern pattern = Pattern.compile("L\\[([^]]*)\\]", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(def);
      boolean matchFound = matcher.find();
      if (matchFound)
      {
         return matcher.group(1).split(",");
      }
      return null;
   }

   /**
    * Helper to get the list index from the list
    * 
    * @param list
    *           is the list in form {"l1","l2","l3",...}
    * @param value
    *           is the list value, e.g. "l2"
    * @return the index of the value in the list (starting at 0)
    */
   private int getListIndex(String[] list, String value)
   {
      int idx = 0;
      for (String val : list)
      {
         if (val.equals(value)) return idx;
         idx++;
      }
      return 0;
   }
}
