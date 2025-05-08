/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.Command;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.geotools.UtilMapGeneration;
import com.mr.apps.JNordVpnManager.gui.connectLine.GuiCommandsToolBar;
import com.mr.apps.JNordVpnManager.gui.connectLine.JPanelConnectTimer;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
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
         "Click here to switch to Compact view.",
         "Click here to switch to Expanded view."
   };

   // Defined connection states
   public static final int         STATUS_UNKNOWN             = -1;
   public static final int         STATUS_CONNECTED           = 0;
   public static final int         STATUS_PAUSED              = 1;
   public static final int         STATUS_DISCONNECTED        = 2;
   public static final int         STATUS_RECONNECT           = 3;
   public static final int         STATUS_LOGGEDOUT           = 99;

   /**
    * Constructor for GUI Status Line
    */
   public GuiStatusLine()
   {
      // Connected / Paused / Disconnected
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_CONNECTED, IconSize.MEDIUM));
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_PAUSED, IconSize.MEDIUM));
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_DISCONNECTED, IconSize.MEDIUM));
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_RECONNECT, IconSize.MEDIUM));
      m_statusImages.add(JResizedIcon.getIcon(IconUrls.ICON_STATUS_WARNING, IconSize.MEDIUM));

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
      m_statusIndicator.setToolTipText("Click here to refresh the status line");
      m_statusIndicator.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            Starter.updateStatusLine();
            UtilMapGeneration.mapRefresh();
         }
      });
      statusPanel.add(m_statusIndicator, BorderLayout.LINE_START);
     
      // ----------------------------------------------------------------------
      // Status message
      m_statusText = new JLabel("Status not updated yet...");
      statusPanel.add(m_statusText, BorderLayout.CENTER);

      // ----------------------------------------------------------------------
      // Compact/Expand mode switch
      int compactMode = UtilPrefs.getCompactMode();
      m_minMaxButton = new JButton(m_collapseExpandImages.get(compactMode));
      m_minMaxButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());

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

      if (null == statusData)
      {
         /*
          *  error
          */
         setStatusLine(2, "No status data available");
         return ret_loc;
      }
      if (null == statusData.getStatus())
      {
         /*
          *  error
          */
         setStatusLine(2, statusData.getStatusText());
         return ret_loc;
      }

      int iconId = 4;
      String sPrefix = "";
      String statusMessage  = "";

      // update pause slider
      int iStatus = (true == Starter.getCurrentAccountData(false).isLoggedIn()) ? JPanelConnectTimer.getTimerWorkMode() : STATUS_LOGGEDOUT;
      String connectTimerMsg = JPanelConnectTimer.syncStatusForTimer(iStatus);
      if (null == connectTimerMsg)
      {
         // Status: ..connect timer not running
         if (iStatus != STATUS_LOGGEDOUT)
         {
            iconId = (Starter.getCurrentStatusData().isConnected()) ? STATUS_CONNECTED : STATUS_DISCONNECTED;
         }
      }
      else if (false == connectTimerMsg.isBlank())
      {
         // Status: Paused
         iconId = 1;
      }
      else
      {
         // Status: automatic reconnect
         iconId = 3;
      }

      if (statusData.isConnected())
      {
         /*
          * connected
          */
         // get the (GUI) current server location object
         ret_loc = Starter.getCurrentServer(true);
         // Create a current server location object based on the current status data (real connected server)
         Location status_loc = UtilLocations.getLocation(statusData.getCity(), statusData.getCountry());
         CurrentLocation new_loc = (null != status_loc) ? new CurrentLocation(status_loc) : null;
         // Check, if GUI current server is the current server
         if ((null == ret_loc) ||
             (false == ret_loc.isEqualLocation(new_loc))) // TODO: check also for server host
         {
            // set changed current server based on actual status data
            ret_loc = new_loc;
         }
         ret_loc.setConnected(true);

         String sShortMsg = null;
         String sLongMsg = null;
         NvpnSettingsData settingsData = Starter.getCurrentSettingsData();
         if (true == NvpnSettingsData.reconnectRequired())
         {
            // reconnect required - settings changed
            iconId = 4;
            sShortMsg = "NordVPN Settings changed";
            sLongMsg = "NordVPN Settings were changed which may need a reconnect. Please manually reconnect to ensure the current settings are active.";
            sPrefix = "[Changed Settings] ";
         }

         if (false == settingsData.getTechnology(false).equals(statusData.getTechnology()))
         {
            // reconnect required - settings technology <> current technology
            iconId = 4;
            sShortMsg = "Server Connection Settings Mismatch";
            sLongMsg = "The current server connection uses '" + statusData.getTechnology() + "' but settings are set to '" + settingsData.getTechnology(false) + "'.";
            sPrefix = "[Technology mismatch] ";
         }

         if (true == settingsData.getTechnology(false).equals("OPENVPN"))
         {
            // .. in case of OPENVPN
            if (false == settingsData.getProtocol(false).equals(statusData.getProtocol()))
            {
               // reconnect required - settings OPENVPN protocol <> current OPENVPN protocol
               iconId = 4;
               sShortMsg = "Server Connection Settings Mismatch";
               sLongMsg = "The current server connection uses '" + statusData.getProtocol() + "' but settings are set to '" + settingsData.getProtocol(false) + "'.";
               sPrefix = "[Protocol mismatch] ";
            }
            else if ((StringFormat.string2boolean(settingsData.getObfuscate(false)) == true) && (false == ret_loc.hasGroup(NordVPNEnumGroups.legacy_obfuscated_servers)))
            {
               // reconnect required - current server does not support obfuscated
               iconId = 4;
               sShortMsg = "Server Connection Settings Mismatch";
               sLongMsg = "The current server does not support obfuscation. Please manually reconnect to a server that supports obfuscation or disable obfuscation.";
               sPrefix = "[No obfuscation] ";
            }
         }
         if (null != sShortMsg)
         {
            JModalDialog.showWarning(sShortMsg + "\n\n" + sLongMsg);
         }
      }
      statusMessage = statusData.getStatusLineMessage(sPrefix);
      
      setStatusLine(iconId, statusMessage);

      return ret_loc;
   }
   
   /**
    * Set the status line
    * 
    * @param iStatus
    *           is the status
    * @param msg
    *           is the message text
    */
   public static void setStatusLine(int iStatus, String msg)
   {
      if (iStatus == STATUS_UNKNOWN) iStatus = (Starter.getCurrentStatusData().isConnected()) ? STATUS_CONNECTED : STATUS_DISCONNECTED;
      m_statusIndicator.setIcon(m_statusImages.get(iStatus));
      if (null == msg)
      {
         msg = "";
      }
      else if (msg.isBlank())
      {
         msg = Starter.getCurrentStatusData().getStatusLineMessage("");
         m_statusText.setText(msg);
      }
      else
      {
         m_statusText.setText(msg);
      }
      Starter._m_logError.TraceDebug("Update Statusline: [" + iStatus + "] " + msg);

      // GUI updates of buttons that depend on the connection status
      Command cmd = Command.getObject(Command.VPN_CMD_DISCONNECT);
      String sToolTip = cmd.getToolTip(0);
      boolean isEnabled = true;
      if ((false == JPanelConnectTimer.isTimerRunning()) && (iStatus == STATUS_DISCONNECTED || iStatus == STATUS_LOGGEDOUT))
      {
         sToolTip = "Not connected.";
         isEnabled = false;
      }
      else
      {
         isEnabled = true;
      }
      cmd.setToolTip(sToolTip);
      cmd.setEnabled(isEnabled);
      GuiCommandsToolBar.updateCommand(Command.VPN_CMD_DISCONNECT);

   }
}
