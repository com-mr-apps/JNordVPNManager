/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.geotools.swing.JMapFrame;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilMapGeneration;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

public class GuiMapArea
{
   private JMapFrame m_mapFrame = null;
   private Cursor m_selectCursor = null;

   // cycle through Color [0] / Grey [1] / Off [2] -> variables toolTip and icon keep values for next stage
   private static String[]    m_displayMapColor = new String[] {"RGB", "GREY", "OFF"};
   private static ImageIcon[] m_iconMapColor = new ImageIcon[] {JResizedIcon.getIcon(IconUrls.ICON_GREYSCALE, IconSize.MEDIUM), JResizedIcon.getIcon(IconUrls.ICON_NOMAP, IconSize.MEDIUM), JResizedIcon.getIcon(IconUrls.ICON_COLOR, IconSize.MEDIUM)};
   private static String[]    m_toolTipMapColor = new String[] {"Click here to display the World Map in Greyscale", "Click here to display the World Map Outline", "Click here to display the World Map in Color"};
   private static int         m_indexMapColor = 0;

   public GuiMapArea()
   {
      ImageIcon myImageIcon = JResizedIcon.getIcon(IconUrls.ICON_MOUSE_POINTER_MAP_LOCATION, null);
      Image customImage = myImageIcon.getImage();
      m_selectCursor = Toolkit.getDefaultToolkit().createCustomCursor(customImage, new Point(10, 28), "mp_MapSelectLocation");
   }
   
   public JMapFrame create()
   { 
      m_mapFrame = UtilMapGeneration.createMap();
      if (null == m_mapFrame) return null;

      JToolBar toolbar = m_mapFrame.getToolBar();
      toolbar.addSeparator();

      ImageIcon imageZoomCurrent = JResizedIcon.getIcon(IconUrls.ICON_MAP_ZOOM_CURRENT, IconSize.MEDIUM);
      JButton showCurrent = new JButton(imageZoomCurrent);
      showCurrent.setToolTipText("Click here to display the current server location");
      showCurrent.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            UtilMapGeneration.zoomIn(Starter.getCurrentServer(false));
         }
      });
      toolbar.add(showCurrent);

      ImageIcon imageZoomAll = JResizedIcon.getIcon(IconUrls.ICON_MAP_ZOOM_ALL, IconSize.MEDIUM);
      JButton showServers = new JButton(imageZoomAll);
      showServers.setToolTipText("Click here to display all [filtered] server locations");
      showServers.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            UtilMapGeneration.zoomServerLayer();
         }
      });
      toolbar.add(showServers);

      ImageIcon imageConnectMap = JResizedIcon.getIcon(IconUrls.ICON_MAP_CONNECT, IconSize.MEDIUM);
      JButton pickServer = new JButton(imageConnectMap);
      pickServer.setToolTipText("Click here to select a VPN Server [city or country]. LMB-direct/RMB-confirm");
      pickServer.addActionListener(e -> m_mapFrame.getMapPane().setCursorTool(
         new CursorTool() {
            @Override
            public void onMouseClicked(MapMouseEvent e)
            {
               Point2D actPos = ((MapMouseEvent) e).getWorldPos();
               Location locSel = UtilMapGeneration.getPickedServer(actPos);
               if (null != locSel)
               {
                  CurrentLocation loc = new CurrentLocation(locSel);
                  if (e.getButton() == MouseEvent.BUTTON1)
                  {
                     if (null != loc)
                     {
                        NvpnCallbacks.executeConnect(loc, "NordVPN Connect", "NordVPN Connect");
                     }
                  }
                  else if (e.getButton() == MouseEvent.BUTTON3)
                  {
                     String sFlagName = (loc.getCountryCode().isBlank()) ? null : loc.getFlagImageFileName();
                     if (JModalDialog.showYesNoDialog("Connect to", sFlagName, loc.getToolTip()) == JOptionPane.YES_OPTION)
                     {
                        NvpnCallbacks.executeConnect(loc, "NordVPN Connect", "NordVPN Connect");
                     }
                  }
               }
               else
               {
                  // Ocean selected...
               }
            }
/*
            @Override public void onMouseMoved(MapMouseEvent event) {
               //TODO end of move detection
               Point2D actPos = ((MapMouseEvent) event).getWorldPos();
               System.out.println("Mouse movement detected! Actual mouse position is: " + event.getX()+ "," + event.getY() + ".");
             }
*/
            public Cursor getCursor()
            {
               return m_selectCursor;
            }
         }
      ));
      toolbar.add(pickServer);

      // display the Worldmap image in Greyscale/RGB
      String displayMode = UtilPrefs.getWorldmapImageDisplayMode();
      if (displayMode.equals("OFF"))
      {
         m_indexMapColor = 2;
      }
      else if (displayMode.equals("GREY"))
      {
         m_indexMapColor = 1;
      }
      else if (displayMode.equals("RGB"))
      {
         m_indexMapColor = 0;
      }
      else
      {
         // this should not happen!
         Starter._m_logError.TraceErr("(GuiMapArea:create) Invalid option for World Map Color Mode!");
      }

      JButton jbColorMap = new JButton(m_iconMapColor[m_indexMapColor]);
      jbColorMap.setToolTipText(m_toolTipMapColor[m_indexMapColor]);
      jbColorMap.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            // cycle through stages 0-1-2
            ++m_indexMapColor;
            if (3 == m_indexMapColor) m_indexMapColor = 0;
            String worldmapImageId = UtilPrefs.getWorldmapImage();
            UtilMapGeneration.changeCurrentWorldmapImageLayer(worldmapImageId, m_displayMapColor[m_indexMapColor]);
            jbColorMap.setToolTipText(m_toolTipMapColor[m_indexMapColor]);
            jbColorMap.setIcon(m_iconMapColor[m_indexMapColor]);
         }
      });
      toolbar.add(jbColorMap);

      return m_mapFrame;
   }
}
