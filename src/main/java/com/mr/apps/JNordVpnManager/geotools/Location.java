/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import java.io.File;
import java.net.URL;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnTechnologies;

/**
 * Store locations from the CSV table.
 * <p>
 * Also used to store the current server during runtime (in main class).
 */
public class Location
{
   public static final String SERVERID_SEPARATOR      = "@";
   public static final String SERVERID_LIST_SEPARATOR = ":";

   protected String           m_serverId;
   protected String           m_cityName;
   protected String           m_countryName;
   protected String           m_countryCode;
   protected double           m_longitude;
   protected double           m_latitude;
   protected boolean          m_isVirtualLocation;
   protected int              m_countryId;
   protected int              m_cityId;
   protected NvpnTechnologies m_technologies;
   protected NvpnGroups       m_groups;
 
   public Location()
   {
      setServerId("nowhere@nowhere");
      setLongitude(0.0);
      setLatitude(0.0);
      setVirtualLocation(false);
      setCountryId(0);
      setCityId(-1);
      setCityName("nowhere");
      setCountryName("nowhere");
      setCountryCode("");
      m_technologies = new NvpnTechnologies();
      m_groups = new NvpnGroups();
   }

   public Location (String serverId, double longitude, double latitude, int cityId)
   {
      String[] sa = serverId.split(SERVERID_SEPARATOR);
      String city = "";
      String country = "";
      if (2 == sa.length)
      {
         city = sa[0];
         country = sa[1];
      }
      else
      {
         country = serverId;
      }
      if (null == city) city = "";
      if (null == country || country.isBlank())
      {
         setServerId(city.trim() + SERVERID_SEPARATOR);
         setCityName(city);
         setCountryName("");
         cityId = 0;
      }
      else
      {
         setServerId(city.trim() + SERVERID_SEPARATOR + country.trim());
         setCityName(city);
         setCountryName(country);
      }
      setLongitude(longitude);
      setLatitude(latitude);
      setCityId(cityId);

      setVirtualLocation(false);
      m_technologies = new NvpnTechnologies();
      m_groups = new NvpnGroups();
 
      if (cityId == 1)
      {
         // internal generated temporary location
         Starter._m_logError.TraceDebug("Temp. Location: " + this.toString());         
      }
      else if (cityId == 0)
      {
         Starter._m_logError.TraceDebug("Temp. Location w/o city: " + this.toString());         
      }
      else
      {
         // from CSV
         //JNordVpnManager._m_logError.TraceDebug("Add location: " + this.toString());
      }
     
   }

   public Location(String serverId)
   {
      this(serverId, 0.0, 0.0, 1);
   }

   public Location (String cityName, String countryName, double longitude, double latitude, int cityId)
   {
      this(cityName+"@"+countryName, longitude, latitude, cityId);
   }

   public String getServerId()
   {
      return m_serverId;
   }

   public void setServerId(String serverId)
   {
      this.m_serverId = serverId;
   }

   public double getLongitude()
   {
      return m_longitude;
   }

   public void setLongitude(double longitude)
   {
      this.m_longitude = longitude;
   }

   public double getLatitude()
   {
      return m_latitude;
   }

   public void setLatitude(double latitude)
   {
      this.m_latitude = latitude;
   }

   public boolean isVirtualLocation()
   {
      return m_isVirtualLocation;
   }

   public void setVirtualLocation(boolean isVirtualLocation)
   {
      this.m_isVirtualLocation = isVirtualLocation;
   }

   public int getCountryId()
   {
      return m_countryId;
   }

   public void setCountryId(int countryId)
   {
      this.m_countryId = countryId;
   }

   public int getCityId()
   {
      return m_cityId;
   }

   public void setCityId(int cityId)
   {
      this.m_cityId = cityId;
   }

   /**
    * Get the city name
    * @return the city name from the original source
    */
   public String getCityName()
   {
      return m_cityName;
   }

   public void setCityName(String cityName)
   {
      this.m_cityName = cityName;
   }

   /**
    * Get the country name
    * @return the country name from the original source
    */
   public String getCountryName()
   {
      return m_countryName;
   }

   public void setCountryName(String countryName)
   {
      this.m_countryName = countryName;
   }
   
   public String getCountryCode()
   {
      return m_countryCode;
   }

   public void setCountryCode(String countryCode)
   {
      this.m_countryCode = countryCode.toLowerCase();
   }
   
   /**
    * Get the city name
    * @return the city name from NordVPN (with underscores)
    */
   public String getCityNordVPN()
   {
      return m_cityName.replace(' ', '_');
   }

   /**
    * Get the country name
    * @return the country name from NordVPN (with underscores)
    */
   public String getCountryNordVPN()
   {
      return m_countryName.replace(' ', '_');
   }

   public void addTechnology(int id)
   {
      m_technologies.addTechnology(id);
   }

   public boolean hasTechnology(int id)
   {
      return m_technologies.hasTechnology(id);
   }

   public void addGroup(NordVPNEnumGroups id)
   {
      m_groups.addGroup(id);
   }

   public void addGroup(int id)
   {
      m_groups.addGroup(id);
   }

   public boolean hasGroup(NordVPNEnumGroups m_filterRegion)
   {
      return m_groups.hasGroup(m_filterRegion);
   }

   public String getFlagImageFileName()
   {
      return "flags" + File.separator + this.getCountryCode() + ".png";
   }

   public URL getFlagUrl()
   {
      return Starter.class.getResource("resources/flags/" + this.getCountryCode() + ".png");
   }

   public String toString()
   {
      return m_serverId + " [" + m_longitude + "," + m_latitude + "] (id=" + m_cityId + ") Groups=" + m_groups.toString() + " / Technologies=" + m_technologies.toString();
   }

   public String exportAsCsvData()
   {
      return m_latitude + "," // LAT
           + m_longitude + "," // LON
           + m_cityName + "," // CITY
           + m_countryName + "," // COUNTRY
           + m_countryCode + "," // FLAG
           + m_cityId + "," // NUM
           + m_groups.toStringId() + "," // GRP 
           + m_technologies.toStringId() + "," // TECH
           + ((m_isVirtualLocation) ? "true" : "false"); // VLOC
   }

}
