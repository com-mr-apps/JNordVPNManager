/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.connectLine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicToolBarUI;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.Command;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

@SuppressWarnings("serial")
public class GuiCommandsToolBar extends JPanel implements ActionListener
{
   private static JToolBar m_toolBar = null;

   /**
    * Constructor for the Commands ToolBar.
    */
   public GuiCommandsToolBar()
   {
      super(new BorderLayout());

      // initialize the available ToolBar Commands
      Command.initAllCommands();

      // Create the Commands ToolBar
      m_toolBar = new JToolBar("JNordVPN Manager Command Bar");
      m_toolBar.setFloatable(true);
      this.createCommandsToolBar();

      // handle the focus lost/gained event in case of floating ToolBar Frame is closed 
      final HierarchyListener hierarchyListener = new HierarchyListener() {

         @Override
         public void hierarchyChanged(HierarchyEvent e)
         {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == 0) return;
            JToolBar bar = (JToolBar) e.getComponent();
            if (((BasicToolBarUI) bar.getUI()).isFloating())
            {
               Window topLevel = SwingUtilities.windowForComponent(bar);
               if (topLevel == null) return;
               if (topLevel.equals(Starter.getMainFrame()))
               {
                  Starter.setSkipWindowGainedFocus();
               }
            }
         }
      };
      m_toolBar.addHierarchyListener(hierarchyListener);

      // add the ToolBar to this panel
      this.add(m_toolBar, BorderLayout.CENTER);
   }

   /**
    * Create the Commands ToolBar by adding commands defined in the commands ToolBar list.
    * <p>
    * This method is called on initialization and on add/remove commands.
    */
   private void createCommandsToolBar()
   {
      if (null != m_toolBar) m_toolBar.removeAll();

      // we use a common popup menu for all 'Add'-Labels
      JPopupMenu customizePopUpMenuAdd = new JPopupMenu();
      JMenuItem title = new JMenuItem("Select Command to add:");
      title.setEnabled(false);
      customizePopUpMenuAdd.add(title);

      // ... and a common popup menu for all 'Remove/Move'-Buttons
      JPopupMenu customizePopUpMenu = new JPopupMenu();

      // get the list of commands to add (from user preferences)
      Vector<Command> commandsToolbarList = Command.getCommandsToolbarList();
      if (null != commandsToolbarList)
      {
         // Add the Customize Command
         Command tbCustomize = new Command();
         JPanel customizeButton = makeToolBarCustomizeButton(tbCustomize, 0, customizePopUpMenuAdd);
         m_toolBar.add(customizeButton);
         
         for (int i = 0; i < commandsToolbarList.size(); i++)
         {
            Command cmd = commandsToolbarList.get(i);
            if (null != cmd)
            {
               if (cmd.getType() == Command.TYPE_BUTTON)
               {
                  JPanel button = makeCommandButton(cmd, customizePopUpMenu);
                  m_toolBar.add(button);
               }
               else if (cmd.getType() == Command.TYPE_CHECKBOX)
               {
                  JPanel checkBox = makeCommandCheckBox(cmd, customizePopUpMenu);
                  m_toolBar.add(checkBox);
               }
               else
               {
                  Starter._m_logError.LoggingError(10997,
                        "Invalid Command Type",
                        "The command type '" + cmd.getType() + "' is not defined! Check source code.");
                  continue;
               }
               cmd.updateUI();

               // Add the Customize Command
               tbCustomize = new Command();
               customizeButton = makeToolBarCustomizeButton(tbCustomize, i+1, customizePopUpMenuAdd);
               m_toolBar.add(customizeButton);
            }
         }

         // Update User Preferences
         Command.saveCommandsToolbarListItems();
      }

      /*
       *  add the pop up menu items for the commands in the ToolBar
       */
      JMenuItem itemRemove = new JMenuItem("Remove");
      itemRemove.setToolTipText("Click here to remove the command from the tool bar");
      itemRemove.setIcon(JResizedIcon.getIcon(JResizedIcon.IconUrls.ICON_CUSTOMIZE_DEL_COMMAND_BAR, JResizedIcon.IconSize.SMALL));
      itemRemove.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            // get the selected command by the object name (== cmdId)
            JMenuItem jm = (JMenuItem) e.getSource();
            JPopupMenu jp = (JPopupMenu) jm.getParent();
            Object cmdObject = (Object) jp.getInvoker();
            String cmdId = null;
            if (cmdObject instanceof JButton)
            {
               // Button
               cmdId = ((JButton)cmdObject).getName();
            }
            else if (cmdObject instanceof JLabel)
            {
               // CheckBox (label)
               cmdId = ((JLabel)cmdObject).getName();
            }
            else
            {
               Starter._m_logError.LoggingError(10997,
                     "Data Inconsistency",
                     "Invalid object Type in CB Customize Commands ToolBar!");
               return;
            }

            Command cmdSel = Command.getObject(cmdId);
            if (null != cmdSel)
            {
               Starter._m_logError.TraceDebug("Removed Command '" + cmdSel.toString() + "' from Tool Bar.");
               boolean rc = cmdSel.removeCommandFromToolbarList();
               if (true == rc)
               {
                  // rebuild the complete toolBar
                  createCommandsToolBar();
                  m_toolBar.revalidate();
                  m_toolBar.repaint();
               }
               else
               {
                  Starter._m_logError.TraceDebug("Could not remove Command '" + cmdSel.toString() + "' from Tool Bar List!");
               }
            }
            else
            {
               Starter._m_logError.TraceDebug("Could not find Command with Id'" + cmdId + "' to remove from Tool Bar!");
            }
         }
      });
      customizePopUpMenu.add(itemRemove);

      /*
       *  add the pop up menu items for the add buttons with the unused commands
       */
      Vector<Command> listOfUnusedCommands = Command.getListOfUnusedCommands();
      for (int i = 0; i < listOfUnusedCommands.size(); i++)
      {
         Command cmd = listOfUnusedCommands.get(i);
         if (null != cmd)
         {
            final int iListIndex = i; // Command popup menuItem list index, used in CB
            JMenuItem item = new JMenuItem(cmd.getCommand());
            item.setToolTipText(cmd.getToolTip());
            item.setIcon(JResizedIcon.getIcon(cmd.getIconUrl().elementAt(0), JResizedIcon.IconSize.SMALL));
            item.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  // insert the new command
                  Vector<Command> listOfUnusedCommands = Command.getListOfUnusedCommands();
                  if (listOfUnusedCommands.size() <= iListIndex)
                  {
                     // this should not happen!
                     Starter._m_logError.LoggingError(10997,
                           "Data Inconsistency",
                           "List index '" + iListIndex + "' out of range of variable listOfUnusedCommands!");
                     return;
                  }
                  // get the insert position in the ToolBar from the label text
                  JMenuItem jm = (JMenuItem) e.getSource();
                  JPopupMenu jp = (JPopupMenu) jm.getParent();
                  JLabel jl = (JLabel) jp.getInvoker();
                  int iPos = Integer.valueOf(jl.getName());

                  // get the new command
                  Command cmdAdd = listOfUnusedCommands.get(iListIndex);
                  if (null != cmdAdd)
                  {
                     // ...and add it to the current commands list
                     Starter._m_logError.TraceDebug("Add Command '" + cmdAdd.toString() + "' at position '" + iPos + "'");
                     Command.insertCommandAt(cmdAdd, iPos);

                     // rebuild the complete toolBar
                     createCommandsToolBar();
                     m_toolBar.revalidate();
                     m_toolBar.repaint();
                  }
               }
            });
            customizePopUpMenuAdd.add(item);
         }
      }

   }

   /**
    * Create a ToolBar Customize Command Label.
    * 
    * @param cmd
    *           is the command
    * @param insertPos
    *           is the position to insert the command (by label text)
    * @param customizePopUpMenu
    *           is the pop up menu with the list of available commands
    * @return the created button
    */
   private JPanel makeToolBarCustomizeButton(Command cmd, int insertPos, JPopupMenu customizePopUpMenu)
   {
      JPanel jPanel = createPanel(null);
      ((FlowLayout) jPanel.getLayout()).setHgap(0);
      jPanel.setBorder(BorderFactory.createEmptyBorder()); //.createLoweredSoftBevelBorder());

      // Create and initialize the label - Label Text is used in CB to get the insert position!
      JLabel customizeMenuSeparator = new JLabel(JResizedIcon.getIcon(cmd.getIconUrl().elementAt(0), 5, JResizedIcon.IconSize.MEDIUM.getSize()));
      customizeMenuSeparator.setName(StringFormat.int2String(insertPos, "0"));
      customizeMenuSeparator.setToolTipText(cmd.getToolTip());

      // add the pop up menu with the list of available commands
      customizeMenuSeparator.setComponentPopupMenu(customizePopUpMenu);

//      customizeMenuSeparator.setBorder(BorderFactory.createLoweredSoftBevelBorder());
      cmd.setComponent(customizeMenuSeparator); // not really required

      jPanel.add(customizeMenuSeparator);
      return jPanel;
   }
   
   /**
    * Create a Command Button.
    * 
    * @param cmd
    *           is the command
    * @return the created button
    */
   private JPanel makeCommandButton(Command cmd, JPopupMenu customizePopUpMenu)
   {
      JPanel jPanel = createPanel(cmd);

      // Create the Button
      JButton button = new JButton();
      button.setToolTipText(cmd.getToolTip());
      button.setActionCommand(cmd.getId());
      button.setName(cmd.getId());
      button.setIcon(JResizedIcon.getIcon(cmd.getIconUrl().elementAt(0), JResizedIcon.IconSize.MEDIUM));
      button.setBorder(BorderFactory.createRaisedSoftBevelBorder());
      button.addActionListener(this);
      cmd.setComponent(button);

      // add the pop up menu with the list of available commands
      button.setComponentPopupMenu(customizePopUpMenu);

      jPanel.add(button);
      return jPanel;
   }
   
   /**
    * Create a CheckBox.
    * 
    * @param cmd
    *           is the command
    * @return the created checkBox
    */
   private JPanel makeCommandCheckBox(Command cmd, JPopupMenu customizePopUpMenu)
   {
      JPanel jPanel = createPanel(cmd);

      // Create the CheckBox (setSelected is done after in updateUI)
      JCheckBox checkBox = new JCheckBox();
      checkBox.setActionCommand(cmd.getId());
      checkBox.setToolTipText(cmd.getToolTip());
      checkBox.addActionListener(this);
      cmd.setComponent(checkBox);

      JLabel jLabel = new JLabel();
      jLabel.setIcon(JResizedIcon.getIcon(cmd.getIconUrl().elementAt(0), JResizedIcon.IconSize.MEDIUM));
      jLabel.setToolTipText(cmd.getToolTip());

      // add the pop up menu with the list of available commands
      jLabel.setComponentPopupMenu(customizePopUpMenu);
      jLabel.setName(cmd.getId());
      cmd.setJLabel(jLabel);

      jPanel.add(checkBox);
      jPanel.add(jLabel);
      return jPanel;
   }

   /**
    * Create the basis panel, parent of each command component
    * @param cmd
    *           is the command
    * @return the created panel
    */
   private JPanel createPanel(Command cmd)
   {
      JPanel jPanel = new JPanel();
      FlowLayout flowLayout = new FlowLayout();
      jPanel.setLayout(flowLayout);
      jPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      if (null != cmd) jPanel.setToolTipText("Press RMB on Icon to customize Commands ToolBar");
//      flowLayout.setHgap(0);
//      flowLayout.setVgap(0);
      return jPanel;
   }

   /**
    * Method to update a command component.
    * <p>
    * This method must be called, when the value for the component is changed (e.g. in UserPrefs, Settings, etc.).<br>
    * It calls the component specific update method, if the command is part of the ToolBar.
    * 
    * @param cmdId
    *           is the command id
    */
   public static void updateCommand(String cmdId)
   {
      // get the command
      Command cmd = Command.getObject(cmdId);
      if (null != cmd)
      {
         Vector<Command> commandsToolbarList = Command.getCommandsToolbarList();
         // check, if the command is part of the current ToolBar
         if ((null != commandsToolbarList) && (true == commandsToolbarList.contains(cmd)))
         {
            // call the component specific update method
            Starter._m_logError.TraceDebug("Update Command UI: " + cmd.getCommand());
            cmd.updateUI();
         }
      }
      else
      {
         Starter._m_logError.LoggingError(10900,
               "Update Command not defined",
               "The update command with Id '" + cmdId + "' is not defined!");
      }
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      String cmdId = e.getActionCommand();

      Command cmd = Command.getObject(cmdId);
      if (null != cmd)
      {
         Starter._m_logError.TraceDebug("Execute selected Command: " + cmd.getCommand());
         cmd.execute(e);
      }
      else
      {
         Starter._m_logError.LoggingError(10900,
               "Command not defined",
               "The command with Id '" + cmdId + "' is not defined!");
      }
   }
}
