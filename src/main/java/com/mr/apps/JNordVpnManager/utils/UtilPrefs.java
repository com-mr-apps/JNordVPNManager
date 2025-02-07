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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.Command;
import com.mr.apps.JNordVpnManager.gui.settings.JUserPrefsDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.gui.GuiMenuBar;
import com.mr.apps.JNordVpnManager.gui.connectLine.GuiCommandsToolBar;
import com.mr.apps.JNordVpnManager.gui.settings.JSettingsPanelField;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * Class to manage the application User Preferences.<p>
 * storage location (Linux): ~/.java/.userPrefs<br>
 * It also defines/manages the settings (preferences) panel
 */
public class UtilPrefs
{
   private static final String ACCOUNTREMINDER                          = "ACCOUNTREMINDER";
   private static final String AUTOCONNECTMODE                          = "AUTOCONNECTMODE";
   private static final String AUTODISCONNECTMODE                       = "AUTODISCONNECTMODE";
   private static final String COMMAND_TIMEOUT                          = "COMMAND_TIMEOUT";
   private static final String CONSOLE_ACTIVE                           = "CONSOLE_ACTIVE";
   private static final String LOGFILE_ACTIVE                           = "LOGFILE_ACTIVE";
   private static final String LOGFILE_NAME                             = "LOGFILE_NAME";
   private static final String LOGFILE_TRACEINIT                        = "TRACEINIT";
   private static final String LOGFILE_TRACECMD                         = "TRACECMD";
   private static final String LOGFILE_TRACEDEBUG                       = "TRACEDEBUG";
   private static final String MESSAGE_AUTOCLOSE                        = "MESSAGEDIALOG_AUTOCLOSE";
   private static final String RECENTSERVER_LIST_LENGTH                 = "RECENTSERVER_LIST_LENGTH";
   private static final String RECENTSERVER_LIST                        = "RECENTSERVER_LIST";
   private static final String RECENTSERVER_COUNTRY                     = "RECENTSERVER_COUNTRY";
   private static final String RECENTSERVER_CITY                        = "RECENTSERVER_CITY";
   private static final String SERVERLIST_TIMESTAMP                     = "SERVERLIST_TIMESTAMP";
   private static final String SERVERLIST_DATA                          = "SERVERLIST_DATA";
   private static final String SERVERLIST_AUTOUPDATE                    = "SERVERLIST_AUTOUPDATE";
   private static final String ADDON_PATH                               = "ADDON_PATH";

   // hidden options
   // private static final String COMPACTMODE                              = "COMPACTMODE";
   // private static final String RECENTSERVER_REGION                      = "RECENTSERVER_REGION";
   // private static final String RECENTSERVER_GROUP                       = "RECENTSERVER_GROUP";

   // Internal Defaults
   private static String DEFAULT_PREF_RECENTSERVER_CITY           = "";
   private static String DEFAULT_PREF_RECENTSERVER_COUNTRY        = "";
   private static String DEFAULT_PREF_RECENTSERVER_LIST           = "";
   private static int    DEFAULT_PREF_RECENTSERVER_REGION         = NordVPNEnumGroups.all_regions.getId();
   private static int    DEFAULT_PREF_RECENTSERVER_GROUP          = NordVPNEnumGroups.Standard_VPN_Servers.getId();
   private static int    DEFAULT_PREF_RECENTSERVER_LIST_LENGTH    = 5;
   private static String DEFAULT_PREF_SERVERLIST_DATA             = "";
   private static String DEFAULT_PREF_SERVERLIST_TIMESTAMP        = "0";
   private static int    DEFAULT_PREF_SERVERLIST_AUTOUPDATE       = 0;
   private static int    DEFAULT_PREF_SETTINGS_COMPACTMODE        = 0;  // this flag represents the current state -> no setting. TODO: (?) add another "Start Program in Compact Mode"
   private static int    DEFAULT_PREF_SETTINGS_AUTOCONNECTMODE    = 0;
   private static int    DEFAULT_PREF_SETTINGS_AUTODISCONNECTMODE = 0;
   private static int    DEFAULT_PREF_SETTINGS_TRACEDEBUG         = 0;
   private static int    DEFAULT_PREF_SETTINGS_TRACECMD           = 0;
   private static int    DEFAULT_PREF_SETTINGS_TRACEINIT          = 0;
   private static String DEFAULT_PREF_SETTINGS_LOGFILE_NAME       = "~/.local/share/JNordVpnManager/JNordVpnManager.log";
   private static String DEFAULT_PREF_SETTINGS_ADDONS_PATH         = "~/.local/share/JNordVpnManager/addons";
   private static int    DEFAULT_PREF_SETTINGS_LOGFILE_ACTIVE     = 0;
   private static int    DEFAULT_PREF_SETTINGS_CONSOLE_ACTIVE     = 0;
   private static int    DEFAULT_PREF_SETTINGS_COMMAMD_TIMEOUT    = 30;
   private static int    DEFAULT_PREF_SETTINGS_MESSAGE_AUTOCLOSE  = 2;
   private static int    DEFAULT_PREF_SETTINGS_ACCOUNTREMINDER    = 31;
   private static String DEFAULT_PREF_SETTINGS_COMMANDS_TOOLBAR   = Command.APP_PREF_AUTOCONNECT + ";" + Command.APP_PREF_AUTODISCONNECT + ";" + Command.VPN_CMD_RECONNECT;

   /**
    * Dataset defining the UserPreference values.
    * <p>
    * Contains the panel field description by Id:
    * <ul>
    * <li>Label text</li>
    * <li>Field Type, where: "T" - Text field / "N[min,max]" - Integer with optional range / "B" - Boolean (CheckBox)</li>
    * <li>Mnemonic (-1 - no KeyEvent)</li>
    * <li>Field length</li>
    * <li>Default value</li>
    * </ul>
    */

   /**
    * Show the User Preferences Panel.
    */
   public static void showUserPreferencesPanel()
   {
      Map<String, JSettingsPanelField> settingsPanelFieldsMap  = new HashMap<String, JSettingsPanelField>();

      settingsPanelFieldsMap.put(ACCOUNTREMINDER, new JSettingsPanelField("Account Expiration Warning Days", "N[1,90]", -1, 2, StringFormat.int2String(DEFAULT_PREF_SETTINGS_ACCOUNTREMINDER, "#")));
      settingsPanelFieldsMap.put(ADDON_PATH, new JSettingsPanelField("Addons", "T", -1, 20, DEFAULT_PREF_SETTINGS_ADDONS_PATH));
      settingsPanelFieldsMap.put(AUTOCONNECTMODE, new JSettingsPanelField("Auto Connect on Program Start", "B", KeyEvent.VK_S, 1, StringFormat.int2String(DEFAULT_PREF_SETTINGS_AUTOCONNECTMODE, "#")));
      settingsPanelFieldsMap.put(AUTODISCONNECTMODE, new JSettingsPanelField("Auto Disconnect on Program Exit", "B", KeyEvent.VK_E, 1, StringFormat.int2String(DEFAULT_PREF_SETTINGS_AUTODISCONNECTMODE, "#")));
      settingsPanelFieldsMap.put(COMMAND_TIMEOUT, new JSettingsPanelField("Command Timeout (in seconds)", "N[5,99]", -1, 2, StringFormat.int2String(DEFAULT_PREF_SETTINGS_COMMAMD_TIMEOUT, "#")));
//      m_settingsFieldMap.put(COMPACTMODE, new JSettingsPanelField("Start Program in Compact Mode", "B", KeyEvent.VK_M, 5, StringFormat.int2String(DEFAULT_PREF_SETTINGS_COMPACTMODE, "#")));
      settingsPanelFieldsMap.put(CONSOLE_ACTIVE, new JSettingsPanelField("Open Console at Program Start", "B", -1, 1, StringFormat.int2String(DEFAULT_PREF_SETTINGS_CONSOLE_ACTIVE, "#")));
      settingsPanelFieldsMap.put(LOGFILE_ACTIVE, new JSettingsPanelField("Write to Logfile", "B", KeyEvent.VK_W, 1, StringFormat.int2String(DEFAULT_PREF_SETTINGS_LOGFILE_ACTIVE, "#")));
      settingsPanelFieldsMap.put(LOGFILE_NAME, new JSettingsPanelField("Logfile", "T", KeyEvent.VK_F, 20, DEFAULT_PREF_SETTINGS_LOGFILE_NAME));
      settingsPanelFieldsMap.put(LOGFILE_TRACECMD, new JSettingsPanelField("Trace Command", "B", KeyEvent.VK_A, 1, StringFormat.int2String(DEFAULT_PREF_SETTINGS_TRACECMD, "#")));
      settingsPanelFieldsMap.put(LOGFILE_TRACEDEBUG, new JSettingsPanelField("Trace Debug", "B", KeyEvent.VK_B, 1, StringFormat.int2String(DEFAULT_PREF_SETTINGS_TRACEDEBUG, "#")));
      settingsPanelFieldsMap.put(LOGFILE_TRACEINIT, new JSettingsPanelField("Trace Init", "B", KeyEvent.VK_I, 1, StringFormat.int2String(DEFAULT_PREF_SETTINGS_TRACEINIT, "#")));
      settingsPanelFieldsMap.put(MESSAGE_AUTOCLOSE, new JSettingsPanelField("Auto Close Messages", "N[-1,99]", -1, 2, StringFormat.int2String(DEFAULT_PREF_SETTINGS_MESSAGE_AUTOCLOSE, "#")));
      settingsPanelFieldsMap.put(RECENTSERVER_CITY, new JSettingsPanelField("Recent Server", "T", KeyEvent.VK_S, 20, DEFAULT_PREF_RECENTSERVER_CITY));
      settingsPanelFieldsMap.put(RECENTSERVER_COUNTRY, new JSettingsPanelField("Recent Country", "T", KeyEvent.VK_C, 20, DEFAULT_PREF_RECENTSERVER_COUNTRY));
//      settingsPanelFieldsMap.put(RECENTSERVER_REGION, new JSettingsPanelField("Recent Server Region", "B", -1, 1, StringFormat.int2String(DEFAULT_PREF_RECENTSERVER_REGION, "#")));
//      settingsPanelFieldsMap.put(RECENTSERVER_GROUP, new JSettingsPanelField("Recent Servers Group", "B", -1, 1, StringFormat.int2String(DEFAULT_PREF_RECENTSERVER_GROUP, "#")));
      settingsPanelFieldsMap.put(RECENTSERVER_LIST, new JSettingsPanelField("Recent Servers List", "T", -1, 20, DEFAULT_PREF_RECENTSERVER_LIST));
      settingsPanelFieldsMap.put(RECENTSERVER_LIST_LENGTH, new JSettingsPanelField("Recent Servers List Size", "N[1,10]", -1, 2, StringFormat.int2String(DEFAULT_PREF_RECENTSERVER_LIST_LENGTH, "#")));
      settingsPanelFieldsMap.put(SERVERLIST_AUTOUPDATE, new JSettingsPanelField("Auto Update Server Data on Program Start", "N[0,99]", KeyEvent.VK_U, 2, StringFormat.int2String(DEFAULT_PREF_SERVERLIST_AUTOUPDATE, "#")));
      settingsPanelFieldsMap.put(SERVERLIST_DATA, new JSettingsPanelField("Server Data", "T", KeyEvent.VK_D, 20, DEFAULT_PREF_SERVERLIST_DATA));
      settingsPanelFieldsMap.put(SERVERLIST_TIMESTAMP, new JSettingsPanelField("Sync. Data Timestamp", "T", KeyEvent.VK_T, 10, DEFAULT_PREF_SERVERLIST_TIMESTAMP));

      JUserPrefsDialog sp = new JUserPrefsDialog(Starter.getMainFrame(), "UserPreferences", settingsPanelFieldsMap);
      sp.getResult();
   }

   /**
    * Action "Export" User Preferences to a file.
    * @param fileName is the file name where to store the data.
    */
   public static boolean exportUserPreferences(String fileName, HashMap <String,String> hm)
   {
      Starter._m_logError.TraceDebug("Export User Preferences to file '" + fileName + "'.");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false)))
      {
         writer.write("INFO JNordVPN User Preferences Export File");
         writer.newLine();

         for (HashMap.Entry<String, String> entry : hm.entrySet())
         {
            String key = entry.getKey();
            String value = entry.getValue();
            String line = key + " " + value;
            writer.write(line);
            writer.newLine();
         }
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionAbend(10901, e);
         return false;
      }
      return true;
   }

   /**
    * Action "Import" User Preferences from a file.
    * @param fileName is the file name where to read the data.
    */
   public static HashMap <String,String> importUserPreferences(String fileName)
   {
      HashMap<String, String> hm = null;

      Starter._m_logError.TraceDebug("Import User Preferences from file '" + fileName + "'.");
      try (Stream<String> lines = Files.lines(Paths.get(fileName)))
      {
         hm = new HashMap<String, String>();
         int iLine = 0;
         for (String line : (Iterable<String>) lines::iterator)
         {
            iLine++;
            Pattern pattern = Pattern.compile("([^\\s]+)\\s+(.*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            boolean matchFound = matcher.find();
            if (matchFound)
            {
               hm.put(matcher.group(1), matcher.group(2));
            }
            else
            {
               Starter._m_logError.TraceDebug("Line '" + iLine + "' does not match the pattern [key value]!");
            }
         }
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionAbend(10901, e);
         hm = null;
      }

      return hm;
   }

   /*
    * Getter/Setter Methods to access the User Data...
    */
   public static String getServerListData()
   {
      Preferences nordVpnServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String city = nordVpnServerList.get("ServerList.Data", DEFAULT_PREF_SERVERLIST_DATA);

      return city;
   }

   public static void setServerListData(String serverList)
   {
      Preferences nordVpnServerList = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnServerList.put("ServerList.Data", serverList);

      return;
   }

   public static int getServerListAutoUpdate()
   {
      Preferences settingsServerListAutoUpdate = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int serverListAutoUpdate = settingsServerListAutoUpdate.getInt("ServerList.AutoUpdate", DEFAULT_PREF_SERVERLIST_AUTOUPDATE);

      return serverListAutoUpdate;
   }

   public static void setServerListAutoUpdate(int serverListAutoUpdate)
   {
      Preferences settingsServerListAutoUpdate = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsServerListAutoUpdate.putInt("ServerList.AutoUpdate", serverListAutoUpdate);

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

   public static String getRecentServerCity()
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String city = nordVpnRecentServer.get("RecentServer.City", DEFAULT_PREF_RECENTSERVER_CITY);

      return city;
   }

   public static void setRecentServerCity(String city)
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnRecentServer.put("RecentServer.City", city);

      return;
   }

   public static String getRecentServerCountry()
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      String country = nordVpnRecentServer.get("RecentServer.Country", DEFAULT_PREF_RECENTSERVER_COUNTRY);

      return country;
   }

   public static void setRecentServerCountry(String country)
   {
      Preferences nordVpnRecentServer = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      nordVpnRecentServer.put("RecentServer.Country", country);

      return;
   }

   public static int getRecentServerRegion()
   {
      Preferences settingsRecentServerRegion = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      int recentServerRegion = settingsRecentServerRegion.getInt("RecentServer.Region", DEFAULT_PREF_RECENTSERVER_REGION);

      return recentServerRegion;
   }

   public static void setRecentServerRegion(int recentServerRegion)
   {
      Preferences settingsRecentServerRegion = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      settingsRecentServerRegion.putInt("RecentServer.Region", recentServerRegion);

      return;
   }

   public static int getRecentServerGroup()
   {
      Preferences settingsRecentServerGroup = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      int recentServerGroup = settingsRecentServerGroup.getInt("RecentServer.Group", DEFAULT_PREF_RECENTSERVER_GROUP);

      return recentServerGroup;
   }

   public static void setRecentServerGroup(int recentServerGroup)
   {
      Preferences settingsRecentServerGroup = Preferences.userRoot().node("com/mr/apps/JNordVpnManager");
      settingsRecentServerGroup.putInt("RecentServer.Group", recentServerGroup);

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

   public static int getMessageAutoclose()
   {
      Preferences settingsMessageAutoclose = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int messageAutoclose = settingsMessageAutoclose.getInt("MessageAutoclose", DEFAULT_PREF_SETTINGS_MESSAGE_AUTOCLOSE);

      return messageAutoclose;
   }

   public static void setMessageAutoclose(int messageAutoclose)
   {
      Preferences settingsMessageAutoclose = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsMessageAutoclose.putInt("MessageAutoclose", messageAutoclose);

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
      GuiCommandsToolBar.updateCommand(Command.APP_PREF_AUTOCONNECT);

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
      GuiCommandsToolBar.updateCommand(Command.APP_PREF_AUTODISCONNECT);

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

      return logfileName.replaceFirst("^~", System.getProperty("user.home"));
   }

   public static void setLogfileName(String logfileName)
   {
      Preferences settingsLogfileName = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsLogfileName.put("Logfile.Name", logfileName.replaceFirst("^~", System.getProperty("user.home")));

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

   public static int isConsoleActive()
   {
      Preferences settingsConsoleActive = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int consoleActive = settingsConsoleActive.getInt("Console.ActiveOnProgramStart", DEFAULT_PREF_SETTINGS_CONSOLE_ACTIVE);

      return consoleActive;
   }

   public static void setConsoleActive(int consoleActive)
   {
      Preferences settingsConsoleActive = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsConsoleActive.putInt("Console.ActiveOnProgramStart", consoleActive);

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

   public static int getAccountReminder()
   {
      Preferences settingsAccountReminder = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      int accountReminder = settingsAccountReminder.getInt("Account.Reminder", DEFAULT_PREF_SETTINGS_ACCOUNTREMINDER);

      return accountReminder;
   }

   public static void setAccountReminder(int accountReminder)
   {
      Preferences settingsAccountReminder = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsAccountReminder.putInt("Account.Reminder", accountReminder);

      return;
   }
   public static void resetAccountReminder()
   {
      setAccountReminder(DEFAULT_PREF_SETTINGS_ACCOUNTREMINDER);
   }

   public static String getAddonsPath()
   {
      Preferences settingsAddonsPath = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      String addonsPath = settingsAddonsPath.get("Addons.Path", DEFAULT_PREF_SETTINGS_ADDONS_PATH);

      return addonsPath.replaceFirst("^~", System.getProperty("user.home"));
   }

   public static void setAddonsPath(String addonsPath)
   {
      Preferences settingsAddonsPath = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsAddonsPath.put("Addons.Path", addonsPath.replaceFirst("^~", System.getProperty("user.home")));

      return;
   }

   public static String getCommandsToolbarIds()
   {
      Preferences settingsCommandsToolbar = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      String commandsToolbar = settingsCommandsToolbar.get("CommandsToolbar", DEFAULT_PREF_SETTINGS_COMMANDS_TOOLBAR);

      return commandsToolbar;
   }

   public static void setCommandsToolbarIds(String commandsToolbar)
   {
      Preferences settingsCommandsToolbar = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings");
      settingsCommandsToolbar.put("CommandsToolbar", commandsToolbar);

      return;
   }

   /**
    * Get a data set with all User Preferences data
    * @return the dataset with all user preferences data.
    */
   public static HashMap <String,String> getUserPreferencesDataSet()
   {
      HashMap <String,String> hm = new HashMap <String,String>();
      
      hm.put(RECENTSERVER_CITY, getRecentServerCity());
      hm.put(RECENTSERVER_COUNTRY, getRecentServerCountry());
//      hm.put(RECENTSERVER_REGION, StringFormat.int2String(getRecentServerRegion(), "#"));
//      hm.put(RECENTSERVER_GROUP, StringFormat.int2String(getRecentServerGroup(), "#"));
      hm.put(RECENTSERVER_LIST, getRecentServerList());
      hm.put(RECENTSERVER_LIST_LENGTH, StringFormat.int2String(getRecentServerListLength(), "#"));
      hm.put(SERVERLIST_DATA, getServerListData());
      hm.put(SERVERLIST_TIMESTAMP, getServerListTimestamp());
      hm.put(SERVERLIST_AUTOUPDATE, StringFormat.int2String(getServerListAutoUpdate(), "#"));
//      hm.put(COMPACTMODE, StringFormat.int2String(getCompactMode(), "#"));
      hm.put(AUTOCONNECTMODE, StringFormat.int2String(getAutoConnectMode(), "#"));
      hm.put(AUTODISCONNECTMODE, StringFormat.int2String(getAutoDisConnectMode(), "#"));
      hm.put(LOGFILE_TRACEDEBUG, StringFormat.int2String(getTraceDebug(), "#"));
      hm.put(LOGFILE_TRACECMD, StringFormat.int2String(getTraceCmd(), "#"));
      hm.put(LOGFILE_TRACEINIT, StringFormat.int2String(getTraceInit(), "#"));
      hm.put(LOGFILE_NAME, getLogfileName());
      hm.put(LOGFILE_ACTIVE, StringFormat.int2String(isLogfileActive(), "#"));
      hm.put(COMMAND_TIMEOUT, StringFormat.int2String(getCommandTimeout(), "#"));
      hm.put(CONSOLE_ACTIVE, StringFormat.int2String(isConsoleActive(), "#"));
      hm.put(MESSAGE_AUTOCLOSE, StringFormat.int2String(getMessageAutoclose(), "#"));
      hm.put(ACCOUNTREMINDER, StringFormat.int2String(getAccountReminder(), "#"));
      hm.put(ADDON_PATH, getAddonsPath());

      return hm;
   }

   /**
    * Set User Preferences data with dataset values
    * @param hm is the data set with the new values
    */
   public static void setUserPreferencesDataSet(HashMap <String,String> hm)
   {
      if (null == hm)
      {
         return;
      }

      Starter._m_logError.TraceDebug("Save all Preference values to UserPrefs:");
      for (HashMap.Entry<String, String> entry : hm.entrySet())
      {
         String key = entry.getKey();
         String value = entry.getValue();
         Starter._m_logError.TraceDebug(key + ": " + value);
      }

      setServerListData(hm.get(SERVERLIST_DATA));
      setServerListTimestamp(hm.get(SERVERLIST_TIMESTAMP));
      setServerListAutoUpdate(Integer.valueOf(hm.get(SERVERLIST_AUTOUPDATE)));
      setRecentServerCity(hm.get(RECENTSERVER_CITY));
      setRecentServerCountry(hm.get(RECENTSERVER_COUNTRY));
//      setRecentServerRegion(Integer.valueOf(hm.get(RECENTSERVER_REGION)));
//      setRecentServerGroup(Integer.valueOf(hm.get(RECENTSERVER_GROUP)));
//      setCompactMode(Integer.valueOf(hm.get(COMPACTMODE)));
      setAutoConnectMode(Integer.valueOf(hm.get(AUTOCONNECTMODE)));
      setAutoDisConnectMode(Integer.valueOf(hm.get(AUTODISCONNECTMODE)));
      setRecentServerListLength(Integer.valueOf(hm.get(RECENTSERVER_LIST_LENGTH)));
      setTraceDebug(Integer.valueOf(hm.get(LOGFILE_TRACEDEBUG)));
      setTraceInit(Integer.valueOf(hm.get(LOGFILE_TRACEINIT)));
      setTraceCmd(Integer.valueOf(hm.get(LOGFILE_TRACECMD)));
      setLogfileActive(Integer.valueOf(hm.get(LOGFILE_ACTIVE)));
      setLogfileName(hm.get(LOGFILE_NAME));
      setCommandTimeout(Integer.valueOf(hm.get(COMMAND_TIMEOUT)));
      setConsoleActive(Integer.valueOf(hm.get(CONSOLE_ACTIVE)));
      setMessageAutoclose(Integer.valueOf(hm.get(MESSAGE_AUTOCLOSE)));

      // GUI Updates
      String sCurrentList = getRecentServerList();
      String sNewList = hm.get(RECENTSERVER_LIST);
      if (false == sCurrentList.equals(sNewList))
      {
         setRecentServerList(sNewList);
         GuiMenuBar.addToMenuRecentServerListItems(null);
      }
      int sCurrentAccountReminder = getAccountReminder();
      int iNewAccountReminder = Integer.valueOf(hm.get(ACCOUNTREMINDER));
      if (sCurrentAccountReminder != iNewAccountReminder)
      {
         setAccountReminder(iNewAccountReminder);
         GuiMenuBar.updateAccountReminder();
      }
      String sCurrentAddonPath = getAddonsPath();
      String sNewAddonPath = hm.get(ADDON_PATH);
      if (false == sCurrentAddonPath.equals(sNewAddonPath))
      {
         // add changed addons classpath
         if (true == UtilSystem.addClasspath(sNewAddonPath, null))
         {
            setAddonsPath(sNewAddonPath);
         }
      }
   }

   /**
    * Reset User Preferences to their default values
    */
   public static void resetUserPreferenceValues()
   {
      Starter._m_logError.TraceDebug("Reset all User Preference values.");

      setServerListAutoUpdate(DEFAULT_PREF_SERVERLIST_AUTOUPDATE);
      setServerListData(DEFAULT_PREF_SERVERLIST_DATA);
      setServerListTimestamp(DEFAULT_PREF_SERVERLIST_TIMESTAMP);
      setRecentServerCity(DEFAULT_PREF_RECENTSERVER_CITY);
      setRecentServerCountry(DEFAULT_PREF_RECENTSERVER_COUNTRY);
      setRecentServerRegion(DEFAULT_PREF_RECENTSERVER_REGION);
      setRecentServerGroup(DEFAULT_PREF_RECENTSERVER_GROUP);
      setCompactMode(DEFAULT_PREF_SETTINGS_COMPACTMODE);
      setAutoConnectMode(DEFAULT_PREF_SETTINGS_AUTOCONNECTMODE);
      setAutoDisConnectMode(DEFAULT_PREF_SETTINGS_AUTODISCONNECTMODE);
      setRecentServerList(DEFAULT_PREF_RECENTSERVER_LIST);
      setRecentServerListLength(DEFAULT_PREF_RECENTSERVER_LIST_LENGTH);
      setTraceDebug(DEFAULT_PREF_SETTINGS_TRACEDEBUG);
      setTraceInit(DEFAULT_PREF_SETTINGS_TRACEINIT);
      setTraceCmd(DEFAULT_PREF_SETTINGS_TRACECMD);
      setLogfileActive(DEFAULT_PREF_SETTINGS_LOGFILE_ACTIVE);
      setLogfileName(DEFAULT_PREF_SETTINGS_LOGFILE_NAME);
      setCommandTimeout(DEFAULT_PREF_SETTINGS_COMMAMD_TIMEOUT);
      setConsoleActive(DEFAULT_PREF_SETTINGS_CONSOLE_ACTIVE);
      setMessageAutoclose(DEFAULT_PREF_SETTINGS_MESSAGE_AUTOCLOSE);
   }
}
