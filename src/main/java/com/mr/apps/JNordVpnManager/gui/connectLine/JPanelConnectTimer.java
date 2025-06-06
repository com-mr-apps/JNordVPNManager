/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.connectLine;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.base.Command;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.gui.GuiStatusLine;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

/**
 * Class for Pause/[Re]Connect connections to VPN
 * <p>
 * This class contains methods to start/stop a timer that will connect/disconnect to/from VPN. The Constructor generates
 * a JPanel with a JSlider to set/display start/stop the timer. The timer can be used without the JSlider - e.g
 * start/stop with Connect/Reconnect Command Buttons, then the default time (stored in UserPrefs) is used.
 */
@SuppressWarnings("serial")
public class JPanelConnectTimer extends JPanel
{
   // timer default values
   private static final int            TIMER_UPDATE_INTERVALL  = 15000;                  // 15 seconds
   private static final int            TIMER_MAX_VALUE         = 3600;                   // 60 minutes

   private static Timer                m_timer                 = null;
   private static JSlider              m_timeSlider            = null;
   private static JMenuItem            m_startPauseMenuItem    = null;
   private static int                  m_timerWorkMode         = GuiStatusLine.STATUS_UNKNOWN;
   private static int                  m_timerValue            = UtilPrefs.getTimerDefaultValue() * 60;

   /**
    * Constructor to create the Timer-JSlider
    */
   public JPanelConnectTimer()
   {
      // Time Slider RMB popup menu
      JPopupMenu timeSliderPopup = new JPopupMenu();
      m_startPauseMenuItem = new JMenuItem("Start Pause");
      m_startPauseMenuItem.setToolTipText("Start the timer to pause VPN connection.");
      m_startPauseMenuItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            // (manual change) - start/stop the timer
            if ((true == isTimerRunning()) || m_timerValue == 0)
            {
               m_timerWorkMode = GuiStatusLine.STATUS_CONNECTED; // ..in case we are in automatic reconnect mode, we stop it and [re]connect to VPN
               stopTheTimer();
            }
            else
            {
               m_timerWorkMode = GuiStatusLine.STATUS_UNKNOWN; // ..independent of current work mode, will be set to STATUS_PAUSED
               startTheTimer((m_timerValue+59)/60);
            }
         }
      });

      JMenuItem m1 = new JMenuItem("1 min.");
      m1.setToolTipText("Pause VPN connection for " + timeSliderValueToText(60) + ".");
      m1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            // (manual change) - update default timer value
            UtilPrefs.setTimerDefaultValue(1);
            startTheTimer(1);
         }
      });

      JMenuItem m5 = new JMenuItem("5 min.");
      m5.setToolTipText("Pause VPN connection for " + timeSliderValueToText(300) + ".");
      m5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            // (manual change) - update default timer value
            UtilPrefs.setTimerDefaultValue(5);
            startTheTimer(5);
         }
      });

      JMenuItem m15 = new JMenuItem("15 min.");
      m15.setToolTipText("Pause VPN connection for " + timeSliderValueToText(900) + ".");
      m15.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            // (manual change) - update default timer value
            UtilPrefs.setTimerDefaultValue(15);
            startTheTimer(15);
         }
      });

      JMenuItem m30 = new JMenuItem("30 min.");
      m30.setToolTipText("Pause VPN connection for " + timeSliderValueToText(1800) + ".");
      m30.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            // (manual change) - update default timer value
            UtilPrefs.setTimerDefaultValue(30);
            startTheTimer(30);
         }
      });

      JMenuItem m60 = new JMenuItem("60 min.");
      m60.setToolTipText("Pause VPN connection for " + timeSliderValueToText(3600) + ".");
      m60.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            // (manual change) - update default timer value
            UtilPrefs.setTimerDefaultValue(60);
            startTheTimer(60);
         }
      });

      timeSliderPopup.add(m_startPauseMenuItem);
      timeSliderPopup.add(m1);
      timeSliderPopup.add(m5);
      timeSliderPopup.add(m15);
      timeSliderPopup.add(m30);
      timeSliderPopup.add(m60);

      // Time Slider - labels
      JLabel l0 = new JLabel("0");
      l0.setFont(new Font("Serif", Font.PLAIN, 8));
      JLabel l5 = new JLabel("5");
      l5.setFont(new Font("Serif", Font.PLAIN, 8));
      JLabel l15 = new JLabel("15");
      l15.setFont(new Font("Serif", Font.PLAIN, 8));
      JLabel l30 = new JLabel("30");
      l30.setFont(new Font("Serif", Font.PLAIN, 8));
      JLabel l60 = new JLabel("60");
      l60.setFont(new Font("Serif", Font.PLAIN, 8));
      Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
      labelTable.put(Integer.valueOf(0), l0);
      labelTable.put(Integer.valueOf(300), l5);
      labelTable.put(Integer.valueOf(900), l15);
      labelTable.put(Integer.valueOf(1800), l30);
      labelTable.put(Integer.valueOf(3600), l60);

      // Time Slider
      m_timeSlider = new JSlider();
      m_timeSlider.setMinimum(0);
      m_timeSlider.setMaximum(TIMER_MAX_VALUE);
      m_timeSlider.setValue(m_timerValue);
      m_timeSlider.setMajorTickSpacing(TIMER_MAX_VALUE/12);
      m_timeSlider.setPaintTicks(true);
      m_timeSlider.setPaintLabels(true);
      m_timeSlider.setLabelTable(labelTable);
      setToolTipTimeSlider();
      m_timeSlider.setComponentPopupMenu(timeSliderPopup);
      m_timeSlider.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            // If slider is changed:
            if (true == isTimerRunning())
            {
               // (automatic change by timer) - update status line
               GuiStatusLine.setStatusLine(m_timerWorkMode, syncStatusForTimer(m_timerWorkMode));
            }
            else
            {
               // (manual change) - update default timer value
               m_timerValue = m_timeSlider.getValue();
               UtilPrefs.setTimerDefaultValue(m_timerValue/60);

               // update tool tip for start/pause buttons
               updateButtons();
            }
         }
      });

      add(m_timeSlider);
   }

   /**
    * Create the timer object
    * @return the timer
    */
   private static Timer createConnectTimer()
   {
      Timer timer = new Timer(TIMER_UPDATE_INTERVALL, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            m_timerValue = m_timerValue - (TIMER_UPDATE_INTERVALL/1000);
            if (m_timerValue <= 0)
            {
               m_timerValue = 0;
               stopTheTimer();                  
            }
            else if (null != m_timeSlider)
            {
               m_timeSlider.setValue(m_timerValue);
            }
         }
      });
      return timer;
   }

   /**
    * Format time slider value for messages.
    * 
    * @param iTimeSliderValue
    *           is the time in seconds
    * @return the message text in minute(s)
    */
   private static String timeSliderValueToText(int iTimeSliderValue)
   {
      int iTime = (iTimeSliderValue+57) / 60;
      return (iTime == 1) ? iTime + " Minute" : iTime + " Minutes";   
   }

   /**
    * Set the time slider value.
    * 
    * @param iTime
    *           is the time in minutes.
    */
   private static void setTimeSlider(int iTime)
   {
      m_timerValue = iTime * 60;
      if (null != m_timeSlider) m_timeSlider.setValue(m_timerValue);      
   }

   /**
    * Start the Timer.
    * <p>
    * Dependent on mode: Disconnect from VPN or Reconnect in time intervals.
    * 
    * @param iTime
    *           is the start time in minutes
    */
   private static void startTheTimer(int iTime)
   {
      CurrentLocation loc = Starter.getCurrentServer(true);
      if (m_timerWorkMode != GuiStatusLine.STATUS_RECONNECT)
      {
         // Pause - Disconnect
         if (null != loc && true == loc.isConnected())
         {
            NvpnCallbacks.executeDisConnect(null, null);
         }
         m_timerWorkMode = GuiStatusLine.STATUS_PAUSED;
      }
      else
      {
         // Reconnect - Connect
         if (null != loc && false == loc.isConnected())
         {
            NvpnCallbacks.executeConnect(loc, null, null);
         }
         m_timerWorkMode = GuiStatusLine.STATUS_RECONNECT;
      }

      if (null == m_timer)
      {
         // start new timer
         Starter._m_logError.TraceDebug("Set Connection Timer - Start for " + timeSliderValueToText(iTime*60) + ". m_timerWorkMode=" + m_timerWorkMode);
         m_timer = createConnectTimer();
         m_timer.start();
         setToolTipTimeSlider();
         setTimeSlider(iTime);
         GuiStatusLine.setStatusLine(m_timerWorkMode, syncStatusForTimer(m_timerWorkMode));
      }
      else
      {
         // re-use running timer
         Starter._m_logError.TraceDebug("Set Connection Timer - Continue remaining " + timeSliderValueToText(m_timerValue) + ". m_timerWorkMode=" + m_timerWorkMode);
      }
   }

   /**
    * Stop the Timer.
    * <p>
    * Reconnect to VPN.
    */
   private static void stopTheTimer()
   {
      if (true == isTimerRunning()) m_timer.stop();
      m_timer = null;
      setToolTipTimeSlider();

      CurrentLocation loc = Starter.getCurrentServer(true);
      if (m_timerWorkMode == GuiStatusLine.STATUS_RECONNECT)
      {
         NvpnCallbacks.executeConnect(loc, null, null);
         if (null == loc || loc.isConnected())
         {
            m_timerWorkMode = GuiStatusLine.STATUS_RECONNECT;
            startTheTimer(UtilPrefs.getTimerDefaultValue());
         }
         else
         {
            m_timerWorkMode = GuiStatusLine.STATUS_DISCONNECTED;
         }
         Starter._m_logError.TraceDebug("Stop connection Timer (Reconnect). m_timerWorkMode = " + m_timerWorkMode);
      }
      else
      {
         // finished
         Starter._m_logError.TraceDebug("Stop connection Timer (Pause). m_timerWorkMode = " + m_timerWorkMode);
         if ((m_timerWorkMode == GuiStatusLine.STATUS_CONNECTED) || (m_timerWorkMode == GuiStatusLine.STATUS_PAUSED))
         {
            if (null != loc && false == loc.isConnected()) NvpnCallbacks.executeConnect(loc, null, null);
         }
         else if (m_timerWorkMode == GuiStatusLine.STATUS_DISCONNECTED)
         {
            if (null != loc && true == loc.isConnected()) NvpnCallbacks.executeDisConnect(null, null);
         }
         forceStopTheTimer();
      }
   }

   /**
    * Force Stop the Timer.
    */
   public static void forceStopTheTimer()
   {
      if (true == isTimerRunning())
      {
         Starter._m_logError.LoggingInfo("Force Stop Connection Timer.");
         Starter._m_logError.TraceDebug("m_timerWorkMode = " + m_timerWorkMode + " set to -1 (STATUS_UNKNOWN)");
         m_timer.stop();
      }
      m_timer = null;
      m_timerWorkMode = GuiStatusLine.STATUS_UNKNOWN;
      setTimeSlider(UtilPrefs.getTimerDefaultValue());
      setToolTipTimeSlider();
      GuiStatusLine.setStatusLine(m_timerWorkMode, JPanelConnectTimer.syncStatusForTimer(m_timerWorkMode));
   }

   /**
    * Synchronize the timer dependent on the requested status 
    * <p>
    * Synchronize the status dependent of the working mode<br>
    * Check, if VPN was [de]activated manually in time of timer is running or logged out manually (outside of the application).<br>
    * Additional this method is used by the Command bar buttons to start/stop the timer. 
    * 
    * @param iStatus
    *           is the timer working mode/status
    * @return the status bar message with remaining minutes or null if the timer is not running (or "" if we are in reconnect mode)
    */
   public static String syncStatusForTimer(int iStatus)
   {
      String sMsg = null;

//      Starter._m_logError.TraceDebug("(syncStatusForTimer) iStatus = " + iStatus + " m_timerWorkMode = " + m_timerWorkMode);

      // update the timer working mode
      if (iStatus == GuiStatusLine.STATUS_UNKNOWN)
      {
         // call from GuiStatusLine.update()
         if (m_timerWorkMode == GuiStatusLine.STATUS_UNKNOWN)
         {
            // initialization (first call from GuiStatusLine.update() or from Starter.init() AutoConnect)
            iStatus = (Starter.getCurrentStatusData().isConnected()) ? GuiStatusLine.STATUS_CONNECTED : GuiStatusLine.STATUS_DISCONNECTED;
         }
         else
         {
            // current working mode
            iStatus = m_timerWorkMode;
         }
      }

      // [updated] working mode
      m_timerWorkMode = iStatus;

      if (true == isTimerRunning())
      {
         if (iStatus == GuiStatusLine.STATUS_RECONNECT)
         {
            // We are in automatic reconnect mode
            startTheTimer(UtilPrefs.getTimerDefaultValue());
            sMsg = "";
         }
         else if (iStatus == GuiStatusLine.STATUS_PAUSED)
         {
            // connection paused
            sMsg = "VPN will be reconnected in " + timeSliderValueToText(m_timerValue) + ".";
         }
         else if ((iStatus == GuiStatusLine.STATUS_CONNECTED) || (iStatus == GuiStatusLine.STATUS_DISCONNECTED))
         {
               // VPN was [dis]connected manually
               stopTheTimer();
               sMsg = null;
         }
         else // (iStatus == GuiStatusLine.STATUS_LOGGEDOUT)
         {
            stopTheTimer();
            sMsg = null;
         }
      }
      else // Timer is not running
      {
         if (iStatus == GuiStatusLine.STATUS_PAUSED)
         {
            // we use this to start pause from command
            startTheTimer(UtilPrefs.getTimerDefaultValue());
            sMsg = "VPN will be reconnected in " + timeSliderValueToText(m_timerValue) + ".";
         }
         else if (iStatus == GuiStatusLine.STATUS_RECONNECT)
         {
            // we use this to start automatic reconnect
            startTheTimer(UtilPrefs.getTimerDefaultValue());
            sMsg = "";
         }
         else if ((iStatus == GuiStatusLine.STATUS_CONNECTED) || (iStatus == GuiStatusLine.STATUS_DISCONNECTED))
         {
            sMsg = null;
         }
         else // (iStatus == GuiStatusLine.STATUS_LOGGEDOUT)
         {
            // not logged in
            sMsg = null;
         }
      }
      // set current working mode - may be changed by start/stop timer methods
      m_timerWorkMode = iStatus;

      // GUI update of the buttons
      updateButtons();

      return sMsg;
   }
   
   /**
    * Get the timer work mode
    * @return m_timerWorkMode.
    */
   public static int getTimerWorkMode()
   {
      return m_timerWorkMode;
   }

   /**
    * Is the connection timer running?
    * @return <code>true</code> if the timer is running
    */
   public static boolean isTimerRunning()
   {
      return ((m_timer != null) && m_timer.isRunning());
   }

   /**
    * Set Time Slider ToolTip depending on running/stopped
    */
   private static void setToolTipTimeSlider()
   {
      if (null == m_timeSlider) return;

      String sTimeSliderToolTip = "Move slider to set timer value - RMB to start timer with predefined values.";
      if (true == isTimerRunning())
      {
         m_timeSlider.setToolTipText("[running] " + sTimeSliderToolTip);
      }
      else
      {
         m_timeSlider.setToolTipText(sTimeSliderToolTip);
      }
   }

   /**
    * Method to update the buttons dependent on the timer work mode.
    */
   private static void updateButtons()
   {
      String sToolTip = null;
      int iconId = 0;
      boolean enabled = true;

      String statusId = Command.BASE_STATUS_KEY;

      if (m_timerWorkMode == GuiStatusLine.STATUS_PAUSED)
      {
         statusId = "CONNECT";
         enabled = true;
         iconId = 1;
         sToolTip = "Press Button to Stop the Timer and [Re]Connect VPN (Remaining time " + timeSliderValueToText(m_timerValue) + ").";
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_RECONNECT)
      {
         statusId = "CONNECT";
         enabled = true;
         iconId = 1;
         sToolTip = "Press Button to Stop Automatic Reconnection (Remaining time " + timeSliderValueToText(m_timerValue) + ").";
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_CONNECTED)
      {
         statusId = "PAUSE";
         enabled = true;
         iconId = 0;
         sToolTip = "Press Button to Pause VPN for " + timeSliderValueToText(m_timerValue) + ".";
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_DISCONNECTED)
      {
         statusId = "PAUSE";
         enabled = true;
         iconId = 0;
         sToolTip = "Press Button to [Re]Connect VPN in " + timeSliderValueToText(m_timerValue) + ".";
      }
      else if (m_timerWorkMode == GuiStatusLine.STATUS_LOGGEDOUT)
      {
         statusId = Command.DISABLED_STATUS_KEY;
         enabled = false;
         iconId = 0;
         sToolTip = "Not logged in.";
      }
      else // unknown
      {
         return;
      }

      if (null != m_startPauseMenuItem)
      {
         // Actualize Start/Pause Menu Item 
         m_startPauseMenuItem.setEnabled(enabled);
         m_startPauseMenuItem.setToolTipText(sToolTip);
         m_startPauseMenuItem.setText(((0 == iconId) ? "Start Timer" : "Stop Timer"));
      }

      // update the commands toolBar command buttons
      Command cmd = Command.getObject(Command.VPN_CMD_TIMER_CONNECT);
      if (null != cmd)
      {
         cmd.setStatusUI(statusId);
         cmd.setCurrentToolTip(sToolTip); // required because ToolTip is variable.
         GuiCommandsToolBar.updateCommand(Command.VPN_CMD_TIMER_CONNECT);
      }

      cmd = Command.getObject(Command.VPN_CMD_TIMER_RECONNECT);
      if (null != cmd)
      {
         if (m_timerWorkMode == GuiStatusLine.STATUS_RECONNECT)
         {
            statusId = "RUNNING";

         }
         else if (m_timerWorkMode != GuiStatusLine.STATUS_LOGGEDOUT)
         {
            statusId = "STOPPED";
         }
         cmd.setStatusUI(statusId);
         GuiCommandsToolBar.updateCommand(Command.VPN_CMD_TIMER_RECONNECT);
      }
   }
}
