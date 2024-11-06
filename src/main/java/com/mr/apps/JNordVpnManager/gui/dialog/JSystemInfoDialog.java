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
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.mr.apps.JNordVpnManager.Starter;

import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class JSystemInfoDialog extends JDialog implements ActionListener
{
   public JSystemInfoDialog(Frame owner, String title)
   {
      super(owner, title, true);

      JTabbedPane jTabbedInfoPane1 = new JTabbedPane();
      JPanel system_panel = new JPanel();

      setBackground(Color.lightGray);
      getContentPane().setLayout(new BorderLayout());
//      setResizable(false);

      // Tab System
      system_panel.setLayout(new BorderLayout());
      Object[][] system_data = {
            {
                  "os.name", System.getProperty("os.name")
            },
            {
                  "os.arch", System.getProperty("os.arch")
            },
            {
                  "os.version", System.getProperty("os.version")
            },
            {
                  "java.home", System.getProperty("java.home")
            },
            {
                  "java.version", System.getProperty("java.version")
            },
            {
                  "java.runtime.name", System.getProperty("java.specification.name")
            },
            {
                  "java.runtime.version", System.getProperty("java.specification.version")
            },
            {
                  "java.class.path", System.getProperty("java.class.path")
            },
            {
                  "user.name", System.getProperty("user.name")
            },
            {
                  "user.home", System.getProperty("user.home")
            },
            {
                  "user.dir", System.getProperty("user.dir")
            },
            {
                  "user.region", System.getProperty("user.region")
            },
            {
                  "user.timezone", System.getProperty("user.timezone")
            }
      };

      String[] system_columnNames = {
            "Name", "Value"
      };

      JTable system_table = new JTable(system_data, system_columnNames);
      system_table.setEnabled(false);
//      system_table.setPreferredScrollableViewportSize(new Dimension(470, 260));
      TableColumn system_column = null;
      system_column = system_table.getColumnModel().getColumn(0);
      system_column.setPreferredWidth(120);
      system_column = system_table.getColumnModel().getColumn(1);
      system_column.setPreferredWidth(350);
      JScrollPane system_scrollPane = new JScrollPane(system_table);
      system_panel.add(system_scrollPane, BorderLayout.CENTER);

      jTabbedInfoPane1.addTab("System Info", system_panel);

      getContentPane().add(jTabbedInfoPane1);
      pack();

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension panelSize = getSize();
      setLocation((screenSize.width / 2) - (panelSize.width / 2), (screenSize.height / 2) - (panelSize.height / 2));
   }

   public void actionPerformed(ActionEvent event)
   {
      Starter.setSkipWindowGainedFocus();
      setVisible(false);
      dispose();
   }
}
