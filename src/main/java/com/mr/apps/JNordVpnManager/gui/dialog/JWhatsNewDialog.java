/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class JWhatsNewDialog
{
   private static final String m_htmlAboutText = "<h2>JNordVPN Manager Version History.</h2>"
         + "<h3>What's New:</h3>"
         + "</ul>"
         + "<h4>Version 2025.5.1 (Snap 2025.5.1):</h4>"
         + "[Released March 2025]"
         + "<ul>"
         + "<li><strong>NordVPN Server Hosts</strong>: Enhanced infrastructure to manage over 7000 VPN Server Hosts.</li>"
         + "<li><strong>NordVPN Server Connect</strong>: Add functionality to connect to server regions.</li>"
         + "<li><strong>Commands Tool Bar</strong>: Add command VpnSetTechnology/Protocol to the quick-access commands tool bar.</li>"
         + "<li><strong>Menu Diagnostics</strong>: Add new <em>Diagnostics</em> menu with <em>Speed Test</em> and access to <em>log files</em>.</li>"
         + "<li>...and another minor bug fixes and enhancements to make the application more reliable and handsome.</li>"
         + "</ul>"
         + "<p>For further information see: <a href=\"https://github.com/com-mr-apps/JNordVPNManager\">JNordVPN on GitHub</a></p>"
         + "<h3>What's Next:</h3>"
         + "<ul>"
         + "<li><strong>Bug fixes and Enhancements</strong>: Maintenance.</li>"
         + "</ul>"
         + "<h3>Planned features for Supporter Edition <em>(suggestions are welcome)</em>:</h3>"
         + "<ul>"
         + "<li><strong>NordVPN Recommended Servers</strong>: Filter Servers by load index (speed) from NordVPN recommended servers list. <a href=\"https://github.com/com-mr-apps/JNordVPNManager/issues/16\">[GitHub Issue #16]</a>.</li>"
         + "<li><strong>Meshnet</strong>: NordVPN Meshnet Settings/Management. <a href=\"https://github.com/com-mr-apps/JNordVPNManager/issues/18\">[GitHub Issue #18]</a>.</li>"
         + "<li><strong>Cycle Connections</strong>: Time-based automatic [re]connect to a list of defined VPN servers.</li>"
         + "<li><strong>Speedtest</strong>: check/show connection speeds.</li>"
         + "</ul>"
         + "<p>For further information see: <a href=\"https://buymeacoffee.com/3dprototyping\">[donations]</a>.</p>"
         + "</ul>"
         + "<h4>Version 2025.3.2 (Snap 2025.3.2):</h4>"
         + "[Released March 2025]"
         + "<ul>"
         + "<li><strong>World Map Enhancements</strong>: Optionally display a colored/greyscale GeoTIFF image as world map background.</li>"
         + "<li><strong>Speed Test</strong>: Experimental Download Speed Test.</li>"
         + "<li><strong>Maintenance</strong>: Bug Fix in parsing NordVPN Settings data if a subnet/port allowlist is defined.</li>"
         + "<li><strong>NordVPN Allowlist</strong>: new functionality for <em>Supporter Edition</em> to manage the NordVPN allow list.</li>"
         + "<li>...and another minor bug fixes and enhancements to make the application more reliable and handsome.</li>"
         + "</ul>"
         + "<h4>Version 2025.3.1 (Snap 2025.3.1):</h4>"
         + "[Released 25. February 2025]"
         + "<ul>"
         + "<li><strong>Commands Tool Bar</strong>: Customizable commands tool bar to quick access common used commands/features.</li>"
         + "<li><strong>Maintenance</strong>: Bug Fix for manual Refresh of the Server List from NordVPN Server.</li>"
         + "<li><strong>Maintenance</strong>: Improved Pause/Reconnect NordVPN Server connection functionality.</li>"
         + "<li><strong>Supporter Edition</strong>: Implemented Infrastructure for Supporter Edition and added first functionality.</li>"
         + "<li>...and another minor bug fixes and enhancements to make the application more reliable and handsome.</li>"
         + "</ul>"
         + "<h3>History:</h3>"
         + "<h4>Version 2025.2.1 (Snap 2025.2.1):</h4>"
         + "[Released 30. January 2025]"
         + "<ul>"
         + "<li><strong>Technology NORDWHISPER</strong>: Support new NordVPN setting <em>Technology NORDWHISPER<em>.</li>"
         + "<li><strong>Maintenance</strong>: Simplified server selection/connection workflow by moving NordVPN setting changes [required for connections] from the selection step to the nordvpn connect step.</li>"
         + "<li><strong>GUI Enhancements</strong>: Renovated Console window and optimized GUI performance.</li>"
         + "<li>...and some bug fixes and enhancements to make the application more reliable and handsome.</li>"
         + "</ul>"
         + "<h4>Version 2025.1.1 (Snap 2025.1.1):</h4>"
         + "[Released 11. January 2025]"
         + "<ul>"
         + "<li><strong>Obfuscated Server Connections</strong>: Added support of the NordVPN setting <code>Obfuscated</code> for OPENVPN technology. If <em>enabled</em>, the <code>Server List Tree</code> shows only servers, available for obfuscated VPN connections. For more information see: <a href=\"https://support.nordvpn.com/hc/en-us/articles/19479130821521-Different-NordVPN-server-categories-explained#h_01HGTG79DQP0NW71K286YDW061\">Obfuscated Servers Explained [NordVPN Support]</a></li>"
         + "<li><strong>Recent Server List</strong>: Each <code>Recent Servers</code> list item now stores additional information about the connection group, technology and protocol.</li>"
         + "<li><strong>Application Preferences</strong>: Added new setting <code>Auto Close Message Dialogs</code>. This setting defines for some dialogs an auto close countdown. Valid values are:<ul><li><em>-1</em>: Auto close disabled</li><li><em>=0</em>: Don't show the dialog</li><li><em>&gt;0</em>: Close countdown in seconds</li></ul></li>"
         + "<li><strong>Speed Up Application Launch Time</strong>: Increased performance for server list access by keeping a local copy of the required data. The local server list must be regulary updated manually with the <code>Refresh</code> button.</p></li>"
         + "<li><strong>Application Preferences</strong>: With the setting <code>Auto Update Server Data on Program Start</code>, now the number of days for an automatic update of the server list on program start can be set.</li>"
         + "<li><strong>Server List Tree</strong>: Added country flags to Server List Tree icons and changed styles.</li>"
         + "</ul>"
         + "<h4>Version 2024.2.2 (Snap 2024.2.2):</h4>"
         + "[Released 09. December 2024]"
         + "<ul>"
         + "<li><strong>Application Preferences</strong>: New setting <code>Auto Update Serverdata on Program Start</code> (Default=<em>off</em>). If set to <em>on</em>, the server list is updated on application start from NordVPN (requires Internet access - <code>GUI Auto Connect=</code><em>on</em> recommanded to avoid problems with <code>killswitch</code>). Only with the server list from NordVPN, Group/Region filters are available.</li>"
         + "<li><strong>Group/Regions Filter</strong>: New Filters for Regions and Legacy Groups are available. VPN connection will be established with the <code>--group</code> argument and the selected group [<code>Double_VPN, Onion_Over_VPN, etc.</code>].</li>"
         + "</ul>"
         + "<h4>Version 2024.2.1 (Snap 2024.2.1b):</h4>"
         + "[Released 26. November 2024]"
         + "<ul>"
         + "<li><strong>NordVPN Settings</strong>: New menu entry <code>NordVPN-&gt;Edit Settings</code> opens a dialog where NordVPN settings can be changed.</li>"
         + "<li><strong>Application Preferences</strong>: New menu entry <code>File-&gt;Preferences</code> opens a dialog where Application User Preferences can be changed.</li>"
         + "<li><strong>What's New</strong>: New Menu entry <code>Info-&gt;What's New</code> opens the What's New / Version History dialog.</li>"
         + "<li><strong>Updated Installation process</strong>: It's no longer required to run the application after an update from the snap installation. The desktop file created beginning with this installation now always executes the current version.</li>"
         + "<li>Made parsing of nordvpn account/settings/status information more failsafe.</li>"
         + "</ul>"
         + "<h4>Version 2024.1.1 (Snap 2024.2.1-pre):</h4>"
         + "[Released 20. November 2024]"
         + "<ul>"
         + "<li>Quickfix for NordVPN Software Update 3.19.1 where <code>nordvpn status</code> command output changed.</li>"
         + "</ul>"
         + "<h4>Version 2024.1.0:</h4>"
         + "[Released 14. November 2024]"
         + "<ul>"
         + "<li>Initial version launched.</li>"
         + "</ul>"
         + "<p>For further information see: <a href=\"https://github.com/com-mr-apps/JNordVPNManager\">JNordVPN on GitHub</a></p>";

   private JFrame       m_aboutFrame = null;

   /**
    * Initiates a new About Screen
    */
   public JWhatsNewDialog()
   {
      m_aboutFrame = new JFrame();
      m_aboutFrame.setTitle("What's New in JNordVPN Manager");
      m_aboutFrame.setLayout(new BorderLayout());
//      m_aboutFrame.setResizable(false);
      // Close Window with "X"
      m_aboutFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      m_aboutFrame.addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            close();
         }
      });

      JEditorPane whatsNewText = new JEditorPane();
      whatsNewText.setEditable(false);
      whatsNewText.setCaretColor(new Color(247, 217, 146)); // hide caret
      whatsNewText.setBackground(new Color(247, 217, 146));
      whatsNewText.setContentType("text/html");
      whatsNewText.setText("<html><body style=\"background-color: #f7d992 \">" + m_htmlAboutText + "</body></html>");
      whatsNewText.setCaretPosition(0);
      whatsNewText.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e)
         {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
               UtilSystem.openWebpage(e.getURL());
            }
         }
      });
      JScrollPane whatsTextScrollPanel = new JScrollPane(whatsNewText,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      m_aboutFrame.add(whatsTextScrollPanel, BorderLayout.CENTER);

      m_aboutFrame.setPreferredSize(new Dimension(600,400));
   }

   /**
    * Close the Window.<p>
    * Return to main frame and suppress gained focus event.
    */
   private void close()
   {
      Starter.setSkipWindowGainedFocus();
      m_aboutFrame.setVisible(false);
      m_aboutFrame.dispose();
   }

   /**
    * Centers the Screen and then shows it.
    */
   public void show()
   {
      m_aboutFrame.setLocationRelativeTo(null);
      m_aboutFrame.setVisible(true);
      m_aboutFrame.pack();
   }
}
