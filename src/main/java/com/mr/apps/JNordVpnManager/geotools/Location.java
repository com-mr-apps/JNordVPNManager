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
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * Store locations data from the CSV table.
 * <p>
 * Also used to create "temporary" Locations for the current server during runtime which store only connection relevant data.
 * E.g. temporary locations don"t have location coordinates. They have a city Id <= 1. 
 */
public class Location
{
   // a) HashMap serverKey (lower case): city@country
   // b) UserPrefs SetverList (mixed case): Country1@City11/.../City1n:Country2@City21/.../City2n:Countrym@Citym1/.../Citymn
   public static final String SERVERID_SEPARATOR      = "@";
   public static final String SERVERID_LIST_SEPARATOR = ":";
   public static final String SERVERID_CITIES_SEPARATOR = "/";
   public static final String SERVERID_HOST_SEPARATOR = "#";

   protected VpnServer            m_vpnServer = null;
   protected String               m_cityName = "";
   protected String               m_countryName = "";
   protected String               m_countryCode = "";
   protected double               m_longitude = 0.0;
   protected double               m_latitude = 0.0;
   protected int                  m_countryId = -1;
   protected int                  m_cityId =-1;
 
   /**
    * Default (empty) Location Constructor
    */
   public Location()
   {
      this(null, 0.0, 0.0, -1);
   }

   /**
    * Constructor for temporary (internal) Location
    * 
    * @param serverId
    *           is the server key (city@country) from HashMap
    */
   public Location(String serverId)
   {
      this(serverId, 0.0, 0.0, 1);
   }

   /**
    * Constructor for Location with ServerKey
    * 
    * @param serverKey
    *           is the server key (city@country) from HashMap
    * @param longitude
    *           is the longitude of the Location
    * @param latitude
    *           is the latitude of the Location
    * @param cityId
    *           is the city Id
    */
   public Location(String serverKey, double longitude, double latitude, int cityId)
   {
      setCityId(cityId);
      setServerKey(serverKey);

      setLongitude(longitude);
      setLatitude(latitude);

      if (cityId == -1)
      {
         // invalid location
         Starter._m_logError.TraceDebug("Temp. No Location: " + this.toString());         
      }
      if (cityId == 1)
      {
         // internal generated temporary city location
         Starter._m_logError.TraceDebug("Temp. City Location: " + this.toString());         
      }
      else if (cityId == 0)
      {
         // internal generated temporary country location
         Starter._m_logError.TraceDebug("Temp. Country Location: " + this.toString());         
      }
      else
      {
         // from CSV / JSON
         //JNordVpnManager._m_logError.TraceDebug("Add location: " + this.toString());
      }
   }

   /**
    * Constructor for Location with city and country
    * 
    * @param cityName (can be empty)
    *           is the city name
    * @param countryName
    *           is the country name (can be empty)
    * @param longitude
    *           is the longitude of the Location
    * @param latitude
    *           is the latitude of the Location
    * @param cityId
    *           is the city Id
    */
   public Location(String cityName, String countryName, double longitude, double latitude, int cityId)
   {
      this(buildServerId(cityName, countryName), longitude, latitude, cityId);
   }

   public String getServerKey()
   {
      return m_vpnServer.getServerKey();
   }

   public VpnServer getVpnServer()
   {
      return m_vpnServer;
   }

   /**
    * Build the serverId HashTable Search-key from city and country
    * 
    * @param city
    *           is the city
    * @param country
    *           is the country
    * @return the search key
    */
   public static String buildServerId(String city, String country)
   {
      return StringFormat.printString(city, "", "") + Location.SERVERID_SEPARATOR + StringFormat.printString(country, "", "");
   }

   public static String[] splitServerKey(String serverKey)
   {
      if (null == serverKey) return new String[] {"", "", ""};

      String[] sa = serverKey.split(SERVERID_SEPARATOR);
      String city = "";
      String country = "";
      if (2 == sa.length)
      {
         city = sa[0].trim();
         country = sa[1].trim();
      }
      else
      {
         country = serverKey.trim();
      }
      return new String[] {city, country};
   }

   public void setServerKey(String serverKey)
   {
      String[] sa = splitServerKey(serverKey);
      setServerKey(sa[0], sa[1]);
   }

   public void setServerKey(String city, String country)
   {
      setCityName(city.trim());
      setCountryName(country.trim());
      if (m_cityName.isBlank() && m_countryName.isBlank())
      {
         // temporary no Location for quick connect command
         this.m_cityId = -1;
      }
      else if (m_cityName.isBlank())
      {
         // temporary country Location for connect command
         this.m_cityId = 0;
      }
      else if (m_countryName.isBlank())
      {
         // temporary city location for connect command
         this.m_cityId = 1;
      }
      else if (m_cityId < 1)
      {
         // temporary city or host location for connect command
         this.m_cityId = 1;
      }

      // Server based on Location object (no host)
      m_vpnServer = new VpnServer(buildServerId(city, country), null, null);
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
      return m_vpnServer.isVirtualLocation();
   }

   public void setVirtualLocation(boolean isVirtualLocation)
   {
      m_vpnServer.setVirtualLocation(isVirtualLocation);
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

   public String getVpnHostName()
   {
      return m_vpnServer.getServerHostName();
   }

   public String getServerName()
   {
      String serverName = "";
      if (null == m_vpnServer.getServer())
      {
         switch (m_cityId)
         {
            case 0 :
               serverName = getCountryName();
               break;
            case 1 :
            default:
               serverName = getCityName();
         }
      }
      else
      {
         serverName = m_vpnServer.getServer();
      }
      return serverName;
   }

   /**
    * Get the server name for connect command
    * @return the server name from NordVPN (with underscores)
    */
   public String getServerNordVPN()
   {
      String serverHostName = m_vpnServer.getServer();
      if (null == serverHostName)
      {
         switch (m_cityId)
         {
            case 0 :
               serverHostName = getCountryNordVPN();
               break;
            case 1 :
            default:
               serverHostName = getCityNordVPN();
         }
      }
      return serverHostName;
   }

   /**
    * Get the city name
    * @return the city name from NordVPN (with underscores)
    */
   private String getCityNordVPN()
   {
      return m_cityName.replace(' ', '_');
   }

   /**
    * Get the country name
    * @return the country name from NordVPN (with underscores)
    */
   private String getCountryNordVPN()
   {
      return m_countryName.replace(' ', '_');
   }

   public void addTechnology(int id)
   {
      m_vpnServer.addTechnology(id);
   }

   public boolean hasTechnology(int id)
   {
      return m_vpnServer.hasTechnology(id);
   }

   public void addGroup(NordVPNEnumGroups id)
   {
      m_vpnServer.addGroup(id);
   }

   public void addGroup(int id)
   {
      m_vpnServer.addGroup(id);
   }

   public boolean hasGroup(NordVPNEnumGroups m_filterGroup)
   {
      return m_vpnServer.hasGroup(m_filterGroup);
   }

   public String getFlagImageFileName()
   {
      return "flags" + File.separator + this.getCountryCode() + ".png";
   }

   public URL getFlagUrl()
   {
      return Starter.class.getResource("resources/flags/" + this.getCountryCode() + ".png");
   }

   public String getLabel()
   {
      String labelName = "";
      if (null == m_vpnServer.getServer())
      {
         switch (m_cityId)
         {
            case 0 :
               labelName = m_countryName;
               break;
            case 1 :
            default:
               if (null != m_countryName && false == m_countryName.isBlank())
               {
                  labelName = m_cityName + " [" + m_countryName + "]";
               }
               else
               {
                  labelName = m_cityName;
               }
         }
      }
      else
      {
         labelName = m_vpnServer.getServer() + " [" + m_cityName + " / " + m_vpnServer.getServerName() + "]";
      }
      return labelName;
   }

   public String toString()
   {
      return getServerNordVPN() + " / " + m_vpnServer.getServerKey() + " [" + m_longitude + "," + m_latitude + "] (id=" + m_cityId + ") Groups=" + m_vpnServer.m_groups.toString() + " / Technologies=" + m_vpnServer.m_technologies.toString();
   }

   public String exportAsCsvData()
   {
      return m_latitude + "," // LAT
           + m_longitude + "," // LON
           + m_cityName + "," // CITY
           + m_countryName + "," // COUNTRY
           + m_countryCode + "," // FLAG
           + m_cityId + "," // NUM
           + m_vpnServer.m_groups.toStringId() + "," // GRP 
           + m_vpnServer.m_technologies.toStringId() + "," // TECH
           + ((m_vpnServer.isVirtualLocation()) ? "true" : "false"); // VLOC
   }

}
