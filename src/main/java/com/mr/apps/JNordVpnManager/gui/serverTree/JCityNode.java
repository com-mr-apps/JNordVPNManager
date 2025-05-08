/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.serverTree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.mr.apps.JNordVpnManager.geotools.Location;

@SuppressWarnings("serial")
public class JCityNode extends DefaultMutableTreeNode
{

   private Location m_loc;

   public JCityNode(Location loc)
   {
      super(loc.getCityName());

      // linked Location object from CSV table
      this.m_loc = loc;
   }

   public String getCountry()
   {
      return m_loc.getCountryName();
   }

   public String getCity()
   {
      return m_loc.getCityName();
   }

   public Location getLocation()
   {
      return m_loc;
   }
}
