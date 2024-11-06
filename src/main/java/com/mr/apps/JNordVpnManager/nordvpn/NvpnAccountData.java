/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class NvpnAccountData
{
   private boolean m_loggedIn               = false;
   private String  m_email                  = null;
   private boolean m_vpnServiceIsActive     = false;
   private String  m_vpnServiceExpDate      = null;
   private boolean m_vpnDedicatedIdIsActive = false;
   private boolean m_mfaIsEnabled           = false;

   public NvpnAccountData()
   {
      String msg = NvpnCommands.getAccountInfo();
      if (UtilSystem.isLastError())
      {
         if (msg.contains("You are not logged in."))
         {
         }
         else
         {
            msg = UtilSystem.getLastError();
            JModalDialog.showError("NordVPN Account", msg);
         }
       }
      else
      {
         // OK - extract data
         int rc = parseData(msg);
         if (1 == rc)
         {
            JModalDialog.showError("NordVPN Account", "'nordvpn account' information cannot be parsed.");
         }
      }
   }

   public int parseData(String data)
   {
      int rc = 0;
      
      Pattern pattern = Pattern.compile("^\\s*Account Information:\\s*\\n" // 
            + "\\s*Email Address:\\s+([^\\n]+)\\n"                         // john.doe@mail.com\n"
            + "\\s*VPN Service:\\s+([^(]+)\\(([^)]+)\\)\\s*\\n"            // Active (Expires on...)\n"
            + "\\s*Dedicated IP:\\s+([^\\n]+)\\n"                          // Inactive\n"
            + "\\s*Multi-factor Authentication \\(MFA\\):\\s+([^\\n]+)",   // disabled"
            Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(data);

      boolean matchFound = matcher.find();
      if (matchFound)
      {
         // Parsing OK
         this.setLoggedIn(true);            
         this.setEmail(matcher.group(1));
         this.setVpnServiceIsActive(matcher.group(2).equalsIgnoreCase("Active"));
         this.setVpnServiceExpDate(matcher.group(3)); // TODO: extract date
         this.setVpnDedicatedIdIsActive(matcher.group(4).equalsIgnoreCase("Active"));
         this.setMfaIsEnabled(matcher.group(4).equalsIgnoreCase("enabled"));
      }
      else
      {
         // Parsing Error
         rc = 1;
         Starter._m_logError.TranslatorError(10100, "Parsing NordVPN Account Information", data);
         Starter._m_logError.TraceDebug("NordVPN Account Parsing Pattern=" + pattern.toString() + "<.");

         this.setLoggedIn(false);
         this.setEmail("noname@mail.com");
         this.setVpnServiceIsActive(false);
         this.setVpnServiceExpDate("n/a");
         this.setVpnDedicatedIdIsActive(false);
         this.setMfaIsEnabled(false);
      }

      return rc;
   }

   public boolean isLoggedIn()
   {
      return m_loggedIn;
   }

   public void setLoggedIn(boolean m_loggedIn)
   {
      this.m_loggedIn = m_loggedIn;
   }

   public String getEmail()
   {
      return m_email;
   }

   public void setEmail(String email)
   {
      this.m_email = email;
   }

   public boolean isVpnServiceIsActive()
   {
      return m_vpnServiceIsActive;
   }

   public void setVpnServiceIsActive(boolean vpnServiceIsActive)
   {
      this.m_vpnServiceIsActive = vpnServiceIsActive;
   }

   public String getVpnServiceExpDate()
   {
      return m_vpnServiceExpDate;
   }

   public void setVpnServiceExpDate(String vpnServiceExpDate)
   {
      this.m_vpnServiceExpDate = vpnServiceExpDate;
   }

   public boolean isVpnDedicatedIdIsActive()
   {
      return m_vpnDedicatedIdIsActive;
   }

   public void setVpnDedicatedIdIsActive(boolean vpnDedicatedIdIsActive)
   {
      this.m_vpnDedicatedIdIsActive = vpnDedicatedIdIsActive;
   }

   public boolean isMfaIsEnabled()
   {
      return m_mfaIsEnabled;
   }

   public void setMfaIsEnabled(boolean mfaIsEnabled)
   {
      this.m_mfaIsEnabled = mfaIsEnabled;
   }
}
