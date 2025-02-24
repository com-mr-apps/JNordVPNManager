/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
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
   public static final int TYPE_CUSTOMIZE = 1;
   public static final int TYPE_BUTTON    = 2;
   public static final int TYPE_CHECKBOX  = 3;

   // Declaration of the available Command UId's (HashMap key)
   public static final String          VPN_CMD_DISCONNECT       = "VPN_CMD_DISCONNECT";
   public static final String          VPN_CMD_RECONNECT        = "VPN_CMD_RECONNECT";
   public static final String          VPN_CMD_QUICKCONNECT     = "VPN_CMD_QUICKCONNECT";
   public static final String          VPN_CMD_TIMER_CONNECT    = "VPN_CMD_TIMER_CONNECT";
   public static final String          VPN_SET_KILLSWITCH       = "VPN_SET_KILLSWITCH";
   public static final String          VPN_SET_OBFUSCATE        = "VPN_SET_OBFUSCATE";
   public static final String          VPN_SET_POSTQUANTUM      = "VPN_SET_POSTQUANTUM";
   public static final String          VPN_SET_THREATPROTECTION = "VPN_SET_THREATPROTECTION";
   public static final String          VPN_SET_VIRTUALLOCATION  = "VPN_SET_VIRTUALSERVER";
   public static final String          APP_PREF_AUTOCONNECT     = "APP_PREF_AUTOCONNECT";
   public static final String          APP_PREF_AUTODISCONNECT  = "APP_PREF_AUTODISCONNECT";
   // Addons
   public static final String          VPN_CMD_TIMER_RECONNECT  = "VPN_CMD_TIMER_RECONNECT";

   // class members
   private Component                     m_component             = null;
   private String                        m_id                    = null;
   private int                           m_iType                 = -1;
   private Vector<JResizedIcon.IconUrls> m_iconUrl               = null;
   private String                        m_toolTip               = null;
   private String                        m_command               = null;
   private ImageIcon                     m_iconImage             = null; // used internally in case of change icons in updateUI
   private boolean                       m_enabled               = true; // used internally in updateUI

   // list of all available commands
   private static Map<String, Command> m_allCommandsMap    = new HashMap<String, Command>();
   private static ArrayList<String> m_allCommandKeysSorted = null;

   // list with current commands in the ToolBar
   private static Vector<Command> m_CommandsToolbarList = null;

   /**
    * Constructor of the customize toolBar menu Command object
    * 
    */
   public Command()
   {
      m_iType = TYPE_CUSTOMIZE;
      m_toolTip = "Click RMB to add a command to the Commands ToolBar";
      m_iconUrl = new Vector<JResizedIcon.IconUrls>();
      m_iconUrl.add(JResizedIcon.IconUrls.ICON_CUSTOMIZE_ADD_COMMAND_BAR);
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
      m_iconUrl = new Vector<JResizedIcon.IconUrls>();
      m_iconUrl.add(iconUrl);
      m_toolTip = toolTip;
      m_command = command;
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
   public Command(String id, int iType, Vector<JResizedIcon.IconUrls> iconUrls, String toolTip, String command)
   {
      m_component = null; // UI Element - set on creation
      m_id = id;
      m_iType = iType;
      m_iconUrl = iconUrls;
      m_toolTip = toolTip;
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
                  "Click here to Disconnect from the VPN Server",
                  "VpnDisconnect"));

      addCommand(new Command(VPN_CMD_RECONNECT, TYPE_BUTTON,
                  JResizedIcon.IconUrls.ICON_RECONNECT,
                  "Click here to [Re]connect the VPN Server",
                  "VpnReconnect"));

      addCommand(new Command(VPN_CMD_QUICKCONNECT, TYPE_BUTTON,
                  JResizedIcon.IconUrls.ICON_QUICKCONNECT,
                  "Click here to quick connect to a VPN Server",
                  "VpnQuickconnect"));

      addCommand(new Command(VPN_CMD_TIMER_CONNECT, TYPE_BUTTON,
                  JResizedIcon.IconUrls.ICON_TIMER_PAUSE,
                  "Pause VPN Server Connection", // used on initialization - as long as updateUI() is not called
                  "VpnTimerConnect"));

      addCommand(new Command(VPN_SET_KILLSWITCH, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_KILLSWITCH,
                  "Click here to change the VPN Setting for Killswitch",
                  "VpnSetKillswitch"));

      addCommand(new Command(VPN_SET_OBFUSCATE, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_OBFUSCATE,
                  "Click here to change the VPN Setting for Obfuscate",
                  "VpnSetObfuscate"));

      addCommand(new Command(VPN_SET_POSTQUANTUM, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_POSTQUANTUM,
                  "Click here to change the VPN Setting for Post-Quantum",
                  "VpnSetPostQuantum"));

      addCommand(new Command(VPN_SET_THREATPROTECTION, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_THREATPROTECTION,
                  "Click here to change the VPN Setting for Threat Protection",
                  "VpnSetThreatprotection"));

      addCommand(new Command(VPN_SET_VIRTUALLOCATION, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_VPN_SET_VIRTUALLOCATION,
                  "Click here to change the VPN Setting for Virtual Location",
                  "VpnSetVirtualLocation"));

      addCommand(new Command(APP_PREF_AUTOCONNECT, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_APP_PREF_AUTOCONNECT,
                  "Click here to change User Preferences for Auto Connect to VPN on Application Start",
                  "AppPrefAutoConnect"));

      addCommand(new Command(APP_PREF_AUTODISCONNECT, TYPE_CHECKBOX,
                  JResizedIcon.IconUrls.ICON_APP_PREF_AUTODISCONNECT,
                  "Click here to change User Preferences for Auto Disconnect from VPN on Application Exit",
                  "AppPrefAutoDisconnect"));

      // make a sorted list
      m_allCommandKeysSorted = new ArrayList<String>(m_allCommandsMap.keySet());
      Collections.sort(m_allCommandKeysSorted);

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
    * Get the icon URL of the command.
    * @return the icon URL of the command
    */
   public Vector<JResizedIcon.IconUrls> getIconUrl()
   {
      return m_iconUrl;
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
      Command.m_CommandsToolbarList = commandsToolbarList;
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
    * Get the toolTip of the command.<p>
    * Used to change the toolTip of the command component for updateUI().
    * 
    * @return the toolTip of the command
    */
   public String getToolTip()
   {
      return m_toolTip;
   }

   /**
    * Set the toolTip of the command.<p>
    * Used in updateUI() to change the toolTip of the command component. 
    * 
    * @param sToolTip
    *           is the toolTip of the command
    */
   public void setToolTip (String sToolTip)
   {
      m_toolTip = sToolTip;
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
    * @return the tring representation of the object
    */
   public String toString()
   {
      return m_id + " " + m_command;
   }
}

