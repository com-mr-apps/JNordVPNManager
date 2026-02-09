/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class NvpnAccountData
{
   private boolean m_failed                   = false;
   private boolean m_loggedIn                 = false;
   private String  m_email                    = null;
   private boolean m_vpnServiceIsActive       = false;
   private String  m_vpnServiceExpDate        = null;
   private boolean m_vpnDedicatedIdIsActive   = false;
   private boolean m_mfaIsEnabled             = false;
   private int     m_nordAccountRemainingDays = 9999;

   public NvpnAccountData()
   {
      String msg = NvpnCommands.getAccountInfo();
      if (UtilSystem.isLastError())
      {
         if (msg.contains("You are not logged in."))
         {
            Starter._m_logError.TraceCmd(msg);
         }
         else
         {
            setFailed(true);
            msg = UtilSystem.getLastError() + "\n" + StringFormat.printString (msg, "<empty message>", "<null mwssage>");
            JModalDialog.showError("NordVPN Account", msg);
         }
       }
      else
      {
         // OK - extract data
         if (false == parseData(msg))
         {
            // failed
            setFailed(true);
            JModalDialog.showError("NordVPN Account", "'nordvpn account' information cannot be parsed.");
            return;
         }
      }
   }

   public boolean parseData(String data)
   {
      HashMap<String,String> values = new HashMap<String, String>();
      try
      {
         String[] saLines = data.split("\\n");
         for (String line : saLines)
         {
            String[] parts = line.split(":");
            if (parts.length == 2)
            {
               values.put(parts[0], parts[1].trim());
            }
            else
            {
               values.put(parts[0], "");
            }
         }
      }
      catch (Exception e)
      {
         // Parsing Error
         Starter._m_logError.LoggingError(10100,
               "Parsing NordVPN Account Information",
               data);
         return false;
      }

      // MR20260206: Changed output in v4.4.0 from 'VPN Service' to 'VPN Service'
      try
      {
         Pattern pattern = null;
         // data line 'VPN Service' contains two information
         String value = values.get("VPN Service");
         if (null != value)
         {
            pattern = Pattern.compile("\\s*([^( ]+)[ (]+([^)]+)\\)\\s*",
                  Pattern.CASE_INSENSITIVE);
         }
         else
         {
            value = values.get("Subscription");
            pattern = Pattern.compile("(Active)+\\s+until\\s+([^$]+)+$",
                  Pattern.CASE_INSENSITIVE);
         }
         Matcher matcher = pattern.matcher(value);
         boolean matchFound = matcher.find();
         if (matchFound)
         {
            // Parsing OK
            this.setVpnServiceIsActive(matcher.group(1));
            this.setVpnServiceExpDate(matcher.group(2));
            pattern = Pattern.compile("(?:Expires on\\s+)?([A-Za-z]+)+\\s+(\\d+)+[snrt]?[tdh]?,\\s+(\\d+)+",
                  Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(matcher.group(2));
            matchFound = matcher.find();
            if (matchFound)
            {
               SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");
               Date date = format.parse(matcher.group(3) + "-" + matcher.group(1) + "-" + matcher.group(2));
               long lTime = date.getTime();
               long daysUntilNow = UtilSystem.getDaysUntilNow(lTime) * (-1);
               Starter._m_logError.TraceIni("NordVPN License expires in " + daysUntilNow + " days.");
               m_nordAccountRemainingDays = (int) daysUntilNow;
               if (m_nordAccountRemainingDays > 180) UtilPrefs.resetAccountReminder(); // reset the settings to keep the
                                                                                       // reminder at the default days
                                                                                       // (after a subscription renew)
            }
         }
         else
         {
            // Parsing Error
            Starter._m_logError.LoggingError(10100, "Parsing NordVPN Account Information 'VPN Service'", data);
            // Fallback
            if (value.toUpperCase().contains("ACTIVE")) this.setVpnServiceIsActive("Active");
         }
      }
      catch (Exception e)
      {
         Starter._m_logError.TraceDebug("### (NvpnAccountData) NordVPN License expiration date could not be parsed.");
      }    

      this.setLoggedIn(true);

      // MR20260206: Changed output in v4.4.0 from 'Email Address' to 'Email address'
      String value = values.get("Email Address");
      if (value != null)
      {
         this.setEmail(value);
      }
      else
      {
         this.setEmail(values.get("Email address"));
      }
      
      this.setVpnDedicatedIdIsActive(values.get("Dedicated IP"));

      // MR20260206: Changed output in v4.4.0 from 'Multi-factor Authentication' to 'Multi-factor authentication'
      value = values.get("Multi-factor Authentication");
      if (null == value)
      {
         this.setMfaIsEnabled(value);
      }
      else
      {
         this.setMfaIsEnabled(values.get("Multi-factor authentication"));
      }

      return true;
   }


   /**
    * Check remaining days of NordVPN Account subscription
    * @return true if the remaining days reach the reminder days
    */
   public boolean warnNordAccountExpires()
   {
      return (UtilPrefs.getAccountReminder() >= m_nordAccountRemainingDays);
   }
   public int getRemainingDays()
   {
      return m_nordAccountRemainingDays;
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
      return StringFormat.printString(m_email, "n/a");
   }

   public void setEmail(String email)
   {
      this.m_email = email;
   }

   public boolean isVpnServiceIsActive()
   {
      return m_vpnServiceIsActive;
   }

   public void setVpnServiceIsActive(String vpnServiceIsActive)
   {
      if (null != vpnServiceIsActive)
      {
         this.m_vpnServiceIsActive = vpnServiceIsActive.equalsIgnoreCase("Active");
      }
   }

   public String getVpnServiceExpDate()
   {
      return StringFormat.printString(m_vpnServiceExpDate, "n/a");
   }

   public void setVpnServiceExpDate(String vpnServiceExpDate)
   {
      this.m_vpnServiceExpDate = vpnServiceExpDate;
   }

   public boolean isVpnDedicatedIdIsActive()
   {
      return m_vpnDedicatedIdIsActive;
   }

   public void setVpnDedicatedIdIsActive(String vpnDedicatedIdIsActive)
   {
      if (null != vpnDedicatedIdIsActive)
      {
         this.m_vpnDedicatedIdIsActive = vpnDedicatedIdIsActive.equalsIgnoreCase("Active");
      }
   }

   public boolean isMfaIsEnabled()
   {
      return m_mfaIsEnabled;
   }

   public void setMfaIsEnabled(String mfaIsEnabled)
   {
      if (null != mfaIsEnabled)
      {
         this.m_mfaIsEnabled = mfaIsEnabled.equalsIgnoreCase("Enabled");
         if (false == this.m_mfaIsEnabled) mfaIsEnabled.equalsIgnoreCase("Turned on"); // Changed in v4.4.0
      }
   }

   public boolean isFailed()
   {
      return m_failed;
   }

   public void setFailed(boolean m_failed)
   {
      this.m_failed = m_failed;
   }
}
