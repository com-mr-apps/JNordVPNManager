/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.dialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JLogo;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class JAboutScreen
{
   private static final String ABOUT_IMAGE        = "resources/AboutScreen.png";
   private static final String NORDVPN_IMAGE      = "resources/NordVPN.png";
   private static final String GEOTOOLS_IMAGE     = "resources/GeoTools.png";
   private static final String NATURALEARTH_IMAGE = "resources/NaturalEarth.png";

   private static final String m_htmlAboutText = "<p>JNordVPN Manager is an open-source graphical user interface (GUI) application designed to make managing NordVPN connections on Linux easier and more intuitive than using the native nordvpn commands. Built with Java, this GUI provides a comprehensive set of features that cater to the needs of NordVPN users on the Linux platform.</p>"
   + "<p>Key Features:</p>"
   + "<ul>"
   + "<li><strong>Server Search</strong>: Easily find and connect to VPN servers from all around the world using filters by country/city, technology/protocol, regions and legacy groups.</li>"
   + "<li><strong>Legacy Groups</strong>: Support (filter) Servers by: Standard servers, Double VPN, Onion over VPN, Dedicated IP, Obfuscated servers.</li>"
   + "<li><strong>Connection Management</strong>: Control your NordVPN connections with ease - (re)connect, disconnect, pause, or resume your sessions with a single click.</li>"
   + "<li><strong>NordVPN Settings</strong>: Manage your individual NordVPN settings direct from the GUI - export/import to/from local settings files for an easy switch between your different setups.</li>"
   + "<li><strong>Recent Connections</strong>: The application stores your recent connections for easy reconnection. Simply select one to reconnect instantly.</li>"
   + "<li><strong>World Map View</strong>: Visualize all available VPN servers on a world map and focus on the active server, providing an intuitive way to navigate through NordVPN&#39;s vast network.</li>"
   + "<li><strong>Security</strong>: The application does not store any login information (Account, Password). It is just an interface for the the native nordvpn commands.</li>"
   + "<li><strong>Quick Access Commands Toolbar</strong>: Free configurable commands Toolbar for commonly used commands/settings.</li>"

   + "</ul>"
   + "<p>The application is released under the Common Development and Distribution License (<a href=\"https://opensource.org/license/cddl-1-0\">CDDL</a>) with a Commons Clause License, ensuring that it remains free for private use only. This means that while you can download and enjoy NordVPN Manager without any costs, commercial exploitation of the software requires my explicit permission.</p>"
   + "<p>The newest version and the source code can be found on GitHub: <a href=\"https://github.com/com-mr-apps/JNordVpnManager\">https://github.com/com-mr-apps/JNordVpnManager</a>."
   + "Requirements, Bugs, etc. can be posted and will be administrated there.</p>"
   + "<p>Thank you for considering JNordVPN Manager as your go-to GUI for managing NordVPN connections on Linux.</p>"
   
   + "<h2>References</h2>"
   + "<h3>NordVPN backend (prerequisit)</h3>"
   + "<ul>"
   + "<li><a href=\"https://snapcraft.io/nordvpn\">NordVPN Snap Store Installation</a></li>"
   + "<li><a href=\"https://support.nordvpn.com/hc/en-us/articles/20196094470929-Installing-NordVPN-on-Linux-distributions\">Installing and Using NordVPN on Linux</a></li>"
   + "<li><a href=\"https://refer-nordvpn.com/ArNNOfynXcu\">Sign up and choose a plan (Affiliate link)</a></li>"
   + "</ul>"
   + "<h3>GeoTools</h3>"
   + "<p>The world map is based on GeoTools, an open source (LGPL) Java code library that provides tools for geospatial data:</p>"
   + "<ul>"
   + "<li><a href=\"https://geotools.org/about.html\">https://geotools.org/about.html</a></li>"
   + "<li><a href=\"https://github.com/geotools/geotools\">https://github.com/geotools/geotools</a></li>"
   + "</ul>"
   + "<h3>World map data</h3>"
   + "<p>Thanks to Natural Earth. Free vector and raster map data from:</p>"
   + "<ul>"
   + "<li><a href=\"https://www.naturalearthdata.com/\">https://www.naturalearthdata.com/</a></li>"
   + "</ul>"
   
   + "<h2>Donations</h2>"
   + "<p>Funding for the ongoing development and maintenance of this application comes from donations made to me through <a href=\"https://buymeacoffee.com/3dprototyping\">https://buymeacoffee.com/3dprototyping</a>. Your support in the form of a coffee or any other contribution is greatly appreciated and helps keep JNordVPN Manager alife and up-to-date.</p>"
   + "<p>If you appreciate NordVPNs services, you can also support me by ordering NordVPN through my affiliate link: <a href=\"https://refer-nordvpn.com/ArNNOfynXcu\">https://refer-nordvpn.com/ArNNOfynXcu</a>. This not only helps fund the development of JNordVPN Manager but also provides a financial incentive for further improving and maintaining this application.</p>"

   + "<h2>Supporter Edition</h2>"
   + "<p>Supporters (Donators) of this project get access to the <em>Supporters Edition</em>. A key file and an add-on library extend the functionality of the application.</p>"
   + "<p>The <em>Supporters Edition</em> can be found on my Donation pages.</p>"
   
   + "<h2>License</h2>"
   + "<p>The \"Commons Clause\" License Condition v1.0</p>"
   + "<p>The Software is provided to you by the Licensor under the License, as defined below, subject to the following condition.</p>"
   + "<p>Without limiting other conditions in the License, the grant of rights under the License will not include, and the License does not grant to you, the right to Sell the Software.</p>"
   + "<p>For purposes of the foregoing, \"Sell\" means practicing any or all of the rights granted to you under the License to provide to third parties, for a fee or other consideration (including without limitation fees for hosting or consulting/ support services related to the Software), a product or service whose value derives, entirely or substantially, from the functionality of the Software. Any license notice or attribution required by the License must also include this Commons Clause License Condition notice.</p>"
   + "<p>Software: JNordVPN Manager</p>"
   + "<p>License: <a href=\"https://spdx.org/licenses/CDDL-1.1.html\">The Common Development and Distribution License 1.1</a></p>"
   + "<p>Licensor: com.mr.apps</p>";

   private JFrame       m_aboutFrame = null;

   /**
    * Initiates a new About Screen
    */
   public JAboutScreen(String version)
   {
      m_aboutFrame = new JFrame();
      m_aboutFrame.setTitle("About JNordVPN Manager");
      m_aboutFrame.setLayout(null);
      m_aboutFrame.setResizable(false);
//    m_aboutFrame.setUndecorated(true);
      // Close Window with "X"
      m_aboutFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      m_aboutFrame.addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            close();
         }
      });

      ImageIcon imageIcon = new ImageIcon(Starter.class.getResource(ABOUT_IMAGE));
      JLabel aboutBaseImageLabel = new JLabel(imageIcon);
      aboutBaseImageLabel.setLocation(0, 0);
      aboutBaseImageLabel.setBounds(0, 0, 605, 831);
      m_aboutFrame.add(aboutBaseImageLabel);

      JLabel lblVersion = new JLabel(version);
      lblVersion.setSize(lblVersion.getPreferredSize());
      lblVersion.setLocation(20, 15);
      lblVersion.setFont(new Font("serif", Font.ITALIC, 12));
      lblVersion.setForeground(new Color(97, 206, 255));
      aboutBaseImageLabel.add(lblVersion);

      JLabel copyright =  new JLabel(Starter.COPYRIGHT_STRING);
      copyright.setSize(copyright.getPreferredSize());
      copyright.setLocation(20, 35);
      copyright.setFont(new Font("serif", Font.ITALIC, 12));
      copyright.setForeground(new Color(97, 206, 255));
      aboutBaseImageLabel.add(copyright);

      if (Starter.isSupporterEdition())
      {
         JLabel supporterEdition = new JLabel(JResizedIcon.getIcon("SupporterEdition.png", 268, 30));
         supporterEdition.setSize(new Dimension(268, 30));
         supporterEdition.setLocation(320, 160);
         aboutBaseImageLabel.add(supporterEdition);
      }

      JLogo mrLogo = new JLogo(JLogo.Logos.LOGO_MR);
      mrLogo.setSize(new Dimension(80,80));
      mrLogo.setLocation(20, 300);
      aboutBaseImageLabel.add(mrLogo);

      JLogo buymeacoffee = new JLogo(JLogo.Logos.LOGO_BUYMEACOFFEE);
      buymeacoffee.setSize(new Dimension(80,80));
      buymeacoffee.setLocation(20, 400);
      aboutBaseImageLabel.add(buymeacoffee);

      ImageIcon imageNordVPN = new ImageIcon(Starter.class.getResource(NORDVPN_IMAGE));
      Image myImage = imageNordVPN.getImage();
      Image resizedImage = myImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
      JLabel nordVPN =  new JLabel(new ImageIcon(resizedImage));
      nordVPN.setToolTipText("<html><font face=\"sansserif\" color=\"black\">Press the Mouse Button to go to NordVPN Affiliate:<br>https://refer-nordvpn.com/ArNNOfynXcu</font></html>");
      nordVPN.setSize(new Dimension(80,80));
      nordVPN.setLocation(20, 500);
      nordVPN.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      nordVPN.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            try
            {
               UtilSystem.openWebpage(new URI("https://refer-nordvpn.com/ArNNOfynXcu"));
            }
            catch (URISyntaxException e)
            {
               Starter._m_logError.LoggingExceptionAbend(10903, e);
            }
         }
      });
      aboutBaseImageLabel.add(nordVPN);

      ImageIcon imageGeoTools = new ImageIcon(Starter.class.getResource(GEOTOOLS_IMAGE));
      myImage = imageGeoTools.getImage();
      resizedImage = myImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
      JLabel geoTools =  new JLabel(new ImageIcon(resizedImage));
      geoTools.setToolTipText("<html><font face=\"sansserif\" color=\"black\">Press the Mouse Button to go to GeoTools:<br>https://geotools.org/about.html</font></html>");
      geoTools.setSize(new Dimension(80,80));
      geoTools.setLocation(20, 600);
      geoTools.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      geoTools.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            try
            {
               UtilSystem.openWebpage(new URI("https://geotools.org/about.html"));
            }
            catch (URISyntaxException e)
            {
               Starter._m_logError.LoggingExceptionAbend(10903, e);
            }
         }
      });
      aboutBaseImageLabel.add(geoTools);

      ImageIcon imageNaturalEarth = new ImageIcon(Starter.class.getResource(NATURALEARTH_IMAGE));
      myImage = imageNaturalEarth.getImage();
      resizedImage = myImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
      JLabel naturalEarth =  new JLabel(new ImageIcon(resizedImage));
      naturalEarth.setToolTipText("<html><font face=\"sansserif\" color=\"black\">Press the Mouse Button to go to Natural Earth:<br>https://www.naturalearthdata.com/</font></html>");
      naturalEarth.setSize(new Dimension(80,80));
      naturalEarth.setLocation(20, 700);
      naturalEarth.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      naturalEarth.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            try
            {
               UtilSystem.openWebpage(new URI("https://www.naturalearthdata.com/"));
            }
            catch (URISyntaxException e)
            {
               Starter._m_logError.LoggingExceptionAbend(10903, e);
            }
         }
      });
      aboutBaseImageLabel.add(naturalEarth);

      JEditorPane aboutText = new JEditorPane();
      aboutText.setEditable(false);
      aboutText.setCaretColor(Color.WHITE); // hide caret
      aboutText.setContentType("text/html");
      aboutText.setText("<html><body style=\"background-color:white\">" + m_htmlAboutText + "</body></html>");
      aboutText.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e)
         {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
               UtilSystem.openWebpage(e.getURL());
            }
         }
      });
      JScrollPane aboutTextScrollPanel = new JScrollPane(aboutText,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      aboutTextScrollPanel.setBounds(148, 212, 460, 620);
      aboutBaseImageLabel.add(aboutTextScrollPanel);

      // About window
      m_aboutFrame.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
      aboutBaseImageLabel.setToolTipText("Press Mouse Button to close.");
      aboutBaseImageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            close();
         }
      });
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
    * Centers the About Screen and then shows it.
    */
   public void show()
   {
      m_aboutFrame.setLocationRelativeTo(null);
      m_aboutFrame.setVisible(true);
      m_aboutFrame.pack();
   }
}
