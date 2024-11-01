/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

public class CurrentLocation extends Location
{
   private boolean isConnected;

   CurrentLocation()
   {
      super();
      setConnected(false);
   }

   public CurrentLocation(Location loc)
   {
      this.serverId = loc.serverId;
      this.city = loc.city;
      this.country = loc.country;
      this.longitude = loc.longitude;
      this.latitude = loc.latitude;
      this.number = loc.number;
      setConnected(false);
   }

   public boolean isConnected()
   {
      return isConnected;
   }

   public void setConnected(boolean isConnected)
   {
      this.isConnected = isConnected;
   }

   public String toString()
   {
       return super.toString() + " " + ((isConnected == true) ? "connected" : "disconnected");
   }
}
