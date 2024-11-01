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
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JSettingsDialog extends JDialog implements ActionListener
{
   private String m_result;
   private String m_buttons = new String("OK,Cancel");
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
         }
      });
      headerPanel.add(jbImport);

      JButton jbExport = new JButton("Export");
      jbExport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // TODO: export a settings file
         }
      });
      headerPanel.add(jbExport);

      JButton jbReset = new JButton("Reset");
      jbExport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // TODO: reset settings
         }
      });
      headerPanel.add(jbReset);

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
