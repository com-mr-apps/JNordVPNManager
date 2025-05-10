/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnTechnologies;

/**
 * Store common NordVPN Server data
 * <p>
 * Data linked to the Location object. To manage additional data for multiple hosts at one location (city).<p>
 * Used/created in two cases:
 * <ul>
 * <li>Location based server - connection "host" is based on Location Object city/country (serverName/serverHostName are null)</li>
 * <li>City list based server - connection "host" is based on this VpnServer Object serverHostName</li>
 * </ul>
 */
public class VpnServer
{
   private String             m_serverKey; // unique server HashMap key, generated as lower case "city@country" with "_" replaced by " "
   private String             m_serverName; // "Switzerland #218" or null
   private String             m_serverHostName; // "ch218[.nordvpn.com]" or null
   protected boolean          m_isVirtualLocation;
   protected NvpnTechnologies m_technologies;
   protected NvpnGroups       m_groups;

   private static Pattern     _m_hostPattern = Pattern.compile("[^\\d]+(\\d+).*", Pattern.CASE_INSENSITIVE);

   /**
    * Create VpnServer data object
    * 
    * @param serverKey
    *           is the server HashMap key - Link to the Location object
    * @param serverName
    *           is the server Label Name - in case of VPN Server "Host" object
    * @param serverHostName
    *           is the server connection Host Name - in case of VPN Server "Host" object
    */
   public VpnServer(String serverKey, String serverName, String serverHostName)
   {
      setServerKey(serverKey);
      setServerName(serverName);
      setServerHostName(serverHostName);

      setVirtualLocation(false);
      m_technologies = new NvpnTechnologies();
      m_groups = new NvpnGroups();
    }

   /**
    * Get the unique Location Object HashMap server key
    * @return is the Location server HashMap key
    */
   public String getServerKey()
   {
      return m_serverKey;
   }

   /**
    * Set the unique Location Object HashMap server key
    * 
    * @param serverKey
    *           is the server key (will be mangled to lower case and '_' replaced by ' ')
    */
   public void setServerKey(String serverKey)
   {
      m_serverKey = serverKey.toLowerCase().replace('_', ' ');
   }

   /**
    * Get the Server name - in case of host connection - required for the NordVPN connect command
    * @return the server (host) for VPN connection
    */
   public String getServer()
   {
      return (null == m_serverHostName) ? null : m_serverHostName.replace(".nordvpn.com", ""); // for nordvpn connect command
   }

   /**
    * Check if the server is a virtual server
    * @return <CODE>true</CODE>, in case of virtual server, else <CODE>false</CODE>
    */
   public boolean isVirtualLocation()
   {
      return m_isVirtualLocation;
   }

   /**
    * Set the virtual server status
    * 
    * @param isVirtualLocation
    *           <CODE>true</CODE>, in case of virtual server, else <CODE>false</CODE>
    */
   public void setVirtualLocation(boolean isVirtualLocation)
   {
      this.m_isVirtualLocation = isVirtualLocation;
   }

   /**
    * Get the server label name
    * @return the server name from the original source
    */
   public String getServerName()
   {
      return m_serverName;
   }

   /**
    * Set the server label name
    * 
    * @param serverName
    *           is the server name (starting with "#" its the host name from recent server objects)
    */
   public void setServerName(String serverName)
   {
      if ((null != serverName) && (serverName.startsWith(Location.SERVERID_HOST_SEPARATOR)))
      {
         // Mangle host name in form "#fr123.nordvpn.com" to label name "Country + # + nb"
         Matcher matcher = _m_hostPattern.matcher(serverName);
         boolean matchFound = matcher.find();
         if (matchFound)
         {
            String[] sa = Location.splitServerKey(m_serverKey);
            this.m_serverName = sa[1].substring(0, 1).toUpperCase() + sa[1].substring(1).toLowerCase() + " " + Location.SERVERID_HOST_SEPARATOR + matcher.group(1);
         }
         else
         {
            this.m_serverName = serverName;
         }
      }
      else
      {
         this.m_serverName = serverName;
      }
   }

   /**
    * Get the server host name (for connect)
    * @return the server host name from the original source
    */
   public String getServerHostName()
   {
      return m_serverHostName;
   }

   /**
    * Set the server host name (for connect)
    * 
    * @param serverHostName
    *           is the server host name from the original source
    */
   public void setServerHostName(String serverHostName)
   {
      this.m_serverHostName = serverHostName;
   }

   /**
    * Add a technology Id to the VPN Server
    * 
    * @param id
    *           is the technology id to add
    */
   public void addTechnology(int id)
   {
      m_technologies.addTechnology(id);
   }

   /**
    * Check, if the VPN server supports this technology
    * 
    * @param id
    *           is the technology Id to check
    * @return <CODE>true</CODE>, if the server supports the technology, else <CODE>false</CODE>
    */
   public boolean hasTechnology(int id)
   {
      return m_technologies.hasTechnology(id);
   }

   /**
    * Add a group Id to the VPN Server
    * 
    * @param id
    *           is the group id to add
    */
   public void addGroup(NordVPNEnumGroups id)
   {
      m_groups.addGroup(id);
   }

   /**
    * Add a group Id to the VPN Server
    * 
    * @param id
    *           is the group id to add
    */
   public void addGroup(int id)
   {
      m_groups.addGroup(id);
   }

   /**
    * Check, if the VPN server is part of this group
    * 
    * @param id
    *           is the group Id to check
    * @return <CODE>true</CODE>, if the server is part of this group, else <CODE>false</CODE>
    */
   public boolean hasGroup(NordVPNEnumGroups m_filterGroup)
   {
      return m_groups.hasGroup(m_filterGroup);
   }

   /**
    * Create the CSV export data record for city list based VPN servers
    * @return the CSV data record or null, if called for a "Location" based server
    */
   public String exportAsCsvData()
   {
      if (null == m_serverHostName) return null;

      return m_serverKey + "," // KEY
           + m_serverName + "," // NAME
           + m_serverHostName + "," // HOST
           + m_groups.toStringId() + "," // GRP 
           + m_technologies.toStringId() + "," // TECH
           + ((isVirtualLocation()) ? "true" : "false"); // VLOC
   }

   public String toString()
   {
      return m_serverKey + 
            " / " + ((null != m_serverHostName) ? m_serverHostName : "-") + 
            " (" + ((null != m_serverName) ? m_serverName : "-") + ")" +
            " Groups=" + m_groups.toString() +
            " Technologies=" + m_technologies.toString();
   }

}
