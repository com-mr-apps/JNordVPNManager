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
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.mr.apps.JNordVpnManager.Starter;

@SuppressWarnings("serial")
public class MyRenderer extends DefaultTreeCellRenderer
{
   ImageIcon m_serverImage = null;

   public MyRenderer()
   {
      setTextSelectionColor(new Color(120, 120, 120));
      setOpaque(true);
      try
      {
         ImageIcon myImageIcon = new ImageIcon(Starter.class.getResource("resources/icons/mpLocation.png"));
         Image myImage = myImageIcon.getImage();
         Image resizedImage = myImage.getScaledInstance(12, 12, java.awt.Image.SCALE_SMOOTH);
         m_serverImage = new ImageIcon(resizedImage);
      }
      catch (Exception e)
      {
         
      }
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      if (value instanceof JServerNode) // leaf
      {
         // server (city)
         this.setForeground(Color.BLUE);
         this.setIcon(m_serverImage);
      }
      else if (value instanceof JCountryNode)
      {
         // country
         this.setBackground(null);
         this.setIcon(((JCountryNode)value).getFlag());
      }

      if (sel)
      {
         // selected server
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
