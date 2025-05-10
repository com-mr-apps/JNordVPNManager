/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.Json.JsonReader;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class NvpnServers
{
   private static final String COUNTRIES_HTTPS = "https://api.nordvpn.com/v1/servers/countries?limit=0";

   /**
    * Get the Countries server list.
    * <p>
    * First, we try to get all the server locations with group and technology information from:<br>
    * https://api.nordvpn.com/v1/servers?limit=0 (or from a local csv file).<p>
    * Second, the VPN Server list is generated from 2(3) "sources":
    * <ul>
    * <li>the server list stored in user preferences, or</li>
    * <li>updated over network direct from NordVPN 'https://api.nordvpn.com/v1/servers/countries'</li>
    * <li>...as fallback from "nordvpn country..' commands (slowly)</li>
    * </ul>
    * For initialization of the GUI, the list must be retrieved over network direct from NordVPN.<br>
    * To refresh the server list over network from NordVPN, the update flag must be set to true! The server list is
    * stored in the format:<br>
    * Country1@City11/.../City1n:Country2@City21/.../City2n:Countrym@Citym1/.../Citymn
    * 
    * @param update
    *           if true, force update server list from NordVPN over network
    * @return the standard server list
    */
   public static String getCountriesServerList(boolean update)
   {
      String rc_serverList = UtilPrefs.getServerListData();
      boolean isUpdated = false;

      Starter.setWaitCursor();
      Starter.setCursorCanChange(false);

      try
      {
         // -------------------------------------------------------------------------------
         // Initialize/Update Server Locations (create server location objects with group and technology information)
         // -------------------------------------------------------------------------------
         int rc = UtilLocations.initNordVpnServersLocations(update);
         if (rc != 0)
         {
            Starter._m_logError.LoggingError(10500,
                  "Get Server Locations",
                  "Failed to get server data from NordVPN - Fallback data is used.");
         }
         else if (update)
         {
            // -------------------------------------------------------------------------------
            // Initialize/Update Server Country@Cities list
            // -------------------------------------------------------------------------------
            Starter._m_logError.TraceIni("Update Countries List from Server: " + COUNTRIES_HTTPS);
            StringBuffer sbListOfServers = new StringBuffer();
            JSONArray jsonArrCountries = JsonReader.readJsonFromUrl(COUNTRIES_HTTPS);
            int n = jsonArrCountries.length();
            for (int i = 0; i < n; ++i)
            {
               // country
               JSONObject jsonObjCountry = jsonArrCountries.getJSONObject(i);
               String country = jsonObjCountry.getString("name");
               if (sbListOfServers.length() > 0) sbListOfServers.append(Location.SERVERID_LIST_SEPARATOR); // delimiter between countries
               sbListOfServers.append(country);
               String delimiter = Location.SERVERID_SEPARATOR; // first delimiter between country and first city

               // --- cities
               JSONArray jsonArrCities = jsonObjCountry.getJSONArray("cities");
               int nCities = jsonArrCities.length();
               for (int iCity = 0; iCity < nCities; ++iCity)
               {
                  JSONObject jsonObjCity = jsonArrCities.getJSONObject(iCity);
                  String city = jsonObjCity.getString("name");
                  sbListOfServers.append(delimiter + city);
                  delimiter = Location.SERVERID_CITIES_SEPARATOR; // delimiter between city2...cityn
               }
            }
            rc_serverList = sbListOfServers.toString();
            isUpdated = true;
         }
      }
      catch (JSONException | IOException | InterruptedException e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10500, e);
      }

      if ((null == rc_serverList) && (update))
      {
         // Fallback from nordvpn command (slowly - and requires Internet connection)
         rc_serverList = getCountriesServerList2();
         isUpdated = true;
      }

      if (isUpdated) 
      {
         // Update the server list stored in user preferences
         UtilPrefs.setServerListData(rc_serverList);

         // ...also update the time stamp
         long timestamp = System.currentTimeMillis();
         UtilPrefs.setServerListTimestamp(StringFormat.long2String(timestamp, null));
         
         // ...and export the data to a local file
         UtilLocations.export2file();
      }

      Starter.setCursorCanChange(true);
      Starter.resetWaitCursor();

      return rc_serverList;
   }

   /**
    * Deprecated method to get the standard server list<p>
    * Uses the (slow) 'nordvpn country' command - called as fallback in case access to Json file fails
    * @return the standard server list
    */
   private static String getCountriesServerList2()
   {
      Starter._m_logError.TraceIni("Update Countries List from 'nordvpn countries...' command (fallback).");

      // Refresh server list data from NordVPN (slowly!!)
      StringBuffer sbListOfServers = new StringBuffer();
      String countries = NvpnCommands.getListOfCountries();
      if (UtilSystem.isLastError())
      {
         String msg = UtilSystem.getLastError();
         JModalDialog.showError("NordVPN get Countries", msg);
      }
      else
      {
         String[] saCountries = countries.split("\n");
         for (String country : saCountries)
         {
            if (sbListOfServers.length() > 0) sbListOfServers.append(":"); // delimiter between countries
            sbListOfServers.append(country);
            String cities = NvpnCommands.getListOfCities(country);
            String delimiter="@"; // first delimiter between country and first city
            if (UtilSystem.isLastError())
            {
               String msg = UtilSystem.getLastError();
               JModalDialog.showError("NordVPN get Cities", msg);
            }
            else
            {
               String[] saCities = cities.split("\n");
               for (String city : saCities)
               {
                  sbListOfServers.append(delimiter+city);
                  delimiter = "/"; // delimiter between city2...cityn
               }
            }
         }
      }
      String rc_serverList = sbListOfServers.toString();

      return rc_serverList;
   }

}
