/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces.base;

import javax.swing.ImageIcon;

import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;

/**
 * Dataset for Command Gadgets.
 * <p>
 * Contains the Properties for a Command Gadget:
 * <ul>
 * <li>Label text</li>
 * <li>ImageIcon</li>
 * <li>ToolTip</li>
 * </ul>
 */
public class CommandGadgetProperties
{
   // members
   private String                               m_label;
   private String                               m_toolTip;
   private JResizedIcon.IconUrls                m_iconUrl;
   private ImageIcon                            m_imageIcon;
   private Boolean                              m_enabled;

   // constructor
   public CommandGadgetProperties(String label, String toolTip, Boolean enabled, JResizedIcon.IconUrls iconUrl, JResizedIcon.IconSize iconSize)
   {
      this.m_label = label;
      this.m_toolTip = toolTip;
      this.m_iconUrl = iconUrl;
      this.m_imageIcon = JResizedIcon.getIcon(iconUrl, iconSize);
      this.m_enabled = enabled;
   }

   // data access methods
   public String getLabel()
   {
      return m_label;
   }

   public String getToolTip()
   {
      return m_toolTip;
   }

   public void setToolTip(String sToolTip)
   {
      this.m_toolTip = sToolTip;
   }

   public JResizedIcon.IconUrls getIconUrl()
   {
      return m_iconUrl;
   }

   public ImageIcon getImageIcon()
   {
      return m_imageIcon;
   }

   public Boolean isEnabled()
   {
      return m_enabled;
   }

   public String toString()
   {
      return m_label + " ToolTip=" + m_toolTip + " Icon=" + m_iconUrl.toString(); 
   }
}
