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
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;

/**
 * Title:        JNumericTextField
 * Description:  Textfield that only allows numeric input
 *               + Enter key act like the tab key
 * @author mr
 * @version 1.0
 */

@SuppressWarnings("serial")
public class JNumericTextField extends JTextField
{
   public JNumericTextField(String _initialStr, int _col)
   {
      super(_initialStr, _col);

      this.addKeyListener(new KeyAdapter() {
         public void keyTyped(KeyEvent e)
         {
            char c = e.getKeyChar();
            if (!((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) ||
                  (c == KeyEvent.VK_ENTER) || (c == KeyEvent.VK_TAB) ||
                  (Character.isDigit(c))))
            {
               e.consume();
            }

            if (c == KeyEvent.VK_ENTER)
            {
               // make ENTER key act like the TAB key
               transferFocus();
            }
         }
      });
   }

   public JNumericTextField()
   {
      this("", 2);
   }

   public JNumericTextField(int _col)
   {
      this("", _col);
   }
}
