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
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
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
      setBackground(Color.lightGray);
//      setResizable(false);

      /*
       *  Tab System
       */

      // Table Header and data
      String[] systemTableHeader = {
            "Name", "Value"
      };

      Object[][] systemTableData = {
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

      // create the table with name, value
      JTable systemTable = new JTable(systemTableData, systemTableHeader);
      systemTable.setEnabled(false);

      // set column widths
      TableColumn systemTableColumn = null;
      systemTableColumn = systemTable.getColumnModel().getColumn(0);
      systemTableColumn.setPreferredWidth(120);
      systemTableColumn.setMinWidth(100);
      systemTableColumn.setMaxWidth(systemTableColumn.getMaxWidth());

      systemTableColumn = systemTable.getColumnModel().getColumn(1);
      systemTableColumn.setPreferredWidth(systemTable.getMaximumSize().width); // required for horizontal scroll bar

      // make the complete table scrollable
      JPanel systemTablePanel = new JPanel();
      systemTablePanel.setLayout(new BorderLayout());
      systemTablePanel.add(systemTable, BorderLayout.CENTER);
      systemTablePanel.add(systemTable);
      systemTablePanel.add(systemTable.getTableHeader(), BorderLayout.PAGE_START);
      JScrollPane systemTablePanelScrollPane = new JScrollPane(systemTablePanel);

      /*
       * Tab Environment
       */

      // get data [sorted]
      Map<String, String> envVarMap = System.getenv();
      TreeMap<String, String> sortedMap = new TreeMap<String, String>(envVarMap);

      //create header for the table
      Vector<String> envTableHeader;
      envTableHeader = new Vector<String>();
      envTableHeader.add("Name"); 
      envTableHeader.add("Value");

      // create the data for the table
      Vector<Vector<String>> envTableData = new Vector<Vector<String>>();
      for (var entry : sortedMap.entrySet())
      {
         Vector<String> rowData = new Vector<String>();
         rowData.add(entry.getKey());
         rowData.add(entry.getValue());
         envTableData.add(rowData);
      }

      // create the table with header name, value and data
      DefaultTableModel envTableModel = new DefaultTableModel(envTableData, envTableHeader);
      JTable envTable = new JTable(envTableModel);
      envTable.setEnabled(false);

      // set column widths
      TableColumn envTableColumn = null;
      envTableColumn = envTable.getColumnModel().getColumn(0);
      envTableColumn.setPreferredWidth(220);
      envTableColumn.setMinWidth(100);
      envTableColumn.setMaxWidth(envTableColumn.getMaxWidth());

      envTableColumn = envTable.getColumnModel().getColumn(1);
      envTableColumn.setPreferredWidth(envTableColumn.getMaxWidth()); // required for horizontal scroll bar
      envTableColumn.setMaxWidth(1000);

      // make the complete table scrollable
      JPanel envTablePanel = new JPanel();
      envTablePanel.setLayout(new BorderLayout());
      envTablePanel.add(envTable, BorderLayout.CENTER);
      envTablePanel.add(envTable);
      envTablePanel.add(envTable.getTableHeader(), BorderLayout.PAGE_START);
      JScrollPane envTablePanelScrollPane = new JScrollPane(envTablePanel);

      /*
       *  add the tabs
       */
      jTabbedInfoPane1.add(systemTablePanelScrollPane, "System Info");
      jTabbedInfoPane1.add(envTablePanelScrollPane, "Environment");
      jTabbedInfoPane1.setPreferredSize(new Dimension(480,250));
      getContentPane().add(jTabbedInfoPane1);

      // pack and set minimum size
      pack();
      setMinimumSize(getSize());

      // position
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
