/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.UtilSpeedtest;
import com.mr.apps.JNordVpnManager.gui.components.JLabeledTextField;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;
import com.mr.apps.JNordVpnManager.gui.components.JSpeedMeter;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconUrls;
import com.mr.apps.JNordVpnManager.utils.String.Wrap;

@SuppressWarnings("serial")
public class JSpeedtestDialog extends JDialog
{
   protected JProgressBar      m_progressBar      = null;
   protected JSpeedMeter       m_speedMeter       = null;
   protected JLabeledTextField m_txtSpeedMin      = null;
   protected JLabeledTextField m_txtSpeedMax      = null;
   protected JLabeledTextField m_txtSpeedAvg      = null;
   protected JTextArea         m_messageTextArea  = null;
   protected String            m_statusMessage    = null;
   protected boolean           m_forceStopTask    = false;

   private JButton             m_jbStartStop      = null;
   private static ImageIcon[]  m_iconStartStop    = new ImageIcon[] {
         JResizedIcon.getIcon(IconUrls.ICON_BUTTON_STOP, IconSize.MEDIUM),
         JResizedIcon.getIcon(IconUrls.ICON_BUTTON_START, IconSize.MEDIUM)
   };
   private static String[]     m_toolTipStartStop = new String[] {
         "Click here to Stop the Speed Test",
         "Click here to [Re]Start the Speed Test"
   };
   private static int          m_statusStartStop   = 0; // 0-running / 1-stopped

   /**
    * Initiates a new Speed test frame
    */
   public JSpeedtestDialog(Frame owner, String uri)
   {
      super(owner, "SpeedTest: " + uri, false); // modal false
      this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

      // Close Window with "X"
      this.addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            if ((false == m_forceStopTask) && (m_progressBar.getValue() < 100))
            {
               // running...
               int ret = JModalDialog.YesNoDialog("Do you want to cancel the Speed Test?");
               if (ret == 0)
               {
                  m_forceStopTask = true;
               }
               return;
            }

            // close dialog
            Starter.setSkipWindowGainedFocus();
            setVisible(false);
//            dispose(); -> no dispose, we re use the dialog!
            UtilSpeedtest.setVisibleSpeedtestMapLayer(false);
         }
      });
      
      // left - Speed meter and progress bar
      JPanel jpl = new JPanel(new BorderLayout());
      jpl.setLayout(new BoxLayout(jpl, BoxLayout.Y_AXIS));

      m_speedMeter = new JSpeedMeter("Speed [Mbit/s]");
      m_speedMeter.setPreferredSize(m_speedMeter.getMinimumSize());
      jpl.add(m_speedMeter);

      JPanel jpl2 = new JPanel(new BorderLayout());
      
      m_progressBar = new JProgressBar(0, 100);
      m_progressBar.setStringPainted(true);
      jpl2.add(m_progressBar, BorderLayout.CENTER);
      
      m_jbStartStop = new JButton(m_iconStartStop[m_statusStartStop]);
      m_jbStartStop.setBorder(BorderFactory.createRaisedSoftBevelBorder());
      m_jbStartStop.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            // cycle through stages 0-1
            m_statusStartStop = 1 - m_statusStartStop;
            if (0 == m_statusStartStop)
            {
               UtilSpeedtest.speedTest(Starter.getCurrentServer(true));
            }
            else
            {
               m_forceStopTask = true;
            }
            m_jbStartStop.setToolTipText(m_toolTipStartStop[m_statusStartStop]);
            m_jbStartStop.setIcon(m_iconStartStop[m_statusStartStop]);
         }
      });
      jpl2.add(m_jbStartStop, BorderLayout.LINE_END);

      jpl.add(jpl2);

      // right - Speeds (min/max/avg) and Status messages
      JPanel jpr = new JPanel();
      jpr.setLayout(new BoxLayout(jpr, BoxLayout.Y_AXIS));
      jpr.setAlignmentX(LEFT_ALIGNMENT);

      m_txtSpeedMin = new JLabeledTextField(10, "min:", "Mbit/s");
      m_txtSpeedMin.setEditable(false);
//      jpr.add(m_txtSpeedMin);
      
      m_txtSpeedMax = new JLabeledTextField(10, "max:", "Mbit/s");
      m_txtSpeedMax.setEditable(false);
      jpr.add(m_txtSpeedMax);

      m_txtSpeedAvg = new JLabeledTextField(10, "avg:", "Mbit/s");
      m_txtSpeedAvg.setEditable(false);
      jpr.add(m_txtSpeedAvg);

      m_messageTextArea = new JTextArea();
      m_messageTextArea.setBorder(new TitledBorder(new LineBorder(Color.gray, 2, true), "Status",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            Color.BLACK));
      m_messageTextArea.setFont(m_messageTextArea.getFont().deriveFont(Font.ITALIC));
      m_messageTextArea.setBackground(Color.lightGray);
      m_messageTextArea.setEditable(false);
      jpr.add(m_messageTextArea, BorderLayout.CENTER);

      // Dialog layout
      JPanel jp = new JPanel(new BorderLayout());
      jp.add(jpl, BorderLayout.LINE_START);
      jp.add(jpr, BorderLayout.CENTER);
      getContentPane().add(jp);

      this.pack();

      // Centers the Dialog
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension panelSize = this.getSize();
      this.setLocation((screenSize.width / 2) - (panelSize.width / 2), (screenSize.height / 2) - (panelSize.height / 2));

      this.init(null);
   }

   public void init(String uri)
   {
      if (null != uri)
      {
         this.setTitle("SpeedTest: " + uri);
      }
      m_forceStopTask = false;
      m_progressBar.setValue(0);
      setMessage("init...", false);
      m_speedMeter.reset();

      m_statusStartStop = 0; // running
      m_jbStartStop.setToolTipText(m_toolTipStartStop[m_statusStartStop]);
      m_jbStartStop.setIcon(m_iconStartStop[m_statusStartStop]);

      this.setVisible(true);
      UtilSpeedtest.setVisibleSpeedtestMapLayer(true);
   }

   /**
    * Sets the speeds (current/min/max/average) for the current progress
    * 
    * @param progress
    *           The progress value to be set
    * @param currentSpeed
    *           is the current transfer speed
    * @param averageSpeed
    *           is the average transfer speed
    */
   public void setSpeeds(int progress, double currentSpeed, double averageSpeed)
   {
      m_progressBar.setValue(progress);
      m_progressBar.revalidate();
      m_progressBar.update(m_progressBar.getGraphics());

      m_speedMeter.setSpeeds(currentSpeed);
      m_txtSpeedMin.setText(m_speedMeter.getSpeedMin(), "0.00");
      m_txtSpeedMax.setText(m_speedMeter.getSpeedMax(), "0.00");
      m_txtSpeedAvg.setText(averageSpeed, "0.00");
   }

   public boolean forceStopTask()
   {
      return m_forceStopTask;
   }

   public void finalize()
   {
      m_statusStartStop = 1; // stopped
      m_jbStartStop.setToolTipText(m_toolTipStartStop[m_statusStartStop]);
      m_jbStartStop.setIcon(m_iconStartStop[m_statusStartStop]);
   }

   public void setMessage(String message, boolean join)
   {
      m_statusMessage = (true == join) ? m_statusMessage + message : message;
      setStatusMessageTextArea();
   }

   private synchronized void setStatusMessageTextArea()
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            m_messageTextArea.setText(Wrap.wrap(m_statusMessage.replaceAll("\t", "   "), 30, null, true, null, "   "));
         }
       });
    }
}