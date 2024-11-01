/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import com.csvreader.CsvReader;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class UtilLocations
{
   // all the locations from the csv table
   private static HashMap<String, Location> m_csvLocations = new HashMap<String, Location>();
   
   // CSV file with location features
   private static final String LOCATIONS_CSV = "resources/locations.csv";

   public static void initCsvLocations()
   {
      Starter._m_logError.TraceIni("Initialize locations from CSV: " + LOCATIONS_CSV + "<.");
      
      try
      {
         // LAT, LON, CITY, NUMBER
         CsvReader locations = new CsvReader(Starter.class.getResourceAsStream(LOCATIONS_CSV), Charset.defaultCharset()); //UtilSystem.getFilePath(LOCATIONS_CSV));

         locations.readHeaders();

         while (locations.readRecord())
         {
            String sLatitude = locations.get("LAT");
            String sLongitude = locations.get("LON");
            String sCity = locations.get("CITY");
            String sCountry = locations.get("COUNTRY");
            String sNumber = locations.get("NUM");

            double latitude = StringFormat.string2number(sLatitude);
            double longitude = StringFormat.string2number(sLongitude);
            int number = Integer.parseInt(sNumber.trim());
            
            Location newLocation = new Location(sCity, sCountry, longitude, latitude, number);
            m_csvLocations.put(newLocation.getServerId().toLowerCase(), newLocation);
         }
         locations.close();
      }
      catch (FileNotFoundException e)
      {
         Starter._m_logError.TranslatorExceptionMessage(5, 10902, e);
      }
      catch (IOException e)
      {
         Starter._m_logError.TranslatorExceptionMessage(5, 10901, e);
      }

      Starter._m_logError.TraceIni("Location Records read from CSV: " + m_csvLocations.size() + "<.");
   }

   /**
    * Get the Location object by city and country.
    * @param city is the city
    * @param country is the country
    * @return the Location object, if found - else null.
    */
   public static Location getLocation(String city, String country)
   {
      return getLocation(getServerId(city, country));
   }
   
   /**
    * Get the Location object by cityId.
    * @param serverId is the server Id [HashTable search-key]
    * @return the Location object, if found - else null.
    */
   public static Location getLocation(String serverId)
   {
      Location loc = m_csvLocations.get(serverId.replace('_', ' ').toLowerCase());
      if (null == loc) loc = new Location();
      return loc;
   }
   
   /**
    * Create the serverId HashTable Search-key from city and country
    * @param city is the city
    * @param country is the country
    * @return the search key
    */
   public static String getServerId(String city, String country)
   {
      return StringFormat.printString(city, "nowhere", "nowhere") + Location.SERVERID_SEPARATOR + StringFormat.printString(country, "nowhere", "nowhere");
   }
}
