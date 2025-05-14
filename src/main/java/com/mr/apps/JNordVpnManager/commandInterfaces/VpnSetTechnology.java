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

import javax.swing.JComboBox;

import com.mr.apps.JNordVpnManager.Starter;

/**
 * Command VPN Settings - Technology
 */
public class VpnSetTechnology extends CoreCommandClass
{
   public static Object get()
   {
      String sTech = Starter.getCurrentSettingsData().getTechnology(false);
      if (true == sTech.equalsIgnoreCase("OPENVPN"))
      {
         sTech += "/" + Starter.getCurrentSettingsData().getProtocol(false);
      }
      return sTech;
   }

   public static boolean execute(ActionEvent e)
   {
      JComboBox<?> cb = (JComboBox<?>) e.getSource();
      if (null != cb)
      {
         int isel = cb.getSelectedIndex();
         switch (isel)
         {
            case 0:
               Starter.getCurrentSettingsData().setTechnology("NORDLYNX", false);
               break;
            case 1:
               Starter.getCurrentSettingsData().setTechnology("NORDWHISPER", false);
               break;
            case 2:
               Starter.getCurrentSettingsData().setTechnology("OPENVPN", false);
               Starter.getCurrentSettingsData().setProtocol("TCP", false);
               break;
            case 3:
               Starter.getCurrentSettingsData().setTechnology("OPENVPN", false);
               Starter.getCurrentSettingsData().setProtocol("UDP", false);
               break;
            default:
               Starter._m_logError.TraceErr("(VpnSetTechnology.execute) Internal Error: ComboBox index '" + isel + "' not supported!");
         }
         Starter.updateStatusLine();
      }
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      JComboBox<?> cb = (JComboBox<?>)cmd.getComponent();
      if (null != cb)
      {
         if (null != cb)
         {
            String sTech = (String)get();
            int iIndex = 0;
            String sToolTip = cmd.getToolTip();
            if (sTech.equalsIgnoreCase("NORDLYNX"))
            {
               iIndex = 0;
            }
            else if (sTech.equalsIgnoreCase("NORDWHISPER"))
            {
               iIndex = 1;
            }
            else if (sTech.equalsIgnoreCase("OPENVPN/TCP"))
            {
               iIndex = 2;
            }
            else if (sTech.equalsIgnoreCase("OPENVPN/UDP"))
            {
               iIndex = 3;
            }
            else
            {
               Starter._m_logError.TraceErr("(VpnSetTechnology.updateUI) Internal Error: Option '" + sTech + "' not supported!");
            }
            cb.setSelectedIndex(iIndex);
            sToolTip = cmd.getToolTip(iIndex);
            cmd.updateToolTipUI(sToolTip);
         }
      }
      return true;
   }

}
