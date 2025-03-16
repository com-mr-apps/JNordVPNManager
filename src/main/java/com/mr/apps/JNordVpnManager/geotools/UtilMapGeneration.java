/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.ChannelSelection;
import org.geotools.api.style.ContrastEnhancement;
import org.geotools.api.style.ContrastMethod;
import org.geotools.api.style.RasterSymbolizer;
import org.geotools.api.style.SelectedChannelType;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.StyleLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapFrame.Tool;
import org.geotools.swing.JMapPane;
import org.geotools.swing.tool.InfoToolResult;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.UtilZip;

/**
 * This class reads data for point locations and associated attributes from a comma separated text (CSV) file and adds
 * them as an existing shapefile. It illustrates how to build a feature type.
 */
public class UtilMapGeneration
{
   // Map
   private static final String         MAP_ARCHIVE             = "resources/ne_50m_admin_0_countries.zip";
   private static final String         MAP_NE1                 = "resources/NE1_50M_SR_W.zip";
   private static final String         MAP_HYP                 = "resources/HYP_50M_SR_W.zip";
   
   private static final String         MAP_NAME                = "ne_50m_admin_0_countries.shp";

   private static String               m_tmpMapDirectory       = null;
   private static JMapPane             m_mapPane               = null;
   private static MapContent           m_map                   = null;
   private static Layer                m_baseWorldMapLayer     = null;
   private static Layer                m_imageMapLayer         = null;
   private static UpdatableLayer       m_currentServerMapLayer = null;
   private static UpdatableLayer       m_vpnServerMapLayer     = null;
   private static ReferencedEnvelope   m_fullMapEnvelope       = null;
   private static ReferencedEnvelope   m_serverMapEnvelope     = null;

   private static GridCoverage2DReader m_2Dreader              = null;
   private static StyleFactory         m_styleFactory          = CommonFactoryFinder.getStyleFactory();
   private static FilterFactory        m_filterFactory         = CommonFactoryFinder.getFilterFactory();

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

         unzipWorldData();

         m_map = new MapContent();
         m_map.setTitle("JNordVPN Manager");

         // ...add our shape file to it..
         Starter._m_logError.TraceDebug("Add World Map Layer...");

         m_baseWorldMapLayer = createWorldMapLayer();
         if (null != m_baseWorldMapLayer) m_map.addLayer(m_baseWorldMapLayer);

         // Set the background image
         String worldmapImageId = UtilPrefs.getWorldmapImage();
         String displayMode = UtilPrefs.getWorldmapImageDisplayMode();
         changeCurrentWorldmapImageLayer(worldmapImageId, displayMode);

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
         Starter._m_logError.TraceDebug("Map zoom Out...");
         m_mapPane.getMapContent().getViewport().setBounds(envelope);
         // force refresh!
         mapRefresh();
         return;
      }
      Starter._m_logError.TraceDebug("Map zoom Out - envelope=null!");

   }

   public static void zoomIn(Location loc)
   {
      if (null != loc)
      {
         Starter._m_logError.TraceDebug("Map zoom In...");
         ReferencedEnvelope envelope = new ReferencedEnvelope(loc.getLongitude()-10, loc.getLongitude()+10, loc.getLatitude()-10,loc.getLatitude()+10, DefaultGeographicCRS.WGS84);      
         m_mapPane.getMapContent().getViewport().setBounds(envelope);
         // force refresh!
         mapRefresh();
         return;
      }
      Starter._m_logError.TraceDebug("Map zoom In - location=null!");

   }

   public static void zoomServerLayer()
   {
      zoomOut(m_serverMapEnvelope);
   }

   public static void mapRefresh()
   {
      if (null != m_mapPane)
      {
         // force refresh!
         m_mapPane.moveImage(1,1);
         m_mapPane.moveImage(-1,-1);
         m_mapPane.repaint();
      }
   }

   public static void changeCurrentServerMapLayer(Location loc)
   {
      if (null != m_currentServerMapLayer)
      {
         if (null != loc && m_currentServerMapLayer.getTitle().equals(loc.getServerId())) return; // already current
         m_map.removeLayer(m_currentServerMapLayer);
         m_currentServerMapLayer = null;
      }

      if (null != loc)
      {
         Starter._m_logError.TraceDebug("Changed VPN Server on map: " + loc.getServerId());
         m_currentServerMapLayer = createCurrentServerMapLayer(loc);
         m_map.addLayer(m_currentServerMapLayer);
         m_currentServerMapLayer.updated();
         zoomIn(loc);
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
      layer.setTitle(loc.getServerId());
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
         mapRefresh();
         Starter._m_logError.TraceDebug("Updated VPN Server locations layer.");
         if (null != m_currentServerMapLayer) m_currentServerMapLayer.setTitle(m_currentServerMapLayer.getTitle() + "force Update");
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
      Style style = SLD.createPointStyle("Circle", Color.BLUE, Color.CYAN, 0.7f, 10);
      UpdatableLayer layer = new UpdatableLayer(DataUtilities.collection(locationFeatures), style);
   
      return layer;
   }

   /**
    * Supply world map data
    */
   private static void unzipWorldData()
   {
      // ...work with a temporary copy (original map may be overwritten with additional feature data!)
      try
      {
         m_tmpMapDirectory = UtilZip.unzipTmp(Starter.class.getResourceAsStream(MAP_ARCHIVE), "NordVPNmap");
         UtilZip.unzipSplitResourceAsStream(Starter.class, MAP_NE1, m_tmpMapDirectory);
         UtilZip.unzipSplitResourceAsStream(Starter.class, MAP_HYP, m_tmpMapDirectory);
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionMessage(3, 10500, e);
      }
   }

   /**
    * Create the world map layer
    * <p>
    * The world map consists of many files (stored in a ZIP archive), which is unpacked in a temporary folder
    */
   private static Layer createWorldMapLayer()
   {
      Layer layer = null;

      try
      {
         File fpMapFileName = new File(m_tmpMapDirectory, MAP_NAME);
         
         // get map file data store
         FileDataStore   store = FileDataStoreFinder.getDataStore(fpMapFileName);
         SimpleFeatureSource   featureSource = store.getFeatureSource();

         // create the world map layer
//         Style style = SLD.createSimpleStyle(featureSource.getSchema());
         Style style = SLD.createPolygonStyle(Color.BLACK, null, 0.0f);
         layer = new FeatureLayer(featureSource, style);
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionMessage(3, 10500, e);
      }
 
      return layer;
   }

   /**
    * Change (first call create) the Worldmap Image layer
    */
   public static void changeCurrentWorldmapImageLayer(String worldmapImageId, String displayMode)
   {
      String layerTitle = worldmapImageId + "_" + displayMode;

      if (null != m_imageMapLayer)
      {
         if (m_imageMapLayer.getTitle().equals(layerTitle)) return; // already current
         m_map.removeLayer(m_imageMapLayer);
         m_imageMapLayer = null;
      }

      if (!displayMode.equals("OFF"))
      {
//         Starter._m_logError.TraceDebug("Changed Worldmap Image: " + layerTitle);
         m_imageMapLayer = createWorldmapImageLayer(worldmapImageId, displayMode);
         if (null != m_imageMapLayer)
         {
            m_imageMapLayer.setTitle(layerTitle);
            m_map.addLayer(m_imageMapLayer);
            List<Layer> layers = m_map.layers();
            int nbLayers = layers.size();
            m_map.moveLayer(nbLayers-1,0);
         }
      }

      mapRefresh();
   }

   /**
    * Create Worldmap image
    * 
    * @param imageId
    *           is the image Id
    * @param displayMode
    *           is the display mode OFF, RGB or GREY
    * @return the created image layer or null in case of error
    */
   private static Layer createWorldmapImageLayer(String imageId, String displayMode)
   {
      if (displayMode.equals("OFF")) return null;

      String worldmapImage = imageId + "_50M_SR_W.tif";
      File fpImageFileName = new File(m_tmpMapDirectory, worldmapImage);
//      Starter._m_logError.TraceDebug("Create Worldmap Image: " + fpImageFileName.getAbsolutePath());

      try
      {
         Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
         GeoTiffFormat format = new GeoTiffFormat();
         m_2Dreader = format.getReader(fpImageFileName, hints);
      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10901, e);
         return null;
      }

      Style rasterStyle = null;
      if (displayMode.equals("RGB"))
      {
         rasterStyle = createRGBStyle();
      }
      else if (displayMode.equals("GREY"))
      {
         rasterStyle = createGreyscaleStyle(1);
      }
      else
      {
         // Internal error - this should not happen!
         Starter._m_logError.TraceErr("(UtilPrefs:setUserPreferencesDataSet) Unknown WorldMap Display Mode: " + displayMode);
         return null;
      }
      
      return new GridReaderLayer(m_2Dreader, rasterStyle);
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
            Starter._m_logError.LoggingInfo("VPN Server location selected: " + info.toString());
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

   /**
    * This method examines the names of the sample dimensions in the provided coverage looking for "red...", "green..."
    * and "blue..." (case insensitive match). If these names are not found it uses bands 1, 2, and 3 for the red, green
    * and blue channels. It then sets up a raster symbolizer and returns this wrapped in a Style.
    *
    * @return a new Style object containing a raster symbolizer set up for RGB image
    */
   public static Style createRGBStyle()
   {
      GridCoverage2D cov = null;
      try
      {
         cov = m_2Dreader.read(null);
      }
      catch (IOException giveUp)
      {
         throw new RuntimeException(giveUp);
      }
      // We need at least three bands to create an RGB style
      int numBands = cov.getNumSampleDimensions();
      if (numBands < 3)
      {
         return null;
      }
      // Get the names of the bands
      String[] sampleDimensionNames = new String[numBands];
      for (int i = 0; i < numBands; i++)
      {
         GridSampleDimension dim = cov.getSampleDimension(i);
         sampleDimensionNames[i] = dim.getDescription().toString();
      }
      final int RED = 0, GREEN = 1, BLUE = 2;
      int[] channelNum = {
            -1, -1, -1
      };
      // We examine the band names looking for "red...", "green...", "blue...".
      // Note that the channel numbers we record are indexed from 1, not 0.
      for (int i = 0; i < numBands; i++)
      {
         String name = sampleDimensionNames[i].toLowerCase();
         if (name != null)
         {
            if (name.matches("red.*"))
            {
               channelNum[RED] = i + 1;
            }
            else if (name.matches("green.*"))
            {
               channelNum[GREEN] = i + 1;
            }
            else if (name.matches("blue.*"))
            {
               channelNum[BLUE] = i + 1;
            }
         }
      }
      // If we didn't find named bands "red...", "green...", "blue..."
      // we fall back to using the first three bands in order
      if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0)
      {
         channelNum[RED] = 1;
         channelNum[GREEN] = 2;
         channelNum[BLUE] = 3;
      }
      // Now we create a RasterSymbolizer using the selected channels
      SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
      ContrastEnhancement ce = m_styleFactory.contrastEnhancement(m_filterFactory.literal(1.0), ContrastMethod.NORMALIZE);
      for (int i = 0; i < 3; i++)
      {
         sct[i] = m_styleFactory.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
      }
      RasterSymbolizer sym = m_styleFactory.getDefaultRasterSymbolizer();
      ChannelSelection sel = m_styleFactory.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
      sym.setChannelSelection(sel);

      return SLD.wrapSymbolizers(sym);
   }

   /**
    * Create a Style to display a selected band of the GeoTIFF image as a greyscale layer
    *
    * @return a new Style instance to render the image in greyscale
    */
   @SuppressWarnings("unused")
   private static Style createGreyscaleStyle()
   {
      GridCoverage2D cov = null;
      try
      {
         cov = m_2Dreader.read(null);
      }
      catch (IOException giveUp)
      {
         throw new RuntimeException(giveUp);
      }
      int numBands = cov.getNumSampleDimensions();
      Integer[] bandNumbers = new Integer[numBands];
      for (int i = 0; i < numBands; i++)
      {
         bandNumbers[i] = i + 1;
      }
      Object selection = JOptionPane.showInputDialog(
            Starter.getMainFrame(), // TODO
            "Band to use for greyscale display",
            "Select an image band",
            JOptionPane.QUESTION_MESSAGE,
            null,
            bandNumbers,
            1);
      if (selection != null)
      {
         int band = ((Number) selection).intValue();
         return createGreyscaleStyle(band);
      }
      return null;
   }

   /**
    * Create a Style to display the specified band of the GeoTIFF image as a greyscale layer.
    *
    * <p>
    * This method is a helper for createGreyScale() and is also called directly by the displayLayers() method when the
    * application first starts.
    *
    * @param band
    *           the image band to use for the greyscale display
    * @return a new Style instance to render the image in greyscale
    */
   private static Style createGreyscaleStyle(int band)
   {
      ContrastEnhancement ce = m_styleFactory.contrastEnhancement(m_filterFactory.literal(1.0), ContrastMethod.NORMALIZE);
      SelectedChannelType sct = m_styleFactory.createSelectedChannelType(String.valueOf(band), ce);

      RasterSymbolizer sym = m_styleFactory.getDefaultRasterSymbolizer();
      ChannelSelection sel = m_styleFactory.channelSelection(sct);
      sym.setChannelSelection(sel);

      return SLD.wrapSymbolizers(sym);
   }

   /**
    * Interface to set the world map image Greyscale
    */
   public static void setGreyscale(int band)
   {
      if (null == m_imageMapLayer) return;

      Style style = createGreyscaleStyle(band);
      if (style != null)
      {
         ((StyleLayer) m_map.layers().get(0)).setStyle(style);
         m_mapPane.repaint();
      }

   }

   /**
    * Interface to set the world map image RGB
    */
   public static void setRgb()
   {
      if (null == m_imageMapLayer) return;

      Style style = createRGBStyle();
      if (style != null)
      {
          ((StyleLayer) m_map.layers().get(0)).setStyle(style);
          m_mapPane.repaint();
      }
   }
}
