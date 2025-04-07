/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

public class SpeedtestLocation
{
   protected String           m_cityName;
   protected double           m_longitude;
   protected double           m_latitude;
   protected String           m_serverUrl;

   public SpeedtestLocation(String cityName, double longitude, double latitude, String serverUrl)
   {
      m_cityName = cityName;
      m_longitude = longitude;
      m_latitude = latitude;
      m_serverUrl = serverUrl;
   }

   public String getCityName()
   {
      return m_cityName;
   }

   public double getLongitude()
   {
      return m_longitude;
   }

   public double getLatitude()
   {
      return m_latitude;
   }

   public String getServerUrl()
   {
      return m_serverUrl;
   }

   public String getServerId()
   {
      return m_cityName;
   }

}
