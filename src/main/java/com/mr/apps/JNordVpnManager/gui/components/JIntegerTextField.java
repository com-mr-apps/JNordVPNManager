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
import javax.swing.SwingConstants;

import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Title: JIntegerTextField 
 * Description: Textfield that only allows integer input + Enter key act like the tab key
 * 
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JIntegerTextField extends JTextField
{

   public JIntegerTextField(String _initialStr, int _col)
   {
      super(_initialStr, _col);

      this.setHorizontalAlignment(SwingConstants.RIGHT);
      this.addKeyListener(new KeyAdapter() {
         public void keyTyped(KeyEvent e)
         {
            char c = e.getKeyChar();
            String strVal;

            if (!((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) ||
                  (c == KeyEvent.VK_ENTER) || (c == KeyEvent.VK_TAB) ||
                  (Character.isDigit(c))))
            {
               e.consume();
            }
            if ((c == '-') || (c == '+'))
            {
               // put +/- sign always on the first position
               strVal = getText();
               if (strVal != null && strVal.length() > 0)
               {
                  if (!((strVal.charAt(0) == '-') || (strVal.charAt(0) == '+')))
                  {
                     // insert sign
                     setText(c + strVal);
                  }
                  else if (strVal.charAt(0) != c)
                  {
                     // overwrite sign
                     setText(strVal.replace(strVal.charAt(0), c));
                  }
               }
            }
            else if (c == KeyEvent.VK_ENTER)
            {
               // make ENTER key act like the TAB key
               transferFocus();
            }
         }
      });
   }

   public JIntegerTextField()
   {
      this("", 2);
   }

   public JIntegerTextField(int _col)
   {
      this("", _col);
   }
   
   public void setValue(Integer iVal)
   {
      if (null == iVal)
      {
         this.setText("");
      }
      else
      {
         this.setText(StringFormat.int2String(iVal, "0"));
      }
   }
}
