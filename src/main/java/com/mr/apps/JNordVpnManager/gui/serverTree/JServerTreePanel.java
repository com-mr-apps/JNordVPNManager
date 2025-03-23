/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.serverTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
   private static JLabel              m_filterLabel           = null;
   private boolean                    m_lockUpdate            = false;
   private JButton                    m_buttonRefresh         = null;
   private JComboBox<?>               m_filterRegions         = null;
   private JComboBox<?>               m_filterGroups          = null;
   private Color                      m_buttonDefaultFgColor  = null;
   private static boolean             m_statusInitServerList  = true;
   private static boolean             m_skipValueChangedEvent = false;

   NordVPNEnumGroups[]                m_iaRegions             = {
         NordVPNEnumGroups.all_regions,
         NordVPNEnumGroups.The_Americas,
         NordVPNEnumGroups.Africa_The_Middle_East_And_India,
         NordVPNEnumGroups.Asia_Pacific,
         NordVPNEnumGroups.Europe
   };
   String                             m_saRegions[]           = {
         "All Regions",
         "America",
         "Africa/Middle East/India",
         "Asia/Pacific",
         "Europe"
   };

   private static NordVPNEnumGroups[] m_iaGroups              = {
         NordVPNEnumGroups.Standard_VPN_Servers,
         NordVPNEnumGroups.P2P,
         NordVPNEnumGroups.Double_VPN,
         NordVPNEnumGroups.Onion_Over_VPN,
         NordVPNEnumGroups.Dedicated_IP,
         NordVPNEnumGroups.legacy_obfuscated_servers
   };
   private static String              m_saGroupsText[]        = {
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

      m_filterRegions = new JComboBox<Object>(m_saRegions);

      int idxRegion = NvpnGroups.getFieldIndex(NvpnGroups.getCurrentFilterRegion(), m_iaRegions, 0);
      m_filterRegions.setSelectedIndex(idxRegion);
      Starter._m_logError.TraceDebug("Init selected Region Filter with: " + m_iaRegions[idxRegion]);

      filterPanelGroups.add(m_filterRegions, BorderLayout.PAGE_START);

      // Legacy Groups Filter
      m_filterGroups = new JComboBox<Object>(m_saGroupsText);

      int idxGroup = NvpnGroups.getFieldIndex(NvpnGroups.getCurrentFilterGroup(), m_iaGroups, 0);
      m_filterGroups.setSelectedIndex(idxGroup);
      Starter._m_logError.TraceDebug("Init selected Group Filter with: " + m_iaGroups[idxGroup]);

      filterPanelGroups.add(m_filterGroups, BorderLayout.PAGE_END);

      filterPanel.add(filterPanelGroups, BorderLayout.PAGE_START);

      // Text Search Filter
      ImageIcon imageLabel = JResizedIcon.getIcon(IconUrls.ICON_SERVER_SEARCH_FILTER, IconSize.MEDIUM);
      m_filterLabel = new JLabel(imageLabel);
      m_filterLabel.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            updateFilterTreeCB(false);
         }
      });
      filterPanel.add(m_filterLabel, BorderLayout.LINE_START);
      m_filterTextField = new JTextField();
      m_filterTextField.setToolTipText("<html><font face=\"sansserif\" color=\"black\">Filter requires min. " + MIN_CHARS_FOR_FILTER + " characters!<br>Press Right Mouse Button to reset.</font></html>");
      m_filterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
         public void keyReleased(java.awt.event.KeyEvent evt)
         {
            if (m_filterTextField.getText().length() >= MIN_CHARS_FOR_FILTER)
            {
               // activate Filter (lower case for case independent search)
               m_filterText = m_filterTextField.getText().toLowerCase();
               updateFilterTreeCB(false);
            }
            else if (!m_filterText.isBlank())
            {
               // reset Filter
               m_filterText = "";
               updateFilterTreeCB(false);
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
               updateFilterTreeCB(false);
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
               updateFilterTreeCB(true);
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

      // ---------------------------------------------------------------------------------------------
      // Add Group/Region action listeners AFTER Server Tree initialization (to avoid launch Event on initialization)
      // ---------------------------------------------------------------------------------------------
      m_filterRegions.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            int index = m_filterRegions.getSelectedIndex();
            Starter._m_logError.TraceDebug("[Enter Region Filter CB] Current selected Region Filter: " +  NvpnGroups.getCurrentFilterRegion().name());
            switch (index)
            {
               case 1 :
                  NvpnGroups.setCurrentFilterRegion(NordVPNEnumGroups.The_Americas);
                  break;
               case 2 :
                  NvpnGroups.setCurrentFilterRegion(NordVPNEnumGroups.Africa_The_Middle_East_And_India);
                  break;
               case 3 :
                  NvpnGroups.setCurrentFilterRegion(NordVPNEnumGroups.Asia_Pacific);
                  break;
               case 4 :
                  NvpnGroups.setCurrentFilterRegion(NordVPNEnumGroups.Europe);
                  break;
               default /* 0 */:
                  NvpnGroups.setCurrentFilterRegion(NordVPNEnumGroups.all_regions);
            }
            updateFilterTreeCB(false);
            UtilMapGeneration.zoomServerLayer();
            GuiMenuBar.updateQuickConnectMenuButton();
         }
      });

      m_filterGroups.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            Starter._m_logError.TraceDebug("[Enter Group Filter CB] Current selected Group Filter: " + NvpnGroups.getCurrentFilterGroup().name());
            int index = m_filterGroups.getSelectedIndex();
            switch (index)
            {
               case 1 :
                  NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.P2P);
                  break;
               case 2 :
                  NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.Double_VPN);
                  break;
               case 3 :
                  NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.Onion_Over_VPN);
                  break;
               case 4 :
                  NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.Dedicated_IP);
                  break;
               case 5 :
                  NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.legacy_obfuscated_servers);
                  break;
               default /* 0 */:
                  NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.Standard_VPN_Servers);
            }
            updateFilterTreeCB(false);
            UtilMapGeneration.zoomServerLayer();
            GuiMenuBar.updateQuickConnectMenuButton();
         }
      });

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
    * Update and filter the Server List Tree Content.
    * <p>
    * This method updates the server list tree based on the filter text field.<br>
    * If a text filter is active, all nodes are automatically expanded.
    * @param update
    *           if true, the server list is updated from NordVPN, else the server list local stored is used
    */
   public void updateFilterTreeCB(boolean update)
   {
      // avoid recursive calls caused by GUI elements updates (callbacks called)
      if (m_lockUpdate) return;
      m_lockUpdate = true;

      // create the server list tree based on the current content
      DefaultMutableTreeNode root = createServerTree(update);
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
      CurrentLocation loc = Starter.getCurrentServer(false);
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

      // Filter from GUI (Region and Legacy Group)
      NordVPNEnumGroups filterRegion = NvpnGroups.getCurrentFilterRegion();
      NordVPNEnumGroups filterGroup = NvpnGroups.getCurrentFilterGroup();
      boolean bObfuscate = filterGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers); // current selection obfuscated
      Starter._m_logError.TraceDebug("Create Server Tree: Filter current Region = '" + filterRegion.name() + "' / Filter current Legacy Group = '" + filterGroup.name() + "' / Filter Text = '" + m_filterText + "'.");

      // Filter from Settings (Technology/Protocol)
      String sTechnology = "NORDLYNX";
      int iTechFilter = NvpnTechnologies.ikev2; // NORDLYNX,NORDWHISPER
      String sTechnologyAndProtocol = "NORDLYNX/UDP";
      NvpnSettingsData csd = Starter.getCurrentSettingsData();
      if (null != csd)
      {
         sTechnology = csd.getTechnology(false);
         if (sTechnology.equalsIgnoreCase("OPENVPN"))
         {
            String sProtocol = csd.getProtocol(false);
            if (sProtocol.equalsIgnoreCase("TCP"))
            {
               if (bObfuscate)
               {
                  // OPENVNP / TCP / Obfuscated
                  iTechFilter = NvpnTechnologies.openvpn_xor_tcp;
                }
               else
               {
                  // OPENVNP / TCP / not Obfuscated
                  iTechFilter = NvpnTechnologies.openvpn_tcp;
               }
            }
            else
            {
               if (bObfuscate)
               {
                  // OPENVNP / UDP / Obfuscated
                  iTechFilter = NvpnTechnologies.openvpn_xor_udp;
               }
               else
               {
                  // OPENVNP / UDP / not Obfuscated
                  iTechFilter = NvpnTechnologies.openvpn_udp;
               }
            }
            sTechnologyAndProtocol = "OPENVPN/" + sProtocol; 
         }
         else if (sTechnology.equalsIgnoreCase("NORDWHISPER"))
         {
            sTechnologyAndProtocol = "NORDWHISPER/Webtunnel"; 
         }
      }
      Starter._m_logError.TraceDebug("...Filter Technology (dependent from Settings) = '" + iTechFilter + "' (" + sTechnologyAndProtocol + ").");
      boolean isVirtual = StringFormat.string2boolean(Starter.getCurrentSettingsData().getVirtualLocation(false));
      String sFilterText = setFilterToolTip(filterRegion.name(), filterGroup.name(), bObfuscate, sTechnologyAndProtocol, isVirtual);

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

                        // 2. Filter Virtual Locations dependent on current settings
                        if ((false == isVirtual) && (true == loc.isVirtualLocation())) continue;

                        
                        // 3. Check Technology
                        if (loc.hasTechnology(iTechFilter))
                        {
                           // 4. check region- and groups-filter defined on server locations
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
            m_buttonRefresh.setText("Refresh List from NordVPN Server");
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
         Starter._m_logError.TraceIni("Filtered " + vpnServers.size() + " VPN Servers (in " + nbCountries + " countries) [" + sFilterText + "].");
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

   /**
    * Set Focus to the tree node of a Location.
    * 
    * @param loc
    *           is the location
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
               // reset selection (not connected)
               mySetSelectionPath(null);
            }
         }
         else
         {
            // reset selection (active server not found in list)
            mySetSelectionPath(null);
            if (loc.isConnected())
            {
               UtilMapGeneration.zoomIn(loc);
            }
            else
            {
               UtilMapGeneration.zoomServerLayer();
            }
         }
      }
      else
      {
         // reset selection (no active server)
         mySetSelectionPath(null);
         UtilMapGeneration.zoomServerLayer();
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

   public void setTreeFilterGroup()
   {
      // check group filter consistency (Settings vs. Current)
      boolean bObfuscateSetting = StringFormat.string2boolean(Starter.getCurrentSettingsData().getObfuscate(false));
      boolean bObfuscateCurrent = NvpnGroups.getCurrentFilterGroup().equals(NordVPNEnumGroups.legacy_obfuscated_servers);

      boolean isVirtual = StringFormat.string2boolean(Starter.getCurrentSettingsData().getVirtualLocation(false));
      Starter._m_logError.TraceDebug("Set Tree Filter Group: Setting Obfuscated=" + Starter.getCurrentSettingsData().getObfuscate(false) + "', Current Group=" + NvpnGroups.getCurrentFilterGroup() + "', Virtual Locations=" + String.valueOf(isVirtual) + "'.");

      int idxFilterGroups = m_filterGroups.getSelectedIndex(); // index of selected filter group
      int idxCurrentGroup = NvpnGroups.getFieldIndex(NvpnGroups.getCurrentFilterGroup(), m_iaGroups, 0); // index of current (required) filter group
      int idxObfuscatedGroup = NvpnGroups.getFieldIndex(NordVPNEnumGroups.legacy_obfuscated_servers, m_iaGroups, 0); // index of filter group obfuscated button
      if ((bObfuscateSetting && (bObfuscateSetting != bObfuscateCurrent)) && (idxFilterGroups != idxObfuscatedGroup))
      {
         // settings Obfuscate=enabled - filter groups selection must be set to 'Obfuscated'
         Starter._m_logError.TraceDebug("Update of filter group selection required, because obfuscate is enabled but current group is '" + NvpnGroups.getCurrentFilterGroup() + "'.");
         m_filterGroups.setSelectedIndex(idxObfuscatedGroup);
      }
      else if ((!bObfuscateSetting && (bObfuscateSetting != bObfuscateCurrent)) && (idxFilterGroups == idxObfuscatedGroup))
      {
         // settings Obfuscate=disabled - filter groups selection must be changed from 'Obfuscated' (Default Standard Servers)
         Starter._m_logError.TraceDebug(
               "Update of filter group selection required, because obfuscate is disabled but current group is '" + NvpnGroups.getCurrentFilterGroup() + "'.");
         m_filterGroups.setSelectedIndex(0);
      }
      else if (idxFilterGroups != idxCurrentGroup)
      {
         // Selected group different from current group
         Starter._m_logError.TraceDebug(
               "Update of filter group selection required, selected group '" + m_iaGroups[idxFilterGroups] + "' is different from current group.");
         m_filterGroups.setSelectedIndex(idxCurrentGroup);
      }

      // update filter toolTip
      setFilterToolTip(NvpnGroups.getCurrentFilterRegion().name(), NvpnGroups.getCurrentFilterGroup().name(), bObfuscateSetting, Starter.getCurrentSettingsData().getTechnology(false) + "/" + Starter.getCurrentSettingsData().getProtocol(false), isVirtual);

   }

   private String setFilterToolTip(String sFilterRegion, String sFilterGroup, boolean bObfuscate, String sTechnologyAndProtocol, boolean isVirtual)
   {
      String sFilterText = sFilterRegion + " & " + sFilterGroup + " (Obfuscate: " + String.valueOf(bObfuscate) + ") & " + sTechnologyAndProtocol + " & Virtual Locations: " + String.valueOf(isVirtual);
      m_filterLabel.setToolTipText("click here to refresh the Server list. Current Filter: " + sFilterText);

      return sFilterText;
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
            boolean rc = NvpnCallbacks.executeConnect(loc, "NordVPN Connect", "NordVPN Connect");
            if (false == rc)
            {
               activateTreeNode(null);
            }
            else
            {
               UtilMapGeneration.zoomIn(loc);
            }
         }
      }
      else if (m_tree.getLastSelectedPathComponent() instanceof JCountryNode)
      {
         JCountryNode node = (JCountryNode) m_tree.getLastSelectedPathComponent();
         if (node != m_tree.getModel().getRoot() && node != null)
         {
            CurrentLocation loc = new CurrentLocation(((JCountryNode) node).getLocation());
            boolean rc = NvpnCallbacks.executeConnect(loc, "NordVPN Connect", "NordVPN Connect");
            if (false == rc)
            {
               activateTreeNode(null);
            }
            else
            {
               UtilMapGeneration.zoomIn(Starter.getCurrentServer(false));
            }
         }
      }
   }
}
