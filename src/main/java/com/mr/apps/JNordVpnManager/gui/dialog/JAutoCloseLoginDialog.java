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
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;

@SuppressWarnings("serial")
public class JAutoCloseLoginDialog extends JDialog implements ActionListener
{
   private Timer m_timer = null;

   /**
    * Create a Dialog Window for login.
    * <p>
    * The dialog closes automatically, if login was successfully.
    */
   public JAutoCloseLoginDialog(Frame owner, String msg)
   {
      super(owner, "Waiting for NordVPN login...", true);

      getContentPane().setLayout(new BorderLayout());
      ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      setResizable(false);
      Point parloc = owner.getLocation();
      setLocation(parloc.x + 50, parloc.y + 50);
      //setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

      JEditorPane message = new JEditorPane();
      message.setEditable(false);
      message.setCaretColor(Color.WHITE);
      message.setContentType( "text/html" );    
      message.setText("<html><font face=\"serif\" color=\"black\">" + msg + "</font><p>" + 
            "(If the Browser does not open automatically, the URL can be pasted from Clipboard [Ctrl+p])<p>" + 
            "<em>This dialog closes automatically after successful login in the external Browser!</em>" + 
            "</font></html>");
      getContentPane().add(message,BorderLayout.CENTER);

      JButton button = new JButton("Cancel Login");
      button.addActionListener(this);
      getContentPane().add(button,BorderLayout.PAGE_END);

      pack();

      // ---------------------------------------------------------------------------
      // define and start the timer
      // check every second, if login was successful.
      m_timer = new Timer(2000, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            NvpnAccountData accountData = new NvpnAccountData();
            boolean loginStatus =  accountData.isLoggedIn();
            if (true == loginStatus)
            {
               setVisible(false);
               dispose();
               m_timer.stop();
            }
            else
            {
               m_timer.start();
            }
         }
      });
      m_timer.start();

      setVisible(true);
}

   @Override
   public void actionPerformed(ActionEvent e)
   {
      m_timer.stop();
      Starter.setSkipWindowGainedFocus();

      // result = e.getActionCommand();
      setVisible(false);
      dispose();
   }
}
