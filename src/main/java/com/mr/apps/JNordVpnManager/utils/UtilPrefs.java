/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.prefs.Preferences;

import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * 
 * storage location (Linux): ~/.java/.userPrefs
 */
public class UtilPrefs
{
   // Defaults:
   private static String DEFAULT_PREF_RECENTSERVER_CITY           = "";
   private static String DEFAULT_PREF_RECENTSERVER_COUNTRY        = "";
   private static String DEFAULT_PREF_RECENTSERVER_LIST           = "";
   private static int    DEFAULT_PREF_RECENTSERVER_LIST_LENGTH    = 5;
   private static String DEFAULT_PREF_SERVERLIST_DATA             = "";
   private static String DEFAULT_PREF_SERVERLIST_TIMESTAMP        = "0";
   private static int    DEFAULT_PREF_SETTINGS_COMPACTMODE        = 0;  // Default is expanded
   private static int    DEFAULT_PREF_SETTINGS_AUTOCONNECTMODE    = 0;
   private static int    DEFAULT_PREF_SETTINGS_AUTODISCONNECTMODE = 0;
   private static int    DEFAULT_PREF_SETTINGS_TRACEDEBUG         = 0;
   private static int    DEFAULT_PREF_SETTINGS_TRACECMD           = 0;
   private static int    DEFAULT_PREF_SETTINGS_TRACEINIT          = 0;
   private static String DEFAULT_PREF_SETTINGS_LOGFILE_NAME       = "~/JNordVpnManager.log";
   private static int    DEFAULT_PREF_SETTINGS_LOGFILE_ACTIVE     = 0;
   private static int    DEFAULT_PREF_SETTINGS_COMMAMD_TIMEOUT    = 30;

   public enum FieldTitle
   {
      RECENTSERVER_CITY("Recent City", KeyEvent.VK_C, 20),
      RECENTSERVER_COUNTRY("Recent Country", KeyEvent.VK_O, 20),
      RECENTSERVER_LIST("Recent Servers", KeyEvent.VK_S, 20),
      RECENTSERVER_LIST_LENGTH("Recent List Size", KeyEvent.VK_L, 2),
      SERVERLIST_DATA("Server Data", KeyEvent.VK_D, 20),
      SERVERLIST_TIMESTAMP("Timestamp", KeyEvent.VK_T, 10),
      COMPACTMODE("Compact Mode", KeyEvent.VK_M, 5),
      AUTOCONNECTMODE("Auto Connect on Program Start", KeyEvent.VK_S, 1),
      AUTODISCONNECTMODE("Auto Disconnect on Program Exit", KeyEvent.VK_E, 1),
      TRACEDEBUG("Trace Debug", KeyEvent.VK_B, 1),
      TRACECMD("Trace Command", KeyEvent.VK_A, 1),
      TRACEINIT("Trace Init", KeyEvent.VK_I, 1),
      LOGFILE_NAME("Logfile", KeyEvent.VK_F, 20),
      LOGFILE_ACTIVE("Write to Logfile", KeyEvent.VK_W, 1),
      COMMAND_TIMEOUT("Command Timeout (in seconds)", -1, 3);

      private String title;
      private int    mnemonic;
      private int    length;

      private FieldTitle(String title, int mnemonic, int length)
      {
         this.title = title;
         this.mnemonic = mnemonic;
         this.length = length;
      }

      public String getTitle()
      {
         return title;
      }

      public int getMnemonic()
      {
         return mnemonic;
      }

      public int getLength()
      {
         return length;
      }
   };

   /**
    * Reset Settings to default values
    */
   public static void resetPreferences()
   {
      /*
      setServerList(DEFAULT_PREF_SERVERLIST_DATA);
      setServerListTimestamp(DEFAULT_PREF_SERVERLIST_TIMESTAMP);
      setRecentCity(DEFAULT_PREF_RECENTSERVER_CITY);
      setRecentCountry(DEFAULT_PREF_RECENTSERVER_COUNTRY);
      setCompactMode(DEFAULT_PREF_SETTINGS_COMPACTMODE);
      setAutoConnectMode(DEFAULT_PREF_SETTINGS_AUTOCONNECTMODE);
      setAutoDisConnectMode(DEFAULT_PREF_SETTINGS_AUTODISCONNECTMODE);
      */
      setRecentServerList(DEFAULT_PREF_RECENTSERVER_LIST);
      setRecentServerListLength(DEFAULT_PREF_RECENTSERVER_LIST_LENGTH);
/*
      setTraceDebug(DEFAULT_PREF_SETTINGS_TRACEDEBUG);
      setTraceInit(DEFAULT_PREF_SETTINGS_TRACEINIT);
      setTraceCmd(DEFAULT_PREF_SETTINGS_TRACECMD);
      setLogfileActive(DEFAULT_PREF_SETTINGS_LOGFILE_ACTIVE);
      setLogfileName(DEFAULT_PREF_SETTINGS_LOGFILE_NAME);
      setCommandTimeout(DEFAULT_PREF_SETTINGS_COMMAMD_TIMEOUT);
*/
   }

   public static void exportPreferences(String fileName)
   {
      // TODO: exportPreferences to file
   }

   public static void importPreferences(String fileName)
   {
      // TODO: importPreferences from file
   }

   public static String getServerList()
   {
      Preferences nordVpnServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String city = nordVpnServerList.get("ServerList.Data", DEFAULT_PREF_SERVERLIST_DATA);

      return city;
   }

   public static void setServerList(String serverList)
   {
      Preferences nordVpnServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnServerList.put("ServerList.Data", serverList);

      return;
   }

   public static String getServerListTimestamp()
   {
      Preferences nordVpnServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String city = nordVpnServerList.get("ServerList.Timestamp", DEFAULT_PREF_SERVERLIST_TIMESTAMP);

      return city;
   }

   public static void setServerListTimestamp(String timestamp)
   {
      Preferences nordVpnServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnServerList.put("ServerList.Timestamp", timestamp);

      return;
   }

   public static String getRecentCity()
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String city = nordVpnRecentServer.get("RecentServer.City", DEFAULT_PREF_RECENTSERVER_CITY);

      return city;
   }

   public static void setRecentCity(String city)
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnRecentServer.put("RecentServer.City", city);

      return;
   }

   public static String getRecentCountry()
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String country = nordVpnRecentServer.get("RecentServer.Country", DEFAULT_PREF_RECENTSERVER_COUNTRY);

      return country;
   }

   public static void setRecentCountry(String country)
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnRecentServer.put("RecentServer.Country", country);

      return;
   }

   public static int getCompactMode()
   {
      Preferences settingsCompactMode = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int compactMode = settingsCompactMode.getInt("CompactMode", DEFAULT_PREF_SETTINGS_COMPACTMODE);

      return compactMode;
   }

   public static void setCompactMode(int compactMode)
   {
      Preferences settingsCompactMode = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsCompactMode.putInt("CompactMode", compactMode);

      return;
   }

   public static int getAutoConnectMode()
   {
      Preferences settingsAutoConnectMode = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int autoConnectMode = settingsAutoConnectMode.getInt("AutoConnectMode", DEFAULT_PREF_SETTINGS_AUTOCONNECTMODE);

      return autoConnectMode;
   }

   public static void setAutoConnectMode(int autoConnectMode)
   {
      Preferences settingsAutoConnectMode = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsAutoConnectMode.putInt("AutoConnectMode", autoConnectMode);

      return;
   }

   public static int getAutoDisConnectMode()
   {
      Preferences settingsAutoDisConnectMode = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int autoConnectMode = settingsAutoDisConnectMode.getInt("AutoDisConnectMode", DEFAULT_PREF_SETTINGS_AUTODISCONNECTMODE);

      return autoConnectMode;
   }

   public static void setAutoDisConnectMode(int autoDisConnectMode)
   {
      Preferences settingsAutoDisConnectMode = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsAutoDisConnectMode.putInt("AutoDisConnectMode", autoDisConnectMode);

      return;
   }

   public static String getRecentServerList()
   {
      Preferences nordVpnRecentServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String recentServerList = nordVpnRecentServerList.get("RecentServer.List", DEFAULT_PREF_RECENTSERVER_LIST);

      return recentServerList;
   }

   public static void setRecentServerList(String list)
   {
      Preferences nordVpnRecentServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnRecentServerList.put("RecentServer.List", list);

      return;
   }

   public static int getRecentServerListLength()
   {
      Preferences nordVpnRecentServerListLength = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      int recentServerListLength = nordVpnRecentServerListLength.getInt("RecentServer.List.Length", DEFAULT_PREF_RECENTSERVER_LIST_LENGTH);

      return recentServerListLength;
   }

   public static void setRecentServerListLength(int listLength)
   {
      Preferences nordVpnRecentServerListLength = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnRecentServerListLength.putInt("RecentServer.List.Length", listLength);

      return;
   }

   public static int getTraceInit()
   {
      Preferences settingsTraceInit = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int traceInit = settingsTraceInit.getInt("Trace.Init", DEFAULT_PREF_SETTINGS_TRACEINIT);

      return traceInit;
   }

   public static void setTraceInit(int traceInit)
   {
      Preferences settingsTraceInit = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsTraceInit.putInt("Trace.Init", traceInit);

      return;
   }

   public static int getTraceCmd()
   {
      Preferences settingsTraceCmd = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int traceCmd = settingsTraceCmd.getInt("Trace.Cmd", DEFAULT_PREF_SETTINGS_TRACECMD);

      return traceCmd;
   }

   public static void setTraceCmd(int traceCmd)
   {
      Preferences settingsTraceCmd = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsTraceCmd.putInt("Trace.Cmd", traceCmd);

      return;
   }

   public static int getTraceDebug()
   {
      Preferences settingsTraceDebug = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int traceDebug = settingsTraceDebug.getInt("Trace.Debug", DEFAULT_PREF_SETTINGS_TRACEDEBUG);

      return traceDebug;
   }

   public static void setTraceDebug(int traceDebug)
   {
      Preferences settingsTraceDebug = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsTraceDebug.putInt("Trace.Debug", traceDebug);

      return;
   }

   public static String getLogfileName()
   {
      Preferences settingsLogfileName = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      String logfileName = settingsLogfileName.get("Logfile.Name", DEFAULT_PREF_SETTINGS_LOGFILE_NAME);

      return logfileName;
   }

   public static void setLogfileName(String logfileName)
   {
      Preferences settingsLogfileName = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsLogfileName.put("Logfile.Name", logfileName);

      return;
   }

   public static int isLogfileActive()
   {
      Preferences settingsWriteLogfile = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int writeLogfile = settingsWriteLogfile.getInt("Logfile.Active", DEFAULT_PREF_SETTINGS_LOGFILE_ACTIVE);

      return writeLogfile;
   }

   public static void setLogfileActive(int logfileActive)
   {
      Preferences settingsLogfileActive = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsLogfileActive.putInt("Logfile.Active", logfileActive);

      return;
   }

   public static int getCommandTimeout()
   {
      Preferences settingsCommandTimeout = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int commandTimeout = settingsCommandTimeout.getInt("Command.Timeout", DEFAULT_PREF_SETTINGS_COMMAMD_TIMEOUT);

      return commandTimeout;
   }

   public static void setCommandTimeout(int commandTimeout)
   {
      Preferences settingsCommandTimeout = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsCommandTimeout.putInt("Command.Timeout", commandTimeout);

      return;
   }

   public static HashMap <FieldTitle,String> getAllValues()
   {
      HashMap <FieldTitle,String> hm = new HashMap <FieldTitle,String>();
      
      String value = getRecentCity();
      hm.put(FieldTitle.RECENTSERVER_CITY, value);
      value = getRecentCountry();
      hm.put(FieldTitle.RECENTSERVER_COUNTRY, value);
      value = getRecentServerList();
      hm.put(FieldTitle.RECENTSERVER_LIST, value);
      value = StringFormat.int2String(getRecentServerListLength(), "#");
      hm.put(FieldTitle.RECENTSERVER_LIST_LENGTH, value);
      value = getServerList();
      hm.put(FieldTitle.SERVERLIST_DATA, value);
      value = getServerListTimestamp();
      hm.put(FieldTitle.SERVERLIST_TIMESTAMP, value);
      value = StringFormat.int2String(getCompactMode(), "#");
      hm.put(FieldTitle.COMPACTMODE, value);
      value = StringFormat.int2String(getAutoConnectMode(), "#");
      hm.put(FieldTitle.AUTOCONNECTMODE, value);
      value = StringFormat.int2String(getAutoDisConnectMode(), "#");
      hm.put(FieldTitle.AUTODISCONNECTMODE, value);
      value = StringFormat.int2String(getTraceDebug(), "#");
      hm.put(FieldTitle.TRACEDEBUG, value);
      value = StringFormat.int2String(getTraceCmd(), "#");
      hm.put(FieldTitle.TRACECMD, value);
      value = StringFormat.int2String(getTraceInit(), "#");
      hm.put(FieldTitle.TRACEINIT, value);
      value = getLogfileName();
      hm.put(FieldTitle.LOGFILE_NAME, value);
      value = StringFormat.int2String(isLogfileActive(), "#");
      hm.put(FieldTitle.LOGFILE_ACTIVE, value);
      value = StringFormat.int2String(getCommandTimeout(), "#");
      hm.put(FieldTitle.COMMAND_TIMEOUT, value);

      
      return hm;
   }

   public static void setAllValues(HashMap <FieldTitle,String> hm)
   {
      for (FieldTitle fieldTitle : FieldTitle.values())
      {
         String value = hm.get(fieldTitle);
         // TODO...
         System.out.printf("%s: %s%n", fieldTitle.getTitle(), value);
      }

   }
}
