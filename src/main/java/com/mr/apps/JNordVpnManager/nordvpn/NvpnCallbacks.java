/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
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
import com.mr.apps.JNordVpnManager.gui.GuiMenuBar;
import com.mr.apps.JNordVpnManager.gui.dialog.JAutoCloseLoginDialog;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class NvpnCallbacks
{
   public static boolean executeConnect(CurrentLocation loc, String titleOk, String titleKo)
   {
      boolean rc = false;
      String msg = "no message...";
      if (null != loc)
      {
         msg = NvpnCommands.connect(loc);
         if (UtilSystem.isLastError())
         {
            // KO
            rc = false;
            String errMsg = "";
            if(null != msg && msg.contains("You are not logged in"))
            {
               errMsg = msg;
            }
            else
            {
               errMsg = UtilSystem.getLastError() + "\n" + StringFormat.printString(msg, "");
            }
            if (null != titleKo)
            {
               JModalDialog.showError(titleKo, errMsg);
            }
            else
            {
               Starter._m_logError.LoggingError(10904,
                     "Execute Connect Failed",
                     errMsg);
            }
         }
         else if (null != msg)
         {
            // OK
            rc = true;
            // overwrite possible "current" (null) values with the for this connection used ones
            loc.setLegacyGroup(loc.getLegacyGroup());
            loc.setVpnTechnology(loc.getVpnTechnology());
            loc.setVpnProtocol(loc.getVpnProtocol());
            // now we can add the location to the recent list
            GuiMenuBar.addToMenuRecentServerListItems(loc);

            if (null != titleOk)
            {
               JModalDialog.showMessageAutoClose(titleOk, msg);
            }
         }
         else
         {
            if (null != titleKo)
            {
               JModalDialog.showWarning("Cancelled new server connection to '" + loc.getServerKey() + "'.");
            }
            rc = false;
         }
      }

      return rc;
   }
   
   public static String executeDisConnect(String titleOk, String titleKo)
   {
      String msg = NvpnCommands.disconnect();
      if (UtilSystem.isLastError())
      {
         //KO
         String errMsg = UtilSystem.getLastError();
         if (null != titleKo)
         {
            JModalDialog.showError(titleKo, errMsg);
         }
         else
         {
            Starter._m_logError.LoggingError(10904,
                  "Execute Disconnect Failed",
                  errMsg);
         }
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

         if (null != titleOk)
         {
            JModalDialog.showMessageAutoClose(titleOk, msg);
         }
      }

      return msg;
   }
   
   private static String executeLogin()
   {
      String msg = NvpnCommands.login();
      if (UtilSystem.isLastError())
      {
         if (false == msg.contains("You are already logged in."))
         {
            //KO
            Starter._m_logError.LoggingError(10904,
                  "Execute Login Failed",
                  UtilSystem.getLastError());
         }
         else
         {
            // OK - already logged in
            msg = null;
         }
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
               }
            }
            else
            {
               // Parsing Error
               Starter._m_logError.LoggingError(10100, "Parsing NordVPN Login Information", msg);
               Starter._m_logError.TraceDebug("NordVPN login Pattern=" + pattern.toString());
            }
         }
      }

      return msg;
   }
   
   /**
    * Execute Login/Logout Callback
    */
   public static void executeLogInOut()
   {
      String msg = null;
      NvpnAccountData accountData = Starter.getCurrentAccountData(false);
      boolean currentStatus = accountData.isLoggedIn();
      if (true == currentStatus)
      {
         // Login -> Logout
         if (JModalDialog.showConfirm("Are you sure you want to logout?") == JOptionPane.YES_OPTION)
         {
            msg = NvpnCommands.logout();
         }
      }
      else
      {
         // Logout -> Login
         msg = NvpnCallbacks.executeLogin();
      }

      if (null != msg) // msg is null in case of cancel Logout
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
      }

      // get the new current status
      accountData = Starter.getCurrentAccountData(true);
      boolean newStatus = accountData.isLoggedIn();
      if (newStatus != currentStatus)
      {
         // OK, status changed
         int iAutoConnect = UtilPrefs.getAutoConnectMode();
         if ((1 == iAutoConnect) && (newStatus == true))
         {
            // AutoConnect after login
            CurrentLocation loc = Starter.getCurrentServer(true);
            if (null != loc)
            {
               NvpnCallbacks.executeConnect(loc, null, null);
            }
         }
         else if (newStatus == false)
         {
            // switch from login to logout -> server disconnected
            Starter.setCurrentServer(null);
            Starter.updateCurrentServer();
         }
      }

      // update current account data dependent GUI elements
      Starter.updateAccountData(false);
   }
}
