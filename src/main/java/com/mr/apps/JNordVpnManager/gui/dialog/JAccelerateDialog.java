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

public class JAccelerateDialog
{
   private static final String m_htmlAboutText = "<h2>JNordVPN Manager Accelerate Development Feature.</h2>"
         + "Functionality that is not planned yet. The start of implementation can be accelerated by <a href=\"https://buymeacoffee.com/3dprototyping\">[donations]</a>."
         + "<h3>List of Accelerate Development Features:</h3>"
         + "<ul>"
         + "<li><strong>Support NordVPN Recommended Servers</strong>: Filter Servers by load index (speed) from NordVPN recommended servers list. <a href=\"https://github.com/com-mr-apps/JNordVPNManager/issues/16\">[GitHub Issue #16]</a>.</li>"
         + "<li><strong>Support Allow-, Whitelist</strong>: Manage allowlist for ports and subnets. <a href=\"https://github.com/com-mr-apps/JNordVPNManager/issues/17\">[GitHub Issue #17]</a>.</li>"
         + "<li><strong>Support Meshnet</strong>: NordVPN Meshnet Settings/Management. <a href=\"https://github.com/com-mr-apps/JNordVPNManager/issues/18\">[GitHub Issue #18]</a>.</li>"
         + "<li><strong>List of Favorite Server Connections</strong>: switch server treelist to user defined Favorite Server Connections (add/delete).</li>"
         + "</ul>";

   private JFrame       m_aboutFrame = null;

   /**
    * Initiates a new About Screen
    */
   public JAccelerateDialog()
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
