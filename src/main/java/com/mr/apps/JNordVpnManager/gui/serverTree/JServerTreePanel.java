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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import com.mr.apps.JNordVpnManager.geotools.UtilMapGeneration;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnServers;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

@SuppressWarnings("serial")
public class JServerTreePanel extends JPanel implements TreeSelectionListener
{
   private static final int MIN_CHARS_FOR_FILTER = 3;
   private static JTree m_tree = null;
   private JTextField m_filterTextField = null;
   private static String m_filterText = "";
   private boolean m_lockUpdate = false;
   private JButton m_buttonRefresh = null;
   private Color m_buttonDefaultFgColor = null;
   private static boolean m_statusInitServerList = true;
   private static boolean m_skipValueChangedEvent = false;

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
      ImageIcon imageLabel = new ImageIcon(Starter.class.getResource("resources/icons/search_in_tree_32.png"));
      JLabel filterLabel = new JLabel(imageLabel);
      filterLabel.setToolTipText("Filter for VPN Servers");
      filterPanel.add(filterLabel, BorderLayout.LINE_START);
      m_filterTextField = new JTextField();
      m_filterTextField.setToolTipText("<html><font face=\"sansserif\" color=\"black\">Filter requires min. " + MIN_CHARS_FOR_FILTER + " characters!<br>Press Right Mouse Button to reset.</font></html>");
      m_filterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
         public void keyReleased(java.awt.event.KeyEvent evt)
         {
            if (m_filterTextField.getText().length() >= MIN_CHARS_FOR_FILTER)
            {
               // activate Filter
               m_filterText = m_filterTextField.getText().toLowerCase();
               updateFilterTreeCB(m_filterText);
            }
            else if (!m_filterText.isBlank())
            {
               // reset Filter
               m_filterText = "";
               updateFilterTreeCB(m_filterText);
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
               updateFilterTreeCB(m_filterText);
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
            if (true == m_statusInitServerList || JOptionPane.showConfirmDialog(null, "Update of the server list takes some time.\nDo you want to continue?", "Please confirm",
                  JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
               DefaultMutableTreeNode root = createServerTree(true);
               m_tree.setModel(new MyModel(root));
               Starter.updateServer();
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
      this.add(jsp, BorderLayout.CENTER); // jsp in center: automatic resize!!! ;)
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
    * @param filterText is the filter text (not case sensitive)
    */
   private void updateFilterTreeCB(String filterText)
   {
      // avoid multiple calls
      if (m_lockUpdate) return;
      m_lockUpdate = true;

      // create the server list tree based on the current content
      DefaultMutableTreeNode root = createServerTree(false);
      m_tree.setModel(new MyModel(root));
      if (!filterText.isBlank())
      {
         // filter
         for (int r = 0; r < m_tree.getRowCount(); r++)
         {
            // in case of active filter we expand all tree nodes
            m_tree.expandRow(r);
         }
      }
      else
      {
         // no filter - navigate tree to current server
         CurrentLocation loc = Starter.getCurrentServer();
         if (null != loc)
         {
            JServerTreePanel.activateTreeNode(loc);            
         }
      }
      m_lockUpdate = false;
   }

   /**
    * Initialize the server tree
    * @return the created tree ScrollPane
    */
   private JScrollPane initTree()
   {
      // create the server tree without update from NordVPN (init/empty or from user prefs)
      DefaultMutableTreeNode root = createServerTree(false);
      DefaultTreeModel model = new MyModel(root);
      m_tree = new JTree(model);
      m_tree.addTreeSelectionListener(this);
      m_tree.setCellRenderer(new MyRenderer());
      m_tree.setRootVisible(false);
      m_tree.setShowsRootHandles(true);

      JScrollPane jsp = new JScrollPane(m_tree);
      jsp.setPreferredSize(new Dimension(200,500));

      return jsp;
   }

   /**
    * Create the server list tree.
    * @param update if true, the server list is updated from NordVPN, else the server list stored in user preferences is used
    * @return the root server node
    */
   private DefaultMutableTreeNode createServerTree(boolean update)
   {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("Serverlist");
      ArrayList<String> vpnServers = new ArrayList<String>(); 

      String serverPrefString = NvpnServers.getServerList(update);
      if (null != serverPrefString && !serverPrefString.isBlank())
      {
         String[] saServerList = serverPrefString.split(Location.SERVERID_LIST_SEPARATOR);
         if (saServerList.length > 0)
         {
            for (String countries : saServerList)
            {
               String[] saCountryCities = countries.split(Location.SERVERID_SEPARATOR);
               if (saCountryCities.length == 2)
               {
                  String country = saCountryCities[0];
                  String cities = saCountryCities[1];

                  DefaultMutableTreeNode countryNode = null;
                  boolean matchCountry = false;
                  if (m_filterText.isBlank() || country.toLowerCase().contains(m_filterText))
                  {
                     countryNode = new DefaultMutableTreeNode(country.replace('_', ' '));
                     root.add(countryNode);
                     matchCountry = true;
                  }

                  String[] saCities = cities.split("/");
                  for (String city : saCities)
                  {
                     if (m_filterText.isBlank() || city.toLowerCase().contains(m_filterText) || matchCountry)
                     {
                        if (null == countryNode)
                        {
                           countryNode = new DefaultMutableTreeNode(country);
                           root.add(countryNode);
                        }
                        countryNode.add(new JServerNode(country, city));
                        if (!vpnServers.contains(city + Location.SERVERID_SEPARATOR + country)) vpnServers.add(city + Location.SERVERID_SEPARATOR + country);
                     }
                  }
               }
               else
               {
                  Starter._m_logError.TranslatorError(10100,
                        "Parsing Error",
                        "The Server Countries List String cannot be parsed:\n" +
                        saCountryCities);
                  return root;
               }
            }
         }
         else
         {
            Starter._m_logError.TranslatorError(10100,
                  "Parsing Error",
                  "The Server List String cannot be parsed:\n" +
                  serverPrefString);
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
         
         // actualize map
         UtilMapGeneration.changeVpnServerLocationsMapLayer(vpnServers);
         UtilMapGeneration.zoomServerLayer();
         Starter._m_logError.TranslatorInfo("Refreshed " + vpnServers.size() + " VPN Servers.");
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
    * @param loc is the location
    * @return if found, the tree path of the location, else null
    */
   private static TreePath findNode(Location loc)
   {
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) m_tree.getModel().getRoot();
      Enumeration<TreeNode> e = root.preorderEnumeration();

      String s = loc.getCity();
      String p = loc.getCountry();
      
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
         else
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
         else
         {
            DefaultMutableTreeNode countryNode = (DefaultMutableTreeNode) node;
            TreeNode[] path = countryNode.getPath();
            System.out.println((countryNode.isLeaf() ? "  - " : "+ ") + path[path.length - 1]);
         }
      }
   }

   @Override
   public void valueChanged(TreeSelectionEvent e)
   {
      if (m_skipValueChangedEvent) return;

      try
      {
         JServerNode node = (JServerNode) m_tree.getLastSelectedPathComponent();
         if (node != m_tree.getModel().getRoot() && node != null)
         {
            Location loc = ((JServerNode) node).getLocation();
            String msg = NvpnCallbacks.executeConnect(loc);
            if (NvpnCallbacks.isLastError()) msg = NvpnCallbacks.getLastError();
            JOptionPane.showMessageDialog(null, msg, "NordVPN Connect", JOptionPane.INFORMATION_MESSAGE);
         }
      }
      catch (Exception e1)
      {
         // TODO: do nothing (country selected)
      }
   }
   
}
