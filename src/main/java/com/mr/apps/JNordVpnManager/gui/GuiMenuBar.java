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
import com.mr.apps.JNordVpnManager.commandInterfaces.base.CallCommand;
import com.mr.apps.JNordVpnManager.commandInterfaces.base.Command;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.geotools.UtilSpeedtest;
import com.mr.apps.JNordVpnManager.geotools.VpnServer;
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
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class GuiMenuBar
{
   private static JMenu                   m_nordvpnMenu                  = null;
   private static JMenuItem               m_menuItemAccount              = null;
   private static JMenuItem               m_menuItemReConnect            = null;
   private static JMenuItem               m_menuItemDisConnect           = null;
   private static JMenuItem               m_menuItemQuickConnect         = null;
   private static JMenuItem               m_menuItemRegionConnect        = null;
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
            Starter.switchConsoleWindow();
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
            GuiCommandsToolBar.execute(Command.VPN_CMD_QUICKCONNECT, e);
         }
      });
      updateQuickConnectMenuButton();
      connectMenu.add(m_menuItemQuickConnect);
      
      m_menuItemRegionConnect = new JMenuItem("VPN Region Connect");
      m_menuItemRegionConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
//            if (NvpnGroups.getCurrentFilterRegion().equals(NvpnGroups.NordVPNEnumGroups.all_regions)) return; // should not happen, because RegionConnect is not enabled 
            // Create a temp. Region Location (cityId==2)
            CurrentLocation loc = new CurrentLocation (new Location (NvpnGroups.getCurrentFilterRegion().toString(), NvpnGroups.getCurrentFilterRegion().toString(), 0.0, 0.0, 2), null);
            NvpnCallbacks.executeConnect(loc, "NordVPN Region Connect", "NordVPN Region Connect");
         }
      });
      updateRegionConnectMenuButton();
      connectMenu.add(m_menuItemRegionConnect);
      
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
      // Menu --- Diagnostics ---
      JMenu diagnosticsMenu = new JMenu("Diagnostics");
      menuBar.add(diagnosticsMenu);

      JMenuItem speedTest = new JMenuItem("Download Speed Test");
      speedTest.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            UtilSpeedtest.speedTest(Starter.getCurrentServer(true));
         }
      });
      diagnosticsMenu.add(speedTest);

      JMenuItem checkLogFiles = new JMenuItem("Check log files");
      checkLogFiles.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            StringBuffer sb =new StringBuffer();
            sb.append("### journalctl -u nordvpnd" + "\n");
            sb.append(UtilSystem.runWithPrivileges("journalctl -u nordvpnd"));
            if (Starter.isSnapInstallation())
            {
               sb.append("\n###\n### tail ~/snap/nordvpn/common/.config/nordvpn/cli.log" + "\n");
               sb.append(UtilSystem.runCommand("/bin/bash", "-c", "tail -n30 ~/snap/nordvpn/common/.config/nordvpn/cli.log"));
               sb.append("\n###\n### tail ~/snap/nordvpn/common/.cache/nordvpn/norduserd.log" + "\n");
               sb.append(UtilSystem.runCommand("/bin/bash", "-c", "tail -n30 ~/snap/nordvpn/common/.cache/nordvpn/norduserd.log"));
            }
            JModalDialog.showMessage("Diagnostic: Log Files", sb.toString());
         }
      });
      diagnosticsMenu.add(checkLogFiles);

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
         m_menuItemDisConnect.setToolTipText("Disconnect from VPN Server.");
      }
      else // not connected
      {
         // disable Disconnect command
         m_menuItemDisConnect.setEnabled(false);
         m_menuItemDisConnect.setToolTipText("Not connected.");
      }
   }

   public static void updateRegionConnectMenuButton()
   {
      if (NvpnGroups.getCurrentFilterRegion().equals(NvpnGroups.NordVPNEnumGroups.all_regions))
      {
         m_menuItemRegionConnect.setEnabled(false);
         m_menuItemRegionConnect.setToolTipText("Select a Region Filter to connect to.");
      }
      else
      {
         m_menuItemRegionConnect.setEnabled(true);
         m_menuItemRegionConnect.setToolTipText("Execute: nordvpn connect " + NvpnGroups.getCurrentFilterRegion());
      }
   }

   public static void updateQuickConnectMenuButton()
   {
      // update Quick connect command Tool tip - display actual command dependent on Group (obfuscated settings)
      String optGroup = (StringFormat.string2boolean(Starter.getCurrentSettingsData().getObfuscate(false)) || NvpnGroups.getCurrentFilterGroup().equals(NordVPNEnumGroups.legacy_obfuscated_servers)) ? "" : "--group " + NvpnGroups.getCurrentFilterGroup().name();
      String sToolTip = "Execute: nordvpn connect " + optGroup;
      m_menuItemQuickConnect.setToolTipText(sToolTip);
      Command cmd = Command.getObject(Command.VPN_CMD_QUICKCONNECT);
      cmd.setCurrentToolTip(sToolTip);
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
         if (!serverId.isBlank())
         {
            CurrentLocation loc = null;
            VpnServer vs = null;
            String sa[] = serverId.split(Location.SERVERID_HOST_SEPARATOR); // check, if this is a host server (serverId = "city@country#host")
            if (sa.length == 2)
            {
               vs = new VpnServer(sa[0], Location.SERVERID_HOST_SEPARATOR + sa[1], sa[1]);
               serverId = sa[0];
            }
            Location l = UtilLocations.getLocation(serverId);
            if (null == l) l = new Location (serverId, 0, 0, -1);
            loc = new CurrentLocation(l, vs);
            
            if (saParts.length == 4)
            {
               // get (optional) connection data from preferences 'server@country[#host],group,technology,protocol' and add them to loc
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
            m_menuItemRecentServerMenuList[i] = new JMenuItem(loc.getLabel());
            m_menuItemRecentServerMenuList[i].setToolTipText(loc.getToolTip());
            m_menuItemRecentServerMenuList[i].addActionListener(new java.awt.event.ActionListener()
            {
               public void actionPerformed(ActionEvent e)
               {
                  recentServerSelectedCB(e, nn);
               }
            });
            m_menuItemRecentServer.add(m_menuItemRecentServerMenuList[i]);

            // String for user preferences 'server@country[#host],group,technology,protocol'
            if (recentServerIds.length() > 0) recentServerIds.append(Location.SERVERID_LIST_SEPARATOR);
            String[] saLocationConnectionData = loc.getLocationConnectionData();
            recentServerIds.append(Location.buildServerId(saLocationConnectionData[0], saLocationConnectionData[1]));
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
      Starter._m_logError.TraceDebug("Selected Recent Server: " + loc.getToolTip());

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
                  Starter._m_logError.TraceDebug("Remove " + loc.getServerKey() + " at position " + n + " from Recentlist.");
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
