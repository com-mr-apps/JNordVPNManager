/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.geotools.swing.JMapFrame;

import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.geotools.UtilMapGeneration;
import com.mr.apps.JNordVpnManager.gui.AboutScreen;
import com.mr.apps.JNordVpnManager.gui.GuiCustomConsole;
import com.mr.apps.JNordVpnManager.gui.GuiMapArea;
import com.mr.apps.JNordVpnManager.gui.GuiMenuBar;
import com.mr.apps.JNordVpnManager.gui.GuiStatusLine;
import com.mr.apps.JNordVpnManager.gui.SplashScreen;
import com.mr.apps.JNordVpnManager.gui.connectLine.GuiConnectLine;
import com.mr.apps.JNordVpnManager.gui.connectLine.JPauseSlider;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.gui.serverTree.JServerTreePanel;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCommands;
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
public class Starter
{
   public static final UtilLogErr  _m_logError                = new UtilLogErr(UtilPrefs.getLogfileName(), null, null);

   public static final int         STATUS_UNKNOWN             = -1;
   public static final int         STATUS_CONNECTED           = 0;
   public static final int         STATUS_PAUSED              = 1;
   public static final int         STATUS_DISCONNECTED        = 2;

   private static final String     APPLICATION_ICON_IMAGE     = "resources/icons/icon.jpg";

   private static JFrame           m_mainFrame                = null;
   private static JServerTreePanel m_serverListPanel          = null;
   private static JMapFrame        m_mapFrame                 = null;
   private static AboutScreen      m_aboutScreen              = null;
   private static SplashScreen     m_splashScreen             = null;
   private static GuiStatusLine    m_statusLine               = null;
   private static GuiConnectLine   m_connectLine              = null;
   private static GuiCustomConsole m_consoleWindow            = null;

   private static String           m_nordvpnVersion;
   private static Cursor           m_applicationDefaultCursor = null;
   private static boolean          m_cursorChangeAllowed      = true;
   private static boolean          m_skipWindowGainedFocus    = false;

   private static CurrentLocation  m_currentServer            = null;

   /**
    * NordVPN GUI application.
    * 
    */
   public static void main(String[] args) throws Exception
   {
      _m_logError.TranslatorInfo("JNordVPN Manager launched...");
 
     splashScreenInit();
     SwingUtilities.invokeLater(() -> new Starter());
   }

   public Starter()
   {
      // get the application implementation version from jar manifest file
      Package p = getClass().getPackage();
      String version = StringFormat.printString(p.getImplementationVersion(), "<n/a>", "<n/a>");
      m_splashScreen.setVersion(version);

      // initialize the application
      init(version);
   }

   /**
    * Create and Display the Splash Screen
    */
   public static void splashScreenInit()
   {
      m_splashScreen = new SplashScreen("JNordVPN Manager loading...");
      m_splashScreen.show();
   }

   /**
    * Method to set the flag to skip "WindowGainedFocus" event for main application<p>
    * The Event is even fired, if a message window closes. It is only required, if we re-enter from another application.
    */
   public static void setSkipWindowGainedFocus()
   {
      m_skipWindowGainedFocus = true;
   }

   /**
    * Allow/Deny wait cursor change.
    * <p>
    * If we execute commands in a row, we want to change the cursor only once at beginning and back once at end.
    * @param allow is true [default] to allow a cursor change
    */
   public static void setCursorCanChange(boolean allow)
   {
      m_cursorChangeAllowed = allow;
   }

   /**
    * Set a wait cursor for the application frame
    */
   public static void setWaitCursor()
   {
      if (!m_cursorChangeAllowed) return;
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
      if (!m_cursorChangeAllowed) return;
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
      String pauseMsg = JPauseSlider.syncStatusForPause(Starter.STATUS_DISCONNECTED);
      if ((0 == iAutoDisConnect) && (null != pauseMsg))
      {
         // paused
         if (JModalDialog.showConfirm("Your VPN connection is paused. If you quit the application, it will not be reconnected automatically.\nAre you sure you want to quit?") == JOptionPane.YES_OPTION)
         {
            // exit confirmed
            cleanUp();
            _m_logError.TranslatorInfo("... exit JNordVPN Manager.");
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
               CurrentLocation loc = getCurrentServer();
               if ((null != loc) && loc.isConnected())
               {
                  NvpnCallbacks.executeDisConnect();
               }
            }

            cleanUp();
            _m_logError.TranslatorInfo("... exit JNordVPN Manager.");

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
      
      // TODO: save current UtilPrefs settings

      updateServer(); // update recent server settings
      
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
      // main frame
      m_mainFrame = new JFrame();
      m_mainFrame.setLayout(new BorderLayout());
      m_mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      m_mainFrame.setTitle("JNordVPN Manager [Copyright Ⓒ 2024 - written by com.mr.apps]");
      m_mainFrame.setLocationRelativeTo(null);

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
               _m_logError.TraceDebug("windowGainedFocus");
               if (m_skipWindowGainedFocus)
               {
                  // Flag is set from the internal message dialogs. On close [re-enter in main application] I don't need an update
                  m_skipWindowGainedFocus = false;
               }
               else
               {
                  updateServer();
                  NvpnAccountData accountData = new NvpnAccountData();
                  GuiMenuBar.updateLoginOut(accountData);
                  GuiConnectLine.updateLoginOut(accountData);            
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
      ImageIcon imageIcon = new ImageIcon(Starter.class.getResource(APPLICATION_ICON_IMAGE));
      Image myImage = imageIcon.getImage();
      if (true == Taskbar.isTaskbarSupported())
      {
         Taskbar.getTaskbar().setIconImage(myImage);
      }
      m_mainFrame.setIconImage(myImage); // TODO: don't work...

      /*
       * Check, if Nordvpn is installed
       */
      if (!NvpnCommands.isInstalled())
      {
         // TranslatorAbend() calls cleanupAndExit()
         _m_logError.TranslatorAbend(10998,
               "Backend NordVPN not installed.",
               "'nordvpn' command not found.\nCheck installation of NordVPN!\n\nExit program.");
      }

      // activate console
      m_consoleWindow = new GuiCustomConsole();
      _m_logError.TranslatorInfo("GUI Version: " + version);
      m_splashScreen.setProgress(10);

      /* 
       * Get the NordVPN version for handle supported features
       */
      String errMsg = null;
      String[] saVersions = NvpnCommands.getVersion();
      if (saVersions[0] == null /* && UtilSystem.isLastError() */)
      {
         errMsg = UtilSystem.getLastError();
      }
      else
      {
         Pattern pattern = Pattern.compile("^.*(\\d+\\.\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);
         Matcher matcher = pattern.matcher(saVersions[0]);
         boolean matchFound = matcher.find();
         if (matchFound)
         {
            m_nordvpnVersion = matcher.group(1);
         }
         else
         {
            errMsg = "Invalid formatted version string=" + saVersions[0] + "<.";
         }
      }
      if (null != errMsg)
      {
         _m_logError.TranslatorError(10000,
               "NordVPN Version error",
               errMsg);
         m_nordvpnVersion = "";
      }
      _m_logError.TraceIni("NordVPN version=" + m_nordvpnVersion + "<.");

      /*
       * get NordVPN account information
       */
      new NvpnAccountData();

      //-------------------------------------------------------------------------------
      // Initialize Server Locations
      UtilLocations.initCsvLocations();

      m_splashScreen.setProgress(20);

      //-------------------------------------------------------------------------------
      // Menu bar
      GuiMenuBar myMenuBar = new GuiMenuBar();
      JMenuBar menubar = myMenuBar.create();
      m_mainFrame.setJMenuBar(menubar);

      //-------------------------------------------------------------------------------
      // Connect Panel
      m_connectLine = new GuiConnectLine();
      JPanel connectPanel = m_connectLine.create();

      m_splashScreen.setProgress(30);

      //-------------------------------------------------------------------------------
      // Status bar Panel
      m_statusLine = new GuiStatusLine();
      JPanel statusPanel = m_statusLine.create();

      m_splashScreen.setProgress(40);
      m_splashScreen.setStatus("Create World Map...");

      //-------------------------------------------------------------------------------
      // Map Area
      GuiMapArea mapArea = new GuiMapArea();
      // Create a map content and add our shape file (with locations) to it
      m_mapFrame = mapArea.create();
      
      m_splashScreen.setProgress(50);
      m_splashScreen.setStatus("Create Server list...");

      //-------------------------------------------------------------------------------
      // Server Location Area
      m_serverListPanel = new JServerTreePanel();
      m_serverListPanel.setPreferredSize(new Dimension(255, 400));

      m_splashScreen.setProgress(70);
      m_splashScreen.setStatus("Create Layout...");

      //-------------------------------------------------------------------------------
      // main frame layout
      m_mainFrame.add(connectPanel, BorderLayout.PAGE_START);
      m_mainFrame.add(m_serverListPanel, BorderLayout.LINE_START);
      m_mainFrame.add(m_mapFrame.getContentPane(), BorderLayout.CENTER);
      m_mainFrame.add(statusPanel, BorderLayout.PAGE_END);

      m_aboutScreen  = new AboutScreen(version);

      m_splashScreen.setProgress(80);
      m_splashScreen.setStatus("Update Status...");

      boolean bConnected = updateServer(); // Update the Status to know if we are already connected

      int iAutoConnect = UtilPrefs.getAutoConnectMode();
      if (false == bConnected && 1 == iAutoConnect)
      {
         m_splashScreen.setProgress(90);
         CurrentLocation loc = getCurrentServer();
         if (null != loc)
         {
            m_splashScreen.setStatus("Connect to " + ((Location)loc).toString());
            NvpnCallbacks.executeConnect(loc);
         }
      }

      m_splashScreen.setProgress(100); // ..and close it

      //-------------------------------------------------------------------------------
      // display main frame
      int compactMode = UtilPrefs.getCompactMode();
      switchCompactMode(compactMode); // calls pack() and sets minimum size
      m_mainFrame.setVisible(true);

      _m_logError.TraceIni("**********************************************************************************\n"
                         + "Finished Initialization.\n"
                         + "**********************************************************************************\n");
   }

   /**
    * Get the current or recent server location
    * @return the current or recent location
    */
   public static CurrentLocation getCurrentServer()
   {
      if (null == m_currentServer)
      {
         String city = UtilPrefs.getRecentCity();
         String country = UtilPrefs.getRecentCountry();
         CurrentLocation loc = new CurrentLocation(UtilLocations.getLocation(city, country));
         loc.setConnected(false);
         // return only a "real" location
         return (loc.getNumber() == 0) ? null : loc;
      }
      return m_currentServer;
   }

   /**
    * Update current server data.
    * <p>
    * Update the current server and connection status based on the command 'nordvpn status'.
    * <ul>
    * <li>Get the real current connected server (if not connected we set the current server to the last connected)</it>
    * <li>Update the status line</it>
    * <li>Update the server tree and world map with current server</it>
    * <li>Update menu buttons dependent from connection status</it>
    * </ul>
    * Remark: Login/Logout status is not handled here. In case of logout, status is disconnected.
    * 
    * @return the current connection status (true == connected)
    */
   public static boolean updateServer()
   {
      if (null == m_statusLine) return false;

      // get the current connected server from the "nordvpn status" command
      CurrentLocation loc = m_statusLine.update();
      m_currentServer = loc;
 
      if (null != m_currentServer && m_currentServer.isConnected())
      {
         // Connected
         _m_logError.TraceDebug("Update Current active Server=" + m_currentServer.toString() + "<.");

         // Update preferences with current connected server
         UtilPrefs.setRecentCountry(m_currentServer.getCountry());
         UtilPrefs.setRecentCity(m_currentServer.getCity());

         // Update the current server map layer and zoom there
         UtilMapGeneration.changeCurrentServerMapLayer(m_currentServer);
      }
      else if (null == m_currentServer)
      {
         // Disconnected

         // get the server (not active) from the previous session/connection (preferences) !!! can be null !!!
         m_currentServer = getCurrentServer();

         // Update (remove) the current server map layer
         UtilMapGeneration.changeCurrentServerMapLayer(null);
      }

      // ... zoom there
      UtilMapGeneration.zoomIn(m_currentServer);

      // .. place the tree to the country
      JServerTreePanel.activateTreeNode(m_currentServer);

      // ... Update the commands Menu
      GuiMenuBar.updateMenuButtons(m_currentServer);
      
      return (null == m_currentServer) ? false : m_currentServer.isConnected();
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
         m_mapFrame.getContentPane().setVisible(true);
         m_mainFrame.setMinimumSize(new Dimension (800, 400));
         m_mainFrame.pack();
      }
      else
      {
         // hide map and tree panels
         m_serverListPanel.setVisible(false);
         m_mapFrame.getContentPane().setVisible(false);
         m_mainFrame.setMinimumSize(new Dimension (800, 80));
         m_mainFrame.pack();
      }
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
}
