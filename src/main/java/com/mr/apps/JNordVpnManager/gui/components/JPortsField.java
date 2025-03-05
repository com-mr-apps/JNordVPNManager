/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.components;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import com.mr.apps.JNordVpnManager.Starter;

import java.awt.FlowLayout;

/**
 * Title:        JPortsField
 * Description:
 *          private JPortsField elem_fieldI;
 *          elem_fieldI = new JPortsField(label_pre,label_post);
 *          elem_panel.add(elem_fieldI.get_jPanel());
 *
 *          if labels == null they will not be displayed
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JPortsField extends JPanel
{
   private JLabel              m_jLabelPre        = null;
   private JIntegerRangeField  m_jTextFields      = null;
   private JList<String>       m_jListProtocol    = null;
   private JLabel              m_jLabelProtocol   = null;
   private JLabel              m_jLabelPost       = null;

   private boolean             m_isEditable       = true;
   private boolean             m_isEnabled        = true;

   private String[] m_saList = new String[] {"TCP","UDP"};

   public JPortsField()
   {
      this(null, null);
   }

   public JPortsField(String in_textPre, String in_textPost)
   {
      super ();
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setHgap(5);
      flowLayout.setVgap(0);
      this.setLayout(flowLayout);

      try
      {
         create(in_textPre, in_textPost);
      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingExceptionMessage(5, 10998, e);
      }
   }

   public JTextField getJTextField(int idx)
   {
      return m_jTextFields.getTextField(idx);
   }

   public String[] getValues()
   {
      return new String[] {m_jTextFields.getValue(0), m_jTextFields.getValue(1), m_saList[m_jListProtocol.getSelectedIndex()]};
   }

   public void setValues(String[] in_String)
   {
      m_jTextFields.setValues(in_String);
      int idx = getListIndex(m_saList, in_String[2]);
      m_jListProtocol.setSelectedIndex(idx);
   }

   public void setVisible(boolean fVisible)
   {
      if (m_jLabelPre != null) m_jLabelPre.setVisible(fVisible);
      if (m_jLabelPost != null) m_jLabelPost.setVisible(fVisible);
      m_jTextFields.setVisible(fVisible);
      m_jListProtocol.setVisible(fVisible);
      m_jLabelProtocol.setVisible(fVisible);
   }

   public void setEditable(boolean isEditable)
   {
      m_jTextFields.setEditable(isEditable);
      m_jListProtocol.setFocusable(isEditable);
      m_isEditable = isEditable;
      return;
   }

   public boolean isEditable(JIntegerTextField jitf)
   {
      return m_isEditable;
   }

   public void setEnabled(boolean fEnable)
   {
      m_jTextFields.setEnabled(fEnable);
      m_jListProtocol.setEnabled(fEnable);
      m_isEnabled = m_isEditable = fEnable;
      return;
   }

   public boolean isEnabled()
   {
      return m_isEnabled;
   }

   private void create(String in_textPre, String in_textPost) throws Exception
   {
      if (in_textPre != null)
      {
         m_jLabelPre = new JLabel();
         m_jLabelPre.setText(in_textPre);
         this.add(m_jLabelPre, null);
      }

      m_jTextFields = new JIntegerRangeField(null, null, 5);
      this.add(m_jTextFields, null);

      m_jLabelProtocol = new JLabel("protocol");
      this.add(m_jLabelProtocol);

      m_jListProtocol = new JList<String>(m_saList);
      m_jListProtocol.setLayoutOrientation(JList.HORIZONTAL_WRAP);
      m_jListProtocol.setVisibleRowCount(1);
      m_jListProtocol.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      this.add(m_jListProtocol);

      if (in_textPost != null)
      {
         m_jLabelPost = new JLabel();
         m_jLabelPost.setText(in_textPost);
         this.add(m_jLabelPost, null);
      }
   }

   private int getListIndex(String[] list, String value)
   {
      int idx = 0;
      for (String val : list)
      {
         if (val.equals(value)) return idx;
         idx++;
      }
      return 0;
   }

   public boolean isSet()
   {
      return (false == m_jTextFields.getValue(0).isBlank());
   }

   public String toString()
   {
      return m_jTextFields.getValue(0) + ".." + m_jTextFields.getValue(1) + "protocol" + m_jTextFields.getValue(2);
   }

   // add ons for changing language
   public void setJLabelPre(String in_text)
   {
      if (m_jLabelPre != null)
      {
         m_jLabelPre.setText(in_text);
      }
   }
}
