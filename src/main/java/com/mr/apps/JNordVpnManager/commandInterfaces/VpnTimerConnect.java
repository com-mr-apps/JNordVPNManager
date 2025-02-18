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
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mr.apps.JNordVpnManager.gui.GuiStatusLine;
import com.mr.apps.JNordVpnManager.gui.connectLine.JPanelConnectTimer;

public class VpnTimerConnect extends CoreCommandClass
{
   public static boolean execute(ActionEvent e)
   {
      int m_timerWorkMode = JPanelConnectTimer.getTimerWorkMode();
      if (m_timerWorkMode == GuiStatusLine.STATUS_PAUSED)
      {
         GuiStatusLine.setStatusLine(GuiStatusLine.STATUS_CONNECTED, JPanelConnectTimer.syncStatusForTimer(GuiStatusLine.STATUS_CONNECTED));
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_RECONNECT)
      {
         GuiStatusLine.setStatusLine(GuiStatusLine.STATUS_CONNECTED, JPanelConnectTimer.syncStatusForTimer(GuiStatusLine.STATUS_CONNECTED));
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_CONNECTED)
      {
         GuiStatusLine.setStatusLine(GuiStatusLine.STATUS_PAUSED, JPanelConnectTimer.syncStatusForTimer(GuiStatusLine.STATUS_PAUSED));
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_DISCONNECTED)
      {
         GuiStatusLine.setStatusLine(GuiStatusLine.STATUS_PAUSED, JPanelConnectTimer.syncStatusForTimer(GuiStatusLine.STATUS_PAUSED));
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_UNKNOWN)
      {
         GuiStatusLine.setStatusLine(GuiStatusLine.STATUS_PAUSED, JPanelConnectTimer.syncStatusForTimer(GuiStatusLine.STATUS_PAUSED));
      }
      else // Starter.STATUS_LOGGEDOUT
      {
      }
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      JButton buttonConnectPause = (JButton)cmd.getComponent();
      if (null != buttonConnectPause)
      {
         String sToolTip = cmd.getToolTip();
         buttonConnectPause.setEnabled(cmd.isEnabled());
         buttonConnectPause.setIcon(cmd.getIconImage());
         buttonConnectPause.setToolTipText(sToolTip);
         JPanel bp = (JPanel) buttonConnectPause.getParent();
         bp.setToolTipText(sToolTip);
      }
      return true;
   }
}
