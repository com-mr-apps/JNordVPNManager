package com.mr.apps.JNordVpnManager.nordvpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

public class NvpnGroups
{
   // Legacy category
   public static final int legacy_group_category = 3;
   public static final int regions = 5;

   // Current region (current selection in server tree filter panel)
   private static NordVPNEnumGroups m_currentRegion = NordVPNEnumGroups.get(UtilPrefs.getRecentServerRegion());

   // Current group (current selection in server tree filter panel)
   private static NordVPNEnumGroups m_currentGroup = NordVPNEnumGroups.get(UtilPrefs.getRecentServerGroup());

   // Storage for groups per location
   private ArrayList<NordVPNEnumGroups> m_groups = null;

   private static boolean m_isValid = false;

   public static enum NordVPNEnumGroups
   {
      all_regions                   (99999),
      Europe                           (19),
      The_Americas                     (21),
      Asia_Pacific                     (23),
      Africa_The_Middle_East_And_India (25),
      
      Double_VPN                        (1),
      Onion_Over_VPN                    (3),
      legacy_ultra_fast_tv              (5),
      legacy_anti_ddos                  (7),
      Dedicated_IP                      (9),
      Standard_VPN_Servers             (11),
      legacy_netflix_usa               (13),
      P2P                              (15),
      legacy_obfuscated_servers        (17),
      anycast_dns                     (233),
      geo_dns                         (236),
      grafana                         (239),
      kapacitor                       (242),
      legacy_socks5_proxy             (245),
      fastnetmon                      (248)
      ;
    
       private int idGroup;
    
       NordVPNEnumGroups(int value) {
           this.idGroup = value;
       }
    
       public int getId() {
           return idGroup;
       }
        
       //****** Reverse Lookup Implementation************//
    
       //Lookup table
       private static final Map<Integer, NordVPNEnumGroups> lookup = new HashMap<>();
     
       //Populate the lookup table on loading time
       static
       {
           for(NordVPNEnumGroups value : NordVPNEnumGroups.values())
           {
               lookup.put(value.getId(), value);
           }
       }
     
       //This method can be used for reverse lookup purpose
       public static NordVPNEnumGroups get(int groupId)
       {
           return lookup.get(groupId);
       }
   }

   public NvpnGroups()
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

   public void addGroup(NordVPNEnumGroups id)
   {
      if (null == m_groups)
      {
         m_groups = new ArrayList<NordVPNEnumGroups>();
         m_groups.add(NordVPNEnumGroups.all_regions);
      }
      if (m_groups.contains(id)) return;
      m_groups.add(id);
      m_isValid = true;
   }

   public void addGroup(int id)
   {
      m_groups.add(NordVPNEnumGroups.get(id));
   }

   public boolean hasGroup(NordVPNEnumGroups idGroup)
   {
      // if m_groups == null, we couldn't access the NordVPN groups data
      return (null == m_groups) ? true : m_groups.contains(idGroup);
   }

   public void resetGroups()
   {
      m_groups = null;      
   }

   public static NordVPNEnumGroups getCurrentRegion()
   {
      return m_currentRegion;
   }

   public static void setCurrentRegion(NordVPNEnumGroups currentRegion)
   {
      NvpnGroups.m_currentRegion = currentRegion;
      Starter._m_logError.TraceDebug("Set current Region Filter to: " + currentRegion);
      UtilPrefs.setRecentServerRegion(currentRegion.getId());
   }

   public static NordVPNEnumGroups getCurrentGroup()
   {
      return m_currentGroup;
   }

   public static void setCurrentGroup(NordVPNEnumGroups currentGroup)
   {
      NvpnGroups.m_currentGroup = currentGroup;
      Starter._m_logError.TraceDebug("Set current Group Filter to: " + currentGroup);
      UtilPrefs.setRecentServerGroup(currentGroup.getId());
   }

   public static int getFieldIndex(NordVPNEnumGroups idGroup, NordVPNEnumGroups[] listGroups, int iDefault)
   {
      for (int idx = 0; idx < listGroups.length; idx++)
      {
         if (listGroups[idx].equals(idGroup))
         {
            return idx;
         }
      }
      return iDefault;
   }

   public String toString()
   {
      if (null == m_groups) return "";
      StringBuffer sb = new StringBuffer("["); 
      for (NordVPNEnumGroups iGroup : m_groups)
      {
         if (sb.length() > 1) sb.append(",");
         sb.append(NordVPNEnumGroups.get(iGroup.getId()));
      }
      sb.append("]");
      return sb.toString();
   }
}