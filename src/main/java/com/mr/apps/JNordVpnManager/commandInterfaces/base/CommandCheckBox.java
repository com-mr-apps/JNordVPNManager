/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces.base;

import java.util.HashMap;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;

public class CommandCheckBox extends Command
{
   /**
    * Simple Command CheckBox with Icon and ToolTip for BASE_STATUS_KEY.<p>
    * Uses CommandCheckBoxProperties.
    * 
    * @param id
    *           is the unique command Id
    * @param iconUrl
    *           is the icon URL
    * @param toolTip
    *           is the tool tip
    * @param command
    *           is the command method name
    */
   public CommandCheckBox(String id, JResizedIcon.IconUrls iconUrl, String toolTip, Boolean enabled, Boolean selected, String command)
   {
      super (id, TYPE_CHECKBOX, command);

      String statusId = BASE_STATUS_KEY;
      m_statusIds = new String[] {statusId};

      CommandCheckBoxProperties ccbp = new CommandCheckBoxProperties(null, toolTip, enabled, selected, iconUrl, BUTTON_ICON_SIZE);
      m_baseProperties = ccbp;

      m_properties = new HashMap<String, CommandGadgetProperties>();
      m_properties.put(statusId, m_baseProperties);
   }

   /**
    * Command CheckBox with two states (TRUE/FALSE) [and DISABLED]
    * 
    * @param id
    *           is the unique command Id
    * @param iconUrl
    *           is the icon for each state
    * @param toolTip
    *           is the tool tip
    * @param command
    *           is the command method name
    */
   public CommandCheckBox(String id, JResizedIcon.IconUrls iconUrl, String toolTip, String command)
   {
      this (id, 
            new String[] { BASE_STATUS_KEY, "TRUE" , "FALSE", DISABLED_STATUS_KEY}, // keys
            new String[] { null, null, null, null }, // labels (no)
            new String[] { toolTip, "Click CheckBox to deactivate " + toolTip, "Click CheckBox to activate " + toolTip, "Disabled Command " + toolTip },
            new Boolean[] {true, true, true, false }, // enabled
            new Boolean[] {null, true, false, null }, // selected
            new JResizedIcon.IconUrls[] {iconUrl, iconUrl, iconUrl, iconUrl}, // icons
            command);
   }

   /**
    * Command CheckBox with different states
    * 
    * @param id
    *           is the unique command Id
    * @param statusIds
    *           is the list of state-ids. First Id must have the name "INIT" (State used for command menu list)
    * @param statusLabels
    *           is the list of labels per state (can be null)
    * @param statusToolTips
    *           is the list of tool tips per state
    * @param statusEnabled
    *           is the list of enabled per state
    * @param statusSelected
    *           is the list of selected per state
    * @param statusIcons
    *           is the list of icons per state
    * @param command
    *           is the command method name
    */
   public CommandCheckBox(String id, String[] statusIds, String[] statusLabels, String[] statusToolTips, Boolean[] statusEnabled, Boolean[] statusSelected, JResizedIcon.IconUrls[] statusIcons, String command)
   {
      super (id, TYPE_CHECKBOX, command);

      if (null == statusIds || null == statusLabels || null == statusToolTips || null == statusIcons || null == statusEnabled || null == statusSelected)
      {
         Starter._m_logError.TraceErr("[Internal Error (CommandCheckBox): Check calling arguments (null pointer)!");
         return;
      }
      if (statusIds.length != statusLabels.length || statusIds.length != statusToolTips.length || statusIds.length != statusIcons.length || statusIds.length != statusEnabled.length || statusIds.length != statusSelected.length)
      {
         Starter._m_logError.TraceErr("[Internal Error (CommandCheckBox): Check calling arguments (length different)!");
         return;
      }

      m_statusIds = statusIds;
      m_properties = new HashMap<String, CommandGadgetProperties>();
      int idx = 0;
      for (String statusId : statusIds)
      {
         CommandCheckBoxProperties cgp = new CommandCheckBoxProperties(statusLabels[idx], statusToolTips[idx], statusEnabled[idx], statusSelected[idx], statusIcons[idx], BUTTON_ICON_SIZE);
         m_properties.put(statusId, cgp);
         if (statusId.equals(BASE_STATUS_KEY)) m_baseProperties = cgp;
         idx++;
      }
   }

   public Boolean isSelected(String statusId)
   {
      if (null == m_properties) return null;
      return ((CommandCheckBoxProperties)m_properties.get(statusId.toUpperCase())).isSelected();
   }

   /**
    * Method to overwrite a CheckBox Tool Tip for the basis state
    * 
    * @param sToolTip
    *           is the Tool Tip
    */
   public void setToolTip(String sToolTip)
   {
      setToolTip(BASE_STATUS_KEY, sToolTip);
   }

   /**
    * Method to overwrite a CheckBox Tool Tip for a specific state
    * 
    * @param statusId
    *           is the state Id
    * @param sToolTip
    *           is the Tool Tip
    */
   public void setToolTip(String statusId, String sToolTip)
   {
      if (null == m_properties) return;
      CommandCheckBoxProperties cp = (CommandCheckBoxProperties)m_properties.get(statusId.toUpperCase());
      cp.setToolTip(sToolTip);
      m_properties.put(statusId, cp);
      if (statusId.equalsIgnoreCase(BASE_STATUS_KEY)) m_baseProperties = cp;
   }
}
