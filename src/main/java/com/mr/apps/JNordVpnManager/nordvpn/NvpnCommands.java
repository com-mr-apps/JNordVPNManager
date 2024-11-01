/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class NvpnCommands {

	private static final String CMD_WHICH = "which";
	private static final String CMD_NORDVPN = "nordvpn"; 
   private static final String ARG_ACCOUNT = "account";
   private static final String ARG_CONNECT = "connect"; 
	private static final String ARG_COUNTRIES = "countries"; 
   private static final String ARG_DISCONNECT = "disconnect"; 
   private static final String ARG_GROUPS = "groups";
   private static final String ARG_LOGIN = "login";
   private static final String ARG_LOGOUT = "logout";
	private static final String ARG_SETTINGS = "settings";
   private static final String ARG_STATUS = "status";
   private static final String ARG_VERSION = "--version";
   private static final String ARG_DAEMON_VERSION = "version";
   private static final String ARG_CITIES = "cities";
	
	/**
	 * Check, if nordvpn is installed
	 * @return 
	 * @return true, if nordvpn is installed, else false
	 */
	public static boolean isInstalled()
	{
		String retVal = UtilSystem.runCommand(CMD_WHICH, CMD_NORDVPN);
		return !retVal.isEmpty();
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
      if (UtilSystem.isLastError()) return null;

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
      String cities = null;
      
      cities = UtilSystem.runCommand(CMD_NORDVPN, ARG_COUNTRIES);
      
      return cities;
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
    * @param country is the country to connect (optional)
    * @param city is the city to connect (optional)
    * @return the current connection status of nordvpn
    */
   public static String connect(String country, String city)
   {
      String status = null;
      
      if (null == city || city.isBlank())
      {
         if (null == country || country.isBlank())
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT);
         }
         else
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, country);
         }
      }
      else
      {
         if (null == country || country.isBlank())
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, city);
         }
         else
         {
            status = UtilSystem.runCommand(CMD_NORDVPN, ARG_CONNECT, country, city);
         }
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
}
