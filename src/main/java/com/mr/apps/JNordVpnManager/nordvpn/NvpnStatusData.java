/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import java.util.HashMap;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;

public class NvpnStatusData
{
   private boolean m_isConnected       = false;
   private String  m_status            = null; // connected / disconnected / if null - error
   private String  m_server            = null;
   private String  m_hostname          = null;
   private String  m_ip                = null;
   private String  m_country           = null;
   private String  m_city              = null;
   private String  m_technology        = null;
   private String  m_protocol          = null;
   private String  m_postQuantum       = null;
   private String  m_transfer          = null;
   private String  m_uptime            = null;
   private String  m_statusText        = null;
   private String  m_statusLineMessage = null;

   public NvpnStatusData()
   {
      m_statusText = NvpnCommands.getStatus();
      if (UtilSystem.isLastError())
      {
         // KO
         m_status = null;
         m_statusText = UtilSystem.getLastError();
         m_isConnected = false;
         m_statusLineMessage = m_statusText;
      }
      else
      {
         // OK - extract data
         if (false == parseData(m_statusText))
         {
            // failed
            setStatus("Disconnected");
            m_statusLineMessage = "'nordvpn status' information cannot be parsed.";
            JModalDialog.showError("NordVPN Status", m_statusLineMessage);
         }
         else
         {
            m_statusLineMessage = this.toString();
         }
         m_statusLineMessage = this.toString();
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
            values.put(parts[0], parts[1].trim());
         }
      }
      catch (Exception e)
      {
         // Parsing Error
         Starter._m_logError.LoggingError(10100,
               "Parsing NordVPN Status Information",
               data);
         return false;
      }

      // Parsing OK
      setStatus(values.get("Status"));
      setServer(values.get("Server"));
      setHostname(values.get("Hostname"));
      setIp(values.get("IP"));
      setCountry(values.get("Country"));
      setCity(values.get("City"));
      setTechnology(values.get("Current technology"));
      setProtocol(values.get("Current protocol"));
      setPostQuantum(values.get("Post-quantum VPN"));
      setTransfer(values.get("Transfer"));
      setUptime(values.get("Uptime"));

      return true;
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


   public String getPostQuantum()
   {
      return m_postQuantum;
   }

   public void setPostQuantum(String postQuantum)
   {
      this.m_postQuantum = postQuantum;
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

   public void setStatusText(String statusText)
   {
      this.m_statusText = statusText;
   }

   public String getStatusLineMessage()
   {
      return m_statusLineMessage;
   }

   public void setStatusLineMessage(String statusLineMessage)
   {
      this.m_statusLineMessage = statusLineMessage;
   }

   public void setStatus(String status)
   {
      this.m_isConnected = (status.equals("Connected")) ? true : false;
      this.m_status = status;
   }

   public String getStatus()
   {
      return m_status;
   }

   public String toString()
   {
      return getStatus() + ((isConnected()) ? " to " + getCity() + " [" + getCountry() + "]"
            + ", IP: " + getIp() + " (" + getHostname() + "), "
            + getTechnology() + "/" + getProtocol() + "/" + NvpnGroups.getCurrentGroup().name()  : ""); 
   }
}
