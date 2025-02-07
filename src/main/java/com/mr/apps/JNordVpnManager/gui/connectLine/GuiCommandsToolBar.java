/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
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
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicToolBarUI;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.Command;
import com.mr.apps.JNordVpnManager.commandInterfaces.CallCommand;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;

@SuppressWarnings("serial")
public class GuiCommandsToolBar extends JPanel implements ActionListener
{
   /**
    * Constructor for the Commands ToolBar.
    */
   public GuiCommandsToolBar()
   {
      super(new BorderLayout());

      // initialize the available ToolBar Commands
      Command.initAllCommands();

      // Create the ToolBar
      JToolBar toolBar = new JToolBar("JNordVPN Manager Command Bar");
      this.addCommands(toolBar);
      toolBar.setFloatable(true);

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
      toolBar.addHierarchyListener(hierarchyListener);

      // add the ToolBar to this panel
      this.add(toolBar, BorderLayout.CENTER);
   }

   /**
    * Add commands defined in the commands ToolBar list to the Commands ToolBar.
    * 
    * @param toolBar
    *           is the ToolBar
    */
   private void addCommands(JToolBar toolBar)
   {
      // get the list of commands to add (from user preferences)
      Vector<Command> commandsToolbarList = Command.getCommandsToolbarList();
      if (null != commandsToolbarList)
      {
         for (int i = 0; i < commandsToolbarList.size(); i++)
         {
            Command cmd = commandsToolbarList.get(i);
            if (null != cmd)
            {
               if (cmd.getType() == Command.TYPE_BUTTON)
               {
                  JPanel button = makeCommandButton(cmd);
                  toolBar.add(button);
               }
               else if (cmd.getType() == Command.TYPE_CHECKBOX)
               {
                  JPanel checkBox = makeCommandCheckBox(cmd);
                  toolBar.add(checkBox);
               }
               else if (cmd.getType() == Command.TYPE_SEPARATOR)
               {
                  toolBar.addSeparator();
               }
            }
         }
      }
   }

   /**
    * Create a Command Button.
    * 
    * @param cmd
    *           is the command
    * @return the created button
    */
   private JPanel makeCommandButton(Command cmd)
   {
      JPanel jPanel = createPanel(cmd);

      // Create and initialize the Button.
      JButton button = new JButton();
      button.setToolTipText(cmd.getToolTip());
      button.setActionCommand(cmd.getId());
      button.setIcon(JResizedIcon.getIcon(cmd.getIconUrl(), JResizedIcon.IconSize.MEDIUM));
      button.setBorder(BorderFactory.createRaisedSoftBevelBorder());
      button.addActionListener(this);
      cmd.setComponent(button);

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
   private JPanel makeCommandCheckBox(Command cmd)
   {
      JPanel jPanel = createPanel(cmd);

      // Create and initialize the CheckBox.
      JCheckBox checkBox = new JCheckBox();
      checkBox.setActionCommand(cmd.getId());
      checkBox.setSelected((boolean)cmd.getValue());
      checkBox.setToolTipText(cmd.getToolTip());
      checkBox.addActionListener(this);
      cmd.setComponent(checkBox);

      JLabel jLabel = new JLabel();
      jLabel.setIcon(JResizedIcon.getIcon(cmd.getIconUrl(), JResizedIcon.IconSize.MEDIUM));

      jPanel.add(checkBox);
      jPanel.add(jLabel);
      return jPanel;
   }

   /**
    * Create the basis panel, parent of the command component
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
      jPanel.setToolTipText(cmd.getToolTip());
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
            Starter._m_logError.LoggingInfo("Update Command: " + cmd.getCommand());
            CallCommand.invokeComponentMethod(cmd, "update", cmd.getComponent(cmdId));
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
         Starter._m_logError.LoggingInfo("Execute selected Command: " + cmd.getCommand());
         CallCommand.invokeEventMethod(cmd, "execute", e);
      }
      else
      {
         Starter._m_logError.LoggingError(10900,
               "Command not defined",
               "The command with Id '" + cmdId + "' is not defined!");
      }
   }
}
