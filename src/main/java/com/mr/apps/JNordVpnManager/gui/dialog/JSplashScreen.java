/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.*;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JLogo;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon;

@SuppressWarnings("serial")
public class JSplashScreen extends JDialog
{
   private static final String SPLASH_IMAGE = "resources/SplashScreen.png";

   protected static String _m_versionText; // set at program start and reused for "About" window

   protected JLabel       m_splashImageIcon;
   protected JLabel       m_splashStatus;
   protected JProgressBar m_progressBar;
   protected JLabel       m_version;
   protected int          m_panelWidth = 0;

   /**
    * Initiates a new Welcome Screen
    */
   public JSplashScreen(Frame owner)
   {
      this(owner, null);
   }

   /**
    * Initiates a new Splash Screen or the Welcome Screen
    * 
    * @param owner
    *           is the owner frame.
    * @param status
    *           is the initial status text to be displayed. In case of null, the Welcome screen is displayed.
    */
   public JSplashScreen(Frame owner, String status)
   {
      super(owner, false);
      this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

      ImageIcon imageIcon = new ImageIcon(Starter.class.getResource(SPLASH_IMAGE));
      this.m_splashImageIcon = new JLabel(imageIcon);
      this.add(this.m_splashImageIcon);

      m_version = new JLabel("Version"); // will be updated from the application
      m_version.setSize(m_version.getPreferredSize());
      m_version.setLocation(25, 290);
      m_version.setFont(new Font("serif", Font.ITALIC, 12));
      m_version.setForeground(new Color(97, 206, 255));
      this.m_splashImageIcon.add(m_version);

      JLabel copyright =  new JLabel(Starter.COPYRIGHT_STRING);
      copyright.setSize(copyright.getPreferredSize());
      copyright.setLocation(25, 310);
      copyright.setFont(new Font("serif", Font.ITALIC, 12));
      copyright.setForeground(new Color(97, 206, 255));
      this.m_splashImageIcon.add(copyright);

      JLogo mrLogo = new JLogo(JLogo.Logos.LOGO_MR);
      mrLogo.setSize(new Dimension(80,80));
      mrLogo.setLocation(20, 300);
      mrLogo.setSize(new Dimension(80,80));
      mrLogo.setLocation(20, 20);
      this.m_splashImageIcon.add(mrLogo);

      if (Starter.isSupporterEdition())
      {
         JLabel supporterEdition = new JLabel(JResizedIcon.getIcon("SupporterEdition.png", 268, 30));
         supporterEdition.setSize(new Dimension(268, 30));
         supporterEdition.setLocation(320, 300);
         this.m_splashImageIcon.add(supporterEdition);
      }

      if (null == status)
      {
         // Welcome screen
         JLogo buymeacoffee = new JLogo(JLogo.Logos.LOGO_BUYMEACOFFEE);
         buymeacoffee.setSize(new Dimension(80,80));
         buymeacoffee.setLocation(500, 20);
         this.m_splashImageIcon.add(buymeacoffee);

         this.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
         m_splashImageIcon.setToolTipText("Press Mouse Button to close.");
         m_splashImageIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
               Starter.setSkipWindowGainedFocus();
               setVisible(false);
               dispose();
            }
         });
         setVersion(_m_versionText);
      }
      else
      {
         // Splash Screen
         this.m_splashStatus = new JLabel();
         this.add(this.m_splashStatus);
         
         m_progressBar = new JProgressBar(0, 100);
         this.add(m_progressBar);
         this.setPreferredSize(new Dimension(imageIcon.getIconWidth()+10, imageIcon.getIconHeight() + 40));
         Starter._m_logError.setCurStartTime();
      }

      this.setUndecorated(true);
      this.pack();
      if (null != status) this.setStatus(status);
      
      // Centers the Splash Screen
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension panelSize = this.getSize();
      m_panelWidth = panelSize.width;
      this.setLocation((screenSize.width / 2) - (panelSize.width / 2), (screenSize.height / 2) - (panelSize.height / 2));
   }

   /**
    * Set the value of progress bar on the Splash Screen
    * 
    * @param progress
    *           The value to be set
    */
   public void setProgress(int progress)
   {
      Starter._m_logError.getCurElapsedTime("Progress " + progress);
      m_progressBar.setValue(progress);
      m_progressBar.revalidate();
      m_progressBar.update(m_progressBar.getGraphics());
      if (progress == 100)
      {
         Starter.setSkipWindowGainedFocus();
         this.setVisible(false);
         this.dispose();
      }
   }

   /**
    * Returns the actual value of the progress bar on the Splash Screen
    * 
    * @return The actual Value of the progress bar
    */
   public int getProgress()
   {
      return m_progressBar.getValue();
   }

   /**
    * Sets a new status text
    * 
    * @param status
    *           The new status text
    */
   public void setStatus(String status)
   {
      m_splashStatus.setText(String.format("%-100s", status));
      if (m_panelWidth > 0)
      {
         // hack - delete background
         m_splashStatus.getGraphics().clearRect(0, 0, m_panelWidth, 50);
         m_splashStatus.update(m_splashStatus.getGraphics());
      }
      Starter._m_logError.TraceIni(status);
   }

   /**
    * Set the minimum Value of the Progress Bar
    * 
    * @param min
    *           The minimum Value of the Progress Bar
    */
   public void setMinimum(int min)
   {
      m_progressBar.setMinimum(min);
   }

   /**
    * Set the maximum Value of the Progress Bar
    * 
    * @param max
    *           The maximum Value of the Progress Bar
    */
   public void setMaximum(int max)
   {
      m_progressBar.setMaximum(max);
   }

   /**
    * Returns the actual minimum value of the Progress Bar on the Splash Screen
    * 
    * @return the minimum value
    */
   public int getMinimum()
   {
      return m_progressBar.getMinimum();
   }

   /**
    * Returns the actual maximum value of the Progress Bar on the Splash Screen
    * 
    * @return the maximum value
    */
   public int getMaximum()
   {
      return m_progressBar.getMaximum();
   }

   /**
    * Gets the actual status text of the Splash Screen
    * 
    * @return the status text
    */
   public String getStatus()
   {
      return m_splashStatus.getText();
   }

   /**
    * Set the program version string
    * 
    * @param version
    *           The version string
    */
   public void setVersion(String version)
   {
      _m_versionText = (null == version) ? "Test..." : version;
      this.m_version.setText("Version " + _m_versionText);
      m_version.setSize(m_version.getPreferredSize());
   }

}