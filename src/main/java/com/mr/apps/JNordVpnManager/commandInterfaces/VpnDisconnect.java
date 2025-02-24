/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces;

import java.awt.event.ActionEvent;

import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;

public class VpnDisconnect extends CoreCommandClass
{
   public static boolean execute(ActionEvent e)
   {
      NvpnCallbacks.executeDisConnect("NordVPN Disconnect", "NordVPN Disconnect");
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      return true;
   }
}
