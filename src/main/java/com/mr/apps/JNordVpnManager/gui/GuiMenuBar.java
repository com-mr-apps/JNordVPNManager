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
import com.mr.apps.JNordVpnManager.gui.dialog.JWhatsNewDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JSplashScreen;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCommands;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnSettingsData;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class GuiMenuBar
{
   private static JMenuItem               m_menuItemReConnect            = null;
   private static JMenuItem               m_menuItemDisConnect           = null;
   private static JMenuItem               m_menuItemQuickConnect         = null;
   private static JMenuItem               m_menuItemLogInOut             = null;
   private static JMenuItem               m_menuItemconsole              = null;

   // "Recent Server" menu
   private static JMenuItem               m_menuItemRecentServer         = null;
   private static JMenuItem[]             m_menuItemRecentServerMenuList = null;
   private static Vector<CurrentLocation> m_recentServerIdList           = new Vector<CurrentLocation>();

   /**
    * Menu Bar Layout definition.
    * 
    * @param accountData
    *           is the nordvpn account data
    * @return the created menu bar
    */
   public JMenuBar create(NvpnAccountData accountData)
   {
      JMenuBar menuBar = new JMenuBar();

      // -------------------------------------------------------------------------------------
      // Menu --- File ---
      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);

      JMenuItem fileSettings = new JMenuItem("Preferences");
      fileSettings.setToolTipText("Show/Edit Application User Preferences.");
      fileSettings.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            UtilPrefs.showUserPreferencesPanel();
         }
      });
      fileMenu.add(fileSettings);

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
            UtilSystem.showResultDialog("NordVPN Account", msg, false);
         }
      });
      nordvpnMenu.add(menuItemAccount);

      JMenuItem menuItemStatus = new JMenuItem("Status");
      menuItemStatus.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getStatus();
            UtilSystem.showResultDialog("NordVPN Status", msg, false);
         }
      });
      nordvpnMenu.add(menuItemStatus);

      JMenuItem menuItemSettings = new JMenuItem("Settings");
      menuItemSettings.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getSettings();
            UtilSystem.showResultDialog("NordVPN Settings", msg, false);
         }
      });
      nordvpnMenu.add(menuItemSettings);

      nordvpnMenu.addSeparator();

      JMenuItem menuItemEditSettings = new JMenuItem("Edit Settings");
      menuItemEditSettings.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            NvpnSettingsData.showNordVpnSettingsPanel();
         }
      });
      nordvpnMenu.add(menuItemEditSettings);

      // -------------------------------------------------------------------------------------
      // Menu --- Connect ---
      JMenu connectMenu = new JMenu("Connect");
      menuBar.add(connectMenu);

      m_menuItemRecentServer = new JMenu("Recent Servers");
      m_menuItemRecentServerMenuList = null;
      connectMenu.add(m_menuItemRecentServer);
      // Initialize the recent serverIds list
      addToMenuRecentServerListItems(null);

      connectMenu.addSeparator();

      m_menuItemReConnect = new JMenuItem("VPN Reconnect");
      m_menuItemReConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            CurrentLocation loc = Starter.getCurrentServer();
            NvpnCallbacks.executeConnect(loc, "NordVPN Reconnect", "NordVPN Reconnect");
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
               Starter.updateCurrentServer();
               JModalDialog.showMessage("NordVPN Connect", msg);
            }
         }
      });
      updateQuickConnectMenuButton();
      connectMenu.add(m_menuItemQuickConnect);
      
      m_menuItemDisConnect = new JMenuItem("VPN Disconnect");
      m_menuItemDisConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            NvpnCallbacks.executeDisConnect("NordVPN Disconnect", "NordVPN Disconnect");
         }
      });
      connectMenu.add(m_menuItemDisConnect);

      
      connectMenu.addSeparator();
      connectMenu.addSeparator();
      
      m_menuItemLogInOut = new JMenuItem();
      m_menuItemLogInOut.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            NvpnCallbacks.executeLogInOut();
         }
      });
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
            JSplashScreen splashScreen = new JSplashScreen();
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

      // MenuItem --- What's New ---
      JMenuItem whatsNewMenu = new JMenuItem("What's New");
      whatsNewMenu.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            JWhatsNewDialog whatsNew = new JWhatsNewDialog();
            whatsNew.show();
         }
      });
      infoMenu.add(whatsNewMenu);

      // MenuItem --- System Info ---
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
    * 
    * @param loc
    *           is the current connected or disconnected location
    */
   public static void updateMenuButtons(CurrentLocation loc)
   {
      String sToolTip = "";
      if (null != loc)
      {
         sToolTip = loc.getCountryName() + " " + loc.getCityName();
      }
      // update Reconnect command - if there is a recent server
      if (sToolTip.isBlank())
      {
         m_menuItemReConnect.setEnabled(false);
      }
      else
      {
         m_menuItemReConnect.setToolTipText("Connect to Server: " + sToolTip);
         m_menuItemReConnect.setEnabled(true);
      }

      if (null != loc && loc.isConnected())
      {
         // enable Disconnect command
         m_menuItemDisConnect.setEnabled(true);
      }
      else // not connected
      {
         // disable Disconnect command
         m_menuItemDisConnect.setEnabled(false);
      }
   }

   public static void updateQuickConnectMenuButton()
   {
      // update Quick connect command Tool tip - display actual command dependent on Region and Group
      String optGroup = (NvpnGroups.getCurrentRegion().equals(NvpnGroups.NordVPNEnumGroups.all_regions)) ? "--group " + NvpnGroups.getCurrentGroup().name() : NvpnGroups.getCurrentRegion().name();
      String sToolTip = "nordvpn connect " + optGroup;
      m_menuItemQuickConnect.setToolTipText(sToolTip);
   }

   /**
    * Update GUI elements dependent on account data.
    * 
    * @param accountData
    *           is the current account data
    */
   public static void updateLoginLogout(NvpnAccountData accountData)
   {
      if (null != accountData && !accountData.isFailed())
      {
         m_menuItemLogInOut.setText((accountData.isLoggedIn()) ? "Logout" : "Login");
         m_menuItemLogInOut.setToolTipText((accountData.isLoggedIn()) ? "Logout from " + accountData.getEmail() : "Login");
      }
      else
      {
         m_menuItemLogInOut.setText("Login");
         m_menuItemLogInOut.setToolTipText("Login");
      }
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
         String[] saParts = recentServerId.split(",");
         String serverId = (saParts.length == 4) ? saParts[0] : recentServerId;
         if (!serverId.isBlank() && false == serverId.startsWith("nowhere"))
         {
            CurrentLocation loc = new CurrentLocation(UtilLocations.getLocation(serverId));
            if (saParts.length == 4)
            {
               // get (optional) connection data from preferences 'server@country,group,technology,protocol' and add them to loc
               loc.setFilterGroup(Integer.valueOf(saParts[1]));
               loc.setVpnTechnology(saParts[2]);
               loc.setVpnProtocol(saParts[3]);
            }
            m_recentServerIdList.addElement((CurrentLocation)loc);
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
         CurrentLocation loc = m_recentServerIdList.get(i);

         if (null != loc)
         {
            m_menuItemRecentServerMenuList[i] = new JMenuItem(loc.getCountryName() + " " + loc.getCityName());
            m_menuItemRecentServerMenuList[i].setToolTipText(loc.getToolTip());
            m_menuItemRecentServerMenuList[i].addActionListener(new java.awt.event.ActionListener()
            {
               public void actionPerformed(ActionEvent e)
               {
                  recentServerSelectedCB(e, nn);
               }
            });
            m_menuItemRecentServer.add(m_menuItemRecentServerMenuList[i]);

            // String for user preferences 'server@country,group,technology,protocol'
            if (recentServerIds.length() > 0) recentServerIds.append(Location.SERVERID_LIST_SEPARATOR);
            recentServerIds.append(loc.getServerId());
            recentServerIds.append(",");
            recentServerIds.append(loc.getFilterGroup());
            recentServerIds.append(",");
            recentServerIds.append(loc.getVpnTechnology());
            recentServerIds.append(",");
            recentServerIds.append(loc.getVpnProtocol());
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
      CurrentLocation loc = m_recentServerIdList.get(which);
      Starter._m_logError.LoggingInfo("Selected Recent Server: " + loc.getToolTip());

      // get and set additional (optional) connection data from location and set Group/Tech/Protocol
      Starter.getCurrentSettingsData().setTechnology(loc.getVpnTechnology(), false);
      Starter.getCurrentSettingsData().setProtocol(loc.getVpnProtocol(), false);
      if (NordVPNEnumGroups.get(loc.getFilterGroup()).equals(NordVPNEnumGroups.legacy_obfuscated_servers))
      {
         Starter.getCurrentSettingsData().setObfuscate("enabled", false);
      }
      else
      {
         Starter.getCurrentSettingsData().setObfuscate("disabled", false);
      }
      Starter.setTreeFilterGroup(NordVPNEnumGroups.get(loc.getFilterGroup()));

      NvpnCallbacks.executeConnect(loc, "NordVPN Connect", "NordVPN Connect");
   }

   /**
    * Add a location on the top of the recent Servers menu items list
    * 
    * @param loc
    *           is the new location. If <code>null</code>, Initialization with User Preferences.
    */
   public static void addToMenuRecentServerListItems(CurrentLocation loc)
   {
      if (null == m_menuItemRecentServer) return;

      boolean foundAtFirstPos = false;
      if (null == loc)
      {
         // init recent list from User Preferences
         initRecentServerIdsList();
      }
      else
      {
         for (int n = 0; n < m_recentServerIdList.size(); n++)
         {
            if (m_recentServerIdList.get(n).isEqualConnection(loc) == true)
            {
               if (n == 0)
               {
                  // we don't need an update
                  foundAtFirstPos = true;
               }
               else
               {
                  m_recentServerIdList.removeElementAt(n);
                  Starter._m_logError.TraceDebug("Remove " + loc.getServerId() + " at position " + n + " from Recentlist.");
               }
               break;
            }
         }
         if (false == foundAtFirstPos)
         {
            m_recentServerIdList.insertElementAt(loc, 0);
            Starter._m_logError.TraceDebug("Add " + loc.getServerId() + " to Recentlist.");

            while (m_recentServerIdList.size() > UtilPrefs.getRecentServerListLength())
            {
               m_recentServerIdList.removeElementAt(m_recentServerIdList.size() - 1);
            }
         }
      }
      if (false == foundAtFirstPos) setMenuRecentServerListItems();
   }
}
