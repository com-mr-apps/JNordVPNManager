/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.gui.connectLine.JPanelConnectTimer;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class NvpnCommands {

   private static final String CMD_WHICH            = "which";
   private static final String CMD_PING             = "ping";
   private static final String CMD_NORDVPN          = "nordvpn";
   private static final String ARG_VERSION          = "--version";
   private static final String ARG_DAEMON_VERSION   = "version";
   private static final String ARG_STATUS           = "status";
   private static final String ARG_ACCOUNT          = "account";

   private static final String ARG_DISCONNECT       = "disconnect";
   private static final String ARG_CONNECT          = "connect";
   private static final String ARG_COUNTRIES        = "countries";
   private static final String ARG_CITIES           = "cities";
   private static final String ARG_GROUPS           = "groups";

   private static final String ARG_LOGIN            = "login";
   private static final String ARG_LOGOUT           = "logout";

   private static final String ARG_SETTINGS         = "settings";
   private static final String ARG_SET              = "set";
   private static final String OPT_DEFAULTS         = "defaults";
   private static final String OPT_AUTOCONNECT      = "autoconnect";
   private static final String OPT_TPLITE           = "tplite";
   private static final String OPT_DNS              = "dns";
   private static final String OPT_FIREWALL         = "firewall";
   private static final String OPT_FWMARK           = "fwmark";
   private static final String OPT_IPV6             = "ipv6";
   private static final String OPT_ROUTING          = "routing";
   private static final String OPT_ANALYTICS        = "analytics";
   private static final String OPT_KILLSWITCH       = "killswitch";
   private static final String OPT_NOTIFY           = "notify";
   private static final String OPT_OBFUSCATE        = "obfuscate";
   private static final String OPT_TRAY             = "tray";
   private static final String OPT_TECHNOLOGY       = "technology";
   private static final String OPT_MESHNET          = "meshnet";
   private static final String OPT_LAN_DISCOVERY    = "lan-discovery";
   private static final String OPT_VIRTUAL_LOCATION = "virtual-location";
   private static final String OPT_POST_QUANTUM     = "post-quantum";
   private static final String OPT_PROTOCOL         = "protocol";

   private static final String OPT_GROUP            = "--group";

   private static final String VAL_ENABLED          = "enabled";
   private static final String VAL_DISABLED         = "disabled";

   // allowlist command
   private static final String ARG_ALLOWLIST        = "allowlist";
   private static final String OPT_ADD              = "add";
   private static final String OPT_SUBNET           = "subnet";
   private static final String OPT_PORT             = "port";
   private static final String OPT_PORTS            = "ports";
//   private static final String OPT_TCP              = "TCP";
//   private static final String OPT_UDP              = "UDP";
   private static final String OPT_REMOVE           = "remove";
   private static final String OPT_ALL              = "all";
   
   /**
    * Check, if nordvpn is installed
    * @return null, if nordvpn is not installed, else the path to the nordvpn command
    */
   public static String isInstalled()
   {
      String retVal = UtilSystem.runCommand(CMD_WHICH, CMD_NORDVPN);
      return (null == retVal || retVal.isEmpty()) ? null : retVal;
   }

   /**
    * Check, if Internet is connected
    * @return result of ping to nordvpn server
    */
   public static String isConnected()
   {
      String retVal = UtilSystem.runCommand(CMD_PING, "-c", "1", "api.nordvpn.com");
      return retVal;
   }


   /**
    * Get nordvpn status information
    * @return the current status of nordvpn
    */
   public static String getStatus()
   {
      String status = null;

      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_STATUS);
		
      return status;
   }

   /**
    * Get nordvpn current settings
    * @return the current status of nordvpn
    */
   public static String getSettings()
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SETTINGS);
      
      return status;
   }

   /**
    * Reset nordvpn settings
    * @return the return of nordvpn set defaults
    */
   public static String resetSettings()
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_DEFAULTS);
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set autoConnect value
    */
   public static String autoConnectSettings(String value)
   {
      String status = null;
      if (value.isBlank() || value.equals(NvpnSettingsData.SETTINGS_OPT_DISABLED))
      {
         status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_AUTOCONNECT, VAL_DISABLED);
      }
      else if (value.equals(NvpnSettingsData.SETTINGS_OPT_ENABLED))
      {
         status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_AUTOCONNECT, VAL_ENABLED);
      }
      else
      {
         status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_AUTOCONNECT, VAL_ENABLED, value);
      }

      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set tplite value
    */
   public static String tpliteSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_TPLITE, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set dns value
    */
   public static String dnsSettings(String value)
   {
      String status = null;
      
      if (value.isBlank() || value.equals(VAL_DISABLED))
      {
         status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_DNS, VAL_DISABLED);
      }
      else
      {
         status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_DNS, value);
      }
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set firewall value
    */
   public static String firewallSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_FIREWALL, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set fwmark value
    */
   public static String fwmarkSettings(String value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_FWMARK, value);
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set ipv6 value
    */
   public static String ipv6Settings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_IPV6, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set routing value
    */
   public static String routingSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_ROUTING, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set analytics value
    */
   public static String analyticsSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_ANALYTICS, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set killswitch value
    */
   public static String killswitchSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_KILLSWITCH, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set notify value
    */
   public static String notifySettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_NOTIFY, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set obfuscate value
    */
   public static String obfuscateSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_OBFUSCATE, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set tray value
    */
   public static String traySettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_TRAY, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set technology value
    */
   public static String technologySettings(String value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_TECHNOLOGY, value);
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set meshnet value
    */
   public static String meshnetSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_MESHNET, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set Lan-discovery value
    */
   public static String lanDiscoverySettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_LAN_DISCOVERY, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set virtual-location value
    */
   public static String virtualLocationSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_VIRTUAL_LOCATION, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set post-quantum value
    */
   public static String postQuantumSettings(boolean value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_POST_QUANTUM, ((value) ? VAL_ENABLED : VAL_DISABLED));
      
      return status;
   }

   /**
    * Set nordvpn settings
    * @return the return of nordvpn set protocol value
    */
   public static String protocolSettings(String value)
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_SET, OPT_PROTOCOL, value);
      
      return status;
   }

   /**
    * Get nordvpn account information
    * @return the account information of nordvpn
    */
   public static String getAccountInfo()
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ACCOUNT);
      
      return status;
   }

   /**
    * Get nordvpn version information
    * @return the current version of nordvpn
    */
   public static String[] getVersion()
   {
      String status[] = new String[] {null, null};

      status[0] = UtilSystem.runCommand(CMD_NORDVPN, ARG_VERSION);
      if (UtilSystem.isLastError()) return status;

      status[1] = UtilSystem.runCommand(CMD_NORDVPN, ARG_DAEMON_VERSION);

      return status;
   }

   /**
    * Get nordvpn server groups
    * @return the server groups of nordvpn
    */
   public static String getGroups()
   {
      String status = null;
      
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_GROUPS);
      
      return status;
   }

   /**
    * Get nordvpn server list of countries.
    * @return the list of countries with VPN servers
    */
   public static String getListOfCountries()
   {
      String countries = null;
      
      countries = UtilSystem.runCommand(CMD_NORDVPN, ARG_COUNTRIES);
      
      return countries;
   }

   /**
    * Get nordvpn server list of cities in a country.
    * @param the country to get the list of cities for
    * @return the server list of cities for a country
    */
   public static String getListOfCities(String country)
   {
      String cities = null;
      
      cities = UtilSystem.runCommand(CMD_NORDVPN, ARG_CITIES, country);
      
      return cities;
   }

   /**
    * Connect to VPN server
    * 
    * @param loc
    *           is the current location to connect to (optional)
    * @param country
    *           is the country to connect (optional)
    * @param city
    *           is the city to connect (optional)
    * @return the current connection status of nordvpn - if <code>null</code>, checkForConnection failed
    */
   public static String connect(CurrentLocation loc)
   {
      String status = null;

      // Set default connection data
      String serverHostName = "";
      int cityId = -1;
      NordVPNEnumGroups currentGroup = NvpnGroups.getCurrentFilterGroup();
      boolean bObfuscate = currentGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers);

      if (null != loc)
      {
         Starter._m_logError.LoggingInfo("Connect to Server: " + loc.toString() + " / Obfuscate=" + bObfuscate);
         // validate group/VPN settings for the connection
         NvpnSettingsData csd = Starter.getCurrentSettingsData();
         boolean rc = csd.checkForConnection(loc);
         if (false == rc)
         {
            // Required settings changes refused
            // ! Connection to the selected server connection cannot established with the current NordVPN settings
            status = "The selected Server location '" + loc.getServerKey() + "' does not support the current settings.";
            Starter._m_logError.LoggingWarning(90500,
                  "Settings Mismatch",
                  status);
            return null;
         }

         // get the connection data from the location object
         cityId = loc.getCityId();
         serverHostName = loc.getServerNordVPN();
         currentGroup = NordVPNEnumGroups.get(loc.getLegacyGroup());
         bObfuscate = (NordVPNEnumGroups.legacy_obfuscated_servers).equals(currentGroup);
      }
      else
      {
         Starter._m_logError.LoggingInfo("Connect to Server: nordvpn quick connect");
      }

      JPanelConnectTimer.forceStopTheTimer();

      if (cityId == -1)
      {
         // Quick Connect
         if (bObfuscate)
         {
            // if 'obfuscate' is set (only for OPENVPN), group attribute is not allowed - in that case, connect without group
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT);
         }
         else
         {
            // ..with group
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, OPT_GROUP, currentGroup.name());
         }
      }
      else if (cityId == 2)
      {
         // Connect to Region
         status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, loc.getCountryName());
      }
      else if (cityId == 0)
      {
         // with country only (with valid loc)
         if (bObfuscate)
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, serverHostName);
         }
         else
         {
            // ... add legacy group
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, OPT_GROUP, currentGroup.name(), serverHostName);
         }
      }
      else // cityId == 1 or >1
      {
         // with [country and] city or serverHostName (with valid loc)
         // In some cases (called from auto connect or recent list) we must check, if the current legacy group is valid for that server...
         // if 'obfuscate' is set (only for OPENVPN), group attribute is not allowed - in that case, the variable 'legacyGroup' is set to ""
         if (loc.hasGroup(currentGroup))
         {
            if (bObfuscate)
            {
               status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, serverHostName);
            }
            else
            {
               status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, OPT_GROUP, currentGroup.name(), serverHostName);
            }
         }
         else
         {
            if (bObfuscate)
            {
               UtilSystem.setLastError(UtilSystem.joinCommand(CMD_NORDVPN, ARG_CONNECT, serverHostName) +
                     "\nServer '" + loc.getCountryName() + "/" + loc.getCityName() + "' [" + serverHostName + "] does not support group: " + currentGroup.name() +
                     "\nPlease check legacy group setting and reconnect or change the server.", -1);
            }
            else
            {
               UtilSystem.setLastError(UtilSystem.joinCommand(CMD_NORDVPN, ARG_CONNECT, OPT_GROUP, currentGroup.name(), serverHostName) +
                     "\nServer '" + loc.getCountryName() + "/" + loc.getCityName() + "' [" + serverHostName + "] does not support group: " + currentGroup.name() +
                     "\nPlease check legacy group setting and reconnect or change the server.", -1);
            }
         }
      }
      if (false == UtilSystem.isLastError())
      {
         NvpnSettingsData.resetRequiresReconnect(); // successfully connected with current settings

         // ensure that after successful connection the current group (for status and JServerTree filter) is updated
         NvpnGroups.setCurrentLegacyGroup(currentGroup);
         if (null != loc) loc.setConnected(true);  // loc = null in case of QuickConnect
         Starter.setCurrentServer(loc);
         Starter.updateCurrentServer();
      }
      else
      {
         // Workaround, because updateCurrentServer() overwrites error message (required in calling program!)
         String sLastError = UtilSystem.getLastError();
         int iLastError = UtilSystem.getLastExitCode();
         if (null != loc) loc.setConnected(false);  // loc = null in case of QuickConnect
         Starter.setCurrentServer(loc);
         Starter.updateCurrentServer();
         UtilSystem.setLastError(sLastError, iLastError);
      }

      return status;
   }

   /**
    * Disconnect from VPN server
    * @return the current connection status of nordvpn
    */
   public static String disconnect()
   {
      String status = null;
      
      JPanelConnectTimer.forceStopTheTimer();
      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_DISCONNECT);
      
      return status;
   }

   /**
    * Login
    * @return the login command response of nordvpn
    */
   public static String login()
   {
      String status = null;

      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_LOGIN);

      return status;
   }

   /**
    * Logout
    * @return the logout command response of nordvpn
    */
   public static String logout()
   {
      String status = null;

      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_LOGOUT);

      return status;
   }

   public static String allowListPortsAdd(String[] saValues)
   {
      // nordvpn allowlist add port 12345 protocol TCP
      // nordvpn allowlist add ports 12345 12355 protocol TCP
      String status = null;

      if (saValues[1].isBlank())
      {
         if (saValues[2].equals("TCP|UDP"))
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_ADD, OPT_PORT, saValues[0]);
         }
         else
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_ADD, OPT_PORT, saValues[0], OPT_PROTOCOL, saValues[2]);
         }
      }
      else
      {
         if (saValues[2].equals("TCP|UDP"))
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_ADD, OPT_PORTS, saValues[0], saValues[1]);
         }
         else
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_ADD, OPT_PORTS, saValues[0], saValues[1], OPT_PROTOCOL, saValues[2]);
         }
      }

      return status;
   }

   public static String allowListPortsRemove(String[] saValues)
   {
      // nordvpn allowlist remove port 12345 protocol TCP
      // nordvpn allowlist remove ports 12345 12355 protocol TCP
      // nordvpn allowlist remove port 12345 (for protocol TCP|UDP)
      // nordvpn allowlist remove ports 12345 12355 (for protocol TCP|UDP)
      String status = null;

      if (saValues[1].isBlank())
      {
         if (saValues[2].equals("TCP|UDP"))
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_REMOVE, OPT_PORT, saValues[0]);            
         }
         else
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_REMOVE, OPT_PORT, saValues[0], OPT_PROTOCOL, saValues[2]);            
         }
      }
      else
      {
         if (saValues[2].equals("TCP|UDP"))
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_REMOVE, OPT_PORTS, saValues[0], saValues[1]);
         }
         else
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_REMOVE, OPT_PORTS, saValues[0], saValues[1], OPT_PROTOCOL, saValues[2]);
         }
      }

      return status;
   }

   public static String allowListSubnetAdd(String sValue)
   {
      //nordvpn allowlist add subnet 127.0.0.1/24
      String status = null;

      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_ADD, OPT_SUBNET, sValue);

      return status;
   }

   public static String allowListSubnetRemove(String sValue)
   {
      //nordvpn allowlist remove subnet 127.0.0.1/24
      String status = null;

      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_REMOVE, OPT_SUBNET, sValue);

      return status;
   }

   public static String allowListRemoveAll()
   {
      String status = null;

      status = UtilSystem.runCommand(CMD_NORDVPN, ARG_ALLOWLIST, OPT_REMOVE, OPT_ALL);

      return status;
   }
}
