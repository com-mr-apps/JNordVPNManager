/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import org.geotools.swing.JMapFrame;

import com.mr.apps.JNordVpnManager.commandInterfaces.CallCommand;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.geotools.UtilMapGeneration;
import com.mr.apps.JNordVpnManager.gui.GuiMapArea;
import com.mr.apps.JNordVpnManager.gui.GuiMenuBar;
import com.mr.apps.JNordVpnManager.gui.GuiStatusLine;
import com.mr.apps.JNordVpnManager.gui.connectLine.GuiConnectLine;
import com.mr.apps.JNordVpnManager.gui.connectLine.JPanelConnectTimer;
import com.mr.apps.JNordVpnManager.gui.dialog.JAboutScreen;
import com.mr.apps.JNordVpnManager.gui.dialog.JCustomConsole;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JSplashScreen;
import com.mr.apps.JNordVpnManager.gui.serverTree.JServerTreePanel;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCommands;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnSettingsData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnStatusData;
import com.mr.apps.JNordVpnManager.utils.UtilLogErr;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * NordVPN user interface.
 *
 * <p>
 * This is a user interface for the NordVPN software.
 */
@SuppressWarnings("serial")
public class Starter extends JFrame 
{
   public static final String      COPYRIGHT_STRING           = "Copyright Ⓒ 2025 - written by com.mr.apps";
   public static final String      APPLICATION_DATA_DIR       = "/.local/share/JNordVpnManager"; // ..added to $HOME directory
   public static final String      SEP_DATARECORD             = ":";
   public static final String      SEP_DATAITEM               = ",";
   public static final Color       Color_Addon                = new Color(81,23,137);

   public static UtilLogErr        _m_logError                = null;

   private static final String     APPLICATION_TITLE          = "JNordVPN Manager [" + COPYRIGHT_STRING + "]";
   private static final String     APPLICATION_ICON_IMAGE     = "resources/icons/icon.png";

   private static JFrame           m_mainFrame                = null;
   private static JServerTreePanel m_serverListPanel          = null;
   private static JMapFrame        m_mapFrame                 = null;
   private static JAboutScreen     m_aboutScreen              = null;
   private static JSplashScreen    m_splashScreen             = null;
   private static GuiStatusLine    m_statusLine               = null;
   private static GuiConnectLine   m_connectLine              = null;
   private static JCustomConsole   m_consoleWindow            = null;

   private static String           m_nordvpnVersion           = "n/a";
   private static Cursor           m_applicationDefaultCursor = null;
   private static int              m_cursorChangeAllowed      = 0;  // counter for nested calls - 0 is allow
   private static boolean          m_skipWindowGainedFocus    = false;
   private static boolean          m_forceWindowGainedFocus   = false;
   private static boolean          m_installMode              = false;
   private static int              m_iConsoleActive           = 0;

   private static CurrentLocation  m_currentServer            = null;
   private static NvpnStatusData   m_nvpnStatusData           = null;
   private static NvpnSettingsData m_nvpnSettingsData         = null;
   private static NvpnAccountData  m_nvpnAccountData          = null;

   private static boolean          m_skipFocusGainedForDebug  = false;
   private static Timer            m_timer                    = null;
   private static String           m_AddOnLibVersion          = null;
   private static boolean          m_isSupporterEdition       = false;

   /**
    * NordVPN GUI application.
    * 
    */
   public static void main(String[] args) throws Exception
   {
      if (System.getenv("COM_MR_APPS_DEBUG") != null)
      {
         // ..for debug
         m_skipFocusGainedForDebug = true;
      }
      _m_logError = new UtilLogErr(UtilPrefs.getLogfileName(), null, null);

      ExecutorService processHandlers = Executors.newFixedThreadPool(3);
//      processHandlers.execute(() -> consoleWindowInit());
      consoleWindowInit();
      _m_logError.LoggingInfo("JNordVPN Manager launched...");

      // add add-ons classpath
      m_isSupporterEdition = CallCommand.initClassLoader(UtilPrefs.getAddonsPath());

      processHandlers.execute(() -> splashScreenInit());

      // wait the two windows to initialize
      m_timer = new Timer(100, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            if ((null != m_splashScreen) && (null != m_consoleWindow))
            {
               processHandlers.execute(() -> new Starter());
               m_timer.stop();
            }
            else
            {
               m_timer.start();
            }
         }
      });
      m_timer.start();
   }

   public Starter()
   {
      super();
      // get the application implementation version from jar manifest file
      Package p = getClass().getPackage();
      String version = StringFormat.printString(p.getImplementationVersion(), "n/a", "n/a") + ((getAddOnLibVersion() == null) ? "" : (" / AddOn Version: " + getAddOnLibVersion()));
      m_splashScreen.setVersion(version);

      // initialize the application
      init(version);
   }

   /**
    * Create and Display the Splash Screen
    */
   public static void splashScreenInit()
   {
      m_splashScreen = new JSplashScreen("JNordVPN Manager loading...");
      m_splashScreen.setVisible(true);
//      new Starter();
   }

   /**
    * Create the Console output Window
    */
   public static void consoleWindowInit()
   {
      m_consoleWindow = new JCustomConsole();

      // activate console
      m_iConsoleActive = UtilPrefs.isConsoleActive();
      if (1 == m_iConsoleActive)
      {
         // open the console at program start
         switchConsoleWindow();
      }
   }


   /**
    * Method to set the flag to skip "WindowGainedFocus" event for main application<p>
    * The Event is even fired, if a message window closes. It is only required, if we re-enter from another application.
    */
   public static void setSkipWindowGainedFocus()
   {
      Starter._m_logError.TraceDebug("Set m_skipWindowGainedFocus=true");
      m_skipWindowGainedFocus = true;
   }

   /**
    * Method to set the flag to force "WindowGainedFocus" event for main application<p>
    * Force the Event to execute from the application, to update the GUI.
    */
   public static void setForceWindowGainedFocus()
   {
      Starter._m_logError.TraceDebug("Set m_forceWindowGainedFocus=true");
      m_forceWindowGainedFocus = true;
   }

   /**
    * Allow/Deny wait cursor change.
    * <p>
    * If we execute commands in a row, we want to change the cursor only once at beginning and back once at end.
    * 
    * @param allow
    *           is true [default] to allow a cursor change
    */
   public static void setCursorCanChange(boolean allow)
   {
      m_cursorChangeAllowed = (allow) ? --m_cursorChangeAllowed : ++m_cursorChangeAllowed;
   }

   /**
    * Set a wait cursor for the application frame
    */
   public static void setWaitCursor()
   {
      if (0 != m_cursorChangeAllowed) return;
      if (null == m_applicationDefaultCursor)
      {
         m_applicationDefaultCursor = m_mainFrame.getCursor();
      }
      Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
      m_mainFrame.setCursor(waitCursor);
      return;
   }
   /**
    * Reset the application frame cursor
    */
   public static void resetWaitCursor()
   {
      if (0 != m_cursorChangeAllowed) return;
      if (null == m_applicationDefaultCursor)
      {
         m_applicationDefaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
      }
      m_mainFrame.setCursor(m_applicationDefaultCursor);
      return;
   }

   /**
    * Cleanup and exit.
    * @param quiet if true, suppress confirm dialog
    */
   public static void cleanupAndExit(boolean quiet)
   {
      // check for automatic disconnect at program end
      int iAutoDisConnect = UtilPrefs.getAutoDisConnectMode();

      // check if paused
      if ((0 == iAutoDisConnect) && (JPanelConnectTimer.getTimerWorkMode() == GuiStatusLine.STATUS_PAUSED))
      {
         // paused
         if (JModalDialog.showConfirm("Your VPN connection is paused. If you quit the application,\nit will not be reconnected automatically.\n\nAre you sure you want to quit?") == JOptionPane.YES_OPTION)
         {
            // exit confirmed
            cleanUp();
            _m_logError.LoggingInfo("... exit JNordVPN Manager.");
            System.exit(0);
         }
      }
      else
      {
         // not paused
         if (quiet || JModalDialog.showConfirm("Are you sure you want to quit?") == JOptionPane.YES_OPTION)
         {
            if (1 == iAutoDisConnect)
            {
               CurrentLocation loc = getCurrentServer(false);
               if ((null != loc) && loc.isConnected())
               {
                  NvpnCallbacks.executeDisConnect(null, null);
               }
            }

            cleanUp();
            _m_logError.LoggingInfo("... exit JNordVPN Manager.");

            // exit program with last error code (or 0)
            System.exit(_m_logError.getLastErrCode());
         }
      }
   }

   /**
    * Cleanup on exit.
    * <p>
    * <ul>
    * <it>Delete temporary created files</it>
    * <it>Save current settings</it>
    * </ul>
    */
   static void cleanUp()
   {
      // delete temporary map files
      UtilMapGeneration.cleanUp();
      
      if (m_splashScreen.getProgress() < 100)
      {
         // close an open [in case of initialization error] splash screen
         m_splashScreen.setProgress(100);
      }
   }

   /**
    * Initialize the application.
    */
   private void init(String version)
   {
      _m_logError.LoggingInfo("GUI Version: " + version);

      // main frame
      m_mainFrame = this;
      m_mainFrame.setLayout(new BorderLayout());
      m_mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      m_mainFrame.setTitle(APPLICATION_TITLE);

      // Close Window with "X"
      m_mainFrame.addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            cleanupAndExit(true);  // exit without requester
         }
      });

      m_mainFrame.addWindowFocusListener(new WindowFocusListener() {
         @Override
         public void windowGainedFocus(WindowEvent e) {
            if (e.getOppositeWindow()==null)
            {
               // gain focus -> update GUI with current data
               if (!m_forceWindowGainedFocus && (m_skipWindowGainedFocus || m_skipFocusGainedForDebug))
               {
                  // Flag is set from the internal message dialogs. On close [re-enter in main application] I don't need an update
                  m_skipWindowGainedFocus = false;
                  _m_logError.TraceDebug("windowGainedFocus skipped");
               }
               else
               {
                  m_forceWindowGainedFocus = false;
                  m_skipWindowGainedFocus = false;
                  if (false == m_installMode)
                  {
                     _m_logError.TraceDebug("windowGainedFocus launched");
                     setWaitCursor();
                     setCursorCanChange(false);

                     m_nvpnSettingsData = new NvpnSettingsData();
                     // update dependent GUI elements based on current account data and 
                     updateAccountData(true);
                     // update the status line, commands menu and world map current server layer
                     updateCurrentServer();
                     // update the server tree (and world map all servers layer)
                     updateFilterTreeCB(false);

                     Starter.setCursorCanChange(true);
                     Starter.resetWaitCursor();
                  }
               }
            }
         }

         @Override
         public void windowLostFocus(WindowEvent e) {
            if (e.getOppositeWindow()==null)
            {
               // lose focus...
               _m_logError.TraceDebug("windowLostFocus");
            }
         }
      });

      // set application icon in Task bar
      URL appIconUrl = Starter.class.getResource(APPLICATION_ICON_IMAGE);
      ImageIcon imageIcon = new ImageIcon(appIconUrl);
      Image appImage = imageIcon.getImage();
      try
      {
         if (true == Taskbar.isTaskbarSupported())
         {
            Taskbar.getTaskbar().setIconImage(appImage);
         }
         else
         {
            _m_logError.TraceDebug("The current platform does not support the Taskbar.Feature.ICON_IMAGE feature!");
         }
      }
      catch (Exception e)
      {
         _m_logError.TraceDebug("Exception from set application taskbar icon!");
      }
      m_mainFrame.setIconImage(appImage);

      // -------------------------------------------------------------------------------
      // Check, if Nordvpn is installed
      // -------------------------------------------------------------------------------
      if (!NvpnCommands.isInstalled())
      {
         // TranslatorAbend() calls cleanupAndExit()
         _m_logError.LoggingAbend(10998,
               "Backend NordVPN not installed.",
               "'nordvpn' command not found.\nCheck installation of NordVPN!\n\nExit program.");
      }

      m_splashScreen.setProgress(10);

      // -------------------------------------------------------------------------------
      // If we are called from snap...: 'strict' doesn't let us execute the 'nordvpn' command outside the snap
      // -> 'Installer mode' to install a local desktop file that runs the jar directly (outside of the snap container)
      // -------------------------------------------------------------------------------
      String usrHome = System.getProperty("user.home");
      String myHome = System.getenv("SNAP_REAL_HOME");
      if ((null != myHome) && (false == myHome.equals(usrHome))) // requires additional check for debug.. because eclipse is a snap and sets the SNAP_* variables..
      {
         // we run the jar from the snap installation
         m_splashScreen.setStatus("Installation...");
         _m_logError.TraceDebug("JNordVPN Manager called in installation mode (in the snap environment)");
         m_installMode = true;
         _m_logError.enableTraceFlag(UtilLogErr.TRACE_Init);
         _m_logError.enableTraceFlag(UtilLogErr.TRACE_Cmd);

         // open the console window (if not already open)
         if (0 == m_iConsoleActive) switchConsoleWindow();

         // check if the local desktop file already exists (in the real home directory!)
         String targetFile = myHome + "/Desktop/JNordVpnManager_Java.desktop";

         // ask, if we should copy the desktop file to the local desktop...
         if (JModalDialog.showConfirm("JNordVPN Manager (install) called.\n" +
               "To run the application you need an actual 'JNordVPNManager_Java.desktop' file in your local '~/Desktop directory', or execute the command:\n" +
               "/snap/j-nordvpn-manager/current/bin/java -jar /snap/j-nordvpn-manager/current/JNordVpnManager-current.jar.\n\n" +
               "Do you want to install the JNordVPNManager_Java.desktop file in your ~/Desktop directory to launch the application?") == JOptionPane.YES_OPTION)
         {
            // yes - copy the desktop file
            try
            {
               UtilSystem.CopyTextFile("/snap/j-nordvpn-manager/current/Desktop/JNordVpnManager_Java.desktop",
                     targetFile,
                     "UTF-8",
                     true);
            }
            catch (IOException e1)
            {
               // we don't exit to give the user the chance to access the console
               _m_logError.LoggingExceptionMessage(4, 10901, e1);
            }
         }

         File fTargetFile = new File(targetFile);
         if (fTargetFile.canRead())
         {
            // desktop file exists
            _m_logError.TraceIni("...'JNordVPNManager_Java.desktop' file found in '~/Desktop directory'.");
            if (JModalDialog.showConfirm("JNordVPNManager (install).\n" +
                  "Please restart the application by the 'JNordVPNManager_Java.desktop' file found in your '~/Desktop directory'.\n\n" +
                  "If you continue direct from Snap, the application has no permission to execute any 'nordvpn' command, but you have access to the console and Info menus to check messages and errors.\n\n" +
                  "Please confirm to exit the program.") == JOptionPane.YES_OPTION)
            {
               // yes - exit
               cleanupAndExit(true);
            }
         }
         else
         {
            _m_logError.TraceIni("...'JNordVPNManager_Java.desktop' file not found in '~/Desktop directory'.");
            if (JModalDialog.showConfirm("JNordVPNManager (install).\n" +
                  "If you continue direct from Snap, the application has no permission to execute any 'nordvpn' command, but you have access to the console and Info menus to check messages and errors.\n\n" +
                  "Please confirm to exit the program.") == JOptionPane.YES_OPTION)
            {
               // yes - exit
               cleanupAndExit(true);
            }
         }
         _m_logError.TraceDebug("...continue execution in 'installer mode'...");
      }

      // -------------------------------------------------------------------------------
      // Get the NordVPN version for handle supported features
      // -------------------------------------------------------------------------------
      m_nordvpnVersion = "n/a";
      String errorGetVersion = null;
      String[] saVersions = NvpnCommands.getVersion();
      if (UtilSystem.isLastError())
      {
         // could not get the version
         errorGetVersion = UtilSystem.getLastError();
      }
      if (null != saVersions && null != saVersions[0])
      {
         Pattern pattern = Pattern.compile("^.*(\\d+\\.\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);
         Matcher matcher = pattern.matcher(saVersions[0]);
         boolean matchFound = matcher.find();
         if (matchFound)
         {
            m_nordvpnVersion = matcher.group(1);
         }
      }
      if ((null != errorGetVersion) && (false == m_installMode))
      {
         JModalDialog.showError("NordVPN Version error",
               errorGetVersion + "\n" + StringFormat.printString(saVersions[0], "") + "\n" + StringFormat.printString(saVersions[1], ""));
      }
      _m_logError.TraceIni("NordVPN version=" + m_nordvpnVersion + ".");

      //-------------------------------------------------------------------------------
      // Status bar Panel
      //-------------------------------------------------------------------------------
      m_statusLine = new GuiStatusLine();
      JPanel statusPanel = m_statusLine.create();

      // -------------------------------------------------------------------------------
      // Get NordVPN account and settings data 
      // -------------------------------------------------------------------------------
      if ((null == errorGetVersion) && (false == m_installMode))
      {
         m_splashScreen.setProgress(20);
         m_splashScreen.setStatus("Get NordVPN Information...");

         m_nvpnAccountData = new NvpnAccountData();
         m_nvpnSettingsData = new NvpnSettingsData();
         m_nvpnStatusData = new NvpnStatusData();
      }

      boolean bConnected = false;
      if ((null != m_nvpnSettingsData) && (null != m_nvpnStatusData))
      {
         // initialize current group from User Preferences
         bConnected = m_nvpnStatusData.isConnected();
         if (bConnected)
         {
            // we don't have information about the correct group for an already connected server, so we cannot set the current group for sure
            // only for obfuscated we can assume, that the selected group may be obfuscated..
            // !!! The GUI can confuse, because the displayed legacy group (filter) may not be correct for the current connection!
            // !!! OK only, after [re]connection in the GUI -> check status line text message 

            // check settings for obfuscated and correct the server tree legacy group filter
            NordVPNEnumGroups currentGroup = NordVPNEnumGroups.get(UtilPrefs.getRecentServerGroup());
            boolean bObfuscate = StringFormat.string2boolean(m_nvpnSettingsData.getObfuscate(false));
            if (bObfuscate && (false == currentGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers)))
            {
               // we can switch to legacy group obfuscated
               UtilPrefs.setRecentServerGroup(NordVPNEnumGroups.legacy_obfuscated_servers.getId());
               // NvpnGroups.setCurrentGroup(NordVPNEnumGroups.legacy_obfuscated_servers); ! not sure, if settings were changed w/o reconnect before GUI start
            }
            _m_logError.TraceIni("Existing connection to server: " + m_nvpnStatusData.getServer());
            _m_logError.TraceDebug("GUI doesn't has information about the legacy_group until a [re]connection in the application!");
         }

         // initialize current region from (corrected) User Preferences
         NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.get(UtilPrefs.getRecentServerGroup()));

         // initialize current region from User Preferences
         NvpnGroups.setCurrentFilterRegion(NordVPNEnumGroups.get(UtilPrefs.getRecentServerRegion()));
      }

      m_splashScreen.setProgress(30);
      m_splashScreen.setStatus("Create Commands ToolBar...");

      //-------------------------------------------------------------------------------
      // Connect Panel
      //-------------------------------------------------------------------------------
      m_connectLine = new GuiConnectLine();
      JPanel connectPanel = m_connectLine.create(m_nvpnAccountData);

      //-------------------------------------------------------------------------------
      // If we are logged in, optionally [auto]connect
      //-------------------------------------------------------------------------------
      if (((null != m_nvpnAccountData) && (true == m_nvpnAccountData.isLoggedIn()) && (false == bConnected)))
      {
         int iAutoConnect = UtilPrefs.getAutoConnectMode();
         if (1 == iAutoConnect)
         {
            m_splashScreen.setProgress(35);
            CurrentLocation loc = getCurrentServer(true);
            if (null != loc)
            {
               m_splashScreen.setStatus("GUI Auto Connect to " + loc.getToolTip());
               if (true == NvpnCallbacks.executeConnect(loc, null, "JNordVPN Manager Auto Connect"))
               {
                  // successfully connected
                  bConnected = true;
               }
            }
         }
      }
/*
      if (!bConnected)
      {
         GuiStatusLine.updateStatusLine(STATUS_DISCONNECTED, "Not logged in to NordVPN Service.");
      }
*/

      m_splashScreen.setProgress(40);
      m_splashScreen.setStatus("Create World Map...");

      //-------------------------------------------------------------------------------
      // Initialize Map Area
      // -------------------------------------------------------------------------------
      GuiMapArea mapArea = new GuiMapArea();
      // Create a map content and add our shape file (with locations) to it
      m_mapFrame = mapArea.create();
      
      m_splashScreen.setProgress(50);
      m_splashScreen.setStatus("Create Server list...");

      //-------------------------------------------------------------------------------
      // Server Location Selection Tree
      //-------------------------------------------------------------------------------
      m_serverListPanel = new JServerTreePanel();

      m_splashScreen.setProgress(60);
      m_splashScreen.setStatus("Create Layout...");

      //-------------------------------------------------------------------------------
      // Menu bar (after JServerTreePanel!)
      //-------------------------------------------------------------------------------
      GuiMenuBar myMenuBar = new GuiMenuBar();
      JMenuBar menubar = myMenuBar.create(m_nvpnAccountData);
      m_mainFrame.setJMenuBar(menubar);

      m_splashScreen.setProgress(70);

      //-------------------------------------------------------------------------------
      // initialize data dependent GUI elements based on current (valid) account data
      //-------------------------------------------------------------------------------
      updateAccountData(false);

      //-------------------------------------------------------------------------------
      // main frame layout
      //-------------------------------------------------------------------------------
      m_mainFrame.add(connectPanel, BorderLayout.PAGE_START);
      m_mainFrame.add(m_serverListPanel, BorderLayout.LINE_START);
      if (null != m_mapFrame) m_mainFrame.add(m_mapFrame.getContentPane(), BorderLayout.CENTER);
      m_mainFrame.add(statusPanel, BorderLayout.PAGE_END);

      m_aboutScreen  = new JAboutScreen(version);

      m_splashScreen.setProgress(90);
      m_splashScreen.setStatus("Finalize...");

      //-------------------------------------------------------------------------------
      // display main frame
      //-------------------------------------------------------------------------------
      int compactMode = (m_installMode) ? 1 : UtilPrefs.getCompactMode();
      switchCompactMode(compactMode); // calls pack() and sets minimum size

      // Center the Frame
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension panelSize = m_mainFrame.getSize();
      m_mainFrame.setLocation((screenSize.width / 2) - (panelSize.width / 2), (screenSize.height / 2) - (panelSize.height / 2));

//      m_mainFrame.setExtendedState(Frame.ICONIFIED);
      m_mainFrame.setVisible(true);

      m_splashScreen.setProgress(100); // ..and close the splash screen
      updateCurrentServer();

      _m_logError.TraceIni("**********************************************************************************\n"
                         + "Finished Initialization.\n"
                         + "**********************************************************************************\n");
   }

   /**
    * Get the current or recent server location<p>
    * For static Connection (recent list, autostart, reconnect) - Filter Group, Technology, Protocol from location object.<br>
    * For Dynamic connection (Tree/Map selection) - Filter Group, Technology, Protocol from current GUI and VPN settings.
    * 
    * @param bStatic
    *           is <code>true</code> for FilterGroup (Obfuscate handling) and Technology/Protocol set fix (static) in
    *           the location object
    * @return the current or recent location
    */
   public static CurrentLocation getCurrentServer(boolean bStatic)
   {
      // first check, if we are logged in
      if (null == m_nvpnAccountData || false == m_nvpnAccountData.isLoggedIn()) return null;

      CurrentLocation loc = null;

      int legacyGroup = NordVPNEnumGroups.legacy_group_unknown.getId();
      String sVpnTechnology = null;
      String sVpnProtocol = null;
      if (null == m_currentServer)
      {
         // generate current server location data from user preferences (last valid connection)
         String city = UtilPrefs.getRecentServerCity();
         String country = UtilPrefs.getRecentServerCountry(); // countryName,group,technology,protocol
         String[] saParts = country.split(",");
         String countryName = (saParts.length == 4) ? saParts[0] : country;
         loc = new CurrentLocation(UtilLocations.getLocation(city, countryName));
         loc.setConnected(false);
         if (saParts.length == 4)
         {
            // get (optional) connection data from preferences 'server@country,group,technology,protocol' and add them to loc
            int iLegacyGroup = Integer.valueOf(saParts[1]);
            legacyGroup = (iLegacyGroup == NordVPNEnumGroups.legacy_group_unknown.getId()) ? NvpnGroups.getCurrentFilterGroup().getId() : iLegacyGroup;
            sVpnTechnology = saParts[2];
            sVpnProtocol = saParts[3];
         }
      }
      else
      {
         // generate current server location data as copy from current location
         boolean bIsConnected = m_currentServer.isConnected();
         loc = new CurrentLocation(m_currentServer);
         loc.setConnected(bIsConnected);
         legacyGroup = m_currentServer.getLegacyGroup();
         sVpnTechnology = m_currentServer.getVpnTechnology();
         sVpnProtocol = m_currentServer.getVpnProtocol();
      }

      if (bStatic)
      {
         // static (use Data for connection from current location)
         loc.setLegacyGroup((legacyGroup == NordVPNEnumGroups.legacy_group_unknown.getId()) ? NvpnGroups.getCurrentFilterGroup().getId() : legacyGroup);
         loc.setVpnTechnology(sVpnTechnology);
         loc.setVpnProtocol(sVpnProtocol);
      }
      else
      {
         // dynamic (use Data for connection from current GUI Filter - and - settings)
         loc.setLegacyGroup(null);
         loc.setVpnTechnology(null);
         loc.setVpnProtocol(null);
      }
      
      Starter._m_logError.TraceDebug("(getCurrentServer) loc=" + loc);
      return loc;

      // return only a "real" location
   }

   public static void updateStatusLine()
   {
      m_currentServer = m_statusLine.update(m_nvpnStatusData);
   }

   /**
    * Update current server data.
    * <p>
    * Update the current server and connection status based on the command 'nordvpn status'.
    * <ul>
    * <li>Get the real current connected server (if not connected we set the current server to the last connected)</it>
    * <li>Update the status line</it>
    * <li>Update the server tree and world map with current server</it>
    * <li>Update menu buttons dependent from status data</it>
    * </ul>
    * Remark: Login/Logout status is not handled here. In case of logout, status is disconnected.
    * 
    * @return the current connection status (true == connected)
    */
   public static boolean updateCurrentServer()
   {
      if ((true == m_installMode) || (m_splashScreen.getProgress() < 100)) return false;

      // get the current connected server from the "nordvpn status" command
      m_nvpnStatusData = new NvpnStatusData();
      updateStatusLine();
      setTreeFilterGroup();

      if (null != m_currentServer && m_currentServer.isConnected())
      {
         // Connected
         _m_logError.TraceDebug("Update Current active Server: " + m_currentServer.toString());

         // Update preferences with current connected server
         UtilPrefs.setRecentServerCity(m_currentServer.getCityName());
         UtilPrefs.setRecentServerCountry(m_currentServer.getLocationConnectionData());

         // Update the current server map layer and zoom there
         UtilMapGeneration.changeCurrentServerMapLayer(m_currentServer);
      }
      else if (null == m_currentServer)
      {
         // Disconnected

         // get the server (not active) from the previous session/connection (preferences) !!! can be null !!!
         m_currentServer = getCurrentServer(true);

         // Update (remove) the current server map layer
         UtilMapGeneration.changeCurrentServerMapLayer(null);
         // ... and zoom to the last active location
         UtilMapGeneration.zoomIn(m_currentServer);
      }

      // .. place the tree to the country
      JServerTreePanel.activateTreeNode(m_currentServer);

      // ... Update the commands Menu
      GuiMenuBar.updateMenuButtons(m_currentServer);
      
      return (null == m_currentServer) ? false : m_currentServer.isConnected();
   }

   /**
    * Update the current valid account data and depending GUI elements.
    * 
    * @param update
    *           is true, if the current account data is not valid and needs to be updated.
    */
   public static void updateAccountData(boolean update)
   {
      if (null == m_nvpnAccountData || update) getCurrentAccountData(update);

      GuiMenuBar.updateAccountReminder();
      GuiMenuBar.updateLoginLogout(m_nvpnAccountData);
      GuiConnectLine.updateLoginLogout(m_nvpnAccountData);
   }

   /**
    * Switch between Compact and Expanded Manager View
    * @param mode is 1 for Compact mode, 0 for Expanded mode.
    */
   public static void switchCompactMode(int mode)
   {
      if (0 == mode)
      {
         // show map and tree panels
         m_serverListPanel.setVisible(true);
         if (null != m_mapFrame) m_mapFrame.getContentPane().setVisible(true);
         m_mainFrame.setMinimumSize(new Dimension (800, 400));
         m_mainFrame.pack();
      }
      else
      {
         // hide map and tree panels
         m_serverListPanel.setVisible(false);
         if (null != m_mapFrame) m_mapFrame.getContentPane().setVisible(false);
         m_mainFrame.setMinimumSize(new Dimension (800, 80));
         m_mainFrame.pack();
      }
   }

   public static void setTreeFilterGroup()
   {
      if (null != m_serverListPanel)
      {
         m_serverListPanel.setTreeFilterGroup();
      }
   }

   public static void updateFilterTreeCB(boolean update)
   {
      m_serverListPanel.updateFilterTreeCB(update);
   }

   public static void showAboutScreen()
   {
      m_aboutScreen.show();
   }

   public static boolean switchConsoleWindow()
   {
      return m_consoleWindow.switchConsoleVisible();
   }
   
   public static JFrame getMainFrame()
   {
      return m_mainFrame;
   }

   public static NvpnStatusData getCurrentStatusData()
   {
      if (null == m_nvpnStatusData) m_nvpnStatusData = new NvpnStatusData(); 
      return m_nvpnStatusData;
   }

   public static NvpnStatusData refreshCurrentStatusData()
   {
      m_nvpnStatusData = new NvpnStatusData(); 
      return m_nvpnStatusData;
   }

   public static NvpnSettingsData getCurrentSettingsData()
   {
      if (null == m_nvpnSettingsData) m_nvpnSettingsData = new NvpnSettingsData(); 
      return m_nvpnSettingsData;
   }

   public static NvpnSettingsData refreshCurrentSettingsData()
   {
      m_nvpnSettingsData = new NvpnSettingsData(); 
      return m_nvpnSettingsData;
   }

   public static NvpnAccountData getCurrentAccountData(boolean update)
   {
      if (null == m_nvpnAccountData || update) m_nvpnAccountData = new NvpnAccountData(); 
      return m_nvpnAccountData;
   }

   public static void setAddOnLibVersion(String addOnLibVersion)
   {
      m_AddOnLibVersion = addOnLibVersion;
   }

   public static String getAddOnLibVersion()
   {
      return m_AddOnLibVersion;
   }

   public static boolean isSupporterEdition()
   {
      return m_isSupporterEdition;
   }

   public static boolean isInstallMode()
   {
      return m_installMode;
   }
}
