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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JLogo;
import com.mr.apps.JNordVpnManager.utils.UtilCallbacks;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

@SuppressWarnings("serial")
public class JSupportersDialog extends JFrame
{
   private static final String m_htmlTitleText = "<p>I would like to thank the supporters of my work and wish you much joy with the application.<br>"
         + "<em>To become a supporter and get a legal version of the add-on library together with supporters specific content,"
         + "others are invited to visit me at Buy Me A Coffee</em>.</p>";

   private static final String m_htmlBodyText = "<h1>Support My Open-Source Software Development</h1>"
//         + "<p>Hello, I&#39;m the creator of <a href=\"https://github.com/com-mr-apps\">com-mr-apps</a>, a seasoned software designer &amp; programmer with over 30 years of experience in creating high-quality, usable software solutions. As a strong advocate for open-source software, I&#39;ve dedicated my career to developing applications that benefit the community.</p>"
         
//         + "<h2>My Open-Source Projects</h2>"
//         + "<p>Through my <a href=\"https://github.com/com-mr-apps\">GitHub profile</a>, you can find various open-source projects, including this application: <a href=\"https://github.com/com-mr-apps/JNordVPNManager\">J NordVPN Manager</a> (more will follow). These projects aim to provide innovative solutions to everyday problems and foster collaboration among developers.</p>"

         + "<h2>Why Your Support Matters</h2>"
         + "<p>Donations are required for the ongoing development and maintenance of these applications. Your donation will not only help sustain my work but also provide a financial incentive for further improving and maintaining the software. This, in turn, will lead to:</p>"
         + "<ul>"
         + "<li>More frequent updates with new features</li>"
         + "<li>Enhanced stability and security</li>"
         + "<li>Broader community engagement and support</li>"
         + "</ul>"

         + "<h2>How Your Donations Will Be Used</h2>"
         + "<p>Your contributions will directly support the development of new features, bug fixes, and documentation for my open-source projects. This will help me allocate more time for creating innovative solutions, rather than focusing on administrative tasks.</p>"

         + "<h3>What Is The JNordVPN Manager Supporters Edition</h3>"
         + "<h4>Features that are available exclusive for <em>my supporters</em>:</h4>"
         + "<ul>"
         + "<li><strong>Allow-, Whitelist</strong>: Manage allowlist for ports and subnets.</li>"
         + "<li><strong>Timer Reconnect</strong>: Time-based automatic [re]connect to VPN location.</li>"
         + "</ul>"
         + "<h4>...planned features are:</h4>"
         + "<ul>"
         + "<li><strong>NordVPN Recommended Servers</strong>: Filter Servers by load index (speed) from NordVPN recommended servers list. <a href=\"https://github.com/com-mr-apps/JNordVPNManager/issues/16\">[GitHub Issue #16]</a>.</li>"
         + "<li><strong>Meshnet</strong>: NordVPN Meshnet Settings/Management. <a href=\"https://github.com/com-mr-apps/JNordVPNManager/issues/18\">[GitHub Issue #18]</a>.</li>"
         + "<li><strong>Cycle Connections</strong>: Time-based automatic [re]connect to a list of defined VPN servers.</li>"
         + "<li><strong>Speedtest</strong>: check/show connection speeds.</li>"
         + "<li><em>...further suggestions are welcome...</em>"
         + "</ul>"

         + "<h2>Join Me in Creating a Better Software Community</h2>"
         + "<p>By supporting my work, you&#39;ll be contributing to the growth and improvement of the software community as a whole. I&#39;m grateful for your support and look forward to collaborating with you!</p>"

         + "<h3>Where you can support me:</h3>"
         + "<ul>"
         + "<li><a href=\"https://buymeacoffee.com/3dprototyping\">Donations and Supporters Membership at Buy Me A Coffee</a></li>"
         + "<li><a href=\"https://github.com/sponsors/com-mr-apps\">GitHub Sponsorship</a></li>"
         + "</ul>";

   /**
    * Initiates a new Supporters Dialog
    */
   public JSupportersDialog()
   {
      this(null);
   }

   /**
    * Initiates a new Supporters Dialog
    * 
    * @param sFeature
    *           is a feature name for the title
    */
   public JSupportersDialog(String sFeature)
   {
      super();
      if (null == sFeature || sFeature.isBlank())
      {
         this.setTitle("Support JNordVPN Manager");
      }
      else
      {
         this.setTitle("Support JNordVPN Manager - Feature: " + sFeature);
      }
      this.setLayout(new BorderLayout());
//      this.setResizable(false);
      // Close Window with "X"
      this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      this.addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            close();
         }
      });

      /*
       *  Title
       */
      JPanel jpTitle = new JPanel();
      jpTitle.setLayout(new BoxLayout(jpTitle, BoxLayout.X_AXIS));
      jpTitle.setBackground(new Color(247, 217, 146));
      JLogo coffeeLogo = new JLogo(JLogo.Logos.LOGO_BUYMEACOFFEE);
      JLogo mrLogo = new JLogo(JLogo.Logos.LOGO_MR);

      JEditorPane editorTitlePane = new JEditorPane();
      editorTitlePane.setEditable(false);
      editorTitlePane.setCaretColor(new Color(247, 217, 146)); // hide caret
      editorTitlePane.setBackground(new Color(247, 217, 146));
      editorTitlePane.setContentType("text/html");
      editorTitlePane.setText("<html><body style=\"background-color: #f7d992 \">" + m_htmlTitleText + "</body></html>");
      editorTitlePane.setCaretPosition(0);
      editorTitlePane.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e)
         {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
               UtilSystem.openWebpage(e.getURL());
            }
         }
      });

      JButton jbManageSupporterEdition = new JButton("Manage Supporter Edition");
      jbManageSupporterEdition.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            UtilCallbacks.cbManageSupporterEdition();
         }
      });
      
      // Layout title
      jpTitle.add(mrLogo);
      jpTitle.add(Box.createRigidArea(new Dimension(20, 0)));
      jpTitle.add(Box.createHorizontalGlue());
      if (Starter.isSupporterEdition())
      {
         jpTitle.add(editorTitlePane);
         jpTitle.add(Box.createHorizontalGlue());
      }
      jpTitle.add(jbManageSupporterEdition);
      jpTitle.add(Box.createRigidArea(new Dimension(20, 0)));
      jpTitle.add(coffeeLogo);

      /*
       *  Body 
       */
      JEditorPane editorTextPane = new JEditorPane();
      editorTextPane.setEditable(false);
      editorTextPane.setCaretColor(new Color(247, 217, 146)); // hide caret
      editorTextPane.setBackground(new Color(247, 217, 146));
      editorTextPane.setContentType("text/html");
      editorTextPane.setText("<html><body style=\"background-color: #f7d992 \">" + m_htmlBodyText + "</body></html>");
      editorTextPane.setCaretPosition(0);
      editorTextPane.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e)
         {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
               UtilSystem.openWebpage(e.getURL());
            }
         }
      });
      JScrollPane whatsTextScrollPanel = new JScrollPane(editorTextPane,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      /*
       *  Layout Dialog
       */
      this.add(jpTitle, BorderLayout.PAGE_START);
      this.add(whatsTextScrollPanel, BorderLayout.CENTER);

      this.setPreferredSize(new Dimension(800,400));
      this.setLocationRelativeTo(null);
      this.pack();
      this.setVisible(true);
   }

   /**
    * Close the Window.<p>
    * Return to main frame and suppress gained focus event.
    */
   private void close()
   {
      Starter.setSkipWindowGainedFocus();
      this.setVisible(false);
      this.dispose();
   }
}
