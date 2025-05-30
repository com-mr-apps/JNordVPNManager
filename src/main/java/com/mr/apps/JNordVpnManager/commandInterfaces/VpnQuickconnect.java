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

import javax.swing.JButton;

import com.mr.apps.JNordVpnManager.commandInterfaces.base.Command;
import com.mr.apps.JNordVpnManager.commandInterfaces.base.CommandInterface;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCommands;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class VpnQuickconnect implements CommandInterface
{
   public static boolean execute(ActionEvent e)
   {
      String msg = NvpnCommands.connect(null);
      if (UtilSystem.isLastError())
      {
         // KO
         msg = UtilSystem.getLastError();
         JModalDialog.showError("NordVPN Connect", msg);
      }
      else
      {
         // OK
         JModalDialog.showMessage("NordVPN Connect", msg);
      }
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      JButton button = (JButton)cmd.getComponent();
      if (null != button)
      {
         cmd.updateCommandGadgetUI();
      }
      return true;
   }
}
