/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.serverTree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;

@SuppressWarnings("serial")
public class JServerNode extends DefaultMutableTreeNode
{

   private String group, server;
   private Location loc;

   public JServerNode(String group, String server)
   {
      super(server.replace('_', ' ')); // mangle display names
      this.group = group;
      this.server = server;

      // linked Location object from CSV table
      this.loc = UtilLocations.getLocation(UtilLocations.getServerId(server, group));
   }

   public String getGroup()
   {
      return group;
   }

   public String getServer()
   {
      return server;
   }

   public Location getLocation()
   {
      return loc;
   }
}
