/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.components;

import java.awt.Cursor;
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JLabel;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.components.JResizedIcon.IconSize;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

/**
 * Class to generate predefined JLabel logos with tooltip and weblink
 */
@SuppressWarnings("serial")
public class JLogo extends JLabel
{
   // definitions for my logos
   public static enum Logos
   {
      LOGO_MR("mrLogo",
            "<html><font face=\"sansserif\" color=\"black\">Press the Mouse Button to go to GitHub Repository:<br>https://github.com/com-mr-apps</font></html>",
            "https://github.com/com-mr-apps"), 
      LOGO_BUYMEACOFFEE("bmc_qr",
            "<html><font face=\"sansserif\" color=\"black\">If you like to support my work,<br>you can press the Mouse Button and buy me a coffee here:<br>https://buymeacoffee.com/3dprototyping</font></html>",
            "https://buymeacoffee.com/3dprototyping");

      private String logoName;
      private String toolTip;
      private String uri;

      Logos(String logoName, String toolTip, String uri)
      {
         this.logoName = logoName;
         this.toolTip = toolTip;
         this.uri = uri;
      }

      public String getToolTip()
      {
         return toolTip;
      }

      public String getName()
      {
         return logoName;
      }

      public URI getURI() throws URISyntaxException
      {
         return new URI(uri);
      }

      public URL getIconUrl()
      {
         return Starter.class.getResource("resources/" + logoName + ".png");
      }
   }

   public JLogo(Logos logo)
   {
      super();
      this.setIcon(JResizedIcon.getIcon(logo.getIconUrl(), IconSize.LOGO));
      this.setToolTipText(logo.toolTip);
      this.setSize(new Dimension(IconSize.LOGO.getSize(), IconSize.LOGO.getSize()));
      this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      this.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            try
            {
               UtilSystem.openWebpage(logo.getURI());
            }
            catch (URISyntaxException e)
            {
               Starter._m_logError.LoggingExceptionAbend(10903, e);
            }
         }
      });
   }
}
