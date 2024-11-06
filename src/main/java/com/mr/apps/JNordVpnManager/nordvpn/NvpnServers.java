/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.nordvpn;

import javax.swing.JOptionPane;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class NvpnServers
{
   /** Get the server list.
    * <p>
    * The VPN Server list is generated from 2 "sources": 
    * <ul>
    * <li>the server list stored in user preferences, or</it>
    * <it>updated over network direct from NordVPN (slowly!)</it>
    * </ul>
    * For initialization of the GUI, the list must be retrieved over network direct from NordVPN.<br>
    * To refresh the server list over network from NordVPN, the update flag must be set to true!
    * The server list is stored in the format:<br>
    * Country1@City11/.../City1n:Country2@City21/.../City2n:Countrym@Citym1/.../Citymn
    * @param update if true, force update server list from NordVPN over network
    * @return the server list
    */
   public static String getServerList(boolean update)
   {
      String rc_serverList = UtilPrefs.getServerList();
      if (update)
      {
         Starter.setWaitCursor();
         Starter.setCursorCanChange(false);

         // Refresh server list data from NordVPN (slowly!!)
         StringBuffer sbListOfServers = new StringBuffer();
         String countries = NvpnCommands.getListOfCountries();
         if (UtilSystem.isLastError())
         {
            String msg = UtilSystem.getLastError();
            JModalDialog.showError("NordVPN get Countries", msg);
         }
         else
         {
            String[] saCountries = countries.split("\n");
            for (String country : saCountries)
            {
               if (sbListOfServers.length() > 0) sbListOfServers.append(":"); // delimiter between countries
               sbListOfServers.append(country);
               String cities = NvpnCommands.getListOfCities(country);
               String delimiter="@"; // first delimiter between country and first city
               if (UtilSystem.isLastError())
               {
                  String msg = UtilSystem.getLastError();
                  JModalDialog.showError("NordVPN get Cities", msg);
               }
               else
               {
                  String[] saCities = cities.split("\n");
                  for (String city : saCities)
                  {
                     sbListOfServers.append(delimiter+city);
                     delimiter = "/"; // delimiter between city2...cityn
                  }
               }
            }
         }
         rc_serverList = sbListOfServers.toString();

         // Update the server list stored in user preferences
         UtilPrefs.setServerList(rc_serverList);

         // ...also update the time stamp
         long timestamp = System.currentTimeMillis();
         UtilPrefs.setServerListTimestamp(StringFormat.long2String(timestamp, null));

         Starter.setCursorCanChange(true);
         Starter.resetWaitCursor();
      }

      return rc_serverList;
   }
}
