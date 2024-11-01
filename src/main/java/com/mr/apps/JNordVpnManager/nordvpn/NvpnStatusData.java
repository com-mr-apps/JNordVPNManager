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

import javax.swing.JOptionPane;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class NvpnStatusData
{
   private boolean         m_isConnected     = false;
   private String          m_status          = null; // connected / disconnected / if null - error
   private String          m_server          = null;
   private String          m_hostname        = null;
   private String          m_ip              = null;
   private String          m_country         = null;
   private String          m_city            = null;
   private String          m_technology      = null;
   private String          m_protocol        = null;
   private String          m_transfer        = null;
   private String          m_uptime          = null;
   private String          m_statusText      = null;

   public NvpnStatusData()
   {
      m_statusText = NvpnCommands.getStatus();
      if (UtilSystem.isLastError())
      {
         // KO
         m_status = null;
         m_statusText = UtilSystem.getLastError();
         m_isConnected = false;
      }
      else
      {
         // OK
         int rc = parseData(m_statusText);
         if (1 == rc)
         {
            JOptionPane.showMessageDialog(null, "'nordvpn status' information cannot be parsed.", "NordVPN Status", JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   public int parseData(String data)
   {
      int rc = 0;
      Pattern pattern = Pattern.compile("^\\s*Status:\\s+([^@]+)@" // Connected
            + "\\s*Server:\\s+([^@]+)@"                            // Denmark #201\n"
            + "\\s*Hostname:\\s+([^@]+)@"                          // dk201.nordvpn.com\n"
            + "\\s*IP:\\s+([^@]+)@"                                // 37.120.131.141\n"
            + "\\s*Country:\\s+([^@]+)@"                           // Denmark\n"
            + "\\s*City:\\s+([^@]+)@"                              // Copenhagen\n"
            + "\\s*Current technology:\\s+([^@]+)@"                // NORDLYNX\n"
            + "\\s*Current protocol:\\s+([^@]+)@"                  // UDP\n"
            + "\\s*Transfer:\\s+([^@]+)@"                          // 42.07 MiB received, 0.60 MiB sent\n"
            + "\\s*Uptime:\\s+([^@]+)",                            // 20 minutes 42 seconds",
            Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(data.replace('\n', '@'));
      boolean matchFound = matcher.find();
      if (matchFound)
      {
         // Status: Connected...
         setConnected(true);
         setStatus(matcher.group(1));
         setServer(matcher.group(2));
         setHostname(matcher.group(3));
         setIp(matcher.group(4));
         setCountry(matcher.group(5));
         setCity(matcher.group(6));
         setTechnology(matcher.group(7));
         setProtocol(matcher.group(8));
         setTransfer(matcher.group(9));
         setUptime(matcher.group(10));
      }
      else
      {
         setConnected(false);
         Pattern pattern2 = Pattern.compile("^\\s*Status:\\s+(.*)", // Disconnected
               Pattern.CASE_INSENSITIVE);
         Matcher matcher2 = pattern2.matcher(data.replace('\n', '@'));
         matchFound = matcher2.find();
         if (matchFound)
         {
            // disconnected
            setStatus(matcher2.group(1));
         }
         else
         {
            // Parsing Error
            rc = 1;
            Starter._m_logError.TranslatorError(10100, "Parsing NordVPN Status information", data);
            Starter._m_logError.TraceDebug("NordVPN Status Parsing Pattern1=" + pattern.toString() + "<.");
            Starter._m_logError.TraceDebug("NordVPN Status Parsing Pattern2=" + pattern2.toString() + "<.");
         }
      }

      return rc;
   }
   
   public boolean isConnected()
   {
      return m_isConnected;
   }


   public void setConnected(boolean isConnected)
   {
      this.m_isConnected = isConnected;
   }


   public String getServer()
   {
      return m_server;
   }


   public void setServer(String server)
   {
      this.m_server = server;
   }


   public String getHostname()
   {
      return m_hostname;
   }


   public void setHostname(String hostname)
   {
      this.m_hostname = hostname;
   }


   public String getIp()
   {
      return m_ip;
   }


   public void setIp(String ip)
   {
      this.m_ip = ip;
   }


   public String getCountry()
   {
      return m_country;
   }


   public void setCountry(String country)
   {
      this.m_country = country;
   }


   public String getCity()
   {
      return m_city;
   }


   public void setCity(String city)
   {
      this.m_city = city;
   }


   public String getTechnology()
   {
      return m_technology;
   }


   public void setTechnology(String technology)
   {
      this.m_technology = technology;
   }


   public String getProtocol()
   {
      return m_protocol;
   }


   public void setProtocol(String protocol)
   {
      this.m_protocol = protocol;
   }


   public String getTransfer()
   {
      return m_transfer;
   }


   public void setTransfer(String transfer)
   {
      this.m_transfer = transfer;
   }


   public String getUptime()
   {
      return m_uptime;
   }


   public void setUptime(String uptime)
   {
      this.m_uptime = uptime;
   }


   public String getStatusText()
   {
      return m_statusText;
   }


   public void setStatusText(String m_statusText)
   {
      this.m_statusText = m_statusText;
   }


   public void setStatus(String m_status)
   {
      this.m_status = m_status;
   }


   public String getStatus()
   {
      return m_status;
   }

}
