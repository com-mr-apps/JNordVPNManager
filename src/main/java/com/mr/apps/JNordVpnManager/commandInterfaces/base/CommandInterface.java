package com.mr.apps.JNordVpnManager.commandInterfaces.base;

import java.awt.event.ActionEvent;

import com.mr.apps.JNordVpnManager.Starter;

public interface CommandInterface
{
   public static final String METHOD_GET       = "get";
   public static final String METHOD_EXECUTE   = "execute";
   public static final String METHOD_UPDATE_UI = "updateUI";

   public static Object get()
   {
      Starter._m_logError.TraceDebug("Get Method not implemented.");
      return null;
   }

   public static boolean execute(ActionEvent e)
   {
      Starter._m_logError.TraceDebug("Execute Method not implemented.");
      return true;
   }

   public static boolean updateUI(Command cmd)
   {
      Starter._m_logError.TraceDebug("No UpdateUI Method for command '" + cmd.toString() + "'");
      return true;
   }
}
