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

import javax.swing.JCheckBox;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.base.Command;
import com.mr.apps.JNordVpnManager.commandInterfaces.base.CommandInterface;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * Command VPN Settings - Post-Quantum
 */
public class VpnSetPostQuantum implements CommandInterface
{
   public static Object get()
   {
      return (StringFormat.string2boolean(Starter.getCurrentSettingsData().getPostQuantum(false)));
   }

   public static boolean execute(ActionEvent e)
   {
      JCheckBox cb = (JCheckBox) e.getSource();
      if (null != cb)
      {
         if (cb.isSelected())
         {
            Starter.getCurrentSettingsData().setPostQuantum("true", false);
         }
         else
         {
            Starter.getCurrentSettingsData().setPostQuantum("false", false);
         }
         Starter.updateStatusLine();
      }
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      JCheckBox cb = (JCheckBox)cmd.getComponent();
      if (null != cb)
      {
         if (null != cb)
         {
            // The feature is not compatible with a dedicated IP, Meshnet, and OpenVPN/NORDWHISPER.
            if ((true == Starter.getCurrentAccountData(false).isVpnDedicatedIdIsActive()) ||
                (false == Starter.getCurrentSettingsData().getTechnology(false).equals("NORDLYNX")) ||
                (true == StringFormat.string2boolean(Starter.getCurrentSettingsData().getMeshnet(false))))
            {
               cmd.setStatusUI(Command.DISABLED_STATUS_KEY);
            }
            else
            {
               cmd.setStatusUI(((true == (boolean)get()) ? "TRUE" : "FALSE"));
            }
            cmd.updateCommandGadgetUI();
         }
      }
      return true;
   }

}
