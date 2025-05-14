/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces;

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
import javax.swing.JLabel;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

/**
 * Class for ToolBar Commands.<p>
 * This Class defines all available ToolBar Commands
 */
public class Command
{
   // Supported GUI Types
   public static final int                     TYPE_CUSTOMIZE           = 1;
   public static final int                     TYPE_BUTTON              = 2;
   public static final int                     TYPE_CHECKBOX            = 3;
   public static final int                     TYPE_COMBOBOX            = 4;

   // Declaration of the available Command UId's (HashMap key)
   public static final String                  VPN_CMD_DISCONNECT       = "VPN_CMD_DISCONNECT";
   public static final String                  VPN_CMD_RECONNECT        = "VPN_CMD_RECONNECT";
   public static final String                  VPN_CMD_QUICKCONNECT     = "VPN_CMD_QUICKCONNECT";
   public static final String                  VPN_CMD_TIMER_CONNECT    = "VPN_CMD_TIMER_CONNECT";
   public static final String                  VPN_SET_KILLSWITCH       = "VPN_SET_KILLSWITCH";
   public static final String                  VPN_SET_OBFUSCATE        = "VPN_SET_OBFUSCATE";
   public static final String                  VPN_SET_POSTQUANTUM      = "VPN_SET_POSTQUANTUM";
   public static final String                  VPN_SET_THREATPROTECTION = "VPN_SET_THREATPROTECTION";
   public static final String                  VPN_SET_VIRTUALLOCATION  = "VPN_SET_VIRTUALSERVER";
   public static final String                  APP_PREF_AUTOCONNECT     = "APP_PREF_AUTOCONNECT";
   public static final String                  APP_PREF_AUTODISCONNECT  = "APP_PREF_AUTODISCONNECT";
   // Addons
   public static final String                  VPN_CMD_TIMER_RECONNECT  = "VPN_CMD_TIMER_RECONNECT";
   public static final String                  VPN_SET_TECHNOLOGY       = "VPN_SET_TECHNOLOGY";

   // class members
   private Component                           m_component              = null;
   private JLabel                              m_jLabel                 = null;
   private String                              m_id                     = null;
   private String                              m_command                = null;
   private int                                 m_iType                  = -1;
   private Map<Integer, JResizedIcon.IconUrls> m_iconUrl                = null;
   private Map<Integer, String>                m_toolTips               = null;
   private String[]                            m_options                = null; // [ComboBox] options
   private String                              m_sToolTipUI             = null; // used internally in case of change tool tip in updateUI
   private ImageIcon                           m_iconImage              = null; // used internally in case of change icons in updateUI
   private boolean                             m_enabled                = true; // used internally in updateUI

   // list of all available commands
   private static Map<String, Command>         m_allCommandsMap         = new HashMap<String, Command>();
   private static ArrayList<String>            m_allCommandKeysSorted   = null;

   // list with current commands in the ToolBar
   private static Vector<Command>              m_CommandsToolbarList    = null;

   /**
    * Constructor of the customize toolBar menu Command object
    * 
    */
   public Command()
   {
      m_iType = TYPE_CUSTOMIZE;
      m_iconUrl = new HashMap<Integer, JResizedIcon.IconUrls>();
      m_iconUrl.put(0, JResizedIcon.IconUrls.ICON_CUSTOMIZE_ADD_COMMAND_BAR);
      m_toolTips = new HashMap<Integer, String>();
      m_toolTips.put(0, "Click RMB to add a command to the Commands ToolBar");
   }

   /**
    * Constructor of the Command object
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
   public Command(String id, int iType, JResizedIcon.IconUrls iconUrl, String toolTip, String command)
   {
      m_component = null; // UI Element - set on creation
      m_id = id;
      m_iType = iType;
      m_iconUrl = new HashMap<Integer, JResizedIcon.IconUrls>();
      m_iconUrl.put(-1, iconUrl);
      m_command = command;
      m_toolTips = new HashMap<Integer, String>();
      m_toolTips.put(-1, toolTip);
   }

   /**
    * Constructor of the Command object with multiple icons
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
   public Command(String id, int iType, HashMap<Integer, JResizedIcon.IconUrls> iconUrls, HashMap<Integer, String> toolTips, String[] labels, String command)
   {
      m_component = null; // UI Element - set on creation
      m_id = id;
      m_iType = iType;
      m_iconUrl = iconUrls;
      m_toolTips = toolTips;
      m_options = labels;
      m_command = command;
   }

   /**
    * initialize all available commands (ONCE per program lifetime)
    */
   public static void initAllCommands()
   {
      // ...add the Basis Commands
      addCommand(new Command(VPN_CMD_DISCONNECT, TYPE_BUTTON,
                  JResizedIcon.IconUrls.ICON_DISCONNECT,
                  "Press Button to Disconnect from the VPN Server",
                  "VpnDisconnect"));

      addCommand(new Command(VPN_CMD_RECONNECT, TYPE_BUTTON,
                  JResizedIcon.IconUrls.ICON_RECONNECT,
                  "Press Button to [Re]connect the VPN Server",
                  "VpnReconnect"));

      addCommand(new Command(VPN_CMD_QUICKCONNECT, TYPE_BUTTON,
                  JResizedIcon.IconUrls.ICON_QUICKCONNECT,
                  "Press Button to quick connect to a VPN Server",
                  "VpnQuickconnect"));

      HashMap<Integer, JResizedIcon.IconUrls> iconUrls = new HashMap<Integer, JResizedIcon.IconUrls>();
      iconUrls.put(-1, JResizedIcon.IconUrls.ICON_TIMER_PAUSE);  // used on initialization for Quick Command ToolBar
      iconUrls.put(0, JResizedIcon.IconUrls.ICON_TIMER_PAUSE);   // used in case of STATUS_CONNECTED
      iconUrls.put(1, JResizedIcon.IconUrls.ICON_TIMER_CONNECT); // used in case of STATUS_DISCONNECTED
      HashMap<Integer, String> toolTips= new HashMap<Integer, String>();
      toolTips.put(-1, "Pause VPN Server Connection"); // used on initialization for Quick Command ToolBar (TODO: list here all the possible tool tips..)
      addCommand(new Command(VPN_CMD_TIMER_CONNECT, TYPE_BUTTON,
                  iconUrls,
                  toolTips,
                  null,
                  "VpnTimerConnect"));

      addCommand(new Command(VPN_SET_KILLSWITCH, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_KILLSWITCH,
                  "Click CheckBox to change the VPN Setting for Killswitch",
                  "VpnSetKillswitch"));

      addCommand(new Command(VPN_SET_OBFUSCATE, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_OBFUSCATE,
                  "Click CheckBox to change the VPN Setting for Obfuscate",
                  "VpnSetObfuscate"));

      addCommand(new Command(VPN_SET_POSTQUANTUM, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_POSTQUANTUM,
                  "Click CheckBox to change the VPN Setting for Post-Quantum",
                  "VpnSetPostQuantum"));

      addCommand(new Command(VPN_SET_THREATPROTECTION, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_THREATPROTECTION,
                  "Click CheckBox to change the VPN Setting for Threat Protection",
                  "VpnSetThreatprotection"));

      addCommand(new Command(VPN_SET_VIRTUALLOCATION, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_VIRTUALLOCATION,
                  "Click CheckBox to change the VPN Setting for Virtual Location",
                  "VpnSetVirtualLocation"));

      addCommand(new Command(APP_PREF_AUTOCONNECT, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_APP_PREF_AUTOCONNECT,
                  "Click CheckBox to change User Preferences for Auto Connect to VPN on Application Start",
                  "AppPrefAutoConnect"));

      addCommand(new Command(APP_PREF_AUTODISCONNECT, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_APP_PREF_AUTODISCONNECT,
                  "Click CheckBox to change User Preferences for Auto Disconnect from VPN on Application Exit",
                  "AppPrefAutoDisconnect"));

      iconUrls = new HashMap<Integer, JResizedIcon.IconUrls>();
      iconUrls.put(-1, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY);  // used on initialization for Quick Command ToolBar
      iconUrls.put(0, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY);
      iconUrls.put(1, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY);
      iconUrls.put(2, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY);
      iconUrls.put(3, JResizedIcon.IconUrls.ICON_VPN_TECHNOLOGY);
      toolTips = new HashMap<Integer, String>();
      toolTips.put(-1, "Click Drop-Down Button to change the VPN Setting for Technology/Protocol");
      toolTips.put(0, "Click here to change the VPN Setting for Technology to NORDLYNX");
      toolTips.put(1, "Click here to change the VPN Setting for Technology to NORDWHISPER");
      toolTips.put(2, "Click here to change the VPN Setting for Technology to OPENVPN/TCP");
      toolTips.put(3, "Click here to change the VPN Setting for Technology to OPENVPN/UDP");
      String[] options = {"NORDLYNX", "NORDWHISPER", "OPENVPN/TCP", "OPENVPN/UDP"};
      addCommand(new Command(VPN_SET_TECHNOLOGY, TYPE_COMBOBOX,
                  iconUrls,
                  toolTips,
                  options,
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
    * (Initialize) Add an additional Icon URL to the command
    * 
    * @param key
    *           is the unique id
    * @param sIconUrl
    *           is the new icon URL
    */
   public void addIconUrl(int key, JResizedIcon.IconUrls sIconUrl)
   {
      m_iconUrl.put(key, sIconUrl);
   }

   /**
    * Get the icon URL of the command by index.
    * @return the icon URL of the command with a specific index
    */
   public JResizedIcon.IconUrls getIconUrl(int idx)
   {
      return m_iconUrl.get(idx);
   }

   /**
    * Get the main icon URL of the command.
    * @return the main icon URL of the command
    */
   public JResizedIcon.IconUrls getIconUrl()
   {
      return m_iconUrl.get(-1);
   }

   /**
    * Get the size of Options of the command.
    * @return the size of options of the command
    */
   public int getOptionsSize()
   {
      return m_options.length;
   }

   /**
    * Get Option for ComboBox List Index
    * 
    * @param index
    *           is the option index
    * @return the option for ComboBox list index
    */
   public String getOption(int index)
   {
      return m_options[index];
   }

   /**
    * Set Options for Combobox
    * @param options are the options for the ComboBox
    */
   public void setOptions(String[] options)
   {
      this.m_options = options;
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
   public ImageIcon getIconImage()
   {
      return m_iconImage;
   }

   /**
    * Set the icon Image of the command.<p>
    * Used in updateUI() to change the imageIcon of the command component. 
    * 
    * @param iconImage
    *           is the icon URL of the command
    */
   public void setIconImage(ImageIcon iconImage)
   {
      m_iconImage = iconImage;
   }

   /**
    * Get the enabled attribute of the command.<p>
    * Used to change the enabled attribute of the command component for updateUI().
    * 
    * @return the icon URL of the command
    */
   public boolean isEnabled()
   {
      return m_enabled;
   }

   /**
    * Set the enabled attribute of the command.<p>
    * Used in updateUI() to change the enabled attribute of the command component. 
    * 
    * @param enabled
    *           is the icon URL of the command
    */
   public void setEnabled(boolean enabled)
   {
      m_enabled = enabled;
   }

   /**
    * Get the toolTip from the list of the command.<p>
    * Used to change the toolTip of the command component for updateUI().
    * 
    * @return the toolTip of the command
    */
   public String getToolTip(int idx)
   {
      return m_toolTips.get(idx);
   }

   /**
    * Get the current toolTip of the command.<p>
    * Used to change the toolTip of the command component for updateUI().
    * 
    * @return the current toolTip of the command - Set before with setToolTip(idx) 
    */
   public String getToolTip()
   {
      return (null == m_sToolTipUI) ? m_toolTips.get(-1) : m_sToolTipUI;
   }

   /**
    * Set the current toolTip of the command.<p>
    * The value is used in updateUI() -> call updateToolTipUI() to change the toolTip of the command component. 
    * 
    * @param sToolTip
    *           is the current toolTip of the command
    */
   public void setToolTip (String sToolTip)
   {
      m_sToolTipUI = sToolTip;
   }

   /**
    * Method to update the ToolTip for the command component(s).
    * <p>
    * To be called from Command updateUI().
    * 
    * @param sToolTip
    *           is the new ToolTip
    */
   public void updateToolTipUI(String sToolTip)
   {
      switch (this.m_iType)
      {
         case TYPE_BUTTON :
            ((JButton)this.getComponent()).setToolTipText(sToolTip);
            break;
         case TYPE_CHECKBOX :
            ((JCheckBox)this.getComponent()).setToolTipText(sToolTip);
            ((JLabel)this.getJLabel()).setToolTipText(sToolTip);
            break;
         case TYPE_CUSTOMIZE :
            break;
         default:
            Starter._m_logError.TraceDebug("Todo: Used type not implemented yet in class Command.updateToolTipUI(): " + this.m_iType);
            break;
      }
   }

   /**
    * Invoke the Method to get the Command value
    * @return the command specific value
    */
   public Object getValue()
   {
      return CallCommand.invokeBasisMethod(this, CoreCommandClass.METHOD_GET, null, null);
   }

   /**
    * Invoke the Method to update the Command specific GUI element (e.g. CheckBox status)
    * @return the command specific return value
    */
   public Object updateUI()
   {
      return CallCommand.invokeCommandMethod(this, CoreCommandClass.METHOD_UPDATE_UI);
   }

   /**
    * Invoke the Method to execute the Command
    * @return the command specific return value
    */
   public Object execute(ActionEvent e)
   {
      
      return CallCommand.invokeEventMethod(this, CoreCommandClass.METHOD_EXECUTE, e);
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

