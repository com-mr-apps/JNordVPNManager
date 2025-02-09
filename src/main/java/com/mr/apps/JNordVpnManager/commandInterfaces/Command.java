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
   public static final String          VPN_CMD_RECONNECT       = "VPN_CMD_RECONNECT";
   public static final String          VPN_CMD_PAUSE           = "VPN_CMD_PAUSE";
   public static final String          VPN_SET_KILLSWITCH      = "VPN_SET_KILLSWITCH";
   public static final String          APP_PREF_AUTOCONNECT    = "APP_PREF_AUTOCONNECT";
   public static final String          APP_PREF_AUTODISCONNECT = "APP_PREF_AUTODISCONNECT";

   // class members
   private Component             m_component = null;
   private String                m_id        = null;
   private int                   m_iType     = -1;
   private JResizedIcon.IconUrls m_iconUrl   = null;
   private String                m_toolTip   = null;
   private String                m_command   = null;

   // list of all available commands
   private static Map<String, Command> m_allCommandsMap  = null;
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
      m_iconUrl = JResizedIcon.IconUrls.ICON_CUSTOMIZE_ADD_COMMAND_BAR;
   }

   /**
    * Constructor of the Command object
    * 
    * @param id
    *           is the unique command Id (HashMap key)
    * @param iType
    *           is the UI type
    * @param iconUrl
    *           is the icob URL
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
      m_iconUrl = iconUrl;
      m_toolTip = toolTip;
      m_command = command;
   }

   public static void initAllCommands()
   {
      if (null == m_allCommandsMap)
      {
         // initialize all available commands (ONCE per program lifetime)
         m_allCommandsMap  = new HashMap<String, Command>();

         m_allCommandsMap.put(VPN_CMD_RECONNECT, 
               new Command(VPN_CMD_RECONNECT, 
                     TYPE_BUTTON,JResizedIcon.IconUrls.ICON_RECONNECT,
                     "Click here to Reconnect the VPN Server",
                     "VpnReconnect"));
         m_allCommandsMap.put(VPN_CMD_PAUSE,
               new Command(VPN_CMD_PAUSE, TYPE_BUTTON,
                     JResizedIcon.IconUrls.ICON_TIMER_PAUSE,
                     "Click here to Pause the Connection to the VPN Server",
                     "VpnPause"));

         m_allCommandsMap.put(VPN_SET_KILLSWITCH,
               new Command(VPN_SET_KILLSWITCH, TYPE_CHECKBOX,
                     JResizedIcon.IconUrls.ICON_VPN_SET_KILLSWITCH,
                     "Click here to change the VPN Setting for Killswitch",
                     "VpnSetKillswitch"));

         m_allCommandsMap.put(APP_PREF_AUTOCONNECT,
               new Command(APP_PREF_AUTOCONNECT,
                     TYPE_CHECKBOX,
                     JResizedIcon.IconUrls.ICON_CMD_AUTOCONNECT_ON_START,
                     "Click here to change User Preferences for Auto Connect to VPN on Application Start",
                     "AppPrefAutoConnect"));
         m_allCommandsMap.put(APP_PREF_AUTODISCONNECT,
               new Command(APP_PREF_AUTODISCONNECT,
                     TYPE_CHECKBOX,
                     JResizedIcon.IconUrls.ICON_CMD_AUTODISCONNECT_ON_EXIT,
                     "Click here to change User Preferences for Auto Disconnect from VPN on Application Exit",
                     "AppPrefAutoDisconnect"));
         
         m_allCommandKeysSorted = new ArrayList<String>(m_allCommandsMap.keySet());
         Collections.sort(m_allCommandKeysSorted);

      }

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
            m_CommandsToolbarList.addElement((Command)cmd);
            Starter._m_logError.TraceDebug("Add Command to ToolBar: " + cmd.toString());
         }
      }

   }

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
   
   public Component getComponent()
   {
      return m_component;
   }
   public void setComponent(Component component)
   {
      m_component = component;
   }

   public String getId()
   {
      return m_id;
   }

   public int getType()
   {
      return m_iType;
   }

   public JResizedIcon.IconUrls getIconUrl()
   {
      return m_iconUrl;
   }

   public String getToolTip()
   {
      return m_toolTip;
   }

   public String getCommand()
   {
      return m_command;
   }

   public static Command getObject(String cmdId)
   {
      return (null == m_allCommandsMap) ? null : m_allCommandsMap.get(cmdId);
   }

  public static Vector<Command> getCommandsToolbarList()
   {
      return m_CommandsToolbarList;
   }

   public static void setCommandsToolbarList(Vector<Command> commandsToolbarList)
   {
      Command.m_CommandsToolbarList = commandsToolbarList;
   }

   /**
    * Save the "Commands ToolBar List Items" in the User Preferences.
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
    * Insert a command in the Commands ToolBar List
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

   public boolean removeCommandFromToolbarList()
   {
      return m_CommandsToolbarList.remove(this);
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
    * @return the command specific return value (boolean)
    */
   public Object updateUI()
   {
      
      return CallCommand.invokeComponentMethod(this, CoreCommandClass.METHOD_UPDATE_UI, m_component);
   }

   /**
    * Invoke the Method to execute the Command
    * @return the command specific return value (boolean)
    */
   public Object execute(ActionEvent e)
   {
      
      return CallCommand.invokeEventMethod(this, CoreCommandClass.METHOD_EXECUTE, e);
   }

   public String toString()
   {
      return m_id + " " + m_command;
   }
}

