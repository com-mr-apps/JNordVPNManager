/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.gui.connectLine.JPauseSlider;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnSettingsData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnStatusData;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * GUI Status Line<p>
 * Show current connection status with additional current server information at the bottom of the GUI.
 */
public class GuiStatusLine
{
   private static JLabel               m_statusIndicator      = null;
   private static JLabel               m_statusText           = null;
   private JButton                     m_minMaxButton         = null;

   private static ArrayList<ImageIcon> m_statusImages         = new ArrayList<>();
   private ArrayList<ImageIcon>        m_collapseExpandImages = new ArrayList<>();

   private static String[]             m_compactModeToolTip   = {
         "Switch to Compact view.",
         "Switch to Expanded view."
   };

   /**
    * Constructor for GUI Status Line
    */
   public GuiStatusLine()
   {
      // Connected / Paused / Disconnected
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_CONNECTED, IconSize.MEDIUM));
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_PAUSED, IconSize.MEDIUM));
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_DISCONNECTED, IconSize.MEDIUM));

      // Collapse / Expand
      m_collapseExpandImages.add(JResizedIcon.getIcon(IconUrls.ICON_WINDOW_COLLAPSE, IconSize.MEDIUM));
      m_collapseExpandImages.add(JResizedIcon.getIcon(IconUrls.ICON_WINDOW_EXPAND, IconSize.MEDIUM));
   }

   /**
    * Status Line Layout definition.
    * 
    * @return the created menu bar
    */
   public JPanel create()
   {
      JPanel statusPanel = new JPanel(new BorderLayout());

      // ----------------------------------------------------------------------
      // Status indicator connected/paused/disconnected
      m_statusIndicator = new JLabel(m_statusImages.get(2)); // disconnected
      statusPanel.add(m_statusIndicator, BorderLayout.LINE_START);
     
      // ----------------------------------------------------------------------
      // Status message
      m_statusText = new JLabel("Status not updated yet...");
      statusPanel.add(m_statusText, BorderLayout.CENTER);

      // ----------------------------------------------------------------------
      // Compact/Expand mode switch
      int compactMode = UtilPrefs.getCompactMode();
      m_minMaxButton = new JButton(m_collapseExpandImages.get(compactMode));
      m_minMaxButton.setToolTipText(m_compactModeToolTip[compactMode]);
      m_minMaxButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // switch between compact- and expanded view
            int compactMode = UtilPrefs.getCompactMode();
            compactMode = 1 - compactMode; // switch mode between Expanded and Collapsed
            Starter.switchCompactMode(compactMode);
            m_minMaxButton.setIcon(m_collapseExpandImages.get(compactMode));
            m_minMaxButton.setToolTipText(m_compactModeToolTip[compactMode]);
            UtilPrefs.setCompactMode(compactMode);
         }
      });

      statusPanel.add(m_minMaxButton, BorderLayout.LINE_END);

      return statusPanel;
   }

   /**
    * Update the connection status line.
    * 
    * @param statusData
    *           is the current nordvpn status information
    * @return the current city/country location from the status command [in case of connected]
    */
   public CurrentLocation update(NvpnStatusData statusData)
   {
      CurrentLocation ret_loc = null;

      if (null == statusData.getStatus())
      {
         /*
          *  error
          */
         m_statusText.setText(statusData.getStatusText());
         return ret_loc;
      }

      if (statusData.isConnected())
      {
         /*
          *  connected
          */
         // update pause slider
         JPauseSlider.syncStatusForPause(Starter.STATUS_CONNECTED);

         ret_loc = new CurrentLocation(UtilLocations.getLocation(statusData.getCity(), statusData.getCountry()));
         ret_loc.setConnected(true);

         NvpnSettingsData settingsData = Starter.getCurrentSettingsData();
         int iconId = 0;
         String sPrefix = "";
         if (true == NvpnSettingsData.reconnectRequired())
         {
            // reconnect required - settings changed
            iconId = 1;
            Starter._m_logError.LoggingError(10905,
                  "NordVPN Settings changed",
                  "NordVPN Settings were changed which need a reconnect. Please manually reconnect to activate the new settings.");
            sPrefix = "[Changed Settings] ";
         }

         if (false == settingsData.getTechnology(false).equals(statusData.getTechnology()))
         {
            // reconnect required - settings technology <> current technology
            iconId = 1;
            Starter._m_logError.LoggingError(10905,
                  "Server Connection Settings Mismatch",
                  "The current server connection uses '" + statusData.getTechnology() + "' but settings are set to '" + settingsData.getTechnology(false) + "'.");
            sPrefix = "[Technology mismatch] ";
         }

         if (true == settingsData.getTechnology(false).equals("OPENVPN"))
         {
            // .. in case of OPENVPN
            if (false == settingsData.getProtocol(false).equals(statusData.getProtocol()))
            {
               // reconnect required - settings OPENVPN protocol <> current OPENVPN protocol
               iconId = 1;
               Starter._m_logError.LoggingError(10905,
                     "Server Connection Settings Mismatch",
                     "The current server connection uses '" + statusData.getProtocol() + "' but settings are set to '" + settingsData.getProtocol(false) + "'.");
               sPrefix = "[Protocol mismatch] ";
            }
            else if ((StringFormat.string2boolean(settingsData.getObfuscate(false)) == true) && (false == ret_loc.hasGroup(NordVPNEnumGroups.legacy_obfuscated_servers)))
            {
               // reconnect required - current server does not support obfuscated
               iconId = 1;
               Starter._m_logError.LoggingError(10905,
                     "Server Connection Settings Mismatch",
                     "The current server does not support obfuscation. Please manually reconnect to a server that supports obfuscation.");
               sPrefix = "[No obfuscation] ";
            }
         }

         if (iconId == 1)
         {
            JModalDialog dlg = JModalDialog.JOptionDialog("Reconnect Required",
                  "To establish the VPN connection with the changed settings, a reconnect or a manual server change is required.\n"
                + "Do you want to reconnect now (may fail if the server does not support the changed settings)?",
                  "Reconnect,Cancel");
            int rc = dlg.getResult();
            if (rc == 0)
            {
               // Reconnect
               if (false == NvpnCallbacks.executeConnect(ret_loc, "NordVPN Reconnect", "NordVPN Reconnect"))
               {
                  rc = 1; // error
               }
            }
            if (rc != 0)
            {
               // requires manual reconnect
               updateStatusLine(iconId, statusData.getStatusLineMessage(sPrefix));
            }
         }
         else
         {
            // connected
            updateStatusLine(0, statusData.getStatusLineMessage(""));
         }
      }
      else
      {
         /*
          *  disconnected or not logged in
          */
         // check if paused and update pause slider
         int iStatus = (true == Starter.getCurrentAccountData(false).isLoggedIn()) ? Starter.STATUS_LOGGEDOUT : Starter.STATUS_DISCONNECTED;
         String pauseMsg = JPauseSlider.syncStatusForPause(iStatus);
         if (null != pauseMsg)
         {
            // Status: Paused
            updateStatusLine(1, pauseMsg);
         }
         else
         {
            // Status: Disconnected (or error message...)
            updateStatusLine(2, statusData.getStatusLineMessage(""));
         }
      }

      return ret_loc;
   }
   
   /**
    * Update the status line
    * @param iStatus is the status 
    * @param msg
    */
   public static void updateStatusLine(int iStatus, String msg)
   {
      m_statusIndicator.setIcon(m_statusImages.get(iStatus));
      if (null != msg)
      {
         Starter._m_logError.TraceDebug("Update Statusline: [" + iStatus + "] " + msg);
         m_statusText.setText(msg);
      }
      else
      {
         Starter._m_logError.TraceDebug("Update Statusline: [" + iStatus + "]");
      }
   }
}
