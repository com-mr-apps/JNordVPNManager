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

public class CommandButton extends Command
{
   /**
    * Simple Command Button with Icon and ToolTip.<p>
    * Uses CommandGadgetProperties.
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
   public CommandButton(String id, JResizedIcon.IconUrls iconUrl, String toolTip, Boolean enabled, String command)
   {
      super (id, 
            TYPE_BUTTON,
            iconUrl, 
            toolTip,
            enabled,
            command);
   }

   /**
    * Command Button with different states
    * 
    * @param id
    *           is the unique command Id
    * @param statusIds
    *           is the list of state-ids. First Id must have the name "INIT" (State used for command menu list)
    * @param statusLabels
    *           is the list of labels per state (can be null)
    * @param statusToolTips
    *           is the list of tool tips per state
    * @param statusIcons
    *           is the list of icons per state
    * @param command
    *           is the command method name
    */
   public CommandButton(String id, String[] statusIds, String[] statusLabels, String[] statusToolTips, Boolean[] enabled, JResizedIcon.IconUrls[] statusIcons, String command)
   {
      super (id, TYPE_BUTTON, command);

      if (null == statusIds || null == statusLabels || null == statusToolTips || null == statusIcons || null == enabled)
      {
         Starter._m_logError.TraceErr("[Internal Error (CommandButton): Check calling arguments (null pointer)!");
         return;
      }
      if (statusIds.length != statusLabels.length || statusIds.length != statusToolTips.length || statusIds.length != statusIcons.length || statusIds.length != enabled.length)
      {
         Starter._m_logError.TraceErr("[Internal Error (CommandButton): Check calling arguments (length different)!");
         return;
      }

      m_statusIds = statusIds;
      m_properties = new HashMap<String, CommandGadgetProperties>();
      int idx = 0;
      for (String statusId : statusIds)
      {
         CommandGadgetProperties cgp = new CommandGadgetProperties(statusLabels[idx], statusToolTips[idx], enabled[idx], statusIcons[idx], BUTTON_ICON_SIZE);
         m_properties.put(statusId, cgp);
         if (statusId.equals(BASE_STATUS_KEY)) m_baseProperties = cgp;
         idx++;
      }
   }

}
