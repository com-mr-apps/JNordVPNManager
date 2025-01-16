/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.Style;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapFrame.Tool;
import org.geotools.swing.JMapPane;
import org.geotools.swing.tool.InfoToolResult;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.UtilZip;

/**
 * This class reads data for point locations and associated attributes from a comma separated text (CSV) file and adds
 * them as an existing shapefile. It illustrates how to build a feature type.
 */
public class UtilMapGeneration
{
   // Map
   private static final String MAP_ARCHIVE = "resources/ne_50m_admin_0_countries.zip";
   private static final String MAP_NAME = "ne_50m_admin_0_countries.shp";
   private static final String MAP_PICTURE = "resources/NE1_50M_SR_W.tif";
   

   private static String m_tmpMapDirectory = null;
   private static JMapPane m_mapPane = null;
   private static MapContent m_map = null;
   private static Layer m_baseWorldMapLayer = null;
   private static UpdatableLayer m_currentServerMapLayer = null;
   private static UpdatableLayer m_vpnServerMapLayer = null;
   private static ReferencedEnvelope m_fullMapEnvelope = null;
   private static ReferencedEnvelope m_serverMapEnvelope = null;
   
   /**
    * Cleanup on exit
    * <p>
    * <ul>
    * <it> delete temporary generated map file directory.<it>
    * <ul>
    */
   public static void cleanUp()
   {
      // delete temporary generated map file directory
      if (null != m_tmpMapDirectory)
      {
         File fpTmpMapDirectory = new File(m_tmpMapDirectory);
         UtilSystem.deleteDir(fpTmpMapDirectory);
      }
   }

   /**
    * Create the world map with VPN Server Locations
    * @return the created world map
    * @throws Exception
    */
   public static JMapFrame createMap()
   {
      JMapFrame mapFrame = null;
      
      try
      {
         // Create a map content...
         Starter._m_logError.TraceDebug("Create new Map Content...");

         m_map = new MapContent();
         m_map.setTitle("JNordVPN Manager");

         // ...add our shape file to it..
         Starter._m_logError.TraceDebug("Add World Map Layer...");

         m_baseWorldMapLayer = createWorldMapLayer();
         if (null != m_baseWorldMapLayer) m_map.addLayer(m_baseWorldMapLayer);
         
         // Create a JMapFrame with custom tool bar buttons
         Starter._m_logError.TraceDebug("Create World Map Frame...");
         mapFrame = new JMapFrame(m_map);
         mapFrame.enableToolBar(true);
         mapFrame.enableTool(Tool.POINTER, Tool.PAN, Tool.RESET, Tool.ZOOM , Tool.SCROLLWHEEL /*, Tool.INFO */);
         mapFrame.enableStatusBar(false);
         //mapFrame.enableLayerTable(true);
         //mapFrame.setSize(800, 400);

         m_mapPane = mapFrame.getMapPane();
         m_fullMapEnvelope = m_mapPane.getMapContent().getViewport().getBounds();
         
      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10996, e);
      }
 
      return mapFrame;
   }

   private static void zoomOut(ReferencedEnvelope envelope)
   {
      if (null != envelope)
      {
         m_mapPane.getMapContent().getViewport().setBounds(envelope);
         // force refresh!
         m_mapPane.moveImage(1,1);
         m_mapPane.moveImage(-1,-1);
         m_mapPane.repaint();
      }
   }

   public static void zoomIn(Location loc)
   {
      if (null != loc)
      {
         ReferencedEnvelope envelope = new ReferencedEnvelope(loc.getLongitude()-10, loc.getLongitude()+10, loc.getLatitude()-10,loc.getLatitude()+10, DefaultGeographicCRS.WGS84);      
         m_mapPane.getMapContent().getViewport().setBounds(envelope);
         // force refresh!
         m_mapPane.moveImage(1,1);
         m_mapPane.moveImage(-1,-1);
         m_mapPane.repaint();
      }
   }

   public static void zoomServerLayer()
   {
      zoomOut(m_serverMapEnvelope);
   }

   public static void changeCurrentServerMapLayer(Location loc)
   {
      if (null != m_currentServerMapLayer)
      {
         m_map.removeLayer(m_currentServerMapLayer);
         m_currentServerMapLayer = null;
      }

      if (null != loc)
      {
         m_currentServerMapLayer = createCurrentServerMapLayer(loc);
         m_map.addLayer(m_currentServerMapLayer);
         m_currentServerMapLayer.updated();
         zoomIn(loc);
         Starter._m_logError.LoggingInfo("Changed VPN Server on map: " + loc.toString());
      }
   }
   
   /**
    * Create the layer with the current VPN server
    * @param serverName is the name of the current active VPN server
    * @param m_longitude is the longitude
    * @param m_latitude is the latitude
    * @return the created layer
    */
   private static UpdatableLayer createCurrentServerMapLayer(Location loc)
   {
      // Feature Type
      SimpleFeatureType LOCATION = createLocationFeatureType();
   
      // create feature[s] using the type defined
      SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(LOCATION);
      GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
      Point point = geometryFactory.createPoint(new Coordinate(loc.getLongitude(), loc.getLatitude()));
      featureBuilder.add(point);
   
      SimpleFeature feature = featureBuilder.buildFeature("ActiveServer");
      feature.setAttribute("Name", loc.getServerId());
      List<SimpleFeature> featureCollection = new ArrayList<>();
      featureCollection.add(feature);
   
      // create the layer
      Style style = SLD.createPointStyle("Circle" /*"Star"*/, Color.GREEN, Color.RED, 0.5f, 20);
      UpdatableLayer layer = new UpdatableLayer(DataUtilities.collection(featureCollection), style);
      layer.setTitle("Current Server");
      return layer;
   }

   public static void changeVpnServerLocationsMapLayer(ArrayList<String> vpnServers)
   {
      if (null != m_vpnServerMapLayer) m_map.removeLayer(m_vpnServerMapLayer);

      if (null != vpnServers && vpnServers.size() > 0)
      {
         m_vpnServerMapLayer = createVpnServerLocationsMapLayer(vpnServers);
         m_vpnServerMapLayer.setTitle("VPN Server");
         m_map.addLayer(m_vpnServerMapLayer);
         m_vpnServerMapLayer.updated();
         m_mapPane.moveImage(1,1);
         m_mapPane.moveImage(-1,-1);
         m_mapPane.repaint();
         Starter._m_logError.TraceDebug("Updated VPN Server locations layer.");
      }
      else
      {
         Starter._m_logError.TraceDebug("Empty VPN Serverlist, removed VPN Server locations layer.");
         zoomOut(m_fullMapEnvelope);
      }
   }
   
   /**
    * Create the VPN Server locations layer
    * @param vpnServers is a list with the [filtered] vpnServers 
    * @return the created map layer with all VPN servers
    */
   private static UpdatableLayer createVpnServerLocationsMapLayer(ArrayList<String> vpnServers)
   {
      // create features using the type defined
      List<SimpleFeature> locationFeatures = null;
      locationFeatures = getVpnServerLocationFeatures(vpnServers);
      
      // create the layer
      Style style = SLD.createPointStyle("Circle", Color.BLUE, Color.BLUE, 0.3f, 10);
      UpdatableLayer layer = new UpdatableLayer(DataUtilities.collection(locationFeatures), style);
   
      return layer;
   }

   /**
    * Create the world map layer
    * <p>
    * The world map consists of many files (stored in a ZIP archive), which is unpacked in a temporary folder
    */
   private static Layer createWorldMapLayer()
   {
      Layer layer = null;

      // ...work with a temporary copy (original map may be overwritten with additional feature data!)
      try
      {
         m_tmpMapDirectory = UtilZip.unzipTmp(Starter.class.getResourceAsStream(MAP_ARCHIVE), "NordVPNmap");

         File fpMapFileName = new File(m_tmpMapDirectory, MAP_NAME);

         // get map file data store
         FileDataStore   store = FileDataStoreFinder.getDataStore(fpMapFileName);
         SimpleFeatureSource   featureSource = store.getFeatureSource();

         // create the world map layer
         Style style = SLD.createSimpleStyle(featureSource.getSchema());
         layer = new FeatureLayer(featureSource, style);
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionMessage(3, 10500, e);
      }
 
      return layer;
   }

   /**
    * Create a feature of type 'location'
    */
   private static SimpleFeatureType createLocationFeatureType()
   {

      SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
      builder.setName("Location");
      builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

      // add attributes in order
      builder.add("Coord", Point.class);
      builder.length(15).add("Name", String.class); // <- 15 chars width for name field
      builder.add("Number", Integer.class);

      // build the type
      SimpleFeatureType LOCATION = builder.buildFeatureType();

      return LOCATION;
   }

   /**
    * Get a feature collection from CSV data
    * @param vpnServers is a list with the [filtered] vpnServers
    * @return the created features collection
    */
   private static List<SimpleFeature> getVpnServerLocationFeatures(ArrayList<String> vpnServers)
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

      double minLon = 99999.0;
      double maxLon = -99999.0;
      double minLat = 99999.0;
      double maxLat = -99999.0;
      for (String cityId : vpnServers)
      {
         Location loc = UtilLocations.getLocation(cityId);
         double latitude = loc.getLatitude();
         double longitude = loc.getLongitude();
         String name = cityId;
         int number = loc.getCityId();

         // Longitude (= x coordinate) first !
         Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

         // min/max
         if (longitude < minLon) minLon = longitude;
         if (longitude > maxLon) maxLon = longitude;
         if (latitude < minLat) minLat = latitude;
         if (latitude > maxLat) maxLat = latitude;

         featureBuilder.add(point);
         featureBuilder.add(name);
         featureBuilder.add(number);
         SimpleFeature feature = featureBuilder.buildFeature(null);
         features.add(feature);

         if (number == 0)
         {
            Starter._m_logError.LoggingInfo("VPN Server location for city=" + cityId + "< not found. Locations database requires an update!");
         }
      }

      // set the envelope to display all servers
      m_serverMapEnvelope = new ReferencedEnvelope(minLon-3, maxLon+3, minLat-3, maxLat+3, DefaultGeographicCRS.WGS84);      

      return features;
   }

   /**
    * Get the server selected with the mouse on the map
    * 
    * @param pt
    *           is the clicked location in world map coordinates
    * @return if found, the server location - else null
    */
   public static Location getPickedServer(Point2D pt)
   {
      Location loc = null;

      Position2D pos = new Position2D(
            m_map.getCoordinateReferenceSystem(),
            pt.getX(),
            pt.getY());

      try
      {
         // first we try to get a match on the VPN Servers Layer
         PickServerLocation helper = new PickServerLocation();
         helper.setMapContent(m_map);
         helper.setLayer(m_vpnServerMapLayer);

         InfoToolResult info = helper.getInfo(pos);
         int nb = info.getNumFeatures();
         if (nb > 0)
         {
            // got a match
            Starter._m_logError.LoggingInfo("VPN Server city selected: " + info.toString());
            Map<String, Object> data = info.getFeatureData(0);
            String serverId = (String) data.get("Name");
            loc = UtilLocations.getLocation(serverId);
         }
         else
         {
            // ..next we try to get a match at the world map (countries) layer
            helper.setMapContent(m_map);
            helper.setLayer(m_baseWorldMapLayer);

            info = helper.getInfo(pos);
            nb = info.getNumFeatures();
            if (nb > 0)
            {
               // got a match
               Starter._m_logError.LoggingInfo("VPN Server country selected: " + info.toString().substring(0, 80) + "...");  
               Map<String, Object> data = info.getFeatureData(0);
               String serverId = (String) data.get("NAME");
               String countryCode = (String) data.get("ISO_A2_EH");

               // name mappings for geo-map country selections to NordVPN names
               if (serverId.equalsIgnoreCase("Laos")) serverId = "Lao People's Democratic Republic";
               if (serverId.startsWith("United States of")) serverId = "United States";
               loc = UtilLocations.getLocation(serverId);
               loc.setCountryCode(countryCode.toLowerCase());
            }
         }
      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10996, e);
      }

      return loc;
   }
}
