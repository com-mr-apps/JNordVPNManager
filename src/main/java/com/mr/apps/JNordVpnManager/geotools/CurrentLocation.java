/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;

/**
 * Class for the current location<p>
 * Extends class Location by connected status attribute.
 */
public class CurrentLocation extends Location
{
   // Status Connected/Disconnected
   private boolean isConnected;

   // Group selection in server tree filter panel
   private Integer m_filterGroup;

   // Technology selection in NordVPN Settings (OPENVPN/NORDLYNX)
   private String m_vpnTechnology;

   // Protocol selection in NordVPN Settings (TCP/UDP)
   private String m_vpnProtocol;

   public CurrentLocation()
   {
      super();
      setConnected(false);
   }

   public CurrentLocation(Location loc)
   {
      this.m_serverId = loc.m_serverId;
      this.m_cityName = loc.m_cityName;
      this.m_countryName = loc.m_countryName;
      this.m_longitude = loc.m_longitude;
      this.m_latitude = loc.m_latitude;
      this.m_cityId = loc.m_cityId;
      this.m_groups = loc.m_groups;
      this.m_technologies = loc.m_technologies;

      setConnected(false);
      setFilterGroup(null); // set to null means return the current setting
      setVpnTechnology(null);
      setVpnProtocol(null);
   }

   public boolean isConnected()
   {
      return isConnected;
   }

   public void setConnected(boolean isConnected)
   {
      this.isConnected = isConnected;
      // set the connection specific attributes
      this.m_filterGroup = NvpnGroups.getCurrentGroup().getId();
      this.m_vpnTechnology = Starter.getCurrentSettingsData().getTechnology(false);
      this.m_vpnProtocol = Starter.getCurrentSettingsData().getProtocol(false);
   }

   public int getFilterGroup()
   {
      return (null == m_filterGroup) ? NvpnGroups.getCurrentGroup().getId() : m_filterGroup;
   }

   public void setFilterGroup(Integer filterGroup)
   {
      this.m_filterGroup = filterGroup;
   }

   public String getVpnTechnology()
   {
      return (null == m_vpnTechnology) ? Starter.getCurrentSettingsData().getTechnology(false) : m_vpnTechnology;
   }

   public void setVpnTechnology(String vpnTechnology)
   {
      this.m_vpnTechnology = vpnTechnology;
   }

   public String getVpnProtocol()
   {
      return (null == m_vpnProtocol) ? Starter.getCurrentSettingsData().getProtocol(false) : m_vpnProtocol;
   }

   public void setVpnProtocol(String vpnProtocol)
   {
      this.m_vpnProtocol = vpnProtocol;
   }

   public String getToolTip()
   {
       return m_serverId + " (" + NordVPNEnumGroups.get(getFilterGroup()) + ") [" + getVpnTechnology() + "/" + getVpnProtocol() + "]";
   }

   public boolean isEqualConnection (CurrentLocation loc)
   {
      if (false == this.m_serverId.equals(loc.m_serverId)) return false;
      if (this.getFilterGroup() != (loc.getFilterGroup())) return false;
      if (false == this.getVpnTechnology().equals(loc.getVpnTechnology())) return false;
      if (false == this.getVpnProtocol().equals(loc.getVpnProtocol())) return false;
      return true;
   }

   public String toString()
   {
       return super.toString() + " " + ((isConnected == true) ? "connected" : "disconnected") + "; Group=" + getFilterGroup() + ", Technology=" + getVpnTechnology() + "/" + getVpnProtocol();
   }
}
