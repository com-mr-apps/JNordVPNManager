/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces.base;

import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;

/**
 * Enhanced Dataset for Command ComboBox Gadgets.
 * <p>
 * Contains the Properties for a Command Gadget and:
 * <ul>
 * <li>n/a</li>
 * </ul>
 */
public class CommandComboBoxProperties extends CommandGadgetProperties
{

   public CommandComboBoxProperties(String label, String toolTip, Boolean enabled, IconUrls iconUrl, IconSize iconSize)
   {
      super(label, toolTip, enabled, iconUrl, iconSize);
   }
}
