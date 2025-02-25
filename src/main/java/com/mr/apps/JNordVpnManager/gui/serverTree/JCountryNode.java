/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.serverTree;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.mr.apps.JNordVpnManager.geotools.Location;

@SuppressWarnings("serial")
public class JCountryNode extends DefaultMutableTreeNode
{

   private Location loc;

   public JCountryNode(Location loc)
   {
      super(loc.getCountryName());

      // linked Location object from CSV table
      this.loc = loc;
   }

   public String getCountry()
   {
      return loc.getCountryName();
   }

   public ImageIcon getFlag()
   {
      try
      {
         ImageIcon myImageIcon = new ImageIcon(loc.getFlagUrl());
         return myImageIcon;
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public Location getLocation()
   {
      return loc;
   }
}
