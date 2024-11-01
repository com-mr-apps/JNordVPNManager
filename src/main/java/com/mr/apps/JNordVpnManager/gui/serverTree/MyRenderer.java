/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.serverTree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("serial")
public class MyRenderer extends DefaultTreeCellRenderer
{

   public MyRenderer()
   {
      setTextSelectionColor(new Color(120, 120, 120));
      setOpaque(true);
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      if (leaf)
      {
         // city
         this.setBackground(Color.CYAN);
      }
      else
      {
         // country
         this.setBackground(null);
      }

      if (sel)
      {
         this.setBackground(Color.ORANGE);
         this.setBorder(new LineBorder(Color.GREEN));
      }
      else
      {
         this.setBorder(null);
      }

      return this;
   }
}
