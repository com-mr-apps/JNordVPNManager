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

import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

/**
 * Command User Preferences - Auto Disconnect on Program Exit
 */
public class AppPrefAutoDisconnect extends CoreCommandClass
{
   public static Object get()
   {
      return (1 == UtilPrefs.getAutoDisConnectMode());
   }

   public static boolean execute(ActionEvent e)
   {
      JCheckBox cb = (JCheckBox) e.getSource();
      if (cb.isSelected())
      {
         UtilPrefs.setAutoDisConnectMode(1);
      }
      else
      {
         UtilPrefs.setAutoDisConnectMode(0);
      }
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      JCheckBox cb = (JCheckBox)cmd.getComponent();
      int iAutoDisconnect = UtilPrefs.getAutoDisConnectMode();
      if (1 == iAutoDisconnect)
      {
         cb.setSelected(true);
      }
      else
      {
         cb.setSelected(false);
      }
      return true;
   }

}
