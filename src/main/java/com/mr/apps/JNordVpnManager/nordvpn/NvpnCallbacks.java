/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.geotools.Location;
import com.mr.apps.JNordVpnManager.gui.GuiMenuBar;
import com.mr.apps.JNordVpnManager.gui.connectLine.GuiConnectLine;
import com.mr.apps.JNordVpnManager.gui.dialog.JAutoCloseLoginDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class NvpnCallbacks
{

   private static String m_lastErrorMessage = null;

   /**
    * Check error condition of last executed command
    * @return true if there is an active error, else false
    */
   public static boolean isLastError()
   {
      return (null == m_lastErrorMessage) ? false : true;
   }

   /**
    * Get the last error
    * <p>
    * This method resets the error.
    * @return the last error string (null if there was no error)
    */
   public static String getLastError()
   {
      String message = m_lastErrorMessage;
      m_lastErrorMessage = null;
      return message;
   }

   public static String executeConnect(Location loc)
   {
      String msg = "no message...";
      m_lastErrorMessage = null;
      if (null != loc)
      {
         Starter._m_logError.LoggingInfo("Change Server: " + loc.toString());

         String city = loc.getCityNordVPN();
         String country = loc.getCountryNordVPN();
         msg = NvpnCommands.connect(country, city);
         if (UtilSystem.isLastError())
         {
            // KO
            if(null != msg && msg.contains("You are not logged in"))
            {
               m_lastErrorMessage = msg;
            }
            else
            {
               m_lastErrorMessage = UtilSystem.getLastError();
            }
         }
         else
         {
            // OK
            Starter.updateCurrentServer();
            GuiMenuBar.addToMenuRecentServerListItems(loc);
         }
      }

      return msg;
   }
   
   public static String executeDisConnect()
   {
      m_lastErrorMessage = null;
      String msg = NvpnCommands.disconnect();
      if (UtilSystem.isLastError())
      {
         //KO
         m_lastErrorMessage = UtilSystem.getLastError();
      }
      else
      {
         // OK
         Starter.updateCurrentServer();

         // if command returns 2 lines, suppress the second line (TODO: Requester with rating)
         String[] msgLines= msg.split("\\n");
         if (msgLines.length == 2)
         {
            msg = msgLines[0];
         }
      }

      return msg;
   }
   
   private static String executeLogin()
   {
      m_lastErrorMessage = null;
      String msg = NvpnCommands.login();
      if (UtilSystem.isLastError())
      {
         //KO
         m_lastErrorMessage = UtilSystem.getLastError();
      }
      else
      {
         // OK
         if (false == msg.contains("You are already logged in."))
         {
            Pattern pattern = Pattern.compile("^\\s*Continue in the browser:\\s*(.*)",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(msg);
            boolean matchFound = matcher.find();
            if (matchFound)
            {
               // Parsing ok
               try
               {
                  // copy link to clipboard
                  Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                  StringSelection strse1 = new StringSelection(matcher.group(1));
                  clip.setContents(strse1, strse1);
                  // open Web browser
                  UtilSystem.openWebpage(new URI(matcher.group(1)));
               }
               catch (URISyntaxException e)
               {
                  Starter._m_logError.LoggingExceptionAbend(10903, e);
                  m_lastErrorMessage = "Could not launch web page URL=" + matcher.group(1);
               }
            }
            else
            {
               // Parsing Error
               Starter._m_logError.LoggingError(10100, "Parsing NordVPN Login Information", msg);
               Starter._m_logError.TraceDebug("NordVPN login Pattern=" + pattern.toString());

               m_lastErrorMessage = "'nordvpn login' information cannot be parsed.";
            }
         }
      }

      return msg;
   }
   
   /**
    * Execute Login/Logout Callback
    * <p>
    * This Callback gets the real status from the command 'nordvpn account'. The current real status may not be the same as the panel status -
    * e.g. in case of manual login/logout during the GUI is open. Remark: should be fixed with WindowGained Event...<br>
    */
   public static void executeLogInOut()
   {
      m_lastErrorMessage = null;
      String msg = null;
      Starter._m_logError.TraceDebug("(execute Logout): Check, if we are still logged in...");
      NvpnAccountData accountData = new NvpnAccountData();
      boolean currentStatus = accountData.isLoggedIn();
      if (true == currentStatus)
      {
         // Login -> Logout
         if (JModalDialog.showConfirm("Are you sure you want to logout?") == JOptionPane.YES_OPTION)
         {
            msg = NvpnCommands.logout();
            if (UtilSystem.isLastError()) m_lastErrorMessage = UtilSystem.getLastError();
         }
      }
      else
      {
         // Logout -> Login
         msg = NvpnCallbacks.executeLogin();
         if (NvpnCallbacks.isLastError()) m_lastErrorMessage = NvpnCallbacks.getLastError();
      }

      if (null != m_lastErrorMessage)
      {
         // KO
         if (true == currentStatus)
         {
            // Logout KO
            JModalDialog.showError("NordVPN Logout", m_lastErrorMessage);
         }
         else
         {
            // Login LO
            JModalDialog.showError("NordVPN Login", m_lastErrorMessage);
         }         
      }
      else if (null != msg) // msg is null in case of cancel Logout
      {
         // OK
         if (true == currentStatus)
         {
            // Logout OK
            JModalDialog.showMessage("NordVPN Logout", msg);
         }
         else
         {
            // Login - wait for login in external Browser (or cancel) 
            /* JAutoCloseLoginDialog jd = */ new JAutoCloseLoginDialog(Starter.getMainFrame(), msg);
         }

         // get the current status
         accountData = new NvpnAccountData();
         boolean newStatus =  accountData.isLoggedIn();
         if (newStatus != currentStatus)
         {
            // OK, status changed
            int iAutoConnect = UtilPrefs.getAutoConnectMode();
            if ((1 == iAutoConnect) && (newStatus == true))
            {
               // AutoConnect after login
               CurrentLocation loc = Starter.getCurrentServer();
               if (null != loc)
               {
                  NvpnCallbacks.executeConnect(loc);
               }
            }
            else if (newStatus == false)
            {
               // switch from login to logout -> server disconnected
               Starter.updateCurrentServer();
            }

            // update GUI
            GuiMenuBar.updateLoginOut(accountData);
            GuiConnectLine.updateLoginOut(accountData);            
         }
      }
   }
}
