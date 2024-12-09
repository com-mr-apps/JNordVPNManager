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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

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

      JPanel frame = new JPanel();
      frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
      JCheckBox autoConnect = new JCheckBox("GUI Auto Connect");
      autoConnect.setToolTipText("Automatic connect with the current VPN Server on next program start.");
      int iAutoConnect = UtilPrefs.getAutoConnectMode();
      if (1 == iAutoConnect)
      {
         autoConnect.setSelected(true);
      }
      else
      {
         autoConnect.setSelected(false);
      }
      autoConnect.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected())
            {
               UtilPrefs.setAutoConnectMode(1);
            }
            else
            {
               UtilPrefs.setAutoConnectMode(0);
            }
         }
      });
      frame.add(autoConnect);

      JCheckBox autoDisConnect = new JCheckBox("GUI Auto Disconnect");
      autoDisConnect.setToolTipText("Automatic disonnect from VPN Server at program exit.");
      int iAutoDisConnect = UtilPrefs.getAutoDisConnectMode();
      if (1 == iAutoDisConnect)
      {
         autoDisConnect.setSelected(true);
      }
      else
      {
         autoDisConnect.setSelected(false);
      }
      autoDisConnect.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected())
            {
               UtilPrefs.setAutoDisConnectMode(1);
            }
            else
            {
               UtilPrefs.setAutoDisConnectMode(0);
            }
         }
      });
      frame.add(autoDisConnect);
      connectPanel.add(frame, BorderLayout.LINE_START);

      JPauseSlider ps = new JPauseSlider();
      connectPanel.add(ps, BorderLayout.CENTER);

      JPanel mailPanel = new JPanel(new FlowLayout());
      m_jbMail = new JButton();
      m_jbMail.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            NvpnCallbacks.executeLogInOut();
         }
      });
      updateLoginOut(accountData);
      mailPanel.add(m_jbMail);

      connectPanel.add(mailPanel, BorderLayout.LINE_END);

      connectPanel.add(new JSeparator(), BorderLayout.PAGE_END);

      return connectPanel;
   }

   public static void updateLoginOut(NvpnAccountData accountData)
   {
      if (null != accountData && !accountData.isFailed())
      {
         m_jbMail.setText((accountData.isLoggedIn()) ? accountData.getEmail() : "Login");
         m_jbMail.setToolTipText((accountData.isLoggedIn()) ? "Logout from " + accountData.getEmail() : "Login");
      }
      else
      {
         m_jbMail.setText("Login");
         m_jbMail.setToolTipText("Login");
      }
   }
}
