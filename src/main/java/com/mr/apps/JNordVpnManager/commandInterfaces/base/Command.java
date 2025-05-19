/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces.base;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

/**
 * Class for ToolBar Commands.<p>
 * This Class defines all available ToolBar Commands
 */
public class Command implements CommandInterface
{
   // Base states for command gadget properties
   public static final String                     BASE_STATUS_KEY          = "BASE";  // used for initialization and (unused) command list
   public static final String                     DISABLED_STATUS_KEY      = "DISABLED";

   protected static final IconSize                BUTTON_ICON_SIZE         = JResizedIcon.IconSize.MEDIUM;

   // Supported GUI Types
   public static final int                        TYPE_CUSTOMIZE           = 1;
   public static final int                        TYPE_BUTTON              = 2;
   public static final int                        TYPE_CHECKBOX            = 3;
   public static final int                        TYPE_COMBOBOX            = 4;

   // Declaration of the available Command UId's (HashMap key)
   public static final String                     VPN_CMD_DISCONNECT       = "VPN_CMD_DISCONNECT";
   public static final String                     VPN_CMD_RECONNECT        = "VPN_CMD_RECONNECT";
   public static final String                     VPN_CMD_QUICKCONNECT     = "VPN_CMD_QUICKCONNECT";
   public static final String                     VPN_CMD_TIMER_CONNECT    = "VPN_CMD_TIMER_CONNECT";
   public static final String                     VPN_SET_KILLSWITCH       = "VPN_SET_KILLSWITCH";
   public static final String                     VPN_SET_OBFUSCATE        = "VPN_SET_OBFUSCATE";
   public static final String                     VPN_SET_POSTQUANTUM      = "VPN_SET_POSTQUANTUM";
   public static final String                     VPN_SET_THREATPROTECTION = "VPN_SET_THREATPROTECTION";
   public static final String                     VPN_SET_VIRTUALLOCATION  = "VPN_SET_VIRTUALSERVER";
   public static final String                     APP_PREF_AUTOCONNECT     = "APP_PREF_AUTOCONNECT";
   public static final String                     APP_PREF_AUTODISCONNECT  = "APP_PREF_AUTODISCONNECT";
   // Addons
   public static final String                     VPN_CMD_TIMER_RECONNECT  = "VPN_CMD_TIMER_RECONNECT";
   public static final String                     VPN_SET_TECHNOLOGY       = "VPN_SET_TECHNOLOGY";

   // list of all available commands
   private static Map<String, Command>            m_allCommandsMap         = new HashMap<String, Command>();
   private static ArrayList<String>               m_allCommandKeysSorted   = null;

   // list with current commands in the ToolBar
   private static Vector<Command>                 m_CommandsToolbarList    = null;

   // class members
   private Component                              m_component              = null;
   private JLabel                                 m_jLabel                 = null;
   private String                                 m_id                     = null;
   private String                                 m_command                = null;
   private int                                    m_iType                  = -1;

   private String                                 m_sToolTipUI             = null; // used internally in case of variable tool tip

   private String                                 m_statusUI               = BASE_STATUS_KEY;

   // enhanced class members
   protected CommandGadgetProperties              m_baseProperties         = null;
   protected Map<String, CommandGadgetProperties> m_properties             = null;
   protected String[]                             m_statusIds              = null;

   /**
    * Basis Constructor for each Command
    * @param id
    *           is the unique command Id (HashMap key)
    * @param iType
    *           is the UI type
    * @param command
    *           is the command (method) name
    */
   public Command(String id, int iType, String command)
   {
      m_component = null; // UI Element - set on creation
      m_jLabel = null; // UI Element - set on creation
      m_id = id;
      m_iType = iType;
      m_command = command;
   }

   /**
    * Constructor of a simple Command object
    * 
    * @param id
    *           is the unique command Id (HashMap key)
    * @param iType
    *           is the UI type
    * @param iconUrl
    *           is the icon URL
    * @param toolTip
    *           is the toolTip
    * @param command
    *           is the command (method) name
    */
   public Command(String id, int iType, JResizedIcon.IconUrls iconUrl, String toolTip, Boolean enabled, String command)
   {
      this(id, iType, command);

      String statusId = BASE_STATUS_KEY;
      m_statusIds = new String[] {statusId};

      CommandGadgetProperties ccbp = new CommandGadgetProperties(null, toolTip, enabled, iconUrl, BUTTON_ICON_SIZE);
      m_baseProperties = ccbp;

      m_properties = new HashMap<String, CommandGadgetProperties>();
      m_properties.put(statusId, m_baseProperties);
   }

   /**
    * Constructor of the customize toolBar menu Command object
    * 
    */
   public Command()
   {
      this("CustomizeToolBar", 
            TYPE_CUSTOMIZE, 
            JResizedIcon.IconUrls.ICON_CUSTOMIZE_ADD_COMMAND_BAR, 
            "Click RMB to add a command to the Commands ToolBar",
            true,
            null);
   }

   /**
    * initialize all available commands (ONCE per program lifetime)
    */
   public static void initAllCommands()
   {
      // ...add the Basis Commands
      Command cmd;
      addCommand(new CommandButton(VPN_CMD_DISCONNECT,
            new String[] { BASE_STATUS_KEY, "CONNECTED" , "DISCONNECTED"}, // keys
            new String[] { null, null, null }, // labels (no)
            new String[] { "Command to Disconnect from VPN Server.", "Disconnect from the VPN Server.", "Not Connected." },
            new Boolean[] {true, true, false },
            new JResizedIcon.IconUrls[] {JResizedIcon.IconUrls.ICON_DISCONNECT, JResizedIcon.IconUrls.ICON_DISCONNECT, JResizedIcon.IconUrls.ICON_DISCONNECT},
                  "VpnDisconnect"));

      addCommand(new CommandButton(VPN_CMD_RECONNECT,
            JResizedIcon.IconUrls.ICON_RECONNECT,
            "Command to to [Re]connect the VPN Server.",
            null,
            "VpnReconnect"));

      addCommand(new CommandButton(VPN_CMD_QUICKCONNECT,
            JResizedIcon.IconUrls.ICON_QUICKCONNECT,
            "Command to to quick connect to a VPN Server.",
            null,
            "VpnQuickconnect"));

      addCommand(new CommandButton(VPN_CMD_TIMER_CONNECT,
            new String[] { BASE_STATUS_KEY, "PAUSE" , "CONNECT", DISABLED_STATUS_KEY}, // keys
            new String[] { null, null, null, null }, // labels (no)
            new String[] { "Command to pause VPN Server Connection.", "Pause VPN Server Connection.", "[Re]Connect to VPN Server", "Not Logged in." },
            new Boolean[] {true, true, true, false },
            new JResizedIcon.IconUrls[] {JResizedIcon.IconUrls.ICON_TIMER_PAUSE, JResizedIcon.IconUrls.ICON_TIMER_PAUSE, JResizedIcon.IconUrls.ICON_TIMER_CONNECT, JResizedIcon.IconUrls.ICON_TIMER_CONNECT},
            "VpnTimerConnect"
            ));

      addCommand(new CommandCheckBox(VPN_SET_KILLSWITCH,
            JResizedIcon.IconUrls.ICON_VPN_SET_KILLSWITCH,
            "VPN Setting for Killswitch.",
            "VpnSetKillswitch"));

      cmd = new CommandCheckBox(VPN_SET_OBFUSCATE,
            JResizedIcon.IconUrls.ICON_VPN_SET_OBFUSCATE,
            "VPN Setting for Obfuscation.",
            "VpnSetObfuscate");
      cmd.setToolTip(DISABLED_STATUS_KEY, "Obfuscated only available for technology 'OPENVPN'.");
      addCommand(cmd);

      cmd = new CommandCheckBox(VPN_SET_POSTQUANTUM,
            JResizedIcon.IconUrls.ICON_VPN_SET_POSTQUANTUM,
            "VPN Setting for Post-Quantum.",
            "VpnSetPostQuantum");
      cmd.setToolTip(DISABLED_STATUS_KEY, "Post-Quantum is not compatible with a dedicated IP, Meshnet, and OpenVPN/NORDWHISPER.");
      addCommand(cmd);

      addCommand(new CommandCheckBox(VPN_SET_THREATPROTECTION,
            JResizedIcon.IconUrls.ICON_VPN_SET_THREATPROTECTION,
            "VPN Setting for Threat Protection.",
            "VpnSetThreatprotection"));

      addCommand(new CommandCheckBox(VPN_SET_VIRTUALLOCATION,
            JResizedIcon.IconUrls.ICON_VPN_SET_VIRTUALLOCATION,
            "VPN Setting for Virtual Location.",
            "VpnSetVirtualLocation"));

      addCommand(new CommandCheckBox(APP_PREF_AUTOCONNECT,
            JResizedIcon.IconUrls.ICON_APP_PREF_AUTOCONNECT,
            "User Preferences for Auto Connect to VPN on Application Start.", 
            "AppPrefAutoConnect"));

      addCommand(new CommandCheckBox(APP_PREF_AUTODISCONNECT,
            JResizedIcon.IconUrls.ICON_APP_PREF_AUTODISCONNECT,
            "User Preferences for Auto Disconnect from VPN on Application Exit.", 
            "AppPrefAutoDisconnect"));
      addCommand(new CommandComboBox(VPN_SET_TECHNOLOGY,
            new String[] { BASE_STATUS_KEY, "NORDLYNX" , "NORDWHISPER", "OPENVPN/TCP", "OPENVPN/UDP", DISABLED_STATUS_KEY}, // keys
            new String[] { null, "NORDLYNX" , "NORDWHISPER", "OPENVPN/TCP", "OPENVPN/UDP", null }, // options (labels)
            new String[] { "VPN Setting for Technology/Protocol.", "Set Technology to NORDLYNX" , "Set Technology to NORDWHISPER", "Set Technology to OPENVPN/TCP", "Set Technology to OPENVPN/UDP", "Disabled VPN Setting for Technology/Protocol." },
            new Boolean[] {true, true, true, true, true, false }, // enabled
            new JResizedIcon.IconUrls[] {JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY},
            "VpnSetTechnology"));

      // make a sorted list
      m_allCommandKeysSorted = new ArrayList<String>(m_allCommandsMap.keySet());
      Collections.sort(m_allCommandKeysSorted);

      // initialize the current available commands in the ToolBar (from User Preferences)
      setCommandsToolbarList();

   }

   /**
    * Method to add a command to the application.
    * <p>
    * Used for addOns - must be called before initAllCommands() [..where the command list is sorted and pre processed]
    * 
    * @param cmd
    *           is the command to add
    */
   public static void addCommand(Command cmd)
   {
      if (null == m_allCommandsMap)
      {
         m_allCommandsMap = new HashMap<String, Command>();
      }
      m_allCommandsMap.put(cmd.getId(), cmd);
   }

   /**
    * Get a list of the commands that are not used ib the commands toolBar
    * @return the list of unused commands
    */
   public static Vector<Command> getListOfUnusedCommands()
   {
      Vector<Command> listOfUnusedCommands = new Vector<Command>();

      for (String x : m_allCommandKeysSorted)
      {
         Command cmd = m_allCommandsMap.get(x);
         if (m_CommandsToolbarList.contains(cmd)) continue;
         listOfUnusedCommands.add(cmd);
      }
      return listOfUnusedCommands;
   }
   
   /**
    * Get the GUI component of the command.
    * @return the GUI component representing the command
    */
   public Component getComponent()
   {
      return m_component;
   }

   /**
    * Set the GUI component of the command.
    * 
    * @param component
    *           is the GUI component representing the command
    */
   public void setComponent(Component component)
   {
      m_component = component;
   }

   /**
    * Get the (optional) GUI JLabel of the command.
    * @return the GUI JLabel of the command - <code>null </code> in case the component doesn't have a label (e.g. buttons)
    */
   public JLabel getJLabel()
   {
      return m_jLabel;
   }

   /**
    * Set the (optional) GUI JLabel of the command.
    * 
    * @param component
    *           is the GUI component representing the command
    */
   public void setJLabel(JLabel jLabel)
   {
      this.m_jLabel = jLabel;
   }

   /**
    * Get the unique Id of the command.
    * @return the unique Id of the command
    */
   public String getId()
   {
      return m_id;
   }

   /**
    * Get the type of the command element (Button, CheckBox, ...).
    * @return the type of the command element
    */
   public int getType()
   {
      return m_iType;
   }

   /**
    * Get the main icon URL of the command.
    * @return the main icon URL of the command
    */
   public JResizedIcon.IconUrls getIconUrl()
   {
      if (null != m_baseProperties) return m_baseProperties.getIconUrl();
      return null;
   }

   /**
    * Get the command method name.
    * 
    * @return the the command method name
    */
   public String getCommand()
   {
      return m_command;
   }

   /**
    * Get the command object.
    * 
    * @param cmdId
    *           is the unique command Id
    * 
    * @return the the command object
    */
   public static Command getObject(String cmdId)
   {
      return (null == m_allCommandsMap) ? null : m_allCommandsMap.get(cmdId);
   }

   /**
    * Get the list of commands that are added to the commands toolBar.
    * 
    * @return the the command method name
    */
   public static Vector<Command> getCommandsToolbarList()
   {
      return m_CommandsToolbarList;
   }

   /**
    * Set the list of commands that are added to the commands toolBar.
    * 
    * @param commandsToolbarList
    *           is the list of commands that are added to the commands toolBar
    */
   public static void setCommandsToolbarList(Vector<Command> commandsToolbarList)
   {
      m_CommandsToolbarList = commandsToolbarList;
   }

   /**
    * Set the list of commands that are added to the commands toolBar (from User Preferences).
    * 
    */
   public static void setCommandsToolbarList()
   {
      // initialize the current available commands in the ToolBar
      m_CommandsToolbarList = new Vector<Command>();

      // get the Commands ToolBar list items from User Preferences
      String savedCommandsToolBar = UtilPrefs.getCommandsToolbarIds();
      String[] saCommandIds = savedCommandsToolBar.split(";");
      for (String commandId : saCommandIds)
      {
         if (!commandId.isBlank())
         {
            Command cmd = m_allCommandsMap.get(commandId);
            if (null != cmd)
            {
               m_CommandsToolbarList.addElement((Command)cmd);
               Starter._m_logError.TraceDebug("Add Command to ToolBar: " + cmd.toString());
            }
            else
            {
               Starter._m_logError.TraceDebug("Command with id: '" + commandId + "' not found!");
            }
         }
      }
   }

   /**
    * Save the list of commands that are added to the commands toolBar in the User Preferences.
    */
   public static void saveCommandsToolbarListItems()
   {
      StringBuffer commandToolbarItemsIds = new StringBuffer();

      for (int i = 0; i < m_CommandsToolbarList.size(); i++)
      {
         Command cmd = m_CommandsToolbarList.get(i);
         if (null != cmd)
         {
            if (commandToolbarItemsIds.length() > 0) commandToolbarItemsIds.append(";");
            commandToolbarItemsIds.append(cmd.getId());
         }
      }
      if (commandToolbarItemsIds.length() > 0)
      {
         // Update the user preferences with the current commands list
         UtilPrefs.setCommandsToolbarIds(commandToolbarItemsIds.toString());
      }
   }

   /**
    * Insert a command in the Commands ToolBar List.
    * 
    * @param addCmd
    *           is the Command to add
    * @param iPos
    *           is the position
    */
   public static void insertCommandAt(Command addCmd, int iPos)
   {
      m_CommandsToolbarList.insertElementAt(addCmd, iPos);
   }

   /**
    * Remove the command from the Commands ToolBar List.
    * @return true if the list contained the specified element
    */
   public boolean removeCommandFromToolbarList()
   {
      return m_CommandsToolbarList.remove(this);
   }

   /**
    * Get the icon Image of the command.<p>
    * Used to change the imageIcon of the command component for updateUI().
    * 
    * @return the icon URL of the command
    */
   public ImageIcon getImageIcon()
   {
      if (null != m_baseProperties) return m_baseProperties.getImageIcon();
      return null;
   }

   public ImageIcon getImageIcon(String statusId)
   {
      if (null == m_properties) return getImageIcon();
      return m_properties.get(statusId.toUpperCase()).getImageIcon();
   }

   /**
    * Get the enabled attribute of the command.<p>
    * Used to change the enabled attribute of the command component for updateUI().
    * 
    * @return the enabled status of the command
    */
   public Boolean isEnabled()
   {
      if (null != m_baseProperties) return m_baseProperties.isEnabled();
      return null;
   }

   public Boolean isEnabled(String statusId)
   {
      if (null == m_properties) return isEnabled();
      return m_properties.get(statusId.toUpperCase()).isEnabled();
   }

   /**
    * Get the current toolTip of the command.<p>
    * Used to change the toolTip of the command component for updateUI().<br>
    * A variable tool tip can be set with cmd.setCurrentToolTip() - e.g. for command buttons "Execute: Command ..."
    * 
    * @return the current toolTip of the command 
    */
   public String getToolTip()
   {
      if (null != m_sToolTipUI) return m_sToolTipUI;
      if (null != m_baseProperties) return m_baseProperties.getToolTip();
      return null;
   }

   /**
    * Set the current toolTip of the command.<p>
    * The value is used in updateUI() for the command component. 
    * 
    * @param sToolTip
    *           is the current toolTip of the command
    */
   public void setCurrentToolTip (String sToolTip)
   {
      m_sToolTipUI = sToolTip;
   }

   /**
    * Method to overwrite a Tool Tip for the basis state
    * 
    * @param sToolTip
    *           is the Tool Tip
    */
   public void setToolTip(String sToolTip)
   {
      setToolTip(BASE_STATUS_KEY, sToolTip);
   }

   /**
    * Method to overwrite a Tool Tip for a specific state
    * 
    * @param statusId
    *           is the state Id
    * @param sToolTip
    *           is the Tool Tip
    */
   public void setToolTip(String statusId, String sToolTip)
   {
      if (null == m_properties) return;
      CommandGadgetProperties cp = m_properties.get(statusId.toUpperCase());
      cp.setToolTip(sToolTip);
      m_properties.put(statusId, cp);
      if (statusId.equalsIgnoreCase(BASE_STATUS_KEY)) m_baseProperties = cp;
   }

   public String getToolTip(String statusId)
   {
      if (null != m_sToolTipUI) return m_sToolTipUI; // required for customized tool tip with variables!
      if (null == m_properties) return null;
      return m_properties.get(statusId.toUpperCase()).getToolTip();
   }

   public String getLabel()
   {
      // if base label is not set, get the label from the base tool tip
      return (null != m_baseProperties.getLabel()) ? m_baseProperties.getLabel() : m_baseProperties.getToolTip();
   }

   public String getLabel(String statusId)
   {
      if (null == m_properties) return null;
      return m_properties.get(statusId.toUpperCase()).getLabel();
   }

   public String[] getStatusIds()
   {
      return m_statusIds;
   }

   public String getStatusUI()
   {
      return m_statusUI;
   }

   public void setStatusUI(String statusId)
   {
      this.m_statusUI = statusId;
   }

   /**
    * Method to update the command component(s) for the current status.
    * <p>
    * To be implemented in each Command updateUI().<br>
    * Set current status before call of updateUI with 'cmd.setStatus(statusId)'! 
    */
   public void updateCommandGadgetUI()
   {
      Boolean bEnabled = this.isEnabled(this.m_statusUI);
      String sToolTip = this.getToolTip(this.m_statusUI);
      switch (this.m_iType)
      {
         case TYPE_BUTTON :
            if (null != bEnabled) ((JButton)this.getComponent()).setEnabled(bEnabled);
            ((JButton)this.getComponent()).setIcon(this.getImageIcon(this.m_statusUI));
            if (null != sToolTip) ((JButton)this.getComponent()).setToolTipText(sToolTip);
            break;

         case TYPE_CHECKBOX :
            Boolean bSelected = ((CommandCheckBox)this).isSelected(this.m_statusUI);
            if (null != bSelected) ((JCheckBox)this.getComponent()).setSelected(bSelected);
            if (null != bEnabled)
            {
               ((JCheckBox)this.getComponent()).setEnabled(bEnabled);
               ((JLabel)this.getJLabel()).setEnabled(bEnabled);
            }
            if (null != sToolTip) 
            {
               ((JCheckBox)this.getComponent()).setToolTipText(sToolTip);
               ((JLabel)this.getJLabel()).setToolTipText(sToolTip);
            }
            break;

         case TYPE_COMBOBOX :
            sToolTip = this.getToolTip();
            ((JComboBox<?>)this.getComponent()).setSelectedItem(this.m_statusUI);
            if (null != bEnabled) ((JComboBox<?>)this.getComponent()).setEnabled(bEnabled);
            if (null != sToolTip) ((JComboBox<?>)this.getComponent()).setToolTipText(sToolTip);
            break;

         case TYPE_CUSTOMIZE :
            break;

         default:
            Starter._m_logError.TraceErr("[Internal Error (Command.updateCommandGadget)] Used type not implemented yet: " + this.m_iType);
            break;
      }
      this.m_sToolTipUI = null;
   }
   /**
    * Invoke the Method to get the Command value
    * @return the command specific value
    */
   public Object getValue()
   {
      return CallCommand.invokeBasisMethod(this, CommandInterface.METHOD_GET, null, null);
   }

   /**
    * Invoke the Method to update the Command specific GUI element (e.g. CheckBox status)
    * @return the command specific return value
    */
   public Object updateUI()
   {
      return CallCommand.invokeCommandMethod(this, CommandInterface.METHOD_UPDATE_UI);
   }

   /**
    * Invoke the Method to execute the Command
    * @return the command specific return value
    */
   public Object execute(ActionEvent e)
   {
      
      return CallCommand.invokeEventMethod(this, CommandInterface.METHOD_EXECUTE, e);
   }

   /**
    * Returns a string representation of the object.
    * @return the string representation of the object
    */
   public String toString()
   {
      return m_id + " " + m_command;
   }
}

