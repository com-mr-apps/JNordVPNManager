package com.mr.apps.JNordVpnManager.nordvpn;

import java.util.ArrayList;

import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class NvpnTechnologies
{
   public static final int ikev2 = 1;
   public static final int openvpn_udp = 3;
   public static final int openvpn_tcp = 5;
   public static final int socks = 7;
   public static final int proxy = 9;
   public static final int pptp = 11;
   public static final int l2tp = 13;
   public static final int openvpn_xor_udp = 15;
   public static final int openvpn_xor_tcp = 17;
   public static final int proxy_cybersec = 19;
   public static final int proxy_ssl = 21;
   public static final int proxy_ssl_cybersec = 23;
   public static final int ikev2_v6 = 26;
   public static final int openvpn_udp_v6 = 29;
   public static final int openvpn_tcp_v6 = 32;
   public static final int wireguard_udp = 35;
   public static final int openvpn_udp_tls_crypt = 38;
   public static final int openvpn_tcp_tls_crypt = 41;
   public static final int openvpn_dedicated_udp = 42;
   public static final int openvpn_dedicated_tcp = 45;
   public static final int skylark = 48;
   public static final int mesh_relay = 50;
   public static final int nordwhisper = 51;

   // Storage for technologies per location
   private ArrayList<Integer> m_techIds = null;

   private static boolean m_isValid = false;

   public NvpnTechnologies()
   {
   }

   public static boolean isValid()
   {
      return m_isValid;
   }

   public static void init()
   {
      m_isValid = false;
   }

   public void addTechnology(int id)
   {
      if (null == m_techIds) m_techIds = new ArrayList<Integer>();
      if (m_techIds.contains(id)) return;
      m_techIds.add(id);
      m_isValid = true;
   }

   public boolean hasTechnology(int id)
   {
      // if m_techIds == null, we couldn't access the NordVPN technology data
      return (null == m_techIds) ? true : m_techIds.contains(id);
   }

   public void resetTechnologies()
   {
      m_techIds = null;      
   }

   public String toString()
   {
      if (null == m_techIds) return "[]";
      return StringFormat.int2String(m_techIds, null);
   }

   public String toStringId()
   {
      if (null == m_techIds) return "";
      StringBuffer sb = new StringBuffer(); 
      for (int iTechId : m_techIds)
      {
         if (sb.length() > 0) sb.append(";");
         sb.append(iTechId);
      }
      return sb.toString();
   }
}
