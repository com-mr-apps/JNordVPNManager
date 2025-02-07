/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.connectLine;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.gui.GuiStatusLine;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnCallbacks;

@SuppressWarnings("serial")
public class JPauseSlider extends JPanel
{
   private static final int       DEFAULT_START_TIME     = 5;                      // ..in minutes  TODO: settings
   private static final int       TIMER_UPDATE_INTERVALL = 15000;                  // 15 seconds
   private static final int       TIMER_MAX_VALUE        = 3600;                   // 60 minutes

   private static ArrayList<ImageIcon> m_timerStartStopImages = new ArrayList<>();

   private static Timer           m_timer;
   private static JSlider         m_timeSlider;
   private static JButton         m_startStopButton;

   public JPauseSlider()
   {
      // Disconnect/Connect
      m_timerStartStopImages.add(JResizedIcon.getIcon(IconUrls.ICON_TIMER_PAUSE, IconSize.MEDIUM));
      m_timerStartStopImages.add(JResizedIcon.getIcon(IconUrls.ICON_TIMER_CONNECT, IconSize.MEDIUM));

      JPopupMenu timeSliderPopup = new JPopupMenu();
      JMenuItem m5 = new JMenuItem("5 min.");
      m5.setToolTipText("Pause VPN connection for " + timeSliderValueToText(300) + ".");
      m5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            startTheTimer(5);
         }
      });
      JMenuItem m15 = new JMenuItem("15 min.");
      m15.setToolTipText("Pause VPN connection for " + timeSliderValueToText(900) + ".");
      m15.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            startTheTimer(15);
         }
      });
      JMenuItem m30 = new JMenuItem("30 min.");
      m30.setToolTipText("Pause VPN connection for " + timeSliderValueToText(1800) + ".");
      m30.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            startTheTimer(30);
         }
      });
      JMenuItem m60 = new JMenuItem("60 min.");
      m60.setToolTipText("Pause VPN connection for " + timeSliderValueToText(3600) + ".");
      m60.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            startTheTimer(60);
         }
      });
      timeSliderPopup.add(m5);
      timeSliderPopup.add(m15);
      timeSliderPopup.add(m30);
      timeSliderPopup.add(m60);

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
      Hashtable<Integer, JLabel> labelTable = 
      new Hashtable<Integer, JLabel>();
      labelTable.put(Integer.valueOf(0), l0);
      labelTable.put(Integer.valueOf(300), l5);
      labelTable.put(Integer.valueOf(900), l15);
      labelTable.put(Integer.valueOf(1800), l30);
      labelTable.put(Integer.valueOf(3600), l60);

      m_timeSlider = new JSlider();
      m_timeSlider.setMinimum(0);
      m_timeSlider.setMaximum(TIMER_MAX_VALUE);
      m_timeSlider.setValue(DEFAULT_START_TIME * 60);
      m_timeSlider.setMajorTickSpacing(TIMER_MAX_VALUE/12);
      m_timeSlider.setPaintTicks(true);
      m_timeSlider.setPaintLabels(true);
      m_timeSlider.setLabelTable(labelTable);
      m_timeSlider.setToolTipText("Set time for pause.");
      m_timeSlider.setComponentPopupMenu(timeSliderPopup);
      m_timeSlider.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            // If slider is changed:
            if (true == m_timer.isRunning())
            {
               // (automatic change by timer) - update status line
               GuiStatusLine.updateStatusLine(Starter.STATUS_PAUSED, syncStatusForPause(Starter.STATUS_PAUSED));
            }
            else
            {
               // (manual change) - update tool tip for start button
               CurrentLocation loc = Starter.getCurrentServer();
               if (null == loc || loc.isConnected() == false)
               {
                  // disconnected
                  m_startStopButton.setToolTipText("Start VPN connection in " + timeSliderValueToText(m_timeSlider.getValue()) + ".");
               }
               else
               {
                  // connected
                  m_startStopButton.setToolTipText("Pause VPN connection for " + timeSliderValueToText(m_timeSlider.getValue()) + ".");
               }
            }
         }
      });

      m_startStopButton = new JButton(m_timerStartStopImages.get(0));
      m_startStopButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
      CurrentLocation loc = Starter.getCurrentServer();
      if (null == loc || loc.isConnected() == false)
      {
         // disconnected
         m_startStopButton.setToolTipText("Start VPN connection in " + timeSliderValueToText(m_timeSlider.getValue()) + ".");
      }
      else
      {
         // connected
         m_startStopButton.setToolTipText("Pause VPN connection for " + timeSliderValueToText(m_timeSlider.getValue()) + ".");
      }
      m_startStopButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            if (m_timer.isRunning() || m_timeSlider.getValue() == 0)
            {
               stopTheTimer();
            }
            else
            {
               startTheTimer((m_timeSlider.getValue()+59)/60);
            }
         }
      });

      add(m_timeSlider);
      add(m_startStopButton);

      // ---------------------------------------------------------------------------
      // define and start the timer
      m_timer = new Timer(TIMER_UPDATE_INTERVALL, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            int value = m_timeSlider.getValue() - (TIMER_UPDATE_INTERVALL/1000);
            if (value <= m_timeSlider.getMinimum())
            {
               stopTheTimer();
            }
            else
            {
               m_timeSlider.setValue(value);
            }
         }
      });
   }

   /**
    * Format time slider value for messages.
    * @param iTimeSliderValue
    * @return the message text
    */
   private static String timeSliderValueToText(int iTimeSliderValue)
   {
      int iTime = (iTimeSliderValue+57) / 60;
      return (iTime == 1) ? iTime + " Minute" : iTime + " Minutes";   
   }

   /**
    * Set the time slider value.
    * @param iTime is the time in minutes.
    */
   private static void setTimeSlider(int iTime)
   {
      m_timeSlider.setValue(iTime * 60);      
   }

   /**
    * Start the Timer.
    * <p>
    * Disconnect from VPN.
    * @param iTime is the start time in minutes
    */
   private static void startTheTimer(int iTime)
   {
      if (m_timer.isRunning()) m_timer.stop();

      CurrentLocation loc = Starter.getCurrentServer();
      if (null == loc || true == loc.isConnected()) NvpnCallbacks.executeDisConnect(null, null);

      if (0 == iTime)
      {
         setTimeSlider(DEFAULT_START_TIME);
      }
      else
      {
         setTimeSlider(iTime);
      }
      Starter._m_logError.LoggingInfo("Start Pause: Disconnect from VPN for " + timeSliderValueToText(m_timeSlider.getValue()) + ".");
      m_timer.start();
      GuiStatusLine.updateStatusLine(Starter.STATUS_PAUSED, syncStatusForPause(Starter.STATUS_PAUSED));

      m_startStopButton.setIcon(m_timerStartStopImages.get(1));
      m_startStopButton.setToolTipText("Reconnect.");
   }

   /**
    * Stop the Timer.
    * <p>
    * Reconnect to VPN.
    */
   private static void stopTheTimer()
   {
      Starter._m_logError.LoggingInfo("Stop Pause (Reconnect to VPN).");
      m_timer.stop();
      m_startStopButton.setIcon(m_timerStartStopImages.get(0));
      m_startStopButton.setToolTipText("Pause VPN connection for " + timeSliderValueToText(m_timeSlider.getValue()) + ".");

      CurrentLocation loc = Starter.getCurrentServer();
      if (null != loc && false == loc.isConnected()) NvpnCallbacks.executeConnect(loc, null, null);
      setTimeSlider(DEFAULT_START_TIME);
   }

   /**
    * Get the status line message for the pause timer
    * <p>
    * And sync the status, if VPN was activated manually in time of timer is running or
    * logged out manually (outside of the application).
    * 
    * @param iStatus
    *           is the timer/pause status
    * @return the message with remaining minutes or null if the timer is not running
    */
   public static String syncStatusForPause(int iStatus)
   {
      if (null == m_timer) return null;

      if (true == m_timer.isRunning())
      {
         if ((iStatus == Starter.STATUS_CONNECTED) || (iStatus == Starter.STATUS_LOGGEDOUT))
         {
            // VPN was connected outside of GUI or User logged out outside of GUI
            stopTheTimer();
            return null;
         }
         return "VPN will be reconnected in " + timeSliderValueToText(m_timeSlider.getValue()) + ".";
      }
      else // Timer is not running
      {
         if (iStatus == Starter.STATUS_PAUSED)
         {
            // we can use this to start pause VPN
            m_startStopButton.setEnabled(true);
            startTheTimer((m_timeSlider.getValue()+59)/60);
         }
         else if ((iStatus == Starter.STATUS_CONNECTED) || (iStatus == Starter.STATUS_DISCONNECTED))
         {
            m_startStopButton.setEnabled(true);
            m_startStopButton.setToolTipText("Pause VPN connection for " + timeSliderValueToText(m_timeSlider.getValue()) + ".");
         }
         else // Starter.STATUS_LOGGEDOUT
         {
            // not logged in
            m_startStopButton.setEnabled(false);
            m_startStopButton.setToolTipText("Not logged in.");
         }
      }
      return null;
   }
}
