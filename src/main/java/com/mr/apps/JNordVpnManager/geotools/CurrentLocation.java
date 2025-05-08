/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;

/**
 * Class for the current location<p>
 * Extends class Location by connected status attributes. Data to establish a connection are:<br> 
 * hostName, cityName, countryName, Legacy Group, Technology, Protocol.<p>
 * This class is used for the nordvpn connection command which differs two types:
 * <ul>
 * <li>Static Connection type (recent list, autostart, reconnect) - Filter Group, Technology, Protocol set in Location object.</li>
 * <li>Dynamic connection type (Tree/Map selection) - Filter Group, Technology, Protocol [== null in Location object] taken from current GUI and VPN settings</li>.
 * </ul>
 * 
 */
public class CurrentLocation extends Location
{
   // Status Connected/Disconnected
   private boolean isConnected;

   // Group selection in server tree filter panel
   private Integer m_legacyGroup;

   // Technology selection in NordVPN Settings (OPENVPN/NORDLYNX)
   private String m_vpnTechnology;

   // Protocol selection in NordVPN Settings (TCP/UDP)
   private String m_vpnProtocol;

   /**
    * Constructor for Current Location
    */
   public CurrentLocation()
   {
      super();
      setConnected(false);
   }

   /**
    * Constructor for Current Location
    * 
    * @param loc
    *           is the location data
    */
   public CurrentLocation(Location loc)
   {
      super(loc.m_cityName, loc.m_countryName, loc.m_longitude, loc.m_latitude, loc.m_cityId);
      this.m_vpnServer = loc.m_vpnServer;
      this.m_countryCode = loc.m_countryCode;
      this.m_countryId = loc.m_countryId;
      this.m_vpnServer.m_groups = loc.m_vpnServer.m_groups;
      this.m_vpnServer.m_technologies = loc.m_vpnServer.m_technologies;

      setConnected(false);
      setLegacyGroup(null); // set to null means return the current GUI setting
      setVpnTechnology(null); // set to null means return the current VPN setting
      setVpnProtocol(null); // set to null means return the current VPN setting
   }

   /**
    * Constructor for Current Location with a specific VPN Server
    * 
    * @param loc
    *           is the location data
    * @param vpnServer
    *           is the specific vpnServer data
    */
   public CurrentLocation(Location loc, VpnServer vpnServer)
   {
      super(loc.m_cityName, loc.m_countryName, loc.m_longitude, loc.m_latitude, loc.m_cityId);
      this.m_countryCode = loc.m_countryCode;
      this.m_countryId = loc.m_countryId;
      if (null != vpnServer)
      {
         this.m_vpnServer = vpnServer;
         this.m_vpnServer.m_groups = vpnServer.m_groups;
         this.m_vpnServer.m_technologies = vpnServer.m_technologies;
      }

      setConnected(false);
      setLegacyGroup(null); // set to null means return the current GUI setting
      setVpnTechnology(null); // set to null means return the current VPN setting
      setVpnProtocol(null); // set to null means return the current VPN setting
   }

   /**
    * Get the connection status
    * @return true if connected, else (disconnected) false
    */
   public boolean isConnected()
   {
      return isConnected;
   }

   /**
    * Set the current connection status<p>
    * In case of connected, set (if not already set) the connection information of group, technology and protocol.
    * @param isConnected is true for connected, else false
    */
   public void setConnected(boolean isConnected)
   {
      this.isConnected = isConnected;
      if (true == isConnected)
      {
         // set the connection specific attributes
         NordVPNEnumGroups legacyGroup = NvpnGroups.getCurrentLegacyGroup();
         if (null != legacyGroup) this.m_legacyGroup = NvpnGroups.getCurrentLegacyGroup().getId();
         this.m_vpnTechnology = this.getVpnTechnology();
         this.m_vpnProtocol = this.getVpnProtocol();
      }
   }

   /**
    * Get Server Legacy Group for server connection
    * @return the legacy group of the location (for connection) - in case of null, return the current filter group
    */
   public int getLegacyGroup()
   {
      return (null == m_legacyGroup) ? NvpnGroups.getCurrentFilterGroup().getId() : m_legacyGroup;
   }

   /**
    * Set Legacy Group for server connection
    * 
    * @param legacyGroup
    *           is the legacy (server tree filter) group
    */
   public void setLegacyGroup(Integer legacyGroup)
   {
      this.m_legacyGroup = legacyGroup;
   }

   /**
    * Get VPN Technology for server connection
    * @return the VPN Technology of the location (for connection) - in case of null, return the current nordvpn settings data
    */
   public String getVpnTechnology()
   {
      return (null == m_vpnTechnology) ? Starter.getCurrentSettingsData().getTechnology(false) : m_vpnTechnology;
   }

   /**
    * Set VPN Technology for server connection
    * 
    * @param vpnTechnology
    *           is the VPN Technology (NORDLYNX, NORDWHISPER or OPENVPN)
    */
   public void setVpnTechnology(String vpnTechnology)
   {
      this.m_vpnTechnology = vpnTechnology;
   }

   /**
    * Get VPN Protocol for server connection
    * @return the VPN Protocol of the location (for connection) - in case of null, return the current nordvpn settings data
    */
   public String getVpnProtocol()
   {
      return (null == m_vpnProtocol) ? Starter.getCurrentSettingsData().getProtocol(false) : m_vpnProtocol;
   }

   /**
    * Set VPN Protocol for server connection
    * 
    * @param vpnProtocol
    *           is the VPN Protocol (TCP or UDP)
    */
   public void setVpnProtocol(String vpnProtocol)
   {
      this.m_vpnProtocol = vpnProtocol;
   }

   /**
    * Check for static location.
    * @return <code>true</code> if the location is static, <code>false</code> if the location is dynamic 
    */
   public boolean isStatic()
   {
      return !(null == m_legacyGroup && null == m_vpnTechnology && null == m_vpnProtocol);
   }

   /**
    * Get Location Connection Data<p>
    * Generates a string array with required data to establish a connection in form:<br>
    * [0] cityName<br>
    * [1] countryName[#host],Group,Technology,Protocol<br>
    * Used e.g. to store information for recent servers list.
    * @return the connection data 
    */
   public String[] getLocationConnectionData()
   {
      String[] rcs = new String[2];
      int legacyGroup = (null == m_legacyGroup) ? NordVPNEnumGroups.legacy_group_unknown.getId() : m_legacyGroup;
      rcs[0] = m_cityName;
      rcs[1] = m_countryName 
            + ((null != getVpnHostName()) ? ("#" + getVpnHostName()) : "") // add optional host
            + "," + legacyGroup + (",") + this.getVpnTechnology() + (",") + this.getVpnProtocol();
      return rcs;
   }

   /**
    * Generate a ToolTip
    * @return the ToolTip string
    */
   public String getToolTip()
   {
      return getServerName() + " (" + NordVPNEnumGroups.get(getLegacyGroup()) + ") [" + getVpnTechnology() + "/" + getVpnProtocol() + "]";
   }

   /**
    * Check, if Locations have the same connection data
    * 
    * @param loc
    *           is another connection location
    * @return true, if the connection data is equal
    */
   public boolean isEqualLocation(CurrentLocation loc)
   {
      if (null == loc) return false;
      if (false == this.getServerKey().equals(loc.getServerKey())) return false; // Location level
      if (this.getLegacyGroup() != (loc.getLegacyGroup())) return false;
      if (false == this.getVpnTechnology().equals(loc.getVpnTechnology())) return false;
      if (false == this.getVpnProtocol().equals(loc.getVpnProtocol())) return false;
      return true;
   }

   /**
    * Check, if servers have the same connection data
    * 
    * @param loc
    *           is another connection location
    * @return true, if the connection data is equal
    */
   public boolean isEqualConnection(CurrentLocation loc)
   {
      if (null == loc) return false;
      if (false == this.getServerNordVPN().equals(loc.getServerNordVPN())) return false; // Server Host level
      return isEqualLocation(loc);
   }

   public String toString()
   {
       return super.toString() + " " + ((isConnected == true) ? "connected" : "disconnected") + "; Group=" + NordVPNEnumGroups.get(getLegacyGroup()) + " (" + getLegacyGroup() + "), Technology=" + getVpnTechnology() + "/" + getVpnProtocol();
   }
}
