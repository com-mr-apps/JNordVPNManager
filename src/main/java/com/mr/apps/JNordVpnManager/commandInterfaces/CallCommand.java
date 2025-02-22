/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.commandInterfaces;

import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.prefs.Preferences;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilCrypt;

public class CallCommand
{
   private static URLClassLoader m_urlClassLoader = null;

   public static boolean initClassLoader(String path)
   {
      String jarFile = "";
      try
      {
         // not hacker proofed - just to protect my work by legal copyright
         // Donations based on completely free software seems not to work - changed the concept to addOns for Supporters...
         Preferences prefAddOns = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings/AddOns");
         String key = prefAddOns.get("Security.Key", "");
         if (key.isBlank()) return false;
         String enc = prefAddOns.get("Data.1", "");
         if (enc.isBlank()) return false;

         UtilCrypt cryptoUtil = new UtilCrypt();
         String dec = cryptoUtil.decrypt(key, enc);
         jarFile = "JNordVpnManager.addons-" + dec + ".jar";
         Starter._m_logError.LoggingInfo("Load add-on library '" + jarFile + "'. This library is protected by copyright. Illegal use is prohibited!");
         Starter._m_logError.LoggingInfo("\nI would like to thank the supporters of my work and wish you much joy with the application.\n"
               + "To become a supporter and get a legal version of the add-on library together with supporters specific content,\n"
               + "you are invited to visit me here: https://buymeacoffee.com/3dprototyping");
      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10500, e);
         return false;
      }

      if (false == path.endsWith("/")) path = path + "/";
      File fpJarFile = new File(path, jarFile);
      try
      {
         if (fpJarFile.canRead())
         {

            URL[] jarURL = {
                  new URL("file:" + fpJarFile.getAbsolutePath())
            };
            if (null != m_urlClassLoader)
            {
               m_urlClassLoader.close();
            }
            m_urlClassLoader = new URLClassLoader(jarURL);
         }
         else
         {
            Starter._m_logError.LoggingWarning(10901,
                  "Add Classpath Error",
                  "Addon jarfile (optional) does not exist:\n" + fpJarFile.getAbsolutePath());
            return false;
         }

      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingWarning(10901,
               "Add Classpath Exception",
               "Addon jarfile (optional) does not exist:\n" + fpJarFile.getAbsolutePath());
         return false;
      }
      return true;
   }

   public static Object invokeAddonMethod(String className, String methodName)
   {
      return invokeMethod(null, "com.mr.apps.JNordVpnManager.addons." + className, methodName, null, null);
   }

   public static Object invokeCommandMethod(Command cmd, String methodName)
   {
      return invokeMethod(cmd, "com.mr.apps.JNordVpnManager.commandInterfaces." + cmd.getCommand(), methodName, new Object[]{cmd}, new Class<?>[]{com.mr.apps.JNordVpnManager.commandInterfaces.Command.class});
   }

   public static Object invokeEventMethod(Command cmd, String methodName, ActionEvent e)
   {
      return invokeMethod(cmd, "com.mr.apps.JNordVpnManager.commandInterfaces." + cmd.getCommand(), methodName, new Object[]{e}, new Class<?>[]{java.awt.event.ActionEvent.class});
   }

   public static Object invokeBasisMethod(Command cmd, String methodName, String[] arguments, Class<String>[] argTypes)
   {
      return invokeMethod(cmd, "com.mr.apps.JNordVpnManager.commandInterfaces." + cmd.getCommand(), methodName, arguments, argTypes);
   }

   private static Object invokeMethod(Command cmd, String className, String methodName, Object[] arguments, Class<?>[] argTypes)
   {
      Object callResult;
//      Starter._m_logError.TraceDebug("Invoke method " + className + "." + methodName);
      try
      {
         if (null != arguments)
         {
            if (null == m_urlClassLoader)
            {
               Class<?> c = Class.forName(className);
               Method m = c.getDeclaredMethod(methodName, argTypes);
               callResult = m.invoke(null, (Object[]) arguments);
            }
            else
            {
               Class<?> c = m_urlClassLoader.loadClass(className);
               Object instance = c.getDeclaredConstructor().newInstance();
               Method m = c.getMethod(methodName, argTypes);
               callResult = m.invoke(instance, (Object[]) arguments);
            }
         }
         else
         {
            if (null == m_urlClassLoader)
            {
               Class<?> c = Class.forName(className);
               Method m = c.getDeclaredMethod(methodName, (Class[]) null);
               callResult = m.invoke(null, (Object[]) null);
            }
            else
            {
               Class<?> c = m_urlClassLoader.loadClass(className);
               Object instance = c.getDeclaredConstructor().newInstance();
               Method m = c.getMethod(methodName);
               callResult = m.invoke(instance);
            }
         }
      }
      catch (ClassNotFoundException e)
      {
         Starter._m_logError.LoggingError(10900,
               "Class not found.",
               "Class [" + className + "] not found exception - skipping method call.");
         callResult = null;
      }
      catch (NoSuchMethodException e)
      {
         Starter._m_logError.LoggingError(10900,
               "Method not found or missmatched arguments.",
               "A method with the name=" + methodName + " - and the supplied arguments could not be found.");
         callResult = null;
      }
      catch (InvocationTargetException e)
      {
         Starter._m_logError.LoggingError(10900,
               "Invoke failed.",
               "Invocation of the method with the name=" + methodName + " failed - skipping method call.");
         callResult = null;
      }
      catch (IllegalAccessException e)
      {
         Starter._m_logError.LoggingError(10900,
               "Illegal access.",
               "Illegal access for method with the name=" + methodName + " - skipping method call.");
         callResult = null;
      }
      catch (IllegalArgumentException e)
      {
         Starter._m_logError.LoggingError(10900,
               "Illegal argument.",
               "Illegal argument for method with the name=" + methodName + " - skipping method call.");
         callResult = null;
      }
      catch (Exception e)
      {
         Starter._m_logError.LoggingError(10900,
               "Generic exception.",
               "Exception encountered when trying to invoke method with the name=" + methodName + " in Class [" + className + "] - skipping method call.");
         callResult = null;
      }

      return callResult;
   }
}
