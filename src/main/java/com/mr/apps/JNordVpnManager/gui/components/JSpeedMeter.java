/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

@SuppressWarnings("serial")
public class JSpeedMeter extends JPanel
{
   // geometric constants.
   private static final int       _m_componentSize      = 180;
   private static final int       _m_radiusNeedle       = (int) (_m_componentSize * 0.2777777);
   private static final int       _m_radiusNumbers      = (int) (_m_componentSize * 0.3888888);
   private static final int       _m_lengthSubInterval  = (int) (_m_componentSize * 0.05);
   private static final int       _m_lengthMainInterval = (int) (_m_componentSize * 0.0888888);
   private static final int       _m_xOrigin            = _m_componentSize / 2;
   private static final int       _m_yOrigin            = _m_componentSize / 2;
   private static final double    _m_startAngle         = 225;
   private static final double    _m_endAngle           = -45;
   private static final double    _m_maxSpeed           = 100;
   private static final double    _m_mainInterval       = (_m_startAngle - _m_endAngle) / 5;
   private static final double    _m_subInterval        = (_m_startAngle - _m_endAngle) / 20;
   private static final int       _m_needleDiameter     = (int) (_m_componentSize * 0.1);
   private static final int       _m_needleLength       = _m_radiusNeedle + _m_lengthSubInterval;

   // center of the speed meter needle
   private static final Ellipse2D _m_e2dNeedleCenter = new Ellipse2D.Double (
         _m_xOrigin-_m_needleDiameter/2, _m_yOrigin-_m_needleDiameter/2, _m_needleDiameter, _m_needleDiameter);
   
   // speed meter needle
   private static final Polygon _m_polygonNeedle = new Polygon (
         new int[] {_m_xOrigin+_m_needleLength,_m_xOrigin,_m_xOrigin,_m_xOrigin+_m_needleLength,_m_xOrigin+_m_needleLength},
         new int[] {_m_yOrigin-2, _m_yOrigin-_m_needleDiameter/4, _m_yOrigin+_m_needleDiameter/4, _m_yOrigin+1, _m_yOrigin-2},
         5);

   // 2D affine transform
   AffineTransform                m_at                  = new AffineTransform();

   // Speeds
   private double                 m_speed               = 0;
   private double                 m_speedMax            = 0;
   private double                 m_speedMin            = _m_maxSpeed;
   private int                    m_recordCount         = 0;
   private double                 m_speedAvg            = 0;

   private String                 m_speedString         = null;
   private String                 m_title               = null;

   private Shape[]                m_scaleShapes;
   
   /**
    * Construct the speed meter
    * 
    * @param title
    *           is the speed meter title
    */
   public JSpeedMeter (String title)
   {
      super();
      m_title = (null == title) ? "Speed" : title;

      this.setSize(_m_componentSize, _m_componentSize);
      m_scaleShapes = new Shape[21];
      
      int i=0;
      for (double k=_m_startAngle; k > _m_endAngle; k -= _m_mainInterval)
      {
         if (k == _m_startAngle)
         {
            m_scaleShapes[i++] = createLine(_m_radiusNeedle, _m_lengthMainInterval, k);
         }
         
         for (double j=_m_subInterval; j <= _m_mainInterval-_m_subInterval; j += _m_subInterval)
         {
            m_scaleShapes[i++] = createLine(_m_radiusNeedle,_m_lengthSubInterval,k-j);
         }
         m_scaleShapes[i++] = createLine(_m_radiusNeedle, _m_lengthMainInterval, k-_m_mainInterval);
      }
   }

   public Dimension getMinimumSize()
   {
      return new Dimension (_m_componentSize, _m_componentSize);
   }

   /**
    * Create a line of the scale gradations.
    * 
    * @param radius
    *           the inner radius of the scale
    * @param length
    *           the length of the line
    * @param alpha
    *           the angle of the line
    * @return the corresponding line as a <class>Shape</class> instance.
    */
   private Shape createLine(double radius, double length, double alpha)
   {
      double sinAlpha = Math.sin(Math.toRadians(alpha));
      double cosAlpha = Math.cos(Math.toRadians(alpha));
      return new Line2D.Double(
            _m_xOrigin + radius * cosAlpha, _m_yOrigin - radius * sinAlpha,
            _m_xOrigin + (radius + length) * cosAlpha, _m_yOrigin - (radius + length) * sinAlpha);
   }

   public void reset()
   {
      this.m_speed = 0;
      this.m_speedMax = 0;
      this.m_speedMin = _m_maxSpeed;
      this.m_speedAvg = 0;
      this.m_recordCount = 0;
      this.m_speedString = "";
      repaint();
   }

   /**
    * Set the current speed and repaint the speed meter
    * 
    * @param currentSpeed
    *           is the current speed
    */
   public void setSpeeds (double currentSpeed)
   {
      this.m_speed = currentSpeed;
      // set max. speed
      if (currentSpeed > this.m_speedMax) this.m_speedMax = currentSpeed;
      if (Math.abs(currentSpeed) > 0.001)
      {
         // set min. speed
         if (currentSpeed < this.m_speedMin) this.m_speedMin = currentSpeed;
         // calculate the average
         double average = this.m_speedAvg * m_recordCount;
         ++m_recordCount;
         this.m_speedAvg = (average + currentSpeed) / m_recordCount;
      }

      // Limit the max display speed
      if (this.m_speed > _m_maxSpeed + 10) this.m_speed = _m_maxSpeed + 10;

      // build the string to display
      this.m_speedString = StringFormat.number2String(this.m_speed, "0.00") + "Mbit/s" /* +
                         " [min:" + StringFormat.number2String(this.m_speedMin, "0.00") +
                         " / max: " + StringFormat.number2String(this.m_speedMax, "0.00") +
                         " / Av: " + StringFormat.number2String(this.m_speedAvg, "0.00") + "]" */ ;
      repaint ();
   }

   public double getSpeed()
   {
      return m_speed;
   }

   public double getSpeedMax()
   {
      return m_speedMax;
   }

   public double getSpeedMin()
   {
      return m_speedMin;
   }

   public double getSpeedAvg()
   {
      return m_speedAvg;
   }

   /**
    * [Re]Paint the speed meter
    * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
    */
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D ga = (Graphics2D) g;

      ga.setColor(Color.white);
      ga.fillRect(0, 0, _m_componentSize - 1, _m_componentSize - 1);
      ga.setColor(Color.black);
      ga.drawRect(0, 0, _m_componentSize - 1, _m_componentSize - 1); // draw border
      ga.drawString(m_title, 5, 15);

      if (m_speedString != null)
      {
         ga.drawString(m_speedString, 5, _m_componentSize - 5);
      }

      for (int k = 0; k < m_scaleShapes.length; k++)
      {
         ga.draw(m_scaleShapes[k]);
      }

      for (double alpha = _m_startAngle; alpha >= _m_endAngle; alpha -= _m_mainInterval)
      {
         double sinAlpha = Math.sin(Math.toRadians(alpha));
         double cosAlpha = Math.cos(Math.toRadians(alpha));
         int speed = (int) (_m_maxSpeed * (_m_startAngle - alpha) / (_m_startAngle - _m_endAngle));
         String text = StringFormat.int2String(speed, "0");
         ga.drawString(text, (int) (_m_xOrigin - 7 + (_m_radiusNumbers + 7) * cosAlpha), (int) (_m_yOrigin + 3 - (_m_radiusNumbers + 5) * sinAlpha));
      }

      m_at.setToIdentity();
      m_at.translate(_m_xOrigin, _m_yOrigin);

      double angle = (m_speed * (_m_startAngle - _m_endAngle) / _m_maxSpeed) - _m_startAngle;
      m_at.rotate(Math.toRadians(angle));
      AffineTransform saveXform = ga.getTransform();
      AffineTransform toCenterAt = new AffineTransform();
      toCenterAt.concatenate(m_at);
      toCenterAt.translate(-_m_xOrigin, -_m_yOrigin);
      ga.transform(toCenterAt);

      ga.fill(_m_e2dNeedleCenter);
      if (m_speed > _m_maxSpeed) ga.setColor(Color.red);
      ga.fillPolygon(_m_polygonNeedle);

      ga.setTransform(saveXform);
   }
}