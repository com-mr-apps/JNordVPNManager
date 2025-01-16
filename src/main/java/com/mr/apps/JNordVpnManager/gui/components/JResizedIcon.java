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
      ICON_STATUS_CONNECTED       ("connected_48.png"),
      ICON_STATUS_PAUSED          ("paused_48.png"),
      ICON_STATUS_DISCONNECTED    ("disconnected_48.png"),
      ICON_MOUSE_POINTER_LOCATION ("mpLocation.png"),
      ICON_MAP_ZOOM_CURRENT       ("zoom_current_location_32.png"),
      ICON_MAP_ZOOM_ALL           ("zoom_all_tree_locations_32.png"),
      ICON_MAP_CONNECT            ("connectMap_32.png"),
      ICON_SERVER_SEARCH_FILTER   ("search_in_tree_32.png"),
      ICON_TIMER_DISCONNECT       ("disconnectTimer_32.png"),
      ICON_TIMER_CONNECT          ("connectTimer_32.png"),
      ICON_TREE_LOCATION          ("mpLocation.png"),
      ICON_WINDOW_COLLAPSE        ("window_collapse_32.png"),
      ICON_WINDOW_EXPAND          ("window_expand_32.png");
    
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
      LARGE             (36);
    
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

}
