/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

/**
 * Class for the current location<p>
 * Extends class Location by connected status attribute.
 */
public class CurrentLocation extends Location
{
   private boolean isConnected;

   public CurrentLocation()
   {
      super();
      setConnected(false);
   }

   public CurrentLocation(Location loc)
   {
      this.m_serverId = loc.m_serverId;
      this.m_cityName = loc.m_cityName;
      this.m_countryName = loc.m_countryName;
      this.m_longitude = loc.m_longitude;
      this.m_latitude = loc.m_latitude;
      this.m_cityId = loc.m_cityId;
      this.m_groups = loc.m_groups;
      this.m_technologies = loc.m_technologies;
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
