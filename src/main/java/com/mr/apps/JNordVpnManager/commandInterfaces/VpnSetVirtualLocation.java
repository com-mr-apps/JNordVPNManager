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

import javax.swing.JCheckBox;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnSettingsData;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * Command VPN Settings - Virtual-Location
 */
public class VpnSetVirtualLocation extends CoreCommandClass
{
   public static Object get()
   {
      return (StringFormat.string2boolean(Starter.getCurrentSettingsData().getVirtualLocation(false)));
   }

   public static boolean execute(ActionEvent e)
   {
      JCheckBox cb = (JCheckBox) e.getSource();
      if (null != cb)
      {
         if (cb.isSelected())
         {
            Starter.getCurrentSettingsData().setVirtualLocation("true", false);
         }
         else
         {
            Starter.getCurrentSettingsData().setVirtualLocation("false", false);
         }
         Starter.updateFilterTreeCB(false);
         if (NvpnSettingsData.reconnectRequired()) Starter.updateCurrentServer(); // tbd.. see setVirtualLocation()..
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
            cb.setSelected((boolean)get());
         }
      }
      return true;
   }

}
