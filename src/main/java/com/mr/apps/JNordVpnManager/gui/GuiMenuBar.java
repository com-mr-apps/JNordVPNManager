/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.CallCommand;
import com.mr.apps.JNordVpnManager.commandInterfaces.Command;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.geotools.UtilSpeedtest;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;
import com.mr.apps.JNordVpnManager.gui.connectLine.GuiCommandsToolBar;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JSystemInfoDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JWhatsNewDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JSplashScreen;
import com.mr.apps.JNordVpnManager.gui.dialog.JSupportersDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnAccountData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCommands;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnSettingsData;
import com.mr.apps.JNordVpnManager.utils.UtilCallbacks;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class GuiMenuBar
{
   private static JMenu                   m_nordvpnMenu                  = null;
   private static JMenuItem               m_menuItemAccount              = null;
   private static JMenuItem               m_menuItemReConnect            = null;
   private static JMenuItem               m_menuItemDisConnect           = null;
   private static JMenuItem               m_menuItemQuickConnect         = null;
   private static JMenuItem               m_menuItemLogInOut             = null;
   private static JMenuItem               m_menuItemConsole              = null;

   // "Recent Server" menu
   private static JMenuItem               m_menuItemRecentServer         = null;
   private static JMenuItem[]             m_menuItemRecentServerMenuList = null;
   private static Vector<CurrentLocation> m_recentServerIdList           = null;

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

      m_menuItemConsole = new JMenuItem("Console on/off");
      m_menuItemConsole.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            /* boolean isVisible = */ Starter.switchConsoleWindow();
         }
      });
      fileMenu.add(m_menuItemConsole);

      JMenuItem manageSupporterEdition = new JMenuItem("Manage Supporter Edition");
      manageSupporterEdition.setToolTipText("Activate/Deactivate the Supporter Edition.");
      manageSupporterEdition.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            UtilCallbacks.cbManageSupporterEdition();
         }
      });
      fileMenu.add(manageSupporterEdition);
      
      fileMenu.addSeparator();
      
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
      m_nordvpnMenu = new JMenu("NordVPN");
      menuBar.add(m_nordvpnMenu);

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
      m_nordvpnMenu.add(menuItemVersion);

      m_menuItemAccount = new JMenuItem("Account Info");
      m_menuItemAccount.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getAccountInfo();
            UtilSystem.showResultDialog("NordVPN Account", msg, false);
            if (Starter.getCurrentAccountData(false).warnNordAccountExpires())
            {
               int iRemainingDays = Starter.getCurrentAccountData(false).getRemainingDays();
               int iRememberMe = Math.max(1, iRemainingDays-10);
               JModalDialog dlg = JModalDialog.JOptionDialog("NordVPN Account Expires",
                     "Your NordVPN Subscription expires in " + iRemainingDays + " days.\n"
                     + "The button 'Renew Subscription' will open the web browser with my affiliate link where you get free months added to your subscription.\n\n"
                     + "(You can configure this reminder in the application user preferences.)",
                     "Renew Subscription,Set Reminder to " + iRememberMe + " days,Cancel");
               int rc = dlg.getResult();
               if (rc == 0)
               {
                  try
                  {
                     UtilSystem.openWebpage(new URI("https://refer-nordvpn.com/ArNNOfynXcu"));
                  }
                  catch (URISyntaxException ex)
                  {
                     Starter._m_logError.LoggingExceptionAbend(10903, ex);
                  }
               }
               else if (rc == 1)
               {
                  UtilPrefs.setAccountReminder(iRememberMe);
               }
            }
         }
      });
      m_nordvpnMenu.add(m_menuItemAccount);

      JMenuItem menuItemStatus = new JMenuItem("Status");
      menuItemStatus.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getStatus();
            UtilSystem.showResultDialog("NordVPN Status", msg, false);
         }
      });
      m_nordvpnMenu.add(menuItemStatus);

      JMenuItem menuItemSettings = new JMenuItem("Settings");
      menuItemSettings.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.getSettings();
            UtilSystem.showResultDialog("NordVPN Settings", msg, false);
         }
      });
      m_nordvpnMenu.add(menuItemSettings);

      m_nordvpnMenu.addSeparator();

      JMenuItem menuItemEditSettings = new JMenuItem("Edit Settings");
      menuItemEditSettings.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            NvpnSettingsData.showNordVpnSettingsPanel();
         }
      });
      m_nordvpnMenu.add(menuItemEditSettings);

      JMenuItem allowList = new JMenuItem("Edit AllowList");
      allowList.setForeground(Starter.Color_Addon);
      allowList.setToolTipText("Show/Edit AllowList.");
      allowList.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            if (Starter.isSupporterEdition())
            {
               CallCommand.invokeAddonMethod("AddonManager", "openAllowListDialog");
            }
            else
            {
               new JSupportersDialog("Edit NordVPN Allow List");
            }
         }
      });
      m_nordvpnMenu.add(allowList);

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
            CurrentLocation loc = Starter.getCurrentServer(true);
            NvpnCallbacks.executeConnect(loc, "NordVPN Reconnect", "NordVPN Reconnect");
         }
      });
      connectMenu.add(m_menuItemReConnect);
      
      m_menuItemQuickConnect = new JMenuItem("VPN Quick Connect");
      m_menuItemQuickConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String msg = NvpnCommands.connect(null);
            if (UtilSystem.isLastError())
            {
               // KO
               msg = UtilSystem.getLastError();
               JModalDialog.showError("NordVPN Connect", msg);
            }
            else
            {
               // OK
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
            JSplashScreen welcomeScreen = new JSplashScreen(Starter.getMainFrame());
            welcomeScreen.setVisible(true);
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

      // -------------------------------------------------------------------------------------
      // Menu --- Experimental ---
      JMenu experimentalMenu = new JMenu("Experimental");
      menuBar.add(experimentalMenu);

      JMenuItem speedTest = new JMenuItem("speedTest");
      speedTest.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            UtilSpeedtest.setVisibleSpeedtestMapLayer(true);
            UtilSpeedtest.speedTest(Starter.getCurrentServer(true));
         }
      });
      experimentalMenu.add(speedTest);

      // -------------------------------------------------------------------------------------
      menuBar.add(Box.createHorizontalGlue());

      // Menu(item) Supporter Edition
      JMenu suporterMenu = new JMenu("");
      suporterMenu.setIcon(JResizedIcon.getIcon("icons/i_MenuSupporterEdition.png", 43, 24));
      suporterMenu.addMenuListener(new MenuListener()
      {
         @Override
         public void menuCanceled(MenuEvent arg0)
         {
         }

         @Override
         public void menuDeselected(MenuEvent arg0)
         {
         }

         @Override
         public void menuSelected(MenuEvent arg0)
         {
            new JSupportersDialog();
            menuBar.setSelected(null);
         }
      });
      menuBar.add(suporterMenu);

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
      String optGroup = (NvpnGroups.getCurrentFilterRegion().equals(NvpnGroups.NordVPNEnumGroups.all_regions)) ? "--group " + NvpnGroups.getCurrentFilterGroup().name() : NvpnGroups.getCurrentFilterRegion().name();
      String sToolTip = "Click here for: nordvpn connect " + optGroup;
      m_menuItemQuickConnect.setToolTipText(sToolTip);
      Command cmd = Command.getObject(Command.VPN_CMD_QUICKCONNECT);
      cmd.setToolTip(sToolTip);
      GuiCommandsToolBar.updateCommand(Command.VPN_CMD_QUICKCONNECT);
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
      m_recentServerIdList = new Vector<CurrentLocation>();

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
               loc.setLegacyGroup(Integer.valueOf(saParts[1]));
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
            recentServerIds.append(loc.getLegacyGroup());
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
      if (NordVPNEnumGroups.get(loc.getLegacyGroup()).equals(NordVPNEnumGroups.legacy_obfuscated_servers))
      {
         Starter.getCurrentSettingsData().setObfuscate("enabled", false);
      }
      else
      {
         Starter.getCurrentSettingsData().setObfuscate("disabled", false);
      }
      Starter.setTreeFilterGroup();

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
         // initialize recent list from User Preferences
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
            Starter._m_logError.TraceDebug("Add " + loc.getToolTip() + " to Recentlist.");

            while (m_recentServerIdList.size() > UtilPrefs.getRecentServerListLength())
            {
               m_recentServerIdList.removeElementAt(m_recentServerIdList.size() - 1);
            }
         }
      }
      if (false == foundAtFirstPos) setMenuRecentServerListItems();
   }
   
   public static void updateAccountReminder()
   {
      if (null == m_nordvpnMenu) return;
      if (Starter.getCurrentAccountData(false).warnNordAccountExpires())
      {
         m_nordvpnMenu.setIcon(JResizedIcon.getIcon(IconUrls.ICON_STATUS_WARNING, IconSize.SMALL));
         m_menuItemAccount.setIcon(JResizedIcon.getIcon(IconUrls.ICON_STATUS_WARNING, IconSize.SMALL));
      }
      else
      {
         m_nordvpnMenu.setIcon(null);
         m_menuItemAccount.setIcon(null);
      }
   }
}
