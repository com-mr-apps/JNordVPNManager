/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces;

import java.awt.event.ActionEvent;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.GuiStatusLine;
import com.mr.apps.JNordVpnManager.gui.connectLine.JPauseSlider;

public class VpnPause extends CoreCommandClass
{
   public static boolean execute(ActionEvent e)
   {
      JPauseSlider.syncStatusForTimer(Starter.STATUS_CONNECTED);
      GuiStatusLine.setStatusLine(Starter.STATUS_PAUSED, JPauseSlider.syncStatusForTimer(Starter.STATUS_PAUSED));
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      return true;
   }
}
