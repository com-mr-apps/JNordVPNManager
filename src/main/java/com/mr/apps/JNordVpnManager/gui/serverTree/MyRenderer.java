/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.serverTree;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.VpnServer;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;

@SuppressWarnings("serial")
public class MyRenderer extends DefaultTreeCellRenderer
{
   ImageIcon m_locationImage = null;
   ImageIcon m_serverImage = null;

   public MyRenderer()
   {
      setTextSelectionColor(new Color(120, 120, 120));
      setOpaque(true);
      m_locationImage = JResizedIcon.getIcon(IconUrls.ICON_TREE_LOCATION, IconSize.SMALL);
      m_serverImage = JResizedIcon.getIcon(IconUrls.ICON_TREE_SERVER, IconSize.SMALL);
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      if (value instanceof JCityNode) // leaf
      {
         // server (city)
         this.setForeground(Color.BLUE);
         this.setBackground(null);
         this.setIcon(m_locationImage);
      }
      else if (value instanceof JServerNode)
      {
         // host
         Color colorFg = Color.DARK_GRAY;
         CurrentLocation cLoc = Starter.getCurrentServer(true);
         if (null != cLoc)
         {
            VpnServer loc = ((JServerNode)value).getVpnServer();
            if (loc.getServer().equals(cLoc.getServerNordVPN()))
            {
               colorFg = Color.MAGENTA;
            }
         }
         
         this.setForeground(colorFg);
         this.setBackground(null);
         this.setIcon(m_serverImage);
      }
      else if (value instanceof JCountryNode)
      {
         // country
         this.setForeground(Color.BLACK);
         this.setBackground(null);
         this.setIcon(((JCountryNode)value).getFlag());
      }

      if (sel)
      {
         // selected server
         this.setForeground(Color.BLUE);
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
