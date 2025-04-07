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
 *          private JIntegerRangeField elem_fieldI;
 *          elem_fieldI = new JIntegerRangeField(label_pre,label_post,iLength);
 *          elem_panel.add(elem_fieldI.get_jPanel());
 *
 *          if labels == null they will not be displayed
 *          iLength is the fields length
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JIntegerRangeField extends JPanel
{
   private JLabel              m_jLabelPre   = null;
   private JIntegerTextField[] m_jTextFields = null;
   private JLabel              m_jLabelDots  = null;
   private JLabel              m_jLabelPost  = null;

   private boolean             m_isEditable  = true;
   private boolean             m_isEnabled   = true;

   public JIntegerRangeField()
   {
      this(null, null, 3);
   }

   public JIntegerRangeField(String in_textPre, String in_textPost, int in_length)
   {
      super ();
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setHgap(5);
      flowLayout.setVgap(0);
      this.setLayout(flowLayout);

      try
      {
         create(in_textPre, in_textPost, in_length);
      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingExceptionMessage(5, 10998, e);
      }
   }

   public JTextField getJTextFieldFrom()
   {
      return m_jTextFields[0];
   }

   public JTextField getJTextFieldTo()
   {
      return m_jTextFields[1];
   }

   public String[] getValues()
   {
      return new String[] {m_jTextFields[0].getText(), m_jTextFields[1].getText()};
   }

   public String getValue(int idx)
   {
      return m_jTextFields[idx].getText();
   }

   public void setValues(String[] in_String)
   {
      m_jTextFields[0].setText(in_String[0]);
      m_jTextFields[1].setText(in_String[1]);
   }

   public void selectAll()
   {
      m_jTextFields[0].selectAll();
   }

   public void requestFocus()
   {
      m_jTextFields[0].requestFocus();
   }

   public void setVisible(boolean fVisible)
   {
      if (m_jLabelPre != null) m_jLabelPre.setVisible(fVisible);
      if (m_jLabelPost != null) m_jLabelPost.setVisible(fVisible);
      if (m_jLabelDots != null) m_jLabelDots.setVisible(fVisible);
      m_jTextFields[0].setVisible(fVisible);
      m_jTextFields[1].setVisible(fVisible);
   }

   public void setEditable(boolean isEditable)
   {
      m_jTextFields[0].setEditable(isEditable);
      m_jTextFields[1].setEditable(isEditable);
      m_isEditable = isEditable;
      return;
   }

   public boolean isEditable(JIntegerTextField jitf)
   {
      return m_isEditable;
   }

   public void setEnabled(boolean fEnable)
   {
      m_jTextFields[0].setEnabled(fEnable);
      m_jTextFields[0].setEditable(fEnable);
      m_jTextFields[1].setEnabled(fEnable);
      m_jTextFields[1].setEditable(fEnable);
      m_isEnabled = m_isEditable = fEnable;
      return;
   }

   public boolean isEnabled()
   {
      return m_isEnabled;
   }

   private void create(String in_textPre, String in_textPost, int iLength) throws Exception
   {
      m_jTextFields = new JIntegerTextField[5];
      
      m_jTextFields[0] = new JIntegerTextField();
      m_jTextFields[0].setColumns(iLength);
      m_jTextFields[0].setHorizontalAlignment(SwingConstants.RIGHT);
      m_jTextFields[0].addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusGained(FocusEvent e)
         {
            jTextFieldFrom_focusGained(e);
         }
      });
      m_jTextFields[0].addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusLost(FocusEvent e)
         {
            jTextFieldFrom_focusLost(e);
         }
      });

      m_jTextFields[1] = new JIntegerTextField();
      m_jTextFields[1].setColumns(iLength);
      m_jTextFields[1].setEnabled(false);
      m_jTextFields[1].setHorizontalAlignment(SwingConstants.RIGHT);
      m_jTextFields[1].addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusGained(FocusEvent e)
         {
            jTextFieldTo_focusGained(e);
         }
      });
      m_jTextFields[1].addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusLost(FocusEvent e)
         {
            jTextFieldTo_focusLost(e);
         }
      });

      if (in_textPre != null)
      {
         m_jLabelPre = new JLabel();
         m_jLabelPre.setText(in_textPre);
         this.add(m_jLabelPre, null);
      }
      this.add(m_jTextFields[0], null);
      m_jLabelDots = new JLabel("..");
      this.add(m_jLabelDots, null);
      this.add(m_jTextFields[1], null);
      if (in_textPost != null)
      {
         m_jLabelPost = new JLabel();
         m_jLabelPost.setText(in_textPost);
         this.add(m_jLabelPost, null);
      }
   }

   private void jTextFieldFrom_focusGained(FocusEvent e)
   {
      JIntegerTextField jtf = (JIntegerTextField) e.getSource();
      jtf.selectAll();
   }

   private void jTextFieldTo_focusGained(FocusEvent e)
   {
      // check values - "to" must be always greater than "from"
      String valFrom = m_jTextFields[0].getText();
      String valTo = m_jTextFields[1].getText();
      Integer iValFrom = (valFrom.isBlank()) ? null : Integer.valueOf(valFrom);
      Integer iValTo = (valTo.isBlank()) ? null : Integer.valueOf(valTo);
      if (iValTo == null || iValTo <= iValFrom)
      {
         iValTo = iValFrom + 1;
         m_jTextFields[1].setValue(iValTo);
         m_jTextFields[1].setEnabled(true);
      }
      JIntegerTextField jtf = (JIntegerTextField) e.getSource();
      jtf.selectAll();
   }

   private void jTextFieldFrom_focusLost(FocusEvent e)
   {
      String valFrom = m_jTextFields[0].getText();
      String valTo = m_jTextFields[1].getText();
      if (valFrom.isBlank())
      {
         // if "from" is reset, reset "to" too
         m_jTextFields[1].setText("");
         m_jTextFields[1].setEnabled(false);
      }
      else
      {
         // check values
         Integer iValFrom = Integer.valueOf(valFrom);
         Integer iValTo = (valTo.isBlank()) ? null : Integer.valueOf(valTo);
         if (iValTo != null && iValTo == iValFrom)
         {
            // if equal, we set "to" blank
            iValTo = null;
         }
         else if (iValTo != null && iValTo < iValFrom)
         {
            // "to" must be always greater than "from"
            iValTo = iValFrom + 1;
         }
         m_jTextFields[1].setValue(iValTo);
         m_jTextFields[1].setEnabled(true);
      }
   }

   private void jTextFieldTo_focusLost(FocusEvent e)
   {
      String valFrom = m_jTextFields[0].getText();
      String valTo = m_jTextFields[1].getText();
      // check values
      Integer iValFrom = Integer.valueOf(valFrom);
      Integer iValTo = (valTo.isBlank()) ? null : Integer.valueOf(valTo);
      if (iValTo != null && iValTo == iValFrom)
      {
         // if equal, we set "to" blank
         iValTo = null;
      }
      else if (iValTo != null && iValTo < iValFrom)
      {
         // "to" must be always greater than "from"
         iValTo = iValFrom + 1;
      }
      m_jTextFields[1].setValue(iValTo);
   }

   public JTextField getTextField(int idx)
   {
      return m_jTextFields[idx];
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
