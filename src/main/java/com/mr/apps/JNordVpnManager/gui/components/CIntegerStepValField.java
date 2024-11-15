package com.mr.apps.JNordVpnManager.gui.components;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.FocusEvent;
import java.awt.Dimension;

/**
 * Title:        GUI for CPost
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      cenit AG Systemhaus
 * @author
 * @version 1.0
 * Description:
 *          private CIntegerStepValField elem_fieldI;
 *          elem_fieldI = new CIntegerStepValField(label_pre,label_post,iMin,iMax,iStep);
 *          elem_panel.add(elem_fieldI.get_jPanel());
 *
 *          if labels == null they will not be displayed
 *          if iMin == iMax no limits are checked and iStep is set to 0
 *          if iStep == 0 no ScrollBar is displayed
 */

public class CIntegerStepValField extends JComponent
{
 
   private static final long serialVersionUID = 1L;
private JIntegerTextField jTextField = null;
  private JPanel jPanel = null;
  private JScrollBar jScrollBar = null;
  private JLabel jLabel_pre = null;
  private JLabel jLabel_post = null;

  private String m_textPre;
  private String m_textPost;
  private int m_nbCol;
  private int m_min;
  private int m_max;
  private int m_step;
  private int m_value;
  private boolean m_valueIsBlank;

  boolean forceEvents = false;

  public CIntegerStepValField()
  {
    try
    {
      set_initValues (null, null, 99999, 99999, 0);
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public CIntegerStepValField(String in_textPre, String in_textPost, int in_min, int in_max, int in_step)
  {
    try
    {
      set_initValues (in_textPre, in_textPost, in_min, in_max, in_step);
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /*
  public static CIntegerStepValField CreateField(String in_textPre, String in_textPost, int in_min, int in_max, int in_step)
  {
     CIntegerStepValField integerStepValField;

     integerStepValField = new CIntegerStepValField(in_textPre, in_textPost, in_min, in_max, in_step);

     return integerStepValField;
  }
  */

  public JTextField get_jTextField()
  {
     return jTextField;
  }

  public JPanel get_jPanel()
  {
     return jPanel;
  }

  public String getText()
  {
     if (m_valueIsBlank == false)
     {
        return new String (String.valueOf(m_value));
     }
     else
     {
        return new String ("");
     }
  }

  public void setText(String in_String)
  {
     if (in_String.equals("")==false)
     {
        m_valueIsBlank = false;
        set_value (Integer.valueOf(in_String).intValue()); //no "" as entry posible
     }
     else
     {
        m_valueIsBlank = true;
        set_value (0);
     }
  }

  public void selectAll()
  {
      jTextField.selectAll();
  }

  public void requestFocus()
  {
      jTextField.requestFocus();
  }

  public void setVisible(boolean fVisible)
  {
      if (jLabel_pre  != null) jLabel_pre.setVisible(fVisible);
      if (jLabel_post != null) jLabel_post.setVisible(fVisible);
      jTextField.setVisible(fVisible);
      if (jScrollBar != null) jScrollBar.setVisible(fVisible);
  }

  public void setEnabled(boolean fEnable)
  {
      jTextField.setEnabled(fEnable);
      jTextField.setEditable(fEnable);
      if (jScrollBar != null) jScrollBar.setEnabled(fEnable);
  }

  public boolean isEnabled()
  {
     return jTextField.isEnabled();
  }

/*
  public void updateUI(boolean fEnable)
  {
      if (jLabel_pre  != null) jLabel_pre.updateUI();
      if (jLabel_post != null) jLabel_post.updateUI();
      jTextField.updateUI();
      if (jScrollBar != null) jScrollBar.updateUI();
  }
*/
  private void set_initValues (String in_textPre, String in_textPost, int in_min, int in_max, int in_step)
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

     //System.out.println("m_min="+m_min+", m_max="+m_max+", m_step="+m_step+", m_nbCol="+m_nbCol);
  }

  private void jbInit() throws Exception
  {
    FlowLayout flowLayout;

    jPanel = new JPanel();
    flowLayout = new FlowLayout();
    jPanel.setLayout(flowLayout);
    flowLayout.setHgap(0);
    flowLayout.setVgap(0);

    jTextField = new JIntegerTextField();
    jTextField.setColumns(m_nbCol);
    jTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    jTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        jTextField_focusLost(e);
      }
    });

    if (m_step != 0)
    {
       jScrollBar = new JScrollBar();
       jScrollBar.setUnitIncrement(-m_step);
       jScrollBar.setPreferredSize(new Dimension(16, 21));
       jScrollBar.setMinimum(m_min);
       jScrollBar.setMaximum(m_max+10);
       jScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener()
       {
          public void adjustmentValueChanged(AdjustmentEvent e)
         {
            jScrollBar_adjustmentValueChanged(e);
         }
       });
    }
    else
    {
       jScrollBar = null;
    }

    this.add(jPanel, null);
    if (m_textPre != null)
    {
       jLabel_pre = new JLabel();
       jLabel_pre.setText(m_textPre);
       jPanel.add(jLabel_pre, null);
    }
    jPanel.add(jTextField, null);
    if (jScrollBar != null)
    {
       jPanel.add(jScrollBar, null);
    }
    if (m_textPost != null)
    {
       jLabel_post = new JLabel();
       jLabel_post.setText(m_textPost);
       jPanel.add(jLabel_post, null);
    }

    set_value (m_value);
  }

  //private int get_value()
  //{
  //   return m_value;
  //}

  private void set_value(int in_value)
  {
     boolean actualForceEventsState;

     m_value = in_value;
     if (m_min != m_max)
     {
        if (m_value < m_min) m_value = m_min;
        if (m_value > m_max) m_value = m_max;
     }

     actualForceEventsState = forceEvents;
     forceEvents = true;

     if (m_valueIsBlank == false)
     {
        jTextField.setText(String.valueOf(m_value));
        if (jScrollBar != null) jScrollBar.setValue(m_value);
     }
     else
     {
        jTextField.setText("");
     }

     forceEvents = actualForceEventsState;
  }

  void jScrollBar_adjustmentValueChanged(AdjustmentEvent e)
  {
     if (forceEvents) return;

     forceEvents = true;
     if (m_valueIsBlank == false)
     {
        set_value(jScrollBar.getValue());
     }
     else
     {
        m_valueIsBlank = false;
        set_value(m_min);
     }
     jTextField.requestFocus(); // important! set cursor to textfield
     forceEvents = false;
  }

  void jTextField_focusLost(FocusEvent e)
  {
     int iVal;
     String strVal;

     if (forceEvents) return;

     forceEvents = true;
     strVal = jTextField.getText();
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
     set_value (iVal);
     forceEvents = false;
  }

  public JTextField getTextField()
  {
     return jTextField;
  }

  //add ons for changing language
  public void set_jLabel_pre(String in_text)
  {
     if (jLabel_pre!=null)
     {
        jLabel_pre.setText(in_text);
     }
  }
}
