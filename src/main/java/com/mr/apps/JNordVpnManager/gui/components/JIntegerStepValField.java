/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.components;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import com.mr.apps.JNordVpnManager.Starter;

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
   private JIntegerTextField m_jTextField  = null;
   private JPanel            m_jPanel      = null;
   private JScrollBar        m_jScrollBar  = null;
   private JLabel            m_jLabelPre   = null;
   private JLabel            m_jLabelPost  = null;

   private String            m_textPre;
   private String            m_textPost;
   private int               m_nbCol;
   private int               m_min;
   private int               m_max;
   private int               m_step;
   private int               m_value;
   private boolean           m_valueIsBlank;

   private boolean           m_forceEvents = false;

   public JIntegerStepValField()
   {
      try
      {
         set_initValues(null, null, 99999, 99999, 0);
         create();
      }
      catch (Exception e)
      {
         Starter._m_logError.TranslatorExceptionMessage(5, 10998, e);
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
         Starter._m_logError.TranslatorExceptionMessage(5, 10998, e);
      }
   }

   public JTextField getJTextField()
   {
      return m_jTextField;
   }

   public JPanel getJPanel()
   {
      return m_jPanel;
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
         set_value(Integer.valueOf(in_String).intValue()); // no "" as entry posible
      }
      else
      {
         m_valueIsBlank = true;
         set_value(0);
      }
   }

   public void selectAll()
   {
      m_jTextField.selectAll();
   }

   public void requestFocus()
   {
      m_jTextField.requestFocus();
   }

   public void setVisible(boolean fVisible)
   {
      if (m_jLabelPre != null) m_jLabelPre.setVisible(fVisible);
      if (m_jLabelPost != null) m_jLabelPost.setVisible(fVisible);
      m_jTextField.setVisible(fVisible);
      if (m_jScrollBar != null) m_jScrollBar.setVisible(fVisible);
   }

   public void setEditable(boolean isEditable)
   {
      m_jTextField.setEditable(isEditable);
      return;
   }

   public boolean isEditable()
   {
      return m_jTextField.isEditable();
   }

   public void setEnabled(boolean fEnable)
   {
      m_jTextField.setEnabled(fEnable);
      m_jTextField.setEditable(fEnable);
      if (m_jScrollBar != null) m_jScrollBar.setEnabled(fEnable);
      return;
   }

   public boolean isEnabled()
   {
      return m_jTextField.isEnabled();
   }

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

      return;
   }

   private void create() throws Exception
   {
      FlowLayout flowLayout;

      m_jPanel = new JPanel();
      flowLayout = new FlowLayout();
      m_jPanel.setLayout(flowLayout);
      flowLayout.setHgap(0);
      flowLayout.setVgap(0);

      m_jTextField = new JIntegerTextField();
      m_jTextField.setColumns(m_nbCol);
      m_jTextField.setHorizontalAlignment(SwingConstants.RIGHT);
      m_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusLost(FocusEvent e)
         {
            jTextField_focusLost(e);
         }
      });

      if (m_step != 0)
      {
         m_jScrollBar = new JScrollBar();
         m_jScrollBar.setUnitIncrement(-m_step);
         m_jScrollBar.setPreferredSize(new Dimension(16, 21));
         m_jScrollBar.setMinimum(m_min);
         m_jScrollBar.setMaximum(m_max + 10);
         m_jScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
               jScrollBar_adjustmentValueChanged(e);
            }
         });
      }
      else
      {
         m_jScrollBar = null;
      }

      this.add(m_jPanel, null);
      if (m_textPre != null)
      {
         m_jLabelPre = new JLabel();
         m_jLabelPre.setText(m_textPre);
         m_jPanel.add(m_jLabelPre, null);
      }
      m_jPanel.add(m_jTextField, null);
      if (m_jScrollBar != null)
      {
         m_jPanel.add(m_jScrollBar, null);
      }
      if (m_textPost != null)
      {
         m_jLabelPost = new JLabel();
         m_jLabelPost.setText(m_textPost);
         m_jPanel.add(m_jLabelPost, null);
      }

      set_value(m_value);
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
         m_jTextField.setText(String.valueOf(m_value));
         if (m_jScrollBar != null) m_jScrollBar.setValue(m_value);
      }
      else
      {
         m_jTextField.setText("");
      }

      m_forceEvents = actualForceEventsState;
   }

   private void jScrollBar_adjustmentValueChanged(AdjustmentEvent e)
   {
      if (m_forceEvents) return;

      m_forceEvents = true;
      if (m_valueIsBlank == false)
      {
         set_value(m_jScrollBar.getValue());
      }
      else
      {
         m_valueIsBlank = false;
         set_value(m_min);
      }
      m_jTextField.requestFocus(); // important! set cursor to textfield
      m_forceEvents = false;
   }

   private void jTextField_focusLost(FocusEvent e)
   {
      int iVal;
      String strVal;

      if (m_forceEvents) return;

      m_forceEvents = true;
      strVal = m_jTextField.getText();
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
      return m_jTextField;
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
