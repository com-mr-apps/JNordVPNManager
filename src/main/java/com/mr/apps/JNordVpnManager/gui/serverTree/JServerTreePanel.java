/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.serverTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.geotools.UtilLocations;
import com.mr.apps.JNordVpnManager.geotools.UtilMapGeneration;
import com.mr.apps.JNordVpnManager.gui.GuiMenuBar;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;
import com.mr.apps.JNordVpnManager.gui.dialog.JAccelerateDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnServers;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnSettingsData;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnTechnologies;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

@SuppressWarnings("serial")
public class JServerTreePanel extends JPanel implements TreeSelectionListener
{
   private static final int           MIN_CHARS_FOR_FILTER    = 3;
   private static JTree               m_tree                  = null;
   private JTextField                 m_filterTextField       = null;
   private static String              m_filterText            = "";
   private boolean                    m_lockUpdate            = false;
   private JButton                    m_buttonRefresh         = null;
   private JComboBox<?>               m_filterRegions         = null;
   private JComboBox<?>               m_filterGroups          = null;
   private Color                      m_buttonDefaultFgColor  = null;
   private static boolean             m_statusInitServerList  = true;
   private static boolean             m_skipValueChangedEvent = false;
   private static boolean             m_skipGroupChangedEvent = false;

   private static NordVPNEnumGroups[] m_iaGroups              = {
         NordVPNEnumGroups.Standard_VPN_Servers,
         NordVPNEnumGroups.P2P,
         NordVPNEnumGroups.Double_VPN,
         NordVPNEnumGroups.Onion_Over_VPN,
         NordVPNEnumGroups.Dedicated_IP,
         NordVPNEnumGroups.legacy_obfuscated_servers
   };
   private static String              saGroupsText[]          = {
         "Standard VPN Servers",
         "P2P",
         "Double VPN",
         "Onion Over VPN",
         "Dedicated IP",
         "Obfuscated Servers"
   };

   /**
    * Server List Panel Layout definition.
    */
   public JServerTreePanel()
   {
      super();
      this.setLayout(new BorderLayout());
      this.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

      // ---------------------------------------------------------------------------------------------
      // Filter row
      // ---------------------------------------------------------------------------------------------
      JPanel filterPanel = new JPanel();
      filterPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,15));
      filterPanel.setLayout(new BorderLayout(10,0));

      // Regions Filter
      JPanel filterPanelGroups = new JPanel();
      filterPanelGroups.setLayout(new BorderLayout());

      NordVPNEnumGroups[] iaRegions = { NordVPNEnumGroups.all_regions, NordVPNEnumGroups.The_Americas, NordVPNEnumGroups.Africa_The_Middle_East_And_India, NordVPNEnumGroups.Asia_Pacific, NordVPNEnumGroups.Europe, null };
      String saRegions[]            = { "All Regions",                 "America",                      "Africa/Middle East/India",                         "Asia/Pacific",                 "Europe",                 "[NordVPN Recommanded Servers]" };
      m_filterRegions = new JComboBox<Object>(saRegions);

      int idxRegion = NvpnGroups.getFieldIndex(NvpnGroups.getCurrentRegion(), iaRegions, 0);
      m_filterRegions.setSelectedIndex(idxRegion);
      Starter._m_logError.TraceDebug("Init selected Region Filter with: " + iaRegions[idxRegion]);

      m_filterRegions.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            int index = m_filterRegions.getSelectedIndex();
            switch (index)
            {
               case 1 :
                  NvpnGroups.setCurrentRegion(NordVPNEnumGroups.The_Americas);
                  break;
               case 2 :
                  NvpnGroups.setCurrentRegion(NordVPNEnumGroups.Africa_The_Middle_East_And_India);
                  break;
               case 3 :
                  NvpnGroups.setCurrentRegion(NordVPNEnumGroups.Asia_Pacific);
                  break;
               case 4 :
                  NvpnGroups.setCurrentRegion(NordVPNEnumGroups.Europe);
                  break;
               case 5 :
                  JAccelerateDialog accelerateDialig = new JAccelerateDialog();
                  accelerateDialig.show();
                  return;
               default /* 0 */:
                  NvpnGroups.setCurrentRegion(NordVPNEnumGroups.all_regions);
            }
            updateFilterTreeCB();
            GuiMenuBar.updateQuickConnectMenuButton();
         }
      });
      filterPanelGroups.add(m_filterRegions, BorderLayout.PAGE_START);

      // Legacy Groups Filter
      m_filterGroups = new JComboBox<Object>(saGroupsText);

      int idxGroup = NvpnGroups.getFieldIndex(NvpnGroups.getCurrentGroup(), m_iaGroups, 0);
      m_filterGroups.setSelectedIndex(idxGroup);
      Starter._m_logError.TraceDebug("Init selected Group Filter with: " + m_iaGroups[idxGroup]);

      m_filterGroups.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            if (m_skipGroupChangedEvent == true) return; // avoid recursion

            // get index of the new selected group filter
            int newIndex = m_filterGroups.getSelectedIndex();

            NvpnSettingsData csd = Starter.getCurrentSettingsData();
            boolean bObfuscate = StringFormat.string2boolean(csd.getObfuscate(false));
            int idxObfuscatedGroup = NvpnGroups.getFieldIndex(NordVPNEnumGroups.legacy_obfuscated_servers, m_iaGroups, 0); // list index of group filter obfuscated button
            int idxCurrentGroup = NvpnGroups.getFieldIndex(NvpnGroups.getCurrentGroup(), m_iaGroups, 0); // list index of the current active group filter
            Starter._m_logError.TraceDebug("Group Filter selection changed: new Index='" + newIndex + "' / Obfuscate='" + bObfuscate + "' / current Index='" + idxCurrentGroup + "' / Index Obfuscated='" + idxObfuscatedGroup + "'.");
            
            if ((newIndex == idxObfuscatedGroup) && (!bObfuscate))
            {
               // require to change setting 'obfuscate enabled' (only available for technology OPENVPN!)
               JModalDialog dlg = JModalDialog.JOptionDialog("Obfuscated Servers",
                     "To connect to VPN servers with obfuscation, the Setting 'Obfuscate' must be enabled in NordVPN settings and the protocol type must be OPENVPN.\n"
                     + "\n Please choose the OPENVPN protocol to enable obfuscation, or choose Cancel.",
                     "OPENVPN TCP,OPENVPN UDP,Cancel");
               int rc = dlg.getResult();
               if (rc == 2)
               {
                  // undo selection
                  m_skipGroupChangedEvent = true;
                  m_filterGroups.setSelectedIndex(idxCurrentGroup);
                  m_skipGroupChangedEvent = false;
                  return;
               }
               else
               {
                  csd.setTechnology("OPENVPN", false);
                  if (rc == 0)
                  {
                     csd.setProtocol("TCP", false);
                  }
                  else // rc == 1
                  {
                     csd.setProtocol("UDP", false);
                  }
                  csd.setObfuscate("Enable", false);
               }
            }
            else if ((newIndex != idxObfuscatedGroup) && (bObfuscate))
            {
               // require to change setting 'obfuscate disabled'
               JModalDialog dlg = JModalDialog.JOptionDialog("Change Legacy Group", "Setting 'Obfuscate' is currently enabled. To connect to VPN servers with another legacy group, obfuscation must be deactivated.\n"
                     + "\n Please choose disable obfuscation, change technology to 'NORDLYNX' or choose Cancel.",
                     "Disable Obfuscation,NORDLYNX,Cancel");
               int rc = dlg.getResult();
               if (rc == 0)
               {
                  csd.setObfuscate("Disabled", false);
               }
               else if (rc == 1)
               {
                  csd.setTechnology("NORDLYNX", false);
               }
               else
               {
                  // undo selection
                  m_skipGroupChangedEvent = true;
                  m_filterGroups.setSelectedIndex(idxCurrentGroup);
                  m_skipGroupChangedEvent = false;
                  return;
               }
            }

            if (newIndex == idxCurrentGroup)
            {
               // no group change -> no update required
               return;
            }

            // Group change -> set the new current legacy group
            switch (newIndex)
            {
               case 1 :
                  NvpnGroups.setCurrentGroup(NordVPNEnumGroups.P2P);
                  break;
               case 2 :
                  NvpnGroups.setCurrentGroup(NordVPNEnumGroups.Double_VPN);
                  break;
               case 3 :
                  NvpnGroups.setCurrentGroup(NordVPNEnumGroups.Onion_Over_VPN);
                  break;
               case 4 :
                  NvpnGroups.setCurrentGroup(NordVPNEnumGroups.Dedicated_IP);
                  break;
               case 5 :
                  NvpnGroups.setCurrentGroup(NordVPNEnumGroups.legacy_obfuscated_servers);
                  break;
               default /* 0 */:
                  NvpnGroups.setCurrentGroup(NordVPNEnumGroups.Standard_VPN_Servers);
            }
            updateFilterTreeCB();
            GuiMenuBar.updateQuickConnectMenuButton();
         }
      });
      filterPanelGroups.add(m_filterGroups, BorderLayout.PAGE_END);
      filterPanel.add(filterPanelGroups, BorderLayout.PAGE_START);

      // Text Search Filter
      ImageIcon imageLabel = JResizedIcon.getIcon(IconUrls.ICON_SERVER_SEARCH_FILTER, IconSize.MEDIUM);
      JLabel filterLabel = new JLabel(imageLabel);
      filterLabel.setToolTipText("Text Filter for VPN Servers");
      filterPanel.add(filterLabel, BorderLayout.LINE_START);
      m_filterTextField = new JTextField();
      m_filterTextField.setToolTipText("<html><font face=\"sansserif\" color=\"black\">Filter requires min. " + MIN_CHARS_FOR_FILTER + " characters!<br>Press Right Mouse Button to reset.</font></html>");
      m_filterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
         public void keyReleased(java.awt.event.KeyEvent evt)
         {
            if (m_filterTextField.getText().length() >= MIN_CHARS_FOR_FILTER)
            {
               // activate Filter (lower case for case independent search)
               m_filterText = m_filterTextField.getText().toLowerCase();
               updateFilterTreeCB();
            }
            else if (!m_filterText.isBlank())
            {
               // reset Filter
               m_filterText = "";
               updateFilterTreeCB();
            }
         }
      });
      m_filterTextField.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            //if the user clicked the right mouse button
            if (javax.swing.SwingUtilities.isRightMouseButton(evt))
            {
               m_filterTextField.setText("");
               m_filterText = "";
               updateFilterTreeCB();
            }
         }
      });
      filterPanel.add(m_filterTextField, BorderLayout.CENTER);
      this.add(filterPanel, BorderLayout.PAGE_START);
      
      // ---------------------------------------------------------------------------------------------
      // Tree Refresh Button
      // Initial state (first call of Application) is "Get..." after first "Get..." the button is changed to "Refresh..."
      // ---------------------------------------------------------------------------------------------
      m_buttonRefresh = new JButton("Get VPN Servers");
      m_buttonRefresh.setToolTipText("Retrieve the actual VPN Server List from NordVPN.");
      m_buttonDefaultFgColor = m_buttonRefresh.getForeground();
      m_buttonRefresh.setForeground(Color.RED);
      m_buttonRefresh.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            if (true == m_statusInitServerList || JModalDialog.showConfirm("Update of the server list may take some time.\nDo you want to continue?") == JOptionPane.YES_OPTION)
            {
               DefaultMutableTreeNode root = createServerTree(true);
               m_tree.setModel(new MyModel(root));
               Starter.updateCurrentServer();
            }
         }
      });
      this.add(m_buttonRefresh, BorderLayout.PAGE_END);

      // Filter will be activated, when the server list is generated
      m_filterTextField.setEnabled(false);

      // ---------------------------------------------------------------------------------------------
      // Server tree
      // ---------------------------------------------------------------------------------------------
      JScrollPane jsp = initTree();
      this.add(jsp, BorderLayout.CENTER); // jsp in 'CENTER': automatic resize!!! ;)

//      this.setPreferredSize(new Dimension(260, 400));
   }

   /**
    * Set the Tree Selection Path without Trigger the Value Changed Callback
    * @param tp is the tree path to activate
    */
   private static void mySetSelectionPath(TreePath tp)
   {
      m_skipValueChangedEvent = true;
      m_tree.setSelectionPath(tp);
      m_skipValueChangedEvent = false;
   }

   /**
    * Filter the Tree Content.
    * <p>
    * This method updates the server list tree based on the filter text field.<br>
    * If a filter is active, all nodes are automatically expanded.
    */
   public void updateFilterTreeCB()
   {
      // avoid recursive calls caused by GUI elements updates (callbacks called)
      if (m_lockUpdate) return;
      m_lockUpdate = true;

      // create the server list tree based on the current content
      DefaultMutableTreeNode root = createServerTree(false);
      m_tree.setModel(new MyModel(root));
      if (!m_filterText.isBlank())
      {
         // filter
         for (int r = 0; r < m_tree.getRowCount(); r++)
         {
            // in case of active filter we expand all tree nodes
            m_tree.expandRow(r);
         }
      }

      // (try to) navigate tree to current server
      CurrentLocation loc = Starter.getCurrentServer();
      if (null != loc)
      {
         JServerTreePanel.activateTreeNode(loc);            
      }

      m_lockUpdate = false;
   }

   /**
    * Initialize the server tree (on program start)
    * @return the created tree ScrollPane
    */
   private JScrollPane initTree()
   {
      // create the server tree from NordVPN (update=true) or from [existing] local data (update=false) - dependent on auto update option(s)
      boolean update = false;
      int autoUpdateIntervall = UtilPrefs.getServerListAutoUpdate();
      if (autoUpdateIntervall > 0)
      {
         // calculate the days between last update and now
         String timestamp = UtilPrefs.getServerListTimestamp();
         long lTimestamp = Long.parseLong(timestamp);
         long days = UtilSystem.getDaysUntilNow(lTimestamp);
         // check with defined auto update interval
         update = (days >= autoUpdateIntervall) ? true : false;
      }
      Starter._m_logError.TraceIni("Auto Update Server List [from Application Preferences]: " + update);

      DefaultMutableTreeNode root = createServerTree(update);
      DefaultTreeModel model = new MyModel(root);
      m_tree = new JTree(model);
      m_tree.addTreeSelectionListener(this);
      m_tree.setCellRenderer(new MyRenderer());
      m_tree.setRootVisible(false);
      m_tree.setShowsRootHandles(true);

      JScrollPane jsp = new JScrollPane(m_tree);

      return jsp;
   }

   /**
    * Create the server list tree.
    * 
    * @param update
    *           if true, the server list is updated from NordVPN, else the server list local stored is used
    * @return the root server node
    */
   private DefaultMutableTreeNode createServerTree(boolean update)
   {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("Serverlist");
      ArrayList<String> vpnServers = new ArrayList<String>(); 

      NvpnSettingsData csd = Starter.getCurrentSettingsData();
      int idxFilterGroups = m_filterGroups.getSelectedIndex(); // current filter group selected
      boolean bObfuscate = false;
      if (null != csd) bObfuscate = StringFormat.string2boolean(csd.getObfuscate(false)); // current setting obfuscated [enabled,disabled]
      int idxObfuscatedGroup = NvpnGroups.getFieldIndex(NordVPNEnumGroups.legacy_obfuscated_servers, m_iaGroups, 0); // list index of filter group obfuscated button
      if (bObfuscate && (idxFilterGroups != idxObfuscatedGroup))
      {
         // we come from change settings update GUI -> Obfuscate=enabled - filter groups selection must be set to 'Obfuscated'
         Starter._m_logError.TraceDebug("Update of filter group selection required, because obfuscate is enabled but selected group index is '" + idxFilterGroups + "'.");
         m_filterGroups.setSelectedIndex(idxObfuscatedGroup);
      }
      else if (!bObfuscate && (idxFilterGroups == idxObfuscatedGroup))
      {
         // we come from change settings update GUI -> Obfuscate=disabled - filter groups selection must be changed from 'Obfuscated' (Default Standard Servers)
         Starter._m_logError.TraceDebug("Update of filter group selection required, because obfuscate is disabled but selected group index is '" + idxFilterGroups + "' (Obfuscated Servers).");
         m_filterGroups.setSelectedIndex(0);
      }

      NordVPNEnumGroups filterRegion = NvpnGroups.getCurrentRegion();
      NordVPNEnumGroups filterGroup = NvpnGroups.getCurrentGroup();
      Starter._m_logError.TraceDebug("Filter current Region = '" + filterRegion.name() + "' / Filter current Legacy Group = '" + filterGroup.name() + "' / Filter Text = '" + m_filterText + "'.");
      String sTechnology = "NORDLYNX";
      if (null != csd) sTechnology = csd.getTechnology(false);
      int iTechFilter = NvpnTechnologies.ikev2; // NORDLYNX
      if ((null != csd) && (sTechnology.equalsIgnoreCase("OPENVPN")))
      {
         String sProtocol = csd.getProtocol(false);
         if (sProtocol.equalsIgnoreCase("TCP"))
         {
            if (bObfuscate)
            {
               // OPENVNP / TCP / Obfuscated
               iTechFilter = NvpnTechnologies.openvpn_xor_tcp;
               if (!filterGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers))
               {
                  // this should not happen!
                  Starter._m_logError.LoggingError(90500,
                        "Legacy Group Mismatch",
                        "KO: Setting Obfuscate=enabled but group is '" + filterGroup.name() + "'");
               }
            }
            else
            {
               // OPENVNP / TCP / not Obfuscated
               iTechFilter = NvpnTechnologies.openvpn_tcp;
               if (filterGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers))
               {
                  // this should not happen!
                  Starter._m_logError.LoggingError(90500,
                        "Legacy Group Mismatch",
                        "KO: Setting Obfuscate=disabled but group is '" + filterGroup.name() + "'");
               }
            }
         }
         else
         {
            if (bObfuscate)
            {
               // OPENVNP / UDP / Obfuscated
               iTechFilter = NvpnTechnologies.openvpn_xor_udp;
               if (!filterGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers))
               {
                  // this should not happen!
                  Starter._m_logError.LoggingError(90500,
                        "Legacy Group Mismatch",
                        "KO: Setting Obfuscate=enabled but group is '" + filterGroup.name() + "'");
               }
            }
            else
            {
               // OPENVNP / UDP / not Obfuscated
               iTechFilter = NvpnTechnologies.openvpn_udp;
               if (filterGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers))
               {
                  // this should not happen!
                  Starter._m_logError.LoggingError(90500,
                        "Legacy Group Mismatch",
                        "KO: Setting Obfuscate=disabled but group is '" + filterGroup.name() + "'");
               }
            }
         }
      }

      // get the complete server list
      int nbCountries = 0;
      String serverListString = NvpnServers.getCountriesServerList(update);
      if (NvpnGroups.isValid())
      {
         // Server list from NordVPN server with region/groups information
         m_filterGroups.setEnabled(true);
         m_filterRegions.setEnabled(true);
         m_filterGroups.setToolTipText("Filter by Server Legacy Groups.");
         m_filterRegions.setToolTipText("Filter by Server Regions.");
      }
      else
      {
         // Fallback from 'nordvpn countries' command (stored in UserPrefs) w/o region/groups information
         m_filterGroups.setEnabled(false);
         m_filterRegions.setEnabled(false);
         m_filterGroups.setToolTipText("Please refresh the server list from internet to get the server legacy groups information!");
         m_filterRegions.setToolTipText("Please refresh the server list from internet to get the server regions information!");
      }

      // create the tree with applied filters (Text/Region/Group + Technology/Protocol)
      if (null != serverListString && !serverListString.isBlank())
      {
         String[] saServerList = serverListString.split(Location.SERVERID_LIST_SEPARATOR);
         if (saServerList.length > 0)
         {
            for (String countries : saServerList)
            {
               String[] saCountryCities = countries.split(Location.SERVERID_SEPARATOR);
               if (saCountryCities.length == 2)
               {
                  String country = saCountryCities[0];
                  String cities = saCountryCities[1];

                  JCountryNode countryNode = null;
                  boolean matchCountry = false;
                  if (m_filterText.isBlank() || country.toLowerCase().contains(m_filterText))
                  {
                     // Country matches the filter search text
                     matchCountry = true;
                  }

                  String[] saCities = cities.split("/");
                  for (String city : saCities)
                  {
                     // 1. check filter search text (or country name did match)
                     if (m_filterText.isBlank() || (!m_filterText.isBlank() && city.toLowerCase().contains(m_filterText)) || matchCountry)
                     {
                        Location loc = UtilLocations.getLocation(UtilLocations.getServerId(city, country));
                        // 2. Check Technology
                        if (loc.hasTechnology(iTechFilter))
                        {
                           // 3. check region- and groups-filter defined on server locations
                           if (loc.hasGroup(filterRegion) && loc.hasGroup(filterGroup))
                           {
                              if (null == countryNode)
                              {
                                 countryNode = new JCountryNode(loc);
                                 root.add(countryNode);
                                 ++nbCountries;
                              }
                              countryNode.add(new JServerNode(loc));
                              if (!vpnServers.contains(loc.getServerId())) vpnServers.add(loc.getServerId());
                           }
                        }
                     }
                  }
               }
               else
               {
                  Starter._m_logError.LoggingError(10100,
                        "Parsing Error",
                        "The Server Countries List String cannot be parsed:\n" +
                        saCountryCities);
                  return root;
               }
            }
         }
         else
         {
            Starter._m_logError.LoggingError(10100,
                  "Parsing Error",
                  "The Server List String cannot be parsed:\n" +
                  serverListString);
            return root;
         }
         
         if (m_statusInitServerList)
         {
            // switch from "Get.."(init) to "Refresh"
            m_buttonRefresh.setText("Refresh");
            m_buttonRefresh.setForeground(m_buttonDefaultFgColor);

            // activate the server filter text field
            m_filterTextField.setEnabled(true);
         }

         // update Tool tip with last server update information
         String timestamp = UtilPrefs.getServerListTimestamp();
         long lTimestamp = Long.parseLong(timestamp);
         long days = UtilSystem.getDaysUntilNow(lTimestamp);
         m_buttonRefresh.setToolTipText("<html><font face=\"sansserif\" color=\"black\">Retrieve an update of the VPN Server List from NordVPN.<br>Last update was: " + days + " days before.</font></html>");

         // we have a server list
         m_statusInitServerList = false;
         
         // actualize world map
         UtilMapGeneration.changeVpnServerLocationsMapLayer(vpnServers);
         UtilMapGeneration.zoomServerLayer();
         Starter._m_logError.LoggingInfo("Refreshed " + vpnServers.size() + " VPN Servers (in " + nbCountries + " countries).");
      }
      else
      {
         // no server list available
         m_statusInitServerList = true;

         // deactivate the server filter text field
         m_filterTextField.setEnabled(false);
      }

      return root;
   }

    /** Set Focus to the tree node of a Location.
    * @param loc is the location
    */
   public static void activateTreeNode(CurrentLocation loc)
   {
      if (null != loc)
      {
         TreePath tp = findNode(loc);
         if (null != tp)
         {
            m_tree.scrollPathToVisible(tp);
            m_tree.expandPath(tp);
            if (loc.isConnected())
            {
               // set selection to active server
               mySetSelectionPath(tp);
            }
            else
            {
               // reset selection (no active server)
               mySetSelectionPath(null);
            }
         }
         /* TODO: keep focus on current server (Preferences?)
         if (loc.isActive() && m_filterText.isBlank())
         {
            UtilMapGeneration.zoomIn(loc);
         }
         */
      }
   }

   /**
    * Utility to find the tree path of a location
    * 
    * @param loc
    *           is the location
    * @return if found, the tree path of the location, else null
    */
   private static TreePath findNode(Location loc)
   {
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) m_tree.getModel().getRoot();
      Enumeration<TreeNode> e = root.preorderEnumeration();

      String s = loc.getCityName();
      String p = loc.getCountryName();
      
      boolean inCountryNode = false;
      while (e.hasMoreElements())
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
         if (node instanceof JServerNode)
         {
            // city node
            if (inCountryNode && node.toString().equalsIgnoreCase(s))
            {
               // found country and city
               return new TreePath(node.getPath());
            }
         }
         else if (node instanceof JCountryNode)
         {
            // country node
            if (node.toString().equalsIgnoreCase(p))
            {
               inCountryNode = true;
               if (null == s || s.isBlank())
               {
                  // connect to country (no city)
                  return new TreePath(node.getPath());
               }
            }
            else
            {
               inCountryNode = false;
            }
         }
      }
      return null;
   }

   @SuppressWarnings("unused")
   private void dumpTree()
   {
      TreeModel model = m_tree.getModel();

      DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
      // Just changing enumeration kind here
      Enumeration<TreeNode> en = rootNode.postorderEnumeration();
      while (en.hasMoreElements())
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
         if (node instanceof JServerNode)
         {
            JServerNode serverNode = (JServerNode) node;
            TreeNode[] path = serverNode.getPath();
            System.out.println((serverNode.isLeaf() ? "  - " : "+ ") + path[path.length - 1]);
         }
         else if (node instanceof JCountryNode)
         {
            JCountryNode countryNode = (JCountryNode) node;
            TreeNode[] path = countryNode.getPath();
            System.out.println((countryNode.isLeaf() ? "  - " : "+ ") + path[path.length - 1]);
         }
      }
   }

   public void setTreeFilterGroup(NordVPNEnumGroups group)
   {
      if (NvpnGroups.getCurrentGroup().equals(group)) return;
      int idxGroup = NvpnGroups.getFieldIndex(group, m_iaGroups, 0);
      m_filterGroups.setSelectedIndex(idxGroup);
   }

   @Override
   public void valueChanged(TreeSelectionEvent e)
   {
      if (m_skipValueChangedEvent) return; // avoid recursive calls

      if (m_tree.getLastSelectedPathComponent() instanceof JServerNode)
      {
         JServerNode node = (JServerNode) m_tree.getLastSelectedPathComponent();
         if (node != m_tree.getModel().getRoot() && node != null)
         {
            CurrentLocation loc = new CurrentLocation(((JServerNode) node).getLocation());
            NvpnCallbacks.executeConnect(loc, "NordVPN Connect", "NordVPN Connect");
         }
      }
      else if (m_tree.getLastSelectedPathComponent() instanceof JCountryNode)
      {
         // TODO: do nothing (country selected)
      }
   }
}
