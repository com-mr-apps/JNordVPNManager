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
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Title:        JDoubleTextField
 * Description:  Textfield that only allows decimal number input
 *               + . for floating-point representation
 *               + Enter key act like the tab key
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JDoubleTextField extends JTextField
{
   public JDoubleTextField(String _initialStr, int _col)
   {
      super(_initialStr, _col);

      this.setHorizontalAlignment(SwingConstants.RIGHT);
      this.addKeyListener(new KeyAdapter() {
         public void keyTyped(KeyEvent e)
         {
            char c = e.getKeyChar();
            String strVal;

            if (!((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)
                  || (c == KeyEvent.VK_ENTER) || (c == KeyEvent.VK_TAB)
                  || (Character.isDigit(c)) || (c == '.')
                  || (c == '-')))
            {
               e.consume();
            }
            if ((c == '.'))
            {
               // put +/- sign always on the first position
               strVal = getText();
               if (strVal.indexOf(".") != -1)
               {
                  // insert sign
                  e.consume();
               }
            }
            else if ((c == '-'))
            {
               strVal = getText();
               if (strVal.indexOf("-") == -1)
               {
                  strVal = new String("-" + strVal);
               }
               else
               {
                  strVal = new String(strVal.substring(1, strVal.length()));
               }
               setText(strVal);
               e.consume();
            }
            else if (c == KeyEvent.VK_ENTER)
            {
               // make ENTER key act like the TAB key
               transferFocus();
            }
         }
      });

   }

   public JDoubleTextField()
   {
      this("", 2);
   }

   public JDoubleTextField(int _col)
   {
      this("", _col);
   }
}
