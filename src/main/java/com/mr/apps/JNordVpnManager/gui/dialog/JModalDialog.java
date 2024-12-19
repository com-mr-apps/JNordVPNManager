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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

/**
 * Utility class for modal dialog panels.<p>
 * It contains a pre-defined set of common dialog panels [which have all the application frame as parent] like:
 * <ul>
 * <li>Simple Message dialog with Ok Button</li>
 * <li>Ok dialog with Ok Button or free configurable</li>
 * <li>Yes-No dialog with Yes and No Buttons</li>
 * <li>Info, Warning and Error Message dialogs</li>
 * <li>...and many more...</li>
 * </ul>
 * On close, the Starter.setSkipWindowGainedFocus() method is called to avoid the execution of the windowGainedFocus event.
 */
@SuppressWarnings("serial")
public class JModalDialog extends JDialog implements ActionListener
{
   private String  m_result;
   private String  m_buttons;
   private JPanel  m_messagePanel;
   private JPanel  m_buttonsPanel;
   private Timer   m_autoCloseTimer;
   private int     m_iTimer;
   private JButton m_button;

   /**
    * Constructor for any modal dialog
    * <p>
    * This method creates a modal dialog panel with a title, a (single - or multi-line) message and buttons.
    * 
    * @param owner
    *           is the dialog owner (application main frame)
    * @param title
    *           is the dialog message title
    * @param msg
    *           is the dialog message
    * @param buttons
    *           is a comma separated list with the available button names e.g. "Ok,Cancel"
    */
   public JModalDialog(Frame owner, String title, String msg, String buttons)
   {
      super(owner, title, true);
//      Starter._m_logError.TraceDebug("(JModalDialog) " + title + " / Buttons=" + buttons + " / Message=\n" + msg);

      m_autoCloseTimer = null;
      m_buttons = new String(buttons);

      // Dialog Main Window
      getContentPane().setLayout(new BorderLayout());
      setUndecorated(true);
      setResizable(false);
      Point parloc = owner.getLocation();
      setLocation(parloc.x + 30, parloc.y + 30);
      setMinimumSize(new Dimension(120, 20));

      // Dialog Panel
      JPanel dialogPanel = new JPanel(new BorderLayout());
      dialogPanel.setBackground(Color.darkGray);
      dialogPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
            createBevelBorder(BevelBorder.RAISED),BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
      getContentPane().add(dialogPanel, BorderLayout.CENTER);

      // Messages Panel
      m_messagePanel = new JPanel(new BorderLayout());
      m_messagePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      dialogPanel.add(m_messagePanel, BorderLayout.CENTER);
      
      // Title + Message(s)
      JTextArea messageText = new JTextArea();
      messageText.setBorder(new TitledBorder(new LineBorder(Color.gray,2, true), title,
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("SansSerif",Font.BOLD, 12),
            Color.BLACK));
      messageText.setFont(messageText.getFont().deriveFont(Font.ITALIC));
//      messageText.setBackground(Color.lightGray);
//      messageText.setCaretColor(Color.lightGray);
      messageText.setText(msg);
      messageText.setEditable(false);
      m_messagePanel.add(messageText, BorderLayout.CENTER);

      // Buttons
      m_buttonsPanel = new JPanel();
      m_buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      StringTokenizer strtok = new StringTokenizer(m_buttons, ",");
      while (strtok.hasMoreTokens())
      {
         m_button = new JButton(strtok.nextToken());
         m_button.addActionListener(this);
         m_buttonsPanel.add(m_button);
      }
      dialogPanel.add(m_buttonsPanel, BorderLayout.PAGE_END);

      pack();
   }

   /**
    * Action, when a button is pressed.
    */
   public void actionPerformed(ActionEvent event)
   {
      m_result = event.getActionCommand();
      closeDialog();
   }

   /**
    * Close the dialog.
    */
   public void closeDialog()
   {
      if (null != m_autoCloseTimer)
      {
         m_autoCloseTimer.stop();
         m_autoCloseTimer = null;
      }
      Starter.setSkipWindowGainedFocus();
      setVisible(false);
      dispose();
   }

   /**
    * Get the result of which button was pressed
    * 
    * @return the index of the button in the creation list (starting at 0)
    */
   public int getResult()
   {
      int iCnt = 0;

      StringTokenizer strtok = new StringTokenizer(m_buttons, ",");
      while (strtok.hasMoreTokens())
      {
         if (strtok.nextToken().equals(m_result))
         {
            return (iCnt);
         }
         iCnt++;
      }
      return -1;
   }

   public static JModalDialog JOptionDialog(String title, String msg, String button_text)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, button_text);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg;
   }

   public static int OKDialog(String title, String msg, String button_text)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, button_text);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int OKDialog(String title, String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title , msg, "OK");
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int YesNoDialog(String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Question", msg, "Yes,No");
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int YesNoCancelDialog(String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Question", msg, "Yes,No,Cancel");
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int showYesNoDialog(String title, String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, "Yes,No");
      dlg.m_messagePanel.setBackground(new Color(51,153,255));
      dlg.m_buttonsPanel.setBackground(new Color(51,153,255));
      dlg.repaint();
      dlg.setVisible(true);
      return (dlg.getResult() == 0) ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION;
   }
   
   public static int showConfirm(String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Please Confirm", msg, "Yes,No");
      dlg.repaint();
      dlg.setVisible(true);
      return (dlg.getResult() == 0) ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION;
   }
   
   public static int showMessage(String title, String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, "Ok");
      dlg.repaint();
      dlg.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }
   
   /**
    * Message Dialog with Auto Close Countdown
    * <p>
    * Countdown time is set in UserPrefs:
    * <ul>
    * <li>&lt;0 - Auto Close Countdown deactivated</li>
    * <li>=0 - don't show Dialog</li>
    * <li>&gt;0 - Auto Close Countdown in seconds</li>
    * </ul>
    * 
    * @param title
    *           is the message title
    * @param msg
    *           is the message
    * @return JOptionPane.CLOSED_OPTION
    */
   public static int showMessageAutoClose(String title, String msg)
   {
      int iTimer = UtilPrefs.getMessageAutoclose();
      if (0 != iTimer)
      {
         JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, "Ok");
         if (iTimer > 0)
         {
            // ---------------------------------------------------------------------------
            // define and start the timer
            dlg.m_iTimer = iTimer;
            dlg.m_autoCloseTimer = new Timer(1000, new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  if (dlg.m_iTimer <= 1)
                  {
                     dlg.closeDialog();
                  }
                  else
                  {
                     dlg.m_iTimer -= 1;
                     dlg.m_button.setText("Ok (" + dlg.m_iTimer + ")");
                     dlg.m_autoCloseTimer.start();
                  }
               }
            });
            dlg.m_autoCloseTimer.start();
         }

         dlg.m_button.setText("Ok (" + dlg.m_iTimer + ")");
         dlg.repaint();
         dlg.setVisible(true);
      }

      return JOptionPane.CLOSED_OPTION;
   }

   public static int showInfo(String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Information", msg, "Close");
      dlg.repaint();
      dlg.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }
   
   public static int showWarning(String msg)
   {
      Starter._m_logError.LoggingWarning(10303, "Warning Dialog", msg);
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Warning", msg, "Close");
      dlg.m_messagePanel.setBackground(new Color(255,255,153));
      dlg.m_buttonsPanel.setBackground(new Color(255,255,153));
      dlg.repaint();
      dlg.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }
   
   public static int showError(String sShortMsg, String sLongMsg)
   {
      Starter._m_logError.LoggingError(10904, sShortMsg, sLongMsg);
      JModalDialog jmd = new JModalDialog(Starter.getMainFrame(), sShortMsg, sLongMsg, "Close");
      jmd.m_messagePanel.setBackground(new Color(255,102,102));
      jmd.m_buttonsPanel.setBackground(new Color(255,102,102));
      jmd.repaint();
      jmd.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }
}
