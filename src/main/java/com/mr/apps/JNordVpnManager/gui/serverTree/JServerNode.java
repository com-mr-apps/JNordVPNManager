/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
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
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.geotools.VpnServer;

@SuppressWarnings("serial")
public class JServerNode extends DefaultMutableTreeNode
{

   private Location m_loc;
   private VpnServer m_vpnServer;

   public JServerNode(VpnServer vpnServer)
   {
      super(vpnServer.getServerName());

      this.m_vpnServer = vpnServer;

      // linked to parent Location object (city) from CSV table
      this.m_loc = UtilLocations.getLocation(vpnServer.getServerKey());
   }

   public JServerNode(Location loc, VpnServer vpnServer)
   {
      super(vpnServer.getServerName());

      this.m_vpnServer = vpnServer;

      // linked Location object from CSV table
      this.m_loc = loc;
   }

   public VpnServer getVpnServer()
   {
      return m_vpnServer;
   }

   public Location getLocation()
   {
      return m_loc;
   }
}
