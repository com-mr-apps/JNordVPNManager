/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.Style;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.Layer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.csvreader.CsvReader;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JSpeedtestDialog;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

/**
 * Utility class for Speed Test Functionality
 */
public class UtilSpeedtest
{
   // CSV file with Speedtest Server location features
   private static final String                 SPEEDTEST_SERVERS_CSV     = "resources/speedtest_servers.csv";

   // all the speedtest locations from the csv table
   private static ArrayList<SpeedtestLocation> m_speedtestLocations      = null;

   // Map server city location to speedtest server URL
   private static HashMap<String, String>      m_speedtestUrls           = null;

   // WorldMap related data: Layer with speedtest servers and layer visible status
   private static Layer                        m_speedtestServerMapLayer = null;
   private static boolean                      m_isVisible               = false;

   private static JSpeedtestDialog             m_speedTestDiaLog         = null; 
   // error messages
   private static boolean                      m_speedtestError          = false;

   public static void setVisibleSpeedtestMapLayer(boolean visible)
   {
      if (m_isVisible != visible)
      {
         m_isVisible = visible;
         if ((true == visible) && (null == m_speedtestServerMapLayer))
         {
            // if not created yet, add speedtest servers layer
            createSpeedtestServerLocationsMapLayer();
            UtilMapGeneration.addLayer(m_speedtestServerMapLayer);
         }
         m_speedtestServerMapLayer.setVisible(visible);
         UtilMapGeneration.mapRefresh();
      }
   }

   public static boolean isVisibleSpeedtestMapLayer()
   {
      return m_isVisible;
   }

   /**
    * Import Speedtest Locations from a CSV data file.
    * <p>
    * CSV Table has the following columns:
    * <ul>
    * <li>LAT - the server location latitude</li>
    * <li>LON - the server location longitude</li>
    * <li>CITY - the server city location</li>
    * <li>URL - the server url with the test download file(s)</li>
    * </ul>
    * 
    * @return 0 if all is ok, else an error code
    */
   private static int importSpeedtestLocations()
   {
      int rc = 0;
      Starter._m_logError.TraceIni("Initialize speedtest locations from CSV: " + SPEEDTEST_SERVERS_CSV);
      InputStream isCsvFile = Starter.class.getResourceAsStream(SPEEDTEST_SERVERS_CSV);
      m_speedtestLocations = new ArrayList<SpeedtestLocation>();
      m_speedtestUrls = new HashMap<String, String>();

      try
      {
         // LAT, LON, CITY, URL
         CsvReader locations = new CsvReader(isCsvFile, Charset.defaultCharset());

         locations.readHeaders();

         while (locations.readRecord())
         {
            // read columns
            String sLatitude = locations.get("LAT");
            String sLongitude = locations.get("LON");
            String sCity = locations.get("CITY");
            String sUrl = locations.get("URL");

            // get data
            double latitude = StringFormat.string2number(sLatitude);
            double longitude = StringFormat.string2number(sLongitude);

            // create location 
            SpeedtestLocation newLocation = new SpeedtestLocation(sCity, longitude, latitude, sUrl);

            // add new location
            m_speedtestLocations.add(newLocation);
            m_speedtestUrls.put(sCity.toLowerCase(), sUrl);
         }

         // close table
         locations.close();
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

      Starter._m_logError.TraceIni("Speedtest Location Records read from CSV: " + m_speedtestLocations.size() + "<.");
      Starter._m_logError.getCurElapsedTime("Import locations end");

      return rc;
   }

   /**
    * Create the Speedtest Server locations layer
    * @return the created map layer with all VPN servers
    */
   private static Layer createSpeedtestServerLocationsMapLayer()
   {
      UtilMapGeneration.removeLayer(m_speedtestServerMapLayer);
      m_speedtestServerMapLayer = null;

      if (null == m_speedtestLocations)
      {
         // first time call - initialization
         importSpeedtestLocations();
      }

      // create features using the type defined
      List<SimpleFeature> locationFeatures = null;
      locationFeatures = getSpeedtestServerLocationFeatures(m_speedtestLocations);
      
      // create the layer
      Style style = SLD.createPointStyle("Circle", Color.BLACK, Color.YELLOW, 0.3f, 30);
      m_speedtestServerMapLayer = new UpdatableLayer(DataUtilities.collection(locationFeatures), style);
      m_speedtestServerMapLayer.setTitle("Speedtest Servers");

      return m_speedtestServerMapLayer;
   }

   /**
    * Get a feature collection from CSV data
    * @param speedtestServers is a list with the [filtered] vpnServers
    * @return the created features collection
    */
   private static List<SimpleFeature> getSpeedtestServerLocationFeatures(ArrayList<SpeedtestLocation> speedtestServers)
   {
      /*
       * A list to collect features as we create them.
       */
      List<SimpleFeature> features = new ArrayList<>();

      /*
       * GeometryFactory will be used to create the geometry attribute of each feature, using a Point object for the
       * location.
       */
      GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

      // Feature Type
      final SimpleFeatureType LOCATION = createLocationFeatureType();
      SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(LOCATION);

      for (SpeedtestLocation loc : speedtestServers)
      {
         double latitude = loc.getLatitude();
         double longitude = loc.getLongitude();
         String name = loc.getCityName();

         // Longitude (= x coordinate) first !
         Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

         featureBuilder.add(point);
         featureBuilder.add(name);
         SimpleFeature feature = featureBuilder.buildFeature(null);
         features.add(feature);
      }

      return features;
   }

   /**
    * Create a feature of type 'location'
    */
   private static SimpleFeatureType createLocationFeatureType()
   {

      SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
      builder.setName("Speedtest");
      builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

      // add attributes in order
      builder.add("Coord", Point.class);
      builder.length(15).add("Name", String.class); // <- 15 chars width for name field

      // build the type
      SimpleFeatureType LOCATION = builder.buildFeatureType();

      return LOCATION;
   }

   public static void speedTest(CurrentLocation loc)
   {
      double currentLat = loc.getLatitude();
      double currentLon = loc.getLongitude();

      String uri = "https://lg-dene.fdcservers.net/100MBtest.zip";
      String city = "Denver";
      double dist = 999999999;
      // search nearest Test Server
      for (SpeedtestLocation sloc : m_speedtestLocations)
      {
         double latitude = sloc.getLatitude();
         double longitude = sloc.getLongitude();
         double d = distance(currentLat, latitude, currentLon, longitude, 0, 0);
         if (d < dist)
         {
            dist = d;
            uri = sloc.getServerUrl();
            city = sloc.getCityName();
         }
      }
      speedTest(city, uri);

   }

   /**
    * Run the Speed test
    * 
    * @param uri
    *           is the city of the Speed test Server
    * @param uri
    *           is the URL of the Speed test Server
    */
   private static void speedTest(String city, String uri)
   {
      Starter._m_logError.TraceDebug("(speedTest) uri=" + uri + " [" + city + "]");
      SpeedTestSocket speedTestSocket = new SpeedTestSocket();

      if (null == m_speedTestDiaLog)
      {
         // create once
         m_speedTestDiaLog = new JSpeedtestDialog(Starter.getMainFrame(), uri);
      }
      else
      {
         // .. re use
         m_speedTestDiaLog.init(uri);
      }

      // add a listener to wait for speedtest completion and progress [or error]
      speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

         @Override
         public void onCompletion(SpeedTestReport report)
         {
            if (m_speedTestDiaLog.forceStopTask())
            {
               // called when download/upload is interrupted manually
               m_speedTestDiaLog.setMessage("interrupted.", false);
            }
            else if (true == m_speedtestError)
            {
               Starter._m_logError.TraceDebug("(SpeedTest) onCompletion() called after forceStopTask().");
            }
            else
            {
               // called when download/upload is successfully completed
               Starter._m_logError.TraceDebug("[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
               m_speedTestDiaLog.setMessage("completed.", false);
               m_speedTestDiaLog.setSpeeds(100, report.getTransferRateBit().divide(new BigDecimal(1000000.0)).doubleValue());
            }
            Starter._m_logError.getCurElapsedTime("Speed Test End");
         }

         @Override
         public void onError(SpeedTestError speedTestError, String errorMessage)
         {
            // called when a download/upload error occur - may be called multiple times
            if (false == m_speedtestError)
            {
               // only once..
               m_speedtestError = true;
               // called when server connection failed
               for (SpeedtestLocation sloc : m_speedtestLocations)
               {
                  // search the failed server and remove it for this session
                  if (true == uri.equals(sloc.getServerUrl()))
                  {
                     m_speedTestDiaLog.setMessage("Speed Test Server located in '" + sloc.getCityName() + "' is currently not available and will be removed for this session.\n" + 
                                                  "Please start Speed Test again to use the next closest server.\n" +
                                                  errorMessage, false);
                     m_speedtestLocations.remove(sloc);
                     break;
                  }
               }

               m_speedTestDiaLog.setSpeeds(100, 0);
               speedTestSocket.forceStopTask();
               return;
            }
            m_speedTestDiaLog.setMessage(errorMessage, true);
         }

         @Override
         public void onProgress(float percent, SpeedTestReport report)
         {
            // called to notify download/upload progress
            Starter._m_logError.TraceDebug("[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
            m_speedTestDiaLog.setSpeeds((int)report.getProgressPercent(), report.getTransferRateBit().divide(new BigDecimal(1000000.0)).doubleValue());
            if (m_speedTestDiaLog.forceStopTask())
            {
               // manual force stop speed test
               speedTestSocket.forceStopTask();
            }
            else
            {
               m_speedTestDiaLog.setMessage("in progress" + " [" + city + "]...", false);
            }
         }
      });

      // start Speed test
      m_speedtestError = false;
      Starter._m_logError.getCurElapsedTime("Speed Test Start");
      speedTestSocket.startFixedDownload(uri, 30000, 2000);
   }

   /**
    * Calculate distance between two points in latitude and longitude taking
    * into account height difference. If you are not interested in height
    * difference pass 0.0. Uses Haversine method as its base.
    * 
    * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
    * el2 End altitude in meters
    * @returns Distance in Meters
    */
   private static double distance(double lat1, double lat2, double lon1,
           double lon2, double el1, double el2) {

       final int R = 6371; // Radius of the earth
       
       double latDistance = Math.toRadians(lat2 - lat1);
       double lonDistance = Math.toRadians(lon2 - lon1);
       double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
               + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
               * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
       double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
       double distance = R * c * 1000; // convert to meters

       double height = el1 - el2;

       distance = Math.pow(distance, 2) + Math.pow(height, 2);

       return Math.sqrt(distance);
   }

}
