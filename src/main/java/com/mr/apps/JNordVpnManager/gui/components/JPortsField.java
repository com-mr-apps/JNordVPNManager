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

   // possible Protocol values are "TCP", "UDP" or "TCP|UDP" - where empty ("") is "TCP|UDP"
   private String[] m_saProtocolList = new String[] {"TCP","UDP"};

   public JPortsField()
   {
      this(null, null);
   }

   /**
    * Constructor to create a Port Fields component
    * 
    * @param in_textPre
    *           is an optional prefix
    * @param in_textPost
    *           is an optional postfix
    */
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

   /**
    * Get a Text component of the Ports Field Component
    * @param idx is the text field index (1 or 2)
    * @return the text field component for the requested idx
    */
   public JTextField getJTextField(int idx)
   {
      return m_jTextFields.getTextField(idx);
   }

   /**
    * Get the Port Field Component values
    * @return Port From, Port To, Protocol
    */
   public String[] getValues()
   {
      return new String[] {m_jTextFields.getValue(0), m_jTextFields.getValue(1), getProtocolValue()};
   }

   /**
    * Set the Port Field Component values
    * @param in_String are Port From, Port To, Protocol
    */
   public void setValues(String[] in_String)
   {
      m_jTextFields.setValues(in_String);
      int idx[] = getProtocolListIndex(in_String[2]);
      m_jListProtocol.setSelectedIndices(idx);
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

   /**
    * Create the Ports Field Component
    * 
    * @param in_textPre
    *           is a field prefix
    * @param in_textPost
    *           is a field postfix
    * @throws Exception
    */
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

      m_jListProtocol = new JList<String>(m_saProtocolList);
      m_jListProtocol.setLayoutOrientation(JList.HORIZONTAL_WRAP);
      m_jListProtocol.setVisibleRowCount(1);
      m_jListProtocol.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      m_jListProtocol.setToolTipText("Hold Ctrl-Key for multi selection.");
      this.add(m_jListProtocol);

      if (in_textPost != null)
      {
         m_jLabelPost = new JLabel();
         m_jLabelPost.setText(in_textPost);
         this.add(m_jLabelPost, null);
      }
   }

   /**
    * Get the list indices (for set) from the text value
    * 
    * @param value
    *           is the protocol text value
    * @return the indices for list selection
    */
   private int[] getProtocolListIndex(String value)
   {
      if ((null == value) || (value.isBlank()))
      {
         return null;
      }
      else
      {
         if (value.contains(m_saProtocolList[0]) && value.contains(m_saProtocolList[1])) // TCP|UDP
         {
            int[] i = {0,1};
            return i;
         }
         else if (value.equals(m_saProtocolList[0])) // TCP
         {
            int[] i = {0};
            return i;
         }
         else if (value.equals(m_saProtocolList[1])) // UDP
         {
            int[] i = {1};
            return i;
         }
      }
      return null;
   }

   /**
    * Check, if the ports field component is set
    * @return true, if the component has values
    */
   public boolean isSet()
   {
      return (false == m_jTextFields.getValue(0).isBlank());
   }

   /**
    * Get the value for export/import (User Preferences)
    * @return the text field values in export format
    */
   public String export()
   {
      String sPortTo = m_jTextFields.getValue(1);
      if (sPortTo.isBlank())
      {
         return m_jTextFields.getValue(0) + " (" + getProtocolValue() + ")";
      }
      else
      {
         return m_jTextFields.getValue(0) + " - " + m_jTextFields.getValue(1) + " (" + getProtocolValue() + ")";
      }
   }

   /**
    * Get the value for the protocol from the list selection
    * @return the protocol value "TCP" or "UDP" or "TCP|UDP"
    */
   private String getProtocolValue()
   {
      int[] selIndex = m_jListProtocol.getSelectedIndices();
      if ((m_jListProtocol.isSelectionEmpty()) || (selIndex.length == 2))
      {
         // "TCP|UDP"
         return m_saProtocolList[0] + "|" + m_saProtocolList[1];
      }
      else
      {
         // "TCP" or "UDP"
         return m_saProtocolList[selIndex[0]];
      }
   }

   // add ons for changing language
   public void setJLabelPre(String in_text)
   {
      if (m_jLabelPre != null)
      {
         m_jLabelPre.setText(in_text);
      }
   }

   public String toString()
   {
      return m_jTextFields.getValue(0) + ".." + m_jTextFields.getValue(1) + "protocol" + getProtocolValue();
   }
}
