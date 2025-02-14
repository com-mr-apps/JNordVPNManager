/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.components;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import com.mr.apps.JNordVpnManager.Starter;

@SuppressWarnings("serial")
public class JResizedIcon extends ImageIcon
{
   // definitions for resource icons
   public static enum IconUrls
   {
      ICON_MOUSE_POINTER_MAP_LOCATION  ("mp_MapSelectLocation.png"),
      ICON_CUSTOMIZE_ADD_COMMAND_BAR   ("i_CustomizeAddCommandBar.png"),
      ICON_CUSTOMIZE_DEL_COMMAND_BAR   ("i_CustomizeDelCommandBar.png"),
      ICON_STATUS_CONNECTED            ("i_StatusOk.png"),
      ICON_STATUS_WARNING              ("i_StatusWarning.png"),
      ICON_STATUS_DISCONNECTED         ("i_StatusKo.png"),
      ICON_STATUS_PAUSED               ("i_StatusPause.png"),
      ICON_STATUS_RECONNECT            ("i_StatusTimerReconnect.png"),
      ICON_LOGGED_IN_TO_NORDVPN        ("i_LoggedInToNordVPN.png"),
      ICON_LOGGED_OUT_FROM_NORDVPN     ("i_LoggedOutFromNordVPN.png"),
      ICON_MAP_ZOOM_CURRENT            ("i_MapZoomCurrentLocation.png"),
      ICON_MAP_ZOOM_ALL                ("i_MapZoomAllTreeLocations.png"),
      ICON_MAP_CONNECT                 ("i_MapConnectToVPN.png"),
      ICON_TIMER_PAUSE                 ("i_TimerPauseVPN.png"),
      ICON_TIMER_CONNECT               ("i_TimerConnectToVPN.png"),
      ICON_RECONNECT                   ("i_ReConnectToVPN.png"),
      ICON_SERVER_SEARCH_FILTER        ("i_SearchInTree.png"),
      ICON_TREE_LOCATION               ("i_TreeLocation.png"),
      ICON_VPN_SET_KILLSWITCH          ("i_VpnSetKillswitch.png"),
      ICON_VPN_SET_OBFUSCATE           ("i_VpnSetObfuscate.png"),
      ICON_WINDOW_COLLAPSE             ("i_WindowCollapse.png"),
      ICON_WINDOW_EXPAND               ("i_WindowExpand.png"),
      ICON_CMD_AUTOCONNECT_ON_START    ("i_AutoConnectOnProgramStart.png"),
      ICON_CMD_AUTODISCONNECT_ON_EXIT  ("i_AutoDisconnectOnProgramExit.png"),
      ICON_SETTINGS_RESET              ("i_SettingsReset.png");
    
       private String iconName;
    
       IconUrls(String values) {
           this.iconName = values;
       }
    
       public String getIcon() {
           return iconName;
       }

       public URL getIconUrl() {
           return Starter.class.getResource("resources/icons/" + iconName);
       }
   }

   // definitions for possible icon sizes
   public static enum IconSize
   {
      SMALL             (12),
      MEDIUM            (24),
      LARGE             (36),
      LOGO              (80);
    
       private int size;
    
       IconSize(int values) {
           this.size = values;
       }
    
       public int getSize() {
           return size;
       }  
   }

   private static ImageIcon m_this = null;

   public JResizedIcon(Image resizedImage)
   {
      super(resizedImage);
      m_this = this;
   }

   /**
    * Constructor to generate a resized ImageIcon from an icon resource name.
    * <p>
    * Icon resources are located in the <code>resources/</code> and should be of type <code>png</code>
    * 
    * @param iconName
    *           is the name of the icon resource
    * @param width
    *           is the width of the generated ImageIcon
    * @param height
    *           is the height of the generated ImageIcon
    */
   private JResizedIcon(String iconName, int width, int height) throws NullPointerException
   {
      this(Starter.class.getResource("resources/" + iconName), width, height);
   }

   /**
    * Constructor to generate a resized ImageIcon from an icon resource name.
    * <p>
    * Icon resources are located in the <code>resources</code> and should be of type <code>png</code>
    * 
    * @param iconName
    *           is the name of the icon resource
    * @param size
    *           is the size [SMALL|MEDIUM|LARGE] of the icon
    */
   private JResizedIcon(String iconName, IconSize size) throws NullPointerException
   {
      this(Starter.class.getResource("resources/" + iconName), size.getSize(), size.getSize());
   }

   /**
    * Constructor to generate a resized ImageIcon from an icon resource URL.
    * <p>
    * Icon resources should be of type <code>png</code>
    * 
    * @param urlIcon
    *           is the URL of the icon resource
    * @param width
    *           is the width of the generated ImageIcon
    * @param height
    *           is the height of the generated ImageIcon
    */
   private JResizedIcon(URL urlIcon, int width, int height)
   {
      super(urlIcon);
      m_this = this;
      if ((this.getIconWidth() != width) && (this.getIconHeight() != height))
      {
         Image myImage = this.getImage();
         Image resizedImage = myImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
         new JResizedIcon(resizedImage);
      }
   }

   /**
    * Wrapper to get a predefined resized ImageIcon by its URL.
    * 
    * @param urlIcon
    *           is the URL of the icon resource
    * @param size
    *           is the size [SMALL|MEDIUM|LARGE] of the icon
    * @return the resized ImageIcon
    */
   public static ImageIcon getIcon (URL urlIcon, IconSize size)
   {
      if (null != size)
      {
         new JResizedIcon(urlIcon, size.getSize(), size.getSize());
         return m_this;
      }
      else
      {
         return new ImageIcon(urlIcon);
      }
   }

   /**
    * Wrapper to get a customized resized ImageIcon by its name.
    * 
    * @param iconName
    *           is the name of the icon resource
    * @param width
    *           is the width of the generated ImageIcon
    * @param height
    *           is the height of the generated ImageIcon
    * @return the resized ImageIcon
    */
   public static ImageIcon getIcon (String iconName, int width, int height)
   {
      new JResizedIcon(iconName, width, height);
      return m_this;
   }

   /**
    * Wrapper to get a customized resized ImageIcon by its name.
    * 
    * @param iconName
    *           is the name of the icon resource
    * @param size
    *           is the size [SMALL|MEDIUM|LARGE] of the icon
    * @return the resized ImageIcon
    */
   public static ImageIcon getIcon (String iconName, IconSize size)
   {
      new JResizedIcon(iconName, size);
      return m_this;
   }

   /**
    * Wrapper to get a customized resized ImageIcon by its URL.
    * 
    * @param urlIcon
    *           is the URL of the icon resource
    * @param width
    *           is the width of the generated ImageIcon
    * @param height
    *           is the height of the generated ImageIcon
    * @return the resized ImageIcon
    */
   public static ImageIcon getIcon (URL urlIcon, int width, int height)
   {
      new JResizedIcon(urlIcon, width, height);
      return m_this;
   }

   /**
    * Wrapper to get a predefined resized ImageIcon by its URL.
    * 
    * @param urlIcon
    *           is the URL of the icon resource
    * @param size
    *           is the size [SMALL|MEDIUM|LARGE] of the icon
    * @return the resized ImageIcon
    */
   public static ImageIcon getIcon (IconUrls iconUrl, IconSize size)
   {
      return getIcon(iconUrl.getIconUrl(), size);
   }

   /**
    * Wrapper to get a predefined resized ImageIcon by its URL.
    * 
    * @param urlIcon
    *           is the URL of the icon resource
    * @param width
    *           is the width of the generated ImageIcon
    * @param height
    *           is the height of the generated ImageIcon
    * @return the resized ImageIcon
    */
   public static ImageIcon getIcon (IconUrls iconUrl, int width, int height)
   {
      return getIcon(iconUrl.getIconUrl(), width, height);
   }

}
