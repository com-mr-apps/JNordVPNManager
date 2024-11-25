/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import com.mr.apps.JNordVpnManager.Starter;

/**
 * Store locations from the CSV table.
 * <p>
 * Also used to store the current server during runtime (in main class).
 */
public class Location
{
   public static final String SERVERID_SEPARATOR = "@";
   public static final String SERVERID_LIST_SEPARATOR = ":";
   protected String serverId;
   protected String city;
   protected String country;
   protected double longitude;
   protected double latitude;
   protected int number;
 
   public Location()
   {
      setServerId("nowhere@nowhere");
      setLongitude(0.0);
      setLatitude(0.0);
      setNumber(0);
      setCity("nowhere");
      setCountry("nowhere");
   }

   public Location (String serverId, double longitude, double latitude, int number)
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
         setCity(city);
         setCountry("");
         number = -1;  // for error handling!!
      }
      else
      {
         setServerId(city.trim() + SERVERID_SEPARATOR + country.trim());
         setCity(city);
         setCountry(country);
      }
      setLongitude(longitude);
      setLatitude(latitude);
      setNumber(number);
 
      if (number == 1)
      {
         Starter._m_logError.TraceDebug("Location=" + this.toString() + "<.");         
      }
      else if (number == -1)
      {
         Starter._m_logError.TranslatorError(10200, "Location Definition Error", "Location=" + this.toString() + "< cannot be defined!");         
      }
      else
      {
         // from CSV
         //JNordVpnManager._m_logError.TraceDebug("Add location=" + this.toString() + "<.");
      }
      
   }
   public Location (String city, String country, double longitude, double latitude, int number)
   {
      this(city+"@"+country, longitude, latitude, number);
   }

   public String getServerId()
   {
      return serverId;
   }

   public void setServerId(String serverId)
   {
      this.serverId = serverId;
   }

   public double getLongitude()
   {
      return longitude;
   }

   public void setLongitude(double longitude)
   {
      this.longitude = longitude;
   }

   public double getLatitude()
   {
      return latitude;
   }

   public void setLatitude(double latitude)
   {
      this.latitude = latitude;
   }

   public int getNumber()
   {
      return number;
   }

   public void setNumber(int number)
   {
      this.number = number;
   }

   /**
    * Get the city name
    * @return the city name from CSV table
    */
   public String getCity()
   {
      return city;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   /**
    * Get the country name
    * @return the country name from CSV table
    */
   public String getCountry()
   {
      return country;
   }

   public void setCountry(String country)
   {
      this.country = country;
   }
   
   /**
    * Get the city name
    * @return the city name from NordVPN (with underscores)
    */
   public String getCityNordVPN()
   {
      return city.replace(' ', '_');
   }

   /**
    * Get the country name
    * @return the country name from NordVPN (with underscores)
    */
   public String getCountryNordVPN()
   {
      return country.replace(' ', '_');
   }

   public String toString()
   {
      return city + "/" + country + " [" + longitude + "," + latitude + "] (id=" + number + ")";
   }
}
