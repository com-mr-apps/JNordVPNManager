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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mr.apps.JNordVpnManager.Starter;

public class CallCommand
{

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

   public static Object invokeAddonsMethod(Command cmd, String methodName, String[] arguments, Class<String>[] argTypes)
   {
      return invokeMethod(cmd, "com.mr.apps.JNordVpnManager.addons.commandInterfaces." + cmd.getCommand(), methodName, arguments, argTypes);
   }

   private static Object invokeMethod(Command cmd, String className, String methodName, Object[] arguments, Class<?>[] argTypes)
   {
      Object callResult;
      Starter._m_logError.TraceDebug("invoke method " + cmd.getCommand() + " - " + methodName);
      try
      {
         if (null != arguments)
         {
            Class<?> c = Class.forName(className);
            Method m = c.getDeclaredMethod(methodName, argTypes);
            callResult = m.invoke(null, (Object[]) arguments);
         }
         else
         {
            Class<?> c = Class.forName(className);
            Method m = c.getDeclaredMethod(methodName, (Class[]) null);
            callResult = m.invoke(null, (Object[]) null);
         }
      }
      catch (ClassNotFoundException e)
      {
         Starter._m_logError.LoggingError(10900,
               "Class not found.",
               "Class not found exception - skipping method call.");
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
      catch (Exception e)
      {
         Starter._m_logError.LoggingError(10900,
               "Generic exception.",
               "Exception encountered when trying to invoke method with the name=" + methodName + " - skipping method call.");
         callResult = null;
      }

      return callResult;
   }
}
