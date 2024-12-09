/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
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
         + "<h4>Version 2024.2.2:</h4>"
         + "[Released in December 2024]"
         + "<ul>"
         + "<li><strong>Application Preferences</strong>: New setting <em>Auto Update Serverdata on Program Start</em> (Default=off). If set to 'on', the server list is updated on application start from NordVPN (requires Internet access - 'GUI Auto Connect=on' recommanded to avoid problems with 'killswitch'). Only with the server list from NordVPN, Group/Region filters are available.</li>"
         + "<li><strong>Group/Regions Filter</strong>: New Filters for Regions and Legacy Groups are available. VPN connection will be established with the <em>--group</em> and the selected group to <em>Double_VPN, Onion_Over_VPN, etc.</em>.</li>"
         + "</ul>"
         + "<h3>What's Next:</h3>"
         + "<ul>"
         + "<li><strong>Support Whitelist</strong>: new functionality.</li>"
         + "<li><strong>Support Meshnet</strong>: new functionality.</li>"
         + "<li><strong>List of Favorite Server Connections</strong>: switch Tree list to user defined Favorite Server Connections (add/delete).</li>"
         + "<h3>History:</h3>"
         + "<h3>Version 2024.2.1 (Snap 2024.2.1b):</h3>"
         + "[Released in November 2024]"
         + "<ul>"
         + "<li><strong>NordVPN Settings</strong>: New menu entry <em>NordVPN-&gt;Edit Settings</em> opens a dialog where NordVPN settings can be changed.</li>"
         + "<li><strong>Application Preferences</strong>: New menu entry <em>File-&gt;Preferences</em> opens a dialog where Application User Preferences can be changed.</li>"
         + "<li><strong>What's New</strong>: New Menu entry <em>Info-&gt;What's New</em> opens the What's New / Version History dialog.</li>"
         + "<li><strong>Updated Installation process</strong>: It's no longer required to run the application after an update from the snap installation. The desktop file created beginning with this installation now always executes the current version.</li>"
         + "<li>Made parsing of nordvpn account/settings/status information more failsafe.</li>"
         + "</ul>"
         + "<h3>Version 2024.1.1-(Snap 2024.2.1-pre):</h3>"
         + "[Released 20. November 2024]"
         + "<ul>"
         + "<li>Quickfix for NordVPN Software Update 3.19.1 where 'nordvpn status' command output changed.</li>"
         + "</ul>"
         + "<h3>Version 2024.1.0:</h3>"
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
