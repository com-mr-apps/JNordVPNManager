/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.dialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class generates a system modal dialog to return a password.<p>
 * Call Sequence is:<br>
 *   JPasswordDialog dialog = new JPasswordDialog(frame);<br>
 *   char[] password = dialog.getPassword();<br>
 *   // Use the password (if not null)...<br>
 *   dialog.clearPassword(); // Clear memory after use<br>
 */
@SuppressWarnings("serial")
public class JPasswordDialog extends JDialog
{
   private JPasswordField              m_passwordField;
   private char                        m_echoChar;
   private JButton                     m_showHideButton;
   private JButton                     m_okButton;
   private JButton                     m_cancelButton;
   private char[]                      m_password        = null;

   private static ArrayList<ImageIcon> m_showHideImages  = new ArrayList<>() {
      {
         add(JResizedIcon.getIcon(IconUrls.ICON_SHOW, IconSize.MEDIUM));
         add(JResizedIcon.getIcon(IconUrls.ICON_HIDE, IconSize.MEDIUM));
      }
   };

   private static String[]             m_showHideToolTip = {
         "Click here to show the password.",
         "Click here to hide the password."
   };

   public JPasswordDialog(JFrame parent, String sTitle, int size)
   {
      // Set up the dialog
      setUndecorated(true);
      setModal(true);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setLayout(new BorderLayout());

      JPanel dialogPanel = new JPanel(new BorderLayout());
      dialogPanel.setBorder(new TitledBorder(new LineBorder(Color.gray,2, true), 
            "Enter sudo password:",
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("SansSerif",Font.BOLD, 12),
            Color.BLACK));
      getContentPane().add(dialogPanel, BorderLayout.CENTER);

      // Initialize components
      JLabel jl = new JLabel(sTitle);
      m_passwordField = new JPasswordField(size);
      m_echoChar = m_passwordField.getEchoChar();
      m_showHideButton = new JButton(m_showHideImages.get(0));
//    m_showHideButton.setText("Show");
      m_showHideButton.setToolTipText(m_showHideToolTip[0]);
      m_showHideButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());

      m_okButton = new JButton("OK");
      m_cancelButton = new JButton("Cancel");

      // Input Panel
      JPanel inputPanel = new JPanel(new BorderLayout());
      inputPanel.add(jl, BorderLayout.PAGE_START);
      inputPanel.add(m_passwordField, BorderLayout.CENTER);
      inputPanel.add(m_showHideButton, BorderLayout.EAST);
      dialogPanel.add(inputPanel, BorderLayout.CENTER);

      // Button Panel
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      buttonPanel.add(m_okButton);
      buttonPanel.add(m_cancelButton);
      dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

      // Action Listeners
      m_showHideButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            char echoChar = m_passwordField.getEchoChar();
            if (echoChar == (char) 0)
            {
               m_passwordField.setEchoChar(m_echoChar);
               m_showHideButton.setIcon(m_showHideImages.get(0));
               m_showHideButton.setToolTipText(m_showHideToolTip[0]);
            }
            else
            {
               m_passwordField.setEchoChar((char) 0);
               m_showHideButton.setIcon(m_showHideImages.get(1));
               m_showHideButton.setToolTipText(m_showHideToolTip[1]);
            }
         }
      });

      m_passwordField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e)
         {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
               // OK button action
               handleOk();
            }
         }
      });

      m_okButton.addActionListener(e -> handleOk());
      m_cancelButton.addActionListener(e -> dispose());

      // Show the dialog
      setLocationRelativeTo(parent);
      pack();
      setVisible(true);
   }

   /**
    * Internal method to handle the OK case
    */
   private void handleOk()
   {
      m_password = new String(m_passwordField.getPassword()).toCharArray();

      Starter.setSkipWindowGainedFocus();
      setVisible(false);
      dispose();
   }

   /**
    * Get the entered password
    * @return the entered password, <CODE>null</CODE> in case of cancel
    */
   public char[] getPassword()
   {
      return m_password;
   }

   /**
    * Clear the password from memory after use
    */
   public void clearPassword()
   {
      if (m_password != null)
      {
         Arrays.fill(m_password, '\0');
         m_password = null;
      }
   }
}