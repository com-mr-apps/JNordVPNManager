/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.connectLine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;

public class GuiConnectLine
{
   private static JButton m_jbMail = null;

   public GuiConnectLine()
   {

   }

   /**
    * Connect Line Panel Layout definition.
    * 
    * @param accountData
    *           is the nordvpn account data
    * @return the created connect line panel
    */
   public JPanel create(NvpnAccountData accountData)
   {
      JPanel connectPanel = new JPanel(new BorderLayout());
      connectPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));

      GuiCommandsToolBar ctb = new GuiCommandsToolBar();
      connectPanel.add(ctb, BorderLayout.LINE_START);

      JPanelConnectTimer ps = new JPanelConnectTimer();
      connectPanel.add(ps, BorderLayout.CENTER);

      JPanel mailPanel = new JPanel(new FlowLayout());
      m_jbMail = new JButton();
      m_jbMail.setBorder(BorderFactory.createRaisedSoftBevelBorder());

      m_jbMail.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            NvpnCallbacks.executeLogInOut();
         }
      });
      mailPanel.add(m_jbMail);

      connectPanel.add(mailPanel, BorderLayout.LINE_END);

      connectPanel.add(new JSeparator(), BorderLayout.PAGE_END);

      return connectPanel;
   }

   /**
    * Update GUI elements dependent on account data.
    * 
    * @param accountData
    *           is the current account data
    */
   public static void updateLoginLogout(NvpnAccountData accountData)
   {
      if (null != accountData && !accountData.isFailed())
      {
//         m_jbMail.setText((accountData.isLoggedIn()) ? accountData.getEmail() : "Login");
         m_jbMail.setIcon((accountData.isLoggedIn()) ? JResizedIcon.getIcon(JResizedIcon.IconUrls.ICON_LOGGED_IN_TO_NORDVPN, JResizedIcon.IconSize.LARGE) : JResizedIcon.getIcon(JResizedIcon.IconUrls.ICON_LOGGED_OUT_FROM_NORDVPN, JResizedIcon.IconSize.LARGE));
         m_jbMail.setBorder((accountData.isLoggedIn()) ? BorderFactory.createLoweredSoftBevelBorder() : BorderFactory.createRaisedSoftBevelBorder());
         m_jbMail.setToolTipText((accountData.isLoggedIn()) ? "Click here to Logout from NordVPN " + accountData.getEmail() : "Click here to Login to NordVPN");
      }
      else
      {
//         m_jbMail.setText("Login");
         m_jbMail.setIcon(JResizedIcon.getIcon(JResizedIcon.IconUrls.ICON_LOGGED_OUT_FROM_NORDVPN, JResizedIcon.IconSize.LARGE));
         m_jbMail.setBorder(BorderFactory.createRaisedSoftBevelBorder());
         m_jbMail.setToolTipText("Click here to Login to NordVPN");
      }
   }
}
