/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csvreader.CsvReader;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnTechnologies;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.Json.JsonReader;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class UtilLocations
{
   // all the locations from the server list or csv table (key = server name [city@country] - names with spaces)
   private static HashMap<String, Location> m_countryLocations = null;
   // all the hosts from the server list (key = server name [city@country] - names with spaces)
   private static HashMap<String, ArrayList<VpnServer>> m_cityServers = null;
   
   // CSV file with location features
   private static final String LOCATIONS_CSV = "resources/locations.csv";
   private static final String LOCATIONS_HTTPS = "https://api.nordvpn.com/v1/servers?limit=0";
   private static final String LOCATIONS_EXPORT = new File(Starter.APPLICATION_DATA_ABS_PATH, "locations.dat").toString();
   private static final String SERVERS_EXPORT = new File(Starter.APPLICATION_DATA_ABS_PATH, "servers.dat").toString();


   public static int initNordVpnServersLocations(boolean update)
   {
      int rc = 0;

      if (update == false)
      {
         // first try to import data from previous exported data
         if (null == m_countryLocations)
         {
            // Import from local generated CSV table
            try
            {
               InputStream fis = new FileInputStream(LOCATIONS_EXPORT);
               rc = importLocations(LOCATIONS_EXPORT, fis);
            }
            catch (FileNotFoundException e)
            {
               // ok
               Starter._m_logError.TraceDebug("Local location export CSV file not found: " + LOCATIONS_EXPORT);
            }
            catch (Exception e)
            {
               // unexpected error
               Starter._m_logError.LoggingExceptionMessage(4, 10500, e);
               rc = 10500;
               m_countryLocations = null;
            }
            try
            {
               InputStream fis = new FileInputStream(SERVERS_EXPORT);
               rc = importServers(SERVERS_EXPORT, fis);
            }
            catch (FileNotFoundException e)
            {
               // ok
               Starter._m_logError.TraceDebug("Local servers export CSV file not found: " + SERVERS_EXPORT);
            }
            catch (Exception e)
            {
               // unexpected error
               Starter._m_logError.LoggingExceptionMessage(4, 10500, e);
               rc = 10500;
               m_cityServers = null;
            }
         }
         if (null == m_countryLocations)
         {
            // Fallback from internal CSV table
            InputStream fis = Starter.class.getResourceAsStream(LOCATIONS_CSV);
            rc = importLocations(LOCATIONS_CSV, fis);
         }
         Starter._m_logError.getCurElapsedTime("Import Local Locations Files end");
         return rc;
      }

      Starter._m_logError.TraceIni("Initialize locations from Server: " + LOCATIONS_HTTPS);
      m_countryLocations = new HashMap<String, Location>();
      m_cityServers = new HashMap<String,ArrayList<VpnServer>>();
      NvpnTechnologies.init();
      NvpnGroups.init();
      try
      {
         // If 'KillSwitch' is activated and we are not connected to a VPN server, we don't have Internet access!
         boolean isConnected = Starter.getCurrentStatusData().isConnected();
         boolean isKillSwitch = StringFormat.string2boolean(Starter.getCurrentSettingsData().getKillswitch(false));
         if (isConnected || !isKillSwitch)
         {
            JSONArray jsonArrAll = JsonReader.readJsonFromUrl(LOCATIONS_HTTPS);

            String sCity = null;
            String sCountry = null;
            Double dLatitude = null;
            Double dLongitude = null;
            int locationId = -1;
            int n = jsonArrAll.length();
            for (int i = 0; i < n; ++i)
            {
               //System.out.println("------- " + i);
               // --- stations
               JSONObject jsonObjStations = jsonArrAll.getJSONObject(i);
               //System.out.println(jsonObjStations.getInt("id"));
               String sStationName = jsonObjStations.getString("name"); // Switzerland #218
               //System.out.println(jsonObjStations.getString("station"));
               String sHostName = jsonObjStations.getString("hostname"); // ch218.nordvpn.com
               //System.out.println(jsonObjStations.getString("status"));
               //System.out.println(jsonObjStations.getInt("load"));

               // --- --- services
               boolean hasVPN = false;
               JSONArray jsonArrServices = jsonObjStations.getJSONArray("services");
               int nSer = jsonArrServices.length();
               for (int iSer = 0; iSer < nSer; ++iSer)
               {
                  JSONObject jsonObjService = jsonArrServices.getJSONObject(iSer);
                  //System.out.println(jsonObjService.getInt("id"));
                  //System.out.println(jsonObjService.getString("identifier"));
                  if (jsonObjService.getString("identifier").equalsIgnoreCase("vpn"))
                  {
                     hasVPN = true;
                     break;
                  }
               }
               if (!hasVPN) continue; // filter only VPN entries

               // --- --- locations (one server per entry!
               JSONArray jsonArrLocations = jsonObjStations.getJSONArray("locations");
               int nLoc = jsonArrLocations.length();
               if (nLoc > 1)
               {
                  Starter._m_logError.LoggingWarning(10995,
                        "Get Server Locations",
                        "Not supported yet: More than one location defined for: " + jsonObjStations.getString("name"));
                  nLoc = 1;
               }

               JSONObject jsonObjLocation = null;
               JSONObject jsonObjCountry = null;
               JSONObject jsonObjCity = null;
               for (int iLoc = 0; iLoc < nLoc; ++iLoc)
               {
                  jsonObjLocation = jsonArrLocations.getJSONObject(iLoc);
                  //System.out.println(jsonObjLocation.getInt("id"));
      
                  jsonObjCountry = jsonObjLocation.getJSONObject("country");
                  sCountry = jsonObjCountry.getString("name");

                  jsonObjCity = jsonObjCountry.getJSONObject("city");
                  sCity = jsonObjCity.getString("name");
               }

               String keyServerId = Location.buildServerId(sCity, sCountry);
               Location newLocation = getLocation(keyServerId);
               if (null == newLocation)
               {
                  // one locations definition entry per city
                  locationId = jsonObjCity.getInt("id");
                  dLatitude = jsonObjCity.getDouble("latitude");
                  dLongitude = jsonObjCity.getDouble("longitude");

                  newLocation = new Location(sCity, sCountry, dLongitude, dLatitude, locationId);
                  newLocation.setCountryId(jsonObjCountry.getInt("id"));
                  newLocation.setCountryCode(jsonObjCountry.getString("code"));
                  m_countryLocations.put(newLocation.getServerKey(), newLocation);
               }
               keyServerId = newLocation.getServerKey();  // mangled HashMap key (lower case and '_' replaced by ' ')

               // Create the city servers list
               ArrayList<VpnServer> vpnServers = getServersList(keyServerId);
               if (null == vpnServers)
               {
                  vpnServers = new ArrayList<VpnServer>();
                  m_cityServers.put(keyServerId, vpnServers);
               }
               VpnServer vpnServer = new VpnServer(keyServerId, sStationName, sHostName);
               vpnServers.add(vpnServer);

               // --- --- technologies
               JSONArray jsonArrTechnologies = jsonObjStations.getJSONArray("technologies");
               int nTec = jsonArrTechnologies.length();
               for (int iTec = 0; iTec < nTec; ++iTec)
               {
                  JSONObject jsonObjTechnology = jsonArrTechnologies.getJSONObject(iTec);
                  //System.out.println(jsonObjTechnology.getInt("id"));
                  //System.out.println(jsonObjTechnology.getString("identifier")); // openvpn_udp/openvpn_tcp/proxy_ssl/ikev2
                  JSONObject jsonObjPivot = jsonObjTechnology.getJSONObject("pivot");
                  //System.out.println(jsonObjPivot.getString("status")); // online/offline
                  newLocation.addTechnology(jsonObjPivot.getInt("technology_id"));
                  if (null != vpnServer) vpnServer.addTechnology(jsonObjPivot.getInt("technology_id"));
               }
      
               // --- --- groups
               JSONArray jsonArrGroups = jsonObjStations.getJSONArray("groups");
               int nGrp = jsonArrGroups.length();
               for (int iGrp = 0; iGrp < nGrp; ++iGrp)
               {
                  JSONObject jsonObjGroup = jsonArrGroups.getJSONObject(iGrp);
                  if ((sCountry.startsWith("United States")  || sCountry.startsWith("Canada")) && (jsonObjGroup.getInt("id") == 19))
                  {
                     // skip invalid group entry "Europe" for Kansas City - Record nb. 6390 (03.12.2024)
                     // ...and other cities in US
                     Starter._m_logError.TraceIni("Skip invalid Group Entry 'Europe' for '" + sCity + "' / '" + sCountry + "' Record Nb: " + i);
                     continue;
                  }

                  newLocation.addGroup(NordVPNEnumGroups.get(jsonObjGroup.getInt("id")));
                  if (null != vpnServer) vpnServer.addGroup(NordVPNEnumGroups.get(jsonObjGroup.getInt("id")));
                  //JSONObject jsonObjType = jsonObjGroup.getJSONObject("type"); // Europe/legacy_standard/legacy_p2p/
                  //System.out.println(jsonObjType.getInt("id"));
                  //System.out.println(jsonObjType.getString("identifier")); // region/legacy_group_category
               }
      
               // --- --- specifications
               JSONArray jsonArrSpecs = jsonObjStations.getJSONArray("specifications");
               int nSpec = jsonArrSpecs.length();
               for (int iSpec = 0; iSpec < nSpec; ++iSpec)
               {
                  JSONObject jsonObjSpec = jsonArrSpecs.getJSONObject(iSpec);
                  if (jsonObjSpec.getInt("id") == 115)
                  {
                     // id = 115 / identifier=virtual_location
                     newLocation.setVirtualLocation(true);
                     if (null != vpnServer) vpnServer.setVirtualLocation(true);
                  }
               }

               // --- --- ips
               // JSONArray jsonArrIps = jsonObjStations.getJSONArray("ips");
      
            }
            Starter._m_logError.TraceIni("Location Records read from Server: " + m_countryLocations.size() + ".");            
         }
         else
         {
            // no connection to Internet
            Starter._m_logError.LoggingError(10500,
                  "Failed to read server data from NordVPN.",
                  "Internet connection is not granted and Killswitch is active.");            
            rc = 10500;

            JModalDialog.showWarning("Failed to read server data from NordVPN:\n" +
                 "Internet connection is not granted and Killswitch is active.\n" +
                 "For full access to all functionality of the application (groups and technology information),\n" +
                 "the server list can be downloaded manually - after successfull connection to the internet - with the 'Refresh' button.");
         }
      }
      catch (JSONException | IOException | InterruptedException e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10500, e);
         rc = 10500;

         JModalDialog.showWarning("Failed to read server data from NordVPN:\n" +
               "Please check, if access to internet is granted and check the console output for errors.\n" +
               "For full access to all functionality of the application (groups and technology information),\n" +
               "the server list can be downloaded manually - after successfull connection to the internet - with the 'Refresh' button.");
      }
      if (rc != 0)
      {
         // Fallback from csv table
         InputStream fis = Starter.class.getResourceAsStream(LOCATIONS_CSV);
         int rc_imp = importLocations(LOCATIONS_CSV, fis);
         if (0 != rc_imp) rc = rc_imp;
      }

      return rc;
   }

   /**
    * Get the Location object by city and country.
    * 
    * @param city
    *           is the city
    * @param country
    *           is the country
    * @return the Location object, if found - else null.
    */
   public static Location getLocation(String city, String country)
   {
      Location loc = getLocation(Location.buildServerId(city, country));
      return loc;
   }

   /**
    * Get the Location object by serverKey.
    * 
    * @param serverKey
    *           is the city@country Location HashMap key
    * @return the Location object, if found - else null.
    */
   public static Location getLocation(String serverKey)
   {
      Location loc = null;
      if (null != m_countryLocations)
      {
         loc = m_countryLocations.get(serverKey.toLowerCase().replace("_", " "));
      }
      return loc;
   }

   /**
    * Get the server list of a city Location
    * 
    * @param serverKey
    *           is the city@country Location HashMap key
    * @return the list of servers or <code>null</code> if not found
    */
   public static ArrayList<VpnServer> getServersList(String serverKey)
   {
      if (null != m_cityServers)
      {
         return m_cityServers.get(serverKey.toLowerCase().replace("_", " "));
      }
      return null;
   }

   /**
    * Import Locations from a CSV data file.
    * <p>
    * CSV Table has the following columns:
    * <ul>
    * <li>LAT - the server location latitude</li>
    * <li>LON - the server location longitude</li>
    * <li>CITY - the server city location</li>
    * <li>COUNTRY - the server country location</li>
    * <li>FLAG - the flag (country) code</li>
    * <li>NUM - the server identifier</li>
    * <li>GRP - (optional) the server supported groups list (semicolon separated integers)</li>
    * <li>TECH - (optional) the server supported technology list (semicolon separated integers)</li>
    * <li>VLOC - (v2025.3.1) the flag for virtual location</li>
    * </ul>
    * 
    * @param sCsvFile
    *           is the CSV file name
    * @param isCsvFile
    *           is the CSV file stream
    * @return 0 if all is ok, else an error code
    */
   public static int importLocations(String sCsvFile, InputStream isCsvFile)
   {
      int rc = 0;
      Starter._m_logError.TraceIni("Initialize locations from CSV: " + sCsvFile);
      m_countryLocations = new HashMap<String, Location>();

      try
      {
         // LAT, LON, CITY, NUMBER
         CsvReader locationRecords = new CsvReader(isCsvFile, Charset.defaultCharset());

         locationRecords.readHeaders();

         while (locationRecords.readRecord())
         {
            // read columns
            String sLatitude = locationRecords.get("LAT");
            String sLongitude = locationRecords.get("LON");
            String sCity = locationRecords.get("CITY");
            String sCountry = locationRecords.get("COUNTRY");
            String sFlag = locationRecords.get("FLAG");
            String sNumber = locationRecords.get("NUM");
            String sGroups = locationRecords.get("GRP");
            String sTechs = locationRecords.get("TECH");
            String sVirtualLocation = locationRecords.get("VLOC");

            // get data
            double latitude = StringFormat.string2number(sLatitude);
            double longitude = StringFormat.string2number(sLongitude);
            int number = Integer.parseInt(sNumber.trim());

            // create location 
            Location newLocation = new Location(sCity, sCountry, longitude, latitude, number);

            // Flag (country) code
            if (null != sFlag) newLocation.setCountryCode(sFlag);

            // Virtual Location (Default is false)
            if ((null != sVirtualLocation) && (true == sVirtualLocation.equalsIgnoreCase("true")))
            {
               newLocation.setVirtualLocation(true);
            }

            // add groups (if exist)
            String[] saGroups = (null == sGroups) ? null : sGroups.split(";");
            if (null != saGroups)
            {
               for (String sGroup : saGroups)
               {
                  try
                  {
                     newLocation.addGroup(Integer.valueOf(sGroup));
                  }
                  catch (NumberFormatException e)
                  {
                     // ignore
                  }
               }
            }
            // add technologies (if exist)
            String[] saTechs = (null == sTechs) ? null : sTechs.split(";");
            if (null != saTechs)
            {
               for (String sTech : saTechs)
               {
                  try
                  {
                     newLocation.addTechnology(Integer.valueOf(sTech));
                  }
                  catch (NumberFormatException e)
                  {
                     // ignore
                  }
               }
            }

            // add new location
            m_countryLocations.put(newLocation.getServerKey().toLowerCase(), newLocation);
         }
         // close table
         locationRecords.close();
      }
      catch (FileNotFoundException e)
      {
         Starter._m_logError.LoggingExceptionMessage(5, 10902, e);
         rc = 10902;
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionMessage(5, 10901, e);
         rc = 10901;
      }

      Starter._m_logError.TraceIni("Location Records read from CSV: " + m_countryLocations.size());
      Starter._m_logError.getCurElapsedTime("Import Locations end");

      return rc;
   }

   /**
    * Import Servers (for locations) from a CSV data file.
    * <p>
    * CSV Table has the following columns:
    * <ul>
    * <li>KEY - the location key (city@country) - link to the location data</li>
    * <li>NAME - the server name</li>
    * <li>HOST - the server hostname (for connection)</li>
    * <li>GRP - the server supported groups list (semicolon separated integers)</li>
    * <li>TECH - the server supported technology list (semicolon separated integers)</li>
    * <li>VLOC - the flag for virtual location</li>
    * </ul>
    * 
    * @param sCsvFile
    *           is the CSV file name
    * @param isCsvFile
    *           is the CSV file stream
    * @return 0 if all is ok, else an error code
    */
   public static int importServers(String sCsvFile, InputStream isCsvFile)
   {
      int rc = 0;
      Starter._m_logError.TraceIni("Initialize servers from CSV: " + sCsvFile);
      m_cityServers = new HashMap<String, ArrayList<VpnServer>>();

      int nbServers = 0;
      try
      {
         // KEY, NAME, HOST, GRP, TECH, VLOC
         CsvReader serverRecords = new CsvReader(isCsvFile, Charset.defaultCharset());

         serverRecords.readHeaders();

         while (serverRecords.readRecord())
         {
            // read columns
            String sKey = serverRecords.get("KEY");
            String sName = serverRecords.get("NAME");
            String sHost = serverRecords.get("HOST");
            String sGroups = serverRecords.get("GRP");
            String sTechs = serverRecords.get("TECH");
            String sVirtualLocation = serverRecords.get("VLOC");

            // create new server object
            VpnServer vpnServer = new VpnServer(sKey, sName, sHost);

            // set Virtual Location (Default is false)
            if ((null != sVirtualLocation) && (true == sVirtualLocation.equalsIgnoreCase("true")))
            {
               vpnServer.setVirtualLocation(true);
            }

            // set groups (if exist)
            String[] saGroups = (null == sGroups) ? null : sGroups.split(";");
            if (null != saGroups)
            {
               for (String sGroup : saGroups)
               {
                  try
                  {
                     vpnServer.addGroup(Integer.valueOf(sGroup));
                  }
                  catch (NumberFormatException e)
                  {
                     // ignore
                  }
               }
            }

            // set technologies (if exist)
            String[] saTechs = (null == sTechs) ? null : sTechs.split(";");
            if (null != saTechs)
            {
               for (String sTech : saTechs)
               {
                  try
                  {
                     vpnServer.addTechnology(Integer.valueOf(sTech));
                  }
                  catch (NumberFormatException e)
                  {
                     // ignore
                  }
               }
            }

            // add new server related to a location (referenced by the location key 'city@country')
            ArrayList<VpnServer> vpnServers = m_cityServers.get(sKey);
            if (null == vpnServers)
            {
               // new location
               vpnServers = new ArrayList<VpnServer>();
               m_cityServers.put(sKey, vpnServers);
            }
            vpnServers.add(vpnServer);
            ++nbServers;
         }
         // close table
         serverRecords.close();
      }
      catch (FileNotFoundException e)
      {
         Starter._m_logError.LoggingExceptionMessage(5, 10902, e);
         rc = 10902;
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionMessage(5, 10901, e);
         rc = 10901;
      }

      Starter._m_logError.TraceIni("Servers Records read from CSV: " + nbServers);
      Starter._m_logError.getCurElapsedTime("Import Servers end");

      return rc;
   }

   /**
    * Export Locations and Servers to a CSV data file.
    */
   public static void export2file()
   {
      // create the export files
      BufferedWriter bwExportLocations = null;
      File fExportLocations = new File(LOCATIONS_EXPORT);
      BufferedWriter bwExportServers = null;
      File fExportServers = new File(SERVERS_EXPORT);
      try
      {
         // if export files already exists, delete them
         if (fExportLocations.exists())
         {
            if (false == fExportLocations.delete())
            {
               // existing locations file cannot be deleted -> return with error
               Starter._m_logError.LoggingWarning(10900, 
                     "Cannot delete locations data export file",
                     "Error on delete locations data export file: '" + LOCATIONS_EXPORT + "'. Please check permissions.");
               return;
            }
         }
         if (fExportServers.exists())
         {
            if (false == fExportServers.delete())
            {
               // existing servers file cannot be deleted -> return with error
               Starter._m_logError.LoggingWarning(10900, 
                     "Cannot delete servers data export file",
                     "Error on delete export server data file: '" + LOCATIONS_EXPORT + "'. Please check permissions.");
               return; // avoid data inconsistency between locations and servers file
            }
         }

         // check if paths exists (same for locations and servers export files) - if not, create
         File fpExportFileDir = new File(LOCATIONS_EXPORT).getParentFile();
         if (!fpExportFileDir.exists())
         {
            // generate the directory
            fpExportFileDir.mkdirs();
         }

         // open export files
         if (null != fExportLocations)
         {
            FileOutputStream FOStream = new FileOutputStream(fExportLocations);
            OutputStreamWriter osw = new OutputStreamWriter(FOStream, "UTF-8");
            bwExportLocations = new BufferedWriter(osw);

            // CSV Columns header
            bwExportLocations.write("LAT,LON,CITY,COUNTRY,FLAG,NUM,GRP,TECH,VLOC\n");
         }
         if (null != fExportServers)
         {
            FileOutputStream FOStream = new FileOutputStream(fExportServers);
            OutputStreamWriter osw = new OutputStreamWriter(FOStream, "UTF-8");
            bwExportServers = new BufferedWriter(osw);

            // CSV Columns header
            bwExportServers.write("KEY,NAME,HOST,GRP,TECH,VLOC\n");
         }

         // export data
         int nbCountries = 0;
         int nbCities = 0;
         int nbServers = 0;
         String serverListString = UtilPrefs.getServerListData();
         if (null != serverListString && !serverListString.isBlank())
         {
            String[] saServerList = serverListString.split(Location.SERVERID_LIST_SEPARATOR);
            if (saServerList.length > 0)
            {
               for (String countryCitiesList : saServerList)
               {
                  ++nbCountries;
                  String[] saCountryCities = countryCitiesList.split(Location.SERVERID_SEPARATOR);
                  if (saCountryCities.length == 2)
                  {
                     String country = saCountryCities[0];
                     String cities = saCountryCities[1];
                     String[] saCities = cities.split(Location.SERVERID_CITIES_SEPARATOR);
                     for (String city : saCities)
                     {
                        ++nbCities;
                        String serverKey = Location.buildServerId(city, country);
                        Location loc = getLocation(serverKey);
                        if (null != loc)
                        {
                           bwExportLocations.write(loc.exportAsCsvData() + "\n");

                           // write second file with VpnServers (per city)
                           ArrayList<VpnServer> vpnServers = getServersList(serverKey);
                           if (null != vpnServers)
                           {
                              for (VpnServer vpnServer : vpnServers)
                              {
                                 ++nbServers;
                                 bwExportServers.write(vpnServer.exportAsCsvData() + "\n");
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
         Starter._m_logError.TraceDebug("Export server data: Exported nb. coutries: " + nbCountries + " / nb. cities: " + nbCities + " / nb. servers: " + nbServers);
      }
      catch (IOException e)
      {
         // Export file could not be accessed.
         Starter._m_logError.LoggingWarning(10900, 
               "Cannot export locations data file",
               "Error on export locations data file: '"  + LOCATIONS_EXPORT + "'. Please check permissions.");
      }

      // finally - close the export files
      try
      {
         bwExportLocations.flush();
         bwExportLocations.close();
      }
      catch (IOException e)
      {
         Starter._m_logError.TraceDebug("Please check: Failed to close the export file: '" + LOCATIONS_EXPORT);
      }
      try
      {
         bwExportServers.flush();
         bwExportServers.close();
      }
      catch (IOException e)
      {
         Starter._m_logError.TraceDebug("Please check: Failed to close the export file: '" + SERVERS_EXPORT);
      }
      Starter._m_logError.getCurElapsedTime("Export Local Locations Files end");
   }
}
