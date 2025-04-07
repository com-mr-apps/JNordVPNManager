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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mr.apps.JNordVpnManager.Starter;

import java.awt.FlowLayout;
import java.awt.event.FocusEvent;

/**
 * Title:        JIntegerRangeField
 * Description:
 *          private JIntegerSRangeField elem_fieldI;
 *          elem_fieldI = new CIntegerRangeField(label_pre,label_post,iLength);
 *          elem_panel.add(elem_fieldI.get_jPanel());
 *
 *          if labels == null they will not be displayed
 *          iLength is the fields length
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JSubnetAddressField extends JPanel
{
   private JLabel              m_jLabelPre        = null;
   private JIntegerTextField[] m_jTextFields      = null;
   private JLabel[]            m_jLabelDelimiters = null;
   private JLabel              m_jLabelPost       = null;

   private boolean             m_isEditable       = true;
   private boolean             m_isEnabled        = true;

   public JSubnetAddressField()
   {
      this(null, null);
   }

   public JSubnetAddressField(String in_textPre, String in_textPost)
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
      return m_jTextFields[idx];
   }

   public String[] getValues()
   {
      return new String[] {m_jTextFields[0].getText(), m_jTextFields[1].getText(), m_jTextFields[2].getText(), m_jTextFields[3].getText(), m_jTextFields[4].getText()};
   }

   public void setValues(String[] in_String)
   {
      for (int i = 0 ; i<5; i++)
      {
         m_jTextFields[i].setText(in_String[i]);
      }
   }

   public void setVisible(boolean fVisible)
   {
      if (m_jLabelPre != null) m_jLabelPre.setVisible(fVisible);
      if (m_jLabelPost != null) m_jLabelPost.setVisible(fVisible);
      for (int i = 0 ; i<5; i++)
      {
         m_jTextFields[i].setVisible(fVisible);
         m_jLabelDelimiters[i].setVisible(fVisible);
      }
   }

   public void setEditable(boolean isEditable)
   {
      for (int i = 0 ; i<5; i++)
      {
         m_jTextFields[i].setEditable(isEditable);
      }
      m_isEditable = isEditable;
      return;
   }

   public boolean isEditable(JIntegerTextField jitf)
   {
      return m_isEditable;
   }

   public void setEnabled(boolean fEnable)
   {
      for (int i = 0 ; i<5; i++)
      {
         m_jTextFields[i].setEnabled(fEnable);
      }
      m_isEnabled = m_isEditable = fEnable;
      return;
   }

   public boolean isEnabled()
   {
      return m_isEnabled;
   }

   private void create(String in_textPre, String in_textPost) throws Exception
   {
      m_jTextFields = new JIntegerTextField[5];
      m_jLabelDelimiters = new JLabel[4];

      String[] sDelimiter = new String[] {".", ".", ".", "/"};
      Integer[] sLength = new Integer [] {3,3,3,3,3}; 
      for (int i = 0 ; i<5; i++)
      {
         m_jTextFields[i] = new JIntegerTextField();
         m_jTextFields[i].setColumns(sLength[i]);
         m_jTextFields[i].setHorizontalAlignment(SwingConstants.RIGHT);
         // automatic select all, when focus gained
         m_jTextFields[i].addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent e)
            {
               jTextField_focusGained(e);
            }
         });
         // automatic transfer focus to next field after 3 digits inserted
         m_jTextFields[i].addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
               JIntegerTextField c = (JIntegerTextField)evt.getSource();
               if (c.getText().length() >= 3)
               {
                  c.transferFocus();
               }
            }
         });

         if (i < 4) m_jLabelDelimiters[i] = new JLabel(sDelimiter[i]); 
      }

      if (in_textPre != null)
      {
         m_jLabelPre = new JLabel();
         m_jLabelPre.setText(in_textPre);
         this.add(m_jLabelPre, null);
      }
      for (int i = 0 ; i<5; i++)
      {
         this.add(m_jTextFields[i], null);
         if (i < 4) this.add(m_jLabelDelimiters[i], null);
      }

      if (in_textPost != null)
      {
         m_jLabelPost = new JLabel();
         m_jLabelPost.setText(in_textPost);
         this.add(m_jLabelPost, null);
      }
   }

   private void jTextField_focusGained(FocusEvent e)
   {
      JIntegerTextField jtf = (JIntegerTextField) e.getSource();
      jtf.selectAll();
   }

   public JTextField getTextField(int idx)
   {
      return m_jTextFields[idx];
   }

   public boolean isSet()
   {
      for (int i = 0 ; i<5; i++)
      {
         if (m_jTextFields[i].getText().isBlank()) return false;
      }
      return true;
   }

   public String toString()
   {
      return m_jTextFields[0].getText()
            + "." + m_jTextFields[1].getText()
            + "." + m_jTextFields[2].getText()
            + "." + m_jTextFields[3].getText()
            + "/" + m_jTextFields[4].getText();
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
