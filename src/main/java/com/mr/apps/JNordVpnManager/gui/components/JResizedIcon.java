package com.mr.apps.JNordVpnManager.gui.components;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.mr.apps.JNordVpnManager.Starter;

@SuppressWarnings("serial")
public class JResizedIcon extends ImageIcon
{

   public JResizedIcon(String iconName, int width, int height)
   {
      super(Starter.class.getResource("resources/icons/" + iconName));
      if ((this.getIconWidth() != width) && (this.getIconHeight() != height))
      {
         Image myImage = this.getImage();
         Image resizedImage = myImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
         new ImageIcon(resizedImage);         
      }
   }
}
