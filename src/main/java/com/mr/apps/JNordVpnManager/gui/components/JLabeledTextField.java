/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.components;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

import java.awt.FlowLayout;

/**
 * Title:        JLabeledTextField
 * Description:
 *          private JLabeledTextField elem_fieldI;
 *          elem_fieldI = new JLabeledTextField(label_pre,label_post);
 *          elem_panel.add(elem_fieldI.get_jPanel());
 *
 *          if labels == null they will not be displayed
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JLabeledTextField extends JPanel
{
   private JLabel     m_jLabelPre  = null;
   private JTextField m_jTextField = null;
   private JLabel     m_jLabelPost = null;

   private boolean    m_isEditable = true;
   private boolean    m_isEnabled  = true;

   public JLabeledTextField()
   {
      this(10, null, null);
   }

   /**
    * Constructor to create a Port Fields component
    * 
    * @param in_size
    *           is the size of the textfield
    * @param in_textPre
    *           is an optional prefix
    * @param in_textPost
    *           is an optional postfix
    */
   public JLabeledTextField(int in_size, String in_textPre, String in_textPost)
   {
      super ();
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setHgap(5);
      flowLayout.setVgap(0);
      this.setLayout(flowLayout);

      create(in_size, in_textPre, in_textPost);
    }

   /**
    * Get a Text component of the labeled Text Field Component
    * 
    * @return the text field component
    */
   public JTextField getJTextField()
   {
      return m_jTextField;
   }

   /**
    * Set the Text Field Component value
    * 
    * @param in_String
    *           is the text to set
    */
   public void setText(String in_String)
   {
      m_jTextField.setText(in_String);
   }

   /**
    * Set the Text Field Component value
    * 
    * @param in_value
    *           is the text[value] to set
    * @param in_format
    *           is the text[value] format to use
    */
   public void setText(double in_value, String in_format)
   {
      if (null == in_format) in_format = "0.0##";
      m_jTextField.setText(StringFormat.number2String(in_value, in_format));
   }

   /**
    * Set the Text Field Component value
    * 
    * @param in_value
    *           is the text[value] to set
    * @param in_format
    *           is the text[value] format to use
    */
   public void setText(int in_value, String in_format)
   {
      if (null == in_format) in_format = "0";
      m_jTextField.setText(StringFormat.int2String(in_value, in_format));
   }

   public void setVisible(boolean fVisible)
   {
      if (m_jLabelPre != null) m_jLabelPre.setVisible(fVisible);
      if (m_jLabelPost != null) m_jLabelPost.setVisible(fVisible);
      m_jTextField.setVisible(fVisible);
   }

   public void setEditable(boolean isEditable)
   {
      m_jTextField.setEditable(isEditable);
      m_isEditable = isEditable;
      return;
   }

   public boolean isEditable(JIntegerTextField jitf)
   {
      return m_isEditable;
   }

   public void setEnabled(boolean fEnable)
   {
      m_jTextField.setEnabled(fEnable);
      m_isEnabled = m_isEditable = fEnable;
      return;
   }

   public boolean isEnabled()
   {
      return m_isEnabled;
   }

   /**
    * Create the Text Field Component
    * 
    * @param in_textPre
    *           is a field prefix
    * @param in_textPost
    *           is a field postfix
    * @throws Exception
    */
   private void create(int size, String in_textPre, String in_textPost)
   {
      if (in_textPre != null)
      {
         m_jLabelPre = new JLabel();
         m_jLabelPre.setText(in_textPre);
         this.add(m_jLabelPre, null);
      }

      m_jTextField = new JTextField(size);
      m_jTextField.setMaximumSize(m_jTextField.getPreferredSize());
      this.add(m_jTextField, null);

      if (in_textPost != null)
      {
         m_jLabelPost = new JLabel();
         m_jLabelPost.setText(in_textPost);
         this.add(m_jLabelPost, null);
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
      return m_jTextField.getText();
   }
}
