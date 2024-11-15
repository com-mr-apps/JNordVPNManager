/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JIntegerTextField;

import java.awt.FlowLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.FocusEvent;
import java.awt.Dimension;

/**
 * Title:        JIntegerStepValField
 * Description:
 *          private JIntegerStepValField elem_fieldI;
 *          elem_fieldI = new CIntegerStepValField(label_pre,label_post,iMin,iMax,iStep);
 *          elem_panel.add(elem_fieldI.get_jPanel());
 *
 *          if labels == null they will not be displayed
 *          if iMin == iMax no limits are checked and iStep is set to 0
 *          if iStep == 0 no ScrollBar is displayed
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JIntegerStepValField extends JComponent
{
   private JIntegerTextField m_JTextField       = null;
   private JPanel            m_JPanel           = null;
   private JScrollBar        m_JScrollBar       = null;
   private JLabel            m_JLabel_pre       = null;
   private JLabel            m_JLabel_post      = null;

   private String            m_textPre;
   private String            m_textPost;
   private int               m_nbCol;
   private int               m_min;
   private int               m_max;
   private int               m_step;
   private int               m_value;
   private boolean           m_valueIsBlank;

   boolean                   m_forceEvents      = false;

   public JIntegerStepValField()
   {
      try
      {
         set_initValues(null, null, 99999, 99999, 0);
         create();
      }
      catch (Exception e)
      {
         Starter._m_logError.TranslatorExceptionMessage(5, 10997, e);
      }
   }

   public JIntegerStepValField(String in_textPre, String in_textPost, int in_min, int in_max, int in_step)
   {
      try
      {
         set_initValues(in_textPre, in_textPost, in_min, in_max, in_step);
         create();
      }
      catch (Exception e)
      {
         Starter._m_logError.TranslatorExceptionMessage(5, 10997, e);
      }
   }

   /*
    * public static CIntegerStepValField CreateField(String in_textPre, String in_textPost, int in_min, int in_max, int
    * in_step) { CIntegerStepValField integerStepValField;
    * 
    * integerStepValField = new CIntegerStepValField(in_textPre, in_textPost, in_min, in_max, in_step);
    * 
    * return integerStepValField; }
    */

   public JTextField getJTextField()
   {
      return m_JTextField;
   }

   public JPanel getJPanel()
   {
      return m_JPanel;
   }

   public String getText()
   {
      if (m_valueIsBlank == false)
      {
         return new String(String.valueOf(m_value));
      }
      else
      {
         return new String("");
      }
   }

   public void setText(String in_String)
   {
      if (in_String.equals("") == false)
      {
         m_valueIsBlank = false;
         set_value(Integer.valueOf(in_String).intValue()); // no "" as entry possible
      }
      else
      {
         m_valueIsBlank = true;
         set_value(0);
      }
   }

   public void selectAll()
   {
      m_JTextField.selectAll();
   }

   public void requestFocus()
   {
      m_JTextField.requestFocus();
   }

   public void setVisible(boolean fVisible)
   {
      if (m_JLabel_pre != null) m_JLabel_pre.setVisible(fVisible);
      if (m_JLabel_post != null) m_JLabel_post.setVisible(fVisible);
      m_JTextField.setVisible(fVisible);
      if (m_JScrollBar != null) m_JScrollBar.setVisible(fVisible);
   }

   public void setEnabled(boolean fEnable)
   {
      m_JTextField.setEnabled(fEnable);
      m_JTextField.setEditable(fEnable);
      if (m_JScrollBar != null) m_JScrollBar.setEnabled(fEnable);
   }

   public boolean isEnabled()
   {
      return m_JTextField.isEnabled();
   }

   /*
    * public void updateUI(boolean fEnable) { if (jLabel_pre != null) jLabel_pre.updateUI(); if (jLabel_post != null)
    * jLabel_post.updateUI(); jTextField.updateUI(); if (jScrollBar != null) jScrollBar.updateUI(); }
    */
   private void set_initValues(String in_textPre, String in_textPost, int in_min, int in_max, int in_step)
   {
      m_textPre = in_textPre;
      m_textPost = in_textPost;
      m_min = in_min;
      m_max = in_max;
      m_step = in_step;

      if ((m_min == 0) && (m_max == 0)) m_min = m_max = 99999; // set default field-width

      if (m_min == m_max)
      {
         m_step = 0; // no limits -> no ScrollBar
         m_valueIsBlank = true;
         m_value = 0;
      }
      else
      {
         m_valueIsBlank = false;
         m_value = m_min;
      }

      // calculate field-with
      m_nbCol = String.valueOf(m_max).length();
      if ((m_min < 0) && (String.valueOf(m_min).length() > m_nbCol))
      {
         m_nbCol = String.valueOf(m_min).length();
      }

      // System.out.println("m_min="+m_min+", m_max="+m_max+", m_step="+m_step+", m_nbCol="+m_nbCol);
   }

   private void create() throws Exception
   {
      FlowLayout flowLayout;

      m_JPanel = new JPanel();
      flowLayout = new FlowLayout();
      m_JPanel.setLayout(flowLayout);
      flowLayout.setHgap(0);
      flowLayout.setVgap(0);

      m_JTextField = new JIntegerTextField();
      m_JTextField.setColumns(m_nbCol);
      m_JTextField.setHorizontalAlignment(SwingConstants.RIGHT);
      m_JTextField.addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusLost(FocusEvent e)
         {
            jTextField_focusLost(e);
         }
      });

      if (m_step != 0)
      {
         m_JScrollBar = new JScrollBar();
         m_JScrollBar.setUnitIncrement(-m_step);
         m_JScrollBar.setPreferredSize(new Dimension(16, 21));
         m_JScrollBar.setMinimum(m_min);
         m_JScrollBar.setMaximum(m_max + 10);
         m_JScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
               jScrollBar_adjustmentValueChanged(e);
            }
         });
      }
      else
      {
         m_JScrollBar = null;
      }

      this.add(m_JPanel, null);
      if (m_textPre != null)
      {
         m_JLabel_pre = new JLabel();
         m_JLabel_pre.setText(m_textPre);
         m_JPanel.add(m_JLabel_pre, null);
      }
      m_JPanel.add(m_JTextField, null);
      if (m_JScrollBar != null)
      {
         m_JPanel.add(m_JScrollBar, null);
      }
      if (m_textPost != null)
      {
         m_JLabel_post = new JLabel();
         m_JLabel_post.setText(m_textPost);
         m_JPanel.add(m_JLabel_post, null);
      }

      set_value(m_value);
      setVisible(true);
   }

   private void set_value(int in_value)
   {
      boolean actualForceEventsState;

      m_value = in_value;
      if (m_min != m_max)
      {
         if (m_value < m_min) m_value = m_min;
         if (m_value > m_max) m_value = m_max;
      }

      actualForceEventsState = m_forceEvents;
      m_forceEvents = true;

      if (m_valueIsBlank == false)
      {
         m_JTextField.setText(String.valueOf(m_value));
         if (m_JScrollBar != null) m_JScrollBar.setValue(m_value);
      }
      else
      {
         m_JTextField.setText("");
      }

      m_forceEvents = actualForceEventsState;
   }

   void jScrollBar_adjustmentValueChanged(AdjustmentEvent e)
   {
      if (m_forceEvents) return;

      m_forceEvents = true;
      if (m_valueIsBlank == false)
      {
         set_value(m_JScrollBar.getValue());
      }
      else
      {
         m_valueIsBlank = false;
         set_value(m_min);
      }
      m_JTextField.requestFocus(); // important! set cursor to textfield
      m_forceEvents = false;
   }

   void jTextField_focusLost(FocusEvent e)
   {
      int iVal;
      String strVal;

      if (m_forceEvents) return;

      m_forceEvents = true;
      strVal = m_JTextField.getText();
      if (strVal.length() == 0)
      {
         if (m_min == m_max)
         {
            // empty field -> empty value
            iVal = 0;
            m_valueIsBlank = true;
         }
         else
         {
            // empty field -> value = m_min
            iVal = m_min;
            m_valueIsBlank = false;
         }
      }
      else
      {
         iVal = Integer.valueOf(strVal).intValue();
         m_valueIsBlank = false;
      }
      set_value(iVal);
      m_forceEvents = false;
   }

   public JTextField getTextField()
   {
      return m_JTextField;
   }

   // add ons for changing language
   public void set_jLabel_pre(String in_text)
   {
      if (m_JLabel_pre != null)
      {
         m_JLabel_pre.setText(in_text);
      }
   }
}
