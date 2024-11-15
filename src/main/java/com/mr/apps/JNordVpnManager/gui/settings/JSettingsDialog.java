/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

@SuppressWarnings("serial")
public class JSettingsDialog extends JDialog implements ActionListener
{
   private String m_result;
   private String m_buttons = new String("Cancel"); // TODO: new String("OK,Cancel");
   private JSettingsPanel m_settingsPanel = null;

   /**
    * Server List Panel Layout definition.
    */
   public JSettingsDialog(Frame owner)
   {
      super(owner, "Settings", true);

      getContentPane().setLayout(new BorderLayout());
      setResizable(false);
      Point parloc = owner.getLocation();
      setLocation(parloc.x + 30, parloc.y + 30);

      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            close();
         }
      });

      // ---------------------------------------------------------------------------------------------
      // Header
      // ---------------------------------------------------------------------------------------------
      JPanel headerPanel = new JPanel();
      headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      JButton jbImport = new JButton("Import");
      jbImport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // TODO: import a settings file
            UtilPrefs.importPreferences("filename");
         }
      });
//      headerPanel.add(jbImport);

      JButton jbExport = new JButton("Export");
      jbExport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // TODO: export a settings file
            UtilPrefs.exportPreferences("filename");
         }
      });
//      headerPanel.add(jbExport);

      JButton jbReset = new JButton("Reset");
      jbReset.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            if (JModalDialog.showConfirm("This will reset all settings to their default values.") == JOptionPane.YES_OPTION)
            {
               // TODO: reset settings
               UtilPrefs.resetPreferences();
               getContentPane().remove(m_settingsPanel);
               m_settingsPanel = new JSettingsPanel();
               getContentPane().add(m_settingsPanel,BorderLayout.CENTER);
               repaint();
            }
         }
      });
//      headerPanel.add(jbReset);

      getContentPane().add(headerPanel,BorderLayout.PAGE_START);

      m_settingsPanel = new JSettingsPanel();
      getContentPane().add(m_settingsPanel,BorderLayout.CENTER);
       
      //Buttons
      JPanel buttonsPanel = new JPanel();
      buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      StringTokenizer strtok = new StringTokenizer(m_buttons,",");
      while (strtok.hasMoreTokens())
      {
         JButton button = new JButton(strtok.nextToken());
         button.addActionListener(this);
         buttonsPanel.add(button);
      }
      getContentPane().add(buttonsPanel,BorderLayout.PAGE_END);

      pack();
      setVisible(true);
      repaint();
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      m_result = e.getActionCommand();
      close();
   }

   private void close()
   {
      Starter.setSkipWindowGainedFocus();
      setVisible(false);
      dispose();
   }

   public int getResult()
   {
      int iCnt = 0;

      StringTokenizer strtok = new StringTokenizer(m_buttons, ",");
      while (strtok.hasMoreTokens())
      {
         if (strtok.nextToken().equals(m_result))
         {
            break;
         }
         iCnt++;
      }

      if (0 == iCnt)
      {
         // ok: Update Preferences
         m_settingsPanel.updatePrefs();
      }
      return 0;
   }

}
