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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JFileSelectionBox;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.String.Wrap;

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
   private String            m_result;
   private String            m_buttons;
   private JPanel            m_messagePanel;
   private JPanel            m_buttonsPanel;
   private Timer             m_autoCloseTimer;
   private int               m_iTimer;
   private JButton           m_button;
   private JFileSelectionBox m_dragAndDropPanel;

   /**
    * Constructor for any modal dialog
    * <p>
    * This method creates a modal dialog panel with a title, a (single - or multi-line) message and buttons.
    * 
    * @param owner
    *           is the dialog owner (application main frame)
    * @param title
    *           is the dialog message title
    * @param iconName
    *           is the name of the (optional) icon
    * @param msg
    *           is the dialog message
    * @param buttons
    *           is a comma separated list with the available button names e.g. "Ok,Cancel"
    * @param color
    *           is the dialog background color
    * @param currentDirectory
    *           is the current directory of the file requester (or null for user.home)
    * @param fileFilter
    *           is the file dialog file filter in form "name [ext]" (or null)
    */
   public JModalDialog(Frame owner, String title, String iconName, String msg, String buttons, Color color, File currentDirectory, String fileFilter)
   {
      super(owner, title, true);
//      Starter._m_logError.TraceDebug("(JModalDialog) " + title + " / Buttons=" + buttons + " / Message=\n" + msg);

      m_autoCloseTimer = null;
      m_buttons = new String(buttons);

      // Dialog Main Window
      getContentPane().setLayout(new BorderLayout());
      setUndecorated(true);
      setResizable(false);

      // Dialog Panel
      JPanel dialogPanel = new JPanel(new BorderLayout());
      dialogPanel.setBackground(Color.darkGray);
      dialogPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
            createBevelBorder(BevelBorder.RAISED),BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
      getContentPane().add(dialogPanel, BorderLayout.CENTER);

      if (null != iconName)
      {
         try
         {
            JLabel jl = new JLabel(JResizedIcon.getIcon(iconName, IconSize.SMALL));
            dialogPanel.add(jl, BorderLayout.LINE_START);
         }
         catch (NullPointerException e)
         {
            // Icon not found
         }
      }

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
      messageText.setText(Wrap.wrap(msg.replaceAll("\t", "   "), 200, null, true, null, "   "));
      messageText.setEditable(false);
      m_messagePanel.add(messageText, BorderLayout.CENTER);

      // optional copy (drag&drop) file region
      if (null != currentDirectory)
      {
         m_dragAndDropPanel = new JFileSelectionBox(currentDirectory, fileFilter);
         m_messagePanel.add(m_dragAndDropPanel, BorderLayout.PAGE_END);
      }

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

      if (null != color)
      {
         dialogPanel.setBackground(color);
         messageText.setBackground(color);
         messageText.setCaretColor(color);
         m_messagePanel.setBackground(color);
         m_buttonsPanel.setBackground(color);
      }
      else
      {
         messageText.setCaretColor(Color.white);
      }

      setMinimumSize(new Dimension(120, 20));
      pack();

      // place the dialog [buttons] in the near of the mouse pointer
      Point parloc = new Point(0, 0);
      PointerInfo pointerInfo = MouseInfo.getPointerInfo();
      Point mousePos = pointerInfo.getLocation();
      if (mousePos != null)
      {
         setLocation(Math.max(0, parloc.x + mousePos.x - this.getWidth() / 2), parloc.y + mousePos.y - this.getHeight() + 20);
      }
      else
      {
         setLocation(parloc.x + 30, parloc.y + 30);
      }
   }

   private JModalDialog(Frame owner, String title, String msg, String buttons, Color color)
   {
      this(owner, title, null, msg, buttons, color, null, null);
   }

   private JModalDialog(Frame owner, String title, String iconName, String msg, String buttons, Color color)
   {
      this(owner, title, iconName, msg, buttons, color, null, null);
   }

   /**
    * Action, when a button is pressed.
    * 
    * @param event
    *           is the action event
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
    * @return the index of the button in the buttons creation list (starting at 0)
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

   /**
    * Get the result [file name] from JFileSelectionBox.
    * @return the selected file
    */
   public File getSelectedFile()
   {
      return m_dragAndDropPanel.getSelectedFile();
   }

   /* =============================================================================================
    * Dialogs
    * ============================================================================================= */
   public static JModalDialog JOptionDialog(String title, String msg, String buttons)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, buttons, null);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg;
   }

   /**
    * Generate a Dialog with customizable buttons and file selection.
    * <p>
    * Example:
    * 
    * <pre>
    * <code>
    * JModalDialog dlg = JModalDialog.JDropFileSelectDialog("Title", "Message", "Cancel,Copy,Move", currentDataDir, "Java Archive File [jar]");<br>
    * int rc = dlg.getResult();<br>
    * File fpFile = dlg.getSelectedFile();<br> 
    * </code>
    * </pre>
    * 
    * @param title
    *           is the dialog message title
    * @param msg
    *           is the dialog message
    * @param buttons
    *           is a comma separated list with the available button names e.g. "Ok,Cancel"
    * @param currentDirectory
    *           is the current directory of the file requester (or null for user.home)
    * @param fileFilter
    *           is the file dialog file filter in form "name [ext]" (or null)
    * @return the index of the button in the buttons creation list (starting at 0)
    */
   public static JModalDialog JDropFileSelectDialog(String title, String msg, String buttons, File currentDirectory, String fileFilter)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, null, msg, buttons, null, currentDirectory, fileFilter);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg;
   }

   public static int OKDialog(String title, String msg, String buttons)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, buttons, null);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int OKDialog(String title, String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title , msg, "OK", null);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int YesNoDialog(String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Question", msg, "Yes,No", null);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int YesNoCancelDialog(String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Question", msg, "Yes,No,Cancel", null);
      dlg.repaint();
      dlg.setVisible(true);
      return dlg.getResult();
   }

   public static int showYesNoDialog(String title, String iconName, String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, iconName, msg, "Yes,No", new Color(51,153,255));
      dlg.repaint();
      dlg.setVisible(true);
      return (dlg.getResult() == 0) ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION;
   }

   public static int showYesNoDialog(String title, String msg)
   {
      return showYesNoDialog(title, null, msg);
   }
   
   public static int showConfirm(String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Please Confirm", msg, "Yes,No", null);
      dlg.repaint();
      dlg.setVisible(true);
      return (dlg.getResult() == 0) ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION;
   }
   
   public static int showMessage(String title, String msg)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, "Ok", null);
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
         JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, msg, "Ok", null);
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
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Information", msg, "Close", null);
      dlg.repaint();
      dlg.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }
   
   public static int showWarning(String msg)
   {
      Starter._m_logError.LoggingWarning(10303, "Warning Dialog", msg);
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), "Warning", msg, "Close", new Color(255,255,153));
      dlg.repaint();
      dlg.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }
   
   public static int showError(String sShortMsg, String sLongMsg)
   {
      Starter._m_logError.LoggingError(10904, sShortMsg, sLongMsg);
      JModalDialog jmd = new JModalDialog(Starter.getMainFrame(), sShortMsg, sLongMsg, "Close", new Color(255,102,102));
      jmd.repaint();
      jmd.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }

   public static int showMessage(String title, String msg, String iconName)
   {
      JModalDialog dlg = new JModalDialog(Starter.getMainFrame(), title, iconName, msg, "Close", null);
      dlg.repaint();
      dlg.setVisible(true);
      return JOptionPane.CLOSED_OPTION;
   }

}
