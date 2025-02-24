/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.settings;

/**
 * Dataset defining the JSettingsPanel values.
 * <p>
 * Contains the panel field description by Id:
 * <ul>
 * <li>Label text</li>
 * <li>Field Type, where: "T" - Text field / "N[min,max]" - Integer with optional range / "B" - Boolean
 * (CheckBox)</li>
 * <li>Mnemonic (-1 - no KeyEvent)</li>
 * <li>Field length</li>
 * <li>Default value</li>
 * </ul>
 */
public class JSettingsPanelField
{
   // members
   private String                               label;
   private String                               elementType;
   private int                                  mnemonic;
   private int                                  length;
   private String                               defaultValue;
   private Object                               JPanelComponent;

   // constructor
   public JSettingsPanelField(String label, String elementType, int mnemonic, int length, String defaultValue)
   {
      this.label = label;
      this.elementType = elementType;
      this.mnemonic = mnemonic;
      this.length = length;
      this.defaultValue = defaultValue;
      this.JPanelComponent = null;
   }

   // data access methods
   public String getLabel()
   {
      return label;
   }

   public String getElementType()
   {
      return elementType;
   }

   public int getMnemonic()
   {
      return mnemonic;
   }

   public int getLength()
   {
      return length;
   }

   public String getDefaultValue()
   {
      return defaultValue;
   }

   public Object getJPanelComponent()
   {
      return JPanelComponent;
   }

   public void setJPanelComponent(Object jPanelComponent)
   {
      JPanelComponent = jPanelComponent;
   }

   public String toString()
   {
      return label + " Type=" + elementType + " Key=" + mnemonic + " Length=" + length + " Default=" + defaultValue + " Component=" +((JPanelComponent == null) ? "not set" : "set"); 
   }
}
