/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JSystemInfoDialog;
import com.mr.apps.JNordVpnManager.gui.settings.JSettingsDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCommands;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class GuiMenuBar
{
   private static JMenuItem        m_menuItemReConnect            = null;
   private static JMenuItem        m_menuItemDisConnect           = null;
   private static JMenuItem        m_menuItemQuickConnect         = null;
   private static JMenuItem        m_menuItemLogInOut             = null;
   private static JMenuItem        m_menuItemconsole              = null;

   // "Recent Server" menu
   private static JMenuItem        m_menuItemRecentServer         = null;
   private static JMenuItem[]      m_menuItemRecentServerMenuList = null;
   private static Vector<Location> m_recentServerIdList           = new Vector<Location>();

   /**
     * Menu Bar Layout definition.
    * @return the created menu bar
    */
   public JMenuBar create()
   {
      JMenuBar menuBar = new JMenuBar();

      // -------------------------------------------------------------------------------------
      // Menu --- File ---
      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);

      JMenuItem fileSettings = new JMenuItem("Settings");
      fileSettings.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            JSettingsDialog sp = new JSettingsDialog(Starter.getMainFrame());
            sp.getResult();
         }
      });
      fileMenu.add(fileSettings);
/*
      JMenuItem test = new JMenuItem("Test");
      test.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            //
         }
      });
      fileMenu.add(test);
*/
      m_menuItemconsole = new JMenuItem("Console on/off");
      m_menuItemconsole.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            /* boolean isVisible = */ Starter.switchConsoleWindow();
         }
      });
      fileMenu.add(m_menuItemconsole);

      JMenuItem fileExit = new JMenuItem("Exit");
      fileExit.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            Starter.cleanupAndExit(false);
         }
      });
      fileMenu.add(fileExit);
      
      // -------------------------------------------------------------------------------------
      // Menu --- NordVPN ---
      JMenu nordvpnMenu = new JMenu("NordVPN");
      menuBar.add(nordvpnMenu);

      JMenuItem menuItemVersion = new JMenuItem("Version");
      menuItemVersion.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String[] saVersions = NvpnCommands.getVersion();
            String msg;
            if (UtilSystem.isLastError())
            {
               if (null != saVersions[0]) msg = saVersions[0] + "\n";
               msg = UtilSystem.getLastError();
            }
            else
            {
               msg = saVersions[0] + "\n" + saVersions[1];
            }
            JModalDialog.showMessage("NordVPN Version", msg);
         }
      });
      nordvpnMenu.add(menuItemVersion);

      JMenuItem menuItemAccount = new JMenuItem("Account Info");
      menuItemAccount.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getAccountInfo();
            if (UtilSystem.isLastError()) msg = UtilSystem.getLastError();
            JModalDialog.showMessage("NordVPN Account", msg);
         }
      });
      nordvpnMenu.add(menuItemAccount);

      JMenuItem menuItemStatus = new JMenuItem("Status");
      menuItemStatus.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getStatus();
            if (UtilSystem.isLastError()) msg = UtilSystem.getLastError();
            JModalDialog.showMessage("NordVPN Status", msg);
         }
      });
      nordvpnMenu.add(menuItemStatus);

      JMenuItem menuItemSettings = new JMenuItem("Settings");
      menuItemSettings.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getSettings();
            if (UtilSystem.isLastError()) msg = UtilSystem.getLastError();
            JModalDialog.showMessage("NordVPN Settings", msg);
         }
      });
      nordvpnMenu.add(menuItemSettings);

      // -------------------------------------------------------------------------------------
      // Menu --- Connect ---
      JMenu connectMenu = new JMenu("Connect");
      menuBar.add(connectMenu);

      m_menuItemRecentServer = new JMenu("Recent Servers");
      m_menuItemRecentServerMenuList = null;
      connectMenu.add(m_menuItemRecentServer);
      // Initialize the recent serverIds list
      initRecentServerIdsList();
      // ...and set the "Recent Server" menu list items
      setMenuRecentServerListItems();

      connectMenu.addSeparator();

      m_menuItemReConnect = new JMenuItem("VPN Reconnect");
      m_menuItemReConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            CurrentLocation loc = Starter.getCurrentServer();
            NvpnCallbacks.executeConnect(loc);
         }
      });
      connectMenu.add(m_menuItemReConnect);
      
      m_menuItemQuickConnect = new JMenuItem("VPN Quick Connect");
      m_menuItemQuickConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.connect("", "");
            if (UtilSystem.isLastError())
            {
               // KO
               msg = UtilSystem.getLastError();
               JModalDialog.showError("NordVPN Connect", msg);
            }
            else
            {
               // OK
               Starter.updateServer();
               JModalDialog.showMessage("NordVPN Connect", msg);
            }
         }
      });
      connectMenu.add(m_menuItemQuickConnect);
      
      m_menuItemDisConnect = new JMenuItem("VPN Disconnect");
      m_menuItemDisConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCallbacks.executeDisConnect();
            if (UtilSystem.isLastError())
            {
               // KO
               msg = UtilSystem.getLastError();
               JModalDialog.showError("NordVPN Disconnect", msg);
            }
            else
            {
               // OK
               JModalDialog.showMessage("NordVPN Disconnect", msg);
            }
         }
      });
      connectMenu.add(m_menuItemDisConnect);

      
      connectMenu.addSeparator();
      connectMenu.addSeparator();
      
      NvpnAccountData accountData = new NvpnAccountData();
      m_menuItemLogInOut = new JMenuItem();
      m_menuItemLogInOut.setToolTipText((accountData.isLoggedIn()) ? "Logout from " + accountData.getEmail() : "Login");
      m_menuItemLogInOut.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            NvpnCallbacks.executeLogInOut();
         }
      });
      updateLoginOut(accountData);
      connectMenu.add(m_menuItemLogInOut);

      // -------------------------------------------------------------------------------------
      // Menu --- Info ---
      JMenu infoMenu = new JMenu("Info");
      menuBar.add(infoMenu);

      // MenuItem --- Welcome ---
      JMenuItem welcomeMenu = new JMenuItem("Welcome");
      welcomeMenu.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            SplashScreen splashScreen = new SplashScreen();
            splashScreen.show();
         }
      });
      infoMenu.add(welcomeMenu);

      // MenuItem --- About ---
      JMenuItem aboutMenu = new JMenuItem("About JNordVPNManager");
      aboutMenu.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            Starter.showAboutScreen();
         }
      });
      infoMenu.add(aboutMenu);

      // MenuItem --- Welcome ---
      JMenuItem infoMenuItem = new JMenuItem("System Info");
      infoMenuItem.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
        	 JSystemInfoDialog systemInfoDialog = new JSystemInfoDialog(Starter.getMainFrame(), "System Info");
        	 systemInfoDialog.setVisible(true);
         }
      });
      infoMenu.add(infoMenuItem);

      return menuBar;
   }

   /**
    * Update Menu Buttons
    * @param loc is the current connected or disconnected location
    */
   public static void updateMenuButtons(CurrentLocation loc)
   {
      if (null != loc && loc.isConnected())
      {
         // enable Disconnect command
         m_menuItemDisConnect.setEnabled(true);

         // disable Reconnect command
         m_menuItemReConnect.setEnabled(false);
         
         // disable Quick connect command
         m_menuItemQuickConnect.setEnabled(false);
      }
      else // not connected
      {
         // disable Disconnect command
         m_menuItemDisConnect.setEnabled(false);

         // enable Reconnect command - if there is a recent server
         String sToolTip = "";
         if (null != loc)
         {
            sToolTip = loc.getCountry() + " " + loc.getCity();
         }

         // ... and update the tool tip
         if (sToolTip.isBlank())
         {
            m_menuItemReConnect.setEnabled(false);
         }
         else
         {
            m_menuItemReConnect.setToolTipText("Connect to Server: " + sToolTip);
            m_menuItemReConnect.setEnabled(true);
         }
         
         // enable Quick connect command
         m_menuItemQuickConnect.setEnabled(true);
      }
   }

   public static void updateLoginOut(NvpnAccountData accountData)
   {
      m_menuItemLogInOut.setText((accountData.isLoggedIn()) ? "Logout" : "Login");
      m_menuItemLogInOut.setToolTipText((accountData.isLoggedIn()) ? "Logout from " + accountData.getEmail() : "Login");
   }

   /**
    * Initialize the Recent ServerIds list.
    */
   private static void initRecentServerIdsList()
   {
      // get the recent Server list items from User Preferences
      String savedRecentServers = UtilPrefs.getRecentServerList();
      String[] saRecentServers = savedRecentServers.split(Location.SERVERID_LIST_SEPARATOR);
      for (String recentServerId : saRecentServers)
      {
         if (!recentServerId.isBlank())
         {
            Location loc = UtilLocations.getLocation(recentServerId);
            m_recentServerIdList.addElement((Location)loc);
         }
      }
   }

   /**
    * Set the "Recent Servers" menu list items.
    * <p>
    * Create a new menu list under the menu "Recent Servers" with the content of the recent serverId list</li>
    */
   private static void setMenuRecentServerListItems()
   {
      StringBuffer recentServerIds = new StringBuffer();

      m_menuItemRecentServer.removeAll();
      m_menuItemRecentServerMenuList = new JMenuItem[m_recentServerIdList.size()];
      for (int i = 0; i < m_recentServerIdList.size(); i++)
      {
         final int nn = i;
         Location loc = m_recentServerIdList.get(i);

         if (null != loc)
         {
            m_menuItemRecentServerMenuList[i] = new JMenuItem(loc.getCountry() + " " + loc.getCity());
            m_menuItemRecentServerMenuList[i].addActionListener(new java.awt.event.ActionListener()
            {
               public void actionPerformed(ActionEvent e)
               {
                  recentServerSelectedCB(e, nn);
               }
            });
            m_menuItemRecentServer.add(m_menuItemRecentServerMenuList[i]);

            // String for user preferences
            if (recentServerIds.length() > 0) recentServerIds.append(Location.SERVERID_LIST_SEPARATOR);
            recentServerIds.append(loc.getServerId());
         }
      }
      if (recentServerIds.length() > 0)
      {
         // Update the user preferences with the current serverId list
         UtilPrefs.setRecentServerList(recentServerIds.toString());

         // enable menu and set tool tip
         m_menuItemRecentServer.setEnabled(true);
         m_menuItemRecentServer.setToolTipText("List of Recent Servers.");
      }
      else
      {
         // disable menu and set tool tip
         m_menuItemRecentServer.setEnabled(false);
         m_menuItemRecentServer.setToolTipText("No Recent Servers available.");
      }
   }

   /**
    * Recent Server menu Item action.
    * @param e is the action event
    * @param which is the selected menu item (== Location)
    */
   private static void recentServerSelectedCB(ActionEvent e, int which)
   {
      Location loc = m_recentServerIdList.get(which);
      String msg = NvpnCallbacks.executeConnect(loc);
      if (NvpnCallbacks.isLastError()) msg = NvpnCallbacks.getLastError();

      JModalDialog.showMessage("NordVPN Connect", msg);
   }

   /**
    * Add a location on the top of the recent Servers menu items list
    * @param loc is the new location 
    */
   public static void addToMenuRecentServerListItems(Location loc)
   {
      for (int n = 0; n < m_recentServerIdList.size(); n++)
      {
         if (m_recentServerIdList.get(n).getServerId().equals(loc.getServerId()) == true)
         {
            m_recentServerIdList.removeElementAt(n);
            break;
         }
      }
      m_recentServerIdList.insertElementAt(loc, 0);
      Starter._m_logError.TraceDebug("Add " + loc.getServerId() + " to Recentlist.");

      while (m_recentServerIdList.size() > UtilPrefs.getRecentServerListLength())
      {
         m_recentServerIdList.removeElementAt(m_recentServerIdList.size() - 1);
      }
      setMenuRecentServerListItems();
   }
}
