package com.mr.apps.JNordVpnManager.nordvpn;

import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.base.Command;
import com.mr.apps.JNordVpnManager.geotools.CurrentLocation;
import com.mr.apps.JNordVpnManager.gui.connectLine.GuiCommandsToolBar;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.gui.settings.JNordVpnSettingsDialog;
import com.mr.apps.JNordVpnManager.gui.settings.JSettingsPanelField;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnGroups.NordVPNEnumGroups;
import com.mr.apps.JNordVpnManager.utils.UtilSystem;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * Class to manage nordvpn settings
 * <p>
 * Usage: nordvpn set command [command options] [arguments...] <code>
Sets a configuration option

Commands:
     autoconnect                                  Enables or disables auto-connect. When enabled, this feature will automatically try to connect to VPN on
                                                  operating system startup.
     threatprotectionlite, tplite, tpl, cybersec  Enables or disables ThreatProtectionLite. When enabled, the ThreatProtectionLite feature will automatically
                                                  block suspicious websites so that no malware or other cyber threats can infect your device. Additionally, no
                                                  flashy ads will come into your sight. More information on how it works:
                                                  https://nordvpn.com/features/threat-protection/.
     defaults                                     Restores settings to their default values.
     dns                                          Sets custom DNS servers
     firewall                                     Enables or disables use of the firewall.
     fwmark                                       Traffic control filter used in policy-based routing. It allows classifying packets based on a previously set
                                                  fwmark by iptables.
     ipv6                                         Enables or disables use of the IPv6.
     obfuscate                                    Enables or disables obfuscation. When enabled, this feature allows to bypass network traffic sensors which aim
                                                  to detect usage of the protocol and log, throttle or block it.
     routing                                      Allows routing traffic through VPN servers and peer devices in Meshnet. This setting must be enabled to send
                                                  your traffic through a VPN server or a peer device. If the setting is disabled, the app will only initiate
                                                  necessary connections to a VPN server or a peer device but won’t start traffic routing.
     analytics                                    Help us improve by sending anonymous aggregate data: crash reports, OS version, marketing performance, and
                                                  feature usage data – nothing that could identify you.
     killswitch                                   Enables or disables Kill Switch. This security feature blocks your device from accessing the Internet while not
                                                  connected to the VPN or in case connection with a VPN server is lost.
     notify                                       Enables or disables notifications
     tray                                         Enables or disables the NordVPN icon in the system tray. The icon provides quick access to basic controls and
                                                  your VPN status details.
     technology                                   Sets the technology
     meshnet, mesh                                Enables or disables Meshnet on this device.
     lan-discovery                                Access printers, TVs, and other devices on your local network while connected to a VPN.
     virtual-location                             Enables or disables access to virtual locations. Virtual location servers let you access more locations
                                                  worldwide.
     post-quantum, pq                             Enables or disables post-quantum VPN. When enabled, your connection uses cutting-edge cryptography designed to
                                                  resist quantum computer attacks. Not compatible with Meshnet.
     protocol                                     Set the protocol to TCP or UDP.
</code>
 */
public class NvpnSettingsData
{
   // Settings Constant Options
   public static final String                      SETTINGS_OPT_EMPTY                      = "";
   public static final String                      SETTINGS_OPT_DISABLED                   = "disabled";
   public static final String                      SETTINGS_OPT_ENABLED                    = "enabled";
   public static final String                      SETTINGS_OPT_TCP                        = "TCP";
   public static final String                      SETTINGS_OPT_UDP                        = "UDP";
   public static final String                      SETTINGS_OPT_WEBTUNNEL                  = "Webtunnel";
   public static final String                      SETTINGS_OPT_NORDWHISPER                = "NORDWHISPER";
   public static final String                      SETTINGS_OPT_NORDLYNX                   = "NORDLYNX";
   public static final String                      SETTINGS_OPT_OPENVPN                    = "OPENVPN";

   // Settings Id's
   private static final String                     POST_QUANTUM                            = "POST_QUANTUM";
   private static final String                     VIRTUAL_LOCATION                        = "VIRTUAL_LOCATION";
   private static final String                     LAN_DISCOVERY                           = "LAN_DISCOVERY";
   private static final String                     MESHNET                                 = "MESHNET";
   private static final String                     TECHNOLOGY                              = "TECHNOLOGY";
   private static final String                     TRAY                                    = "TRAY";
   private static final String                     NOTIFY                                  = "NOTIFY";
   private static final String                     OBFUSCATE                               = "OBFUSCATE";
   private static final String                     KILLSWITCH                              = "KILLSWITCH";
   private static final String                     ANALYTICS                               = "ANALYTICS";
   private static final String                     ROUTING                                 = "ROUTING";
   private static final String                     IPV6                                    = "IPV6";
   private static final String                     FIREWALL                                = "FIREWALL";
   private static final String                     FWMARK                                  = "FWMARK";
   private static final String                     DNS                                     = "DNS";
   private static final String                     TPLITE                                  = "TPLITE";
   private static final String                     AUTOCONNECT                             = "AUTOCONNECT";
   private static final String                     PROTOCOL                                = "PROTOCOL";
   private static final String                     ALLOWLIST_SUBNETS                       = "ALLOWLIST_SUBNETS";
   private static final String                     ALLOWLIST_PORTS                         = "ALLOWLIST_PORTS";

   private static String                           DEFAULT_NVPN_SETTINGS_AUTOCONNECT       = SETTINGS_OPT_EMPTY;
   private static String                           DEFAULT_NVPN_SETTINGS_TPLITE            = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_DNS               = SETTINGS_OPT_EMPTY;
   private static String                           DEFAULT_NVPN_SETTINGS_FIREWALL          = SETTINGS_OPT_ENABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_FWMARK            = SETTINGS_OPT_EMPTY;
   private static String                           DEFAULT_NVPN_SETTINGS_IPV6              = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_OBFUSCATE         = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_ROUTING           = SETTINGS_OPT_ENABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_ANALYTICS         = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_KILLSWITCH        = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_NOTIFY            = SETTINGS_OPT_ENABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_TRAY              = SETTINGS_OPT_ENABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_TECHNOLOGY        = SETTINGS_OPT_NORDLYNX;
   private static String                           DEFAULT_NVPN_SETTINGS_MESHNET           = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_LAN_DISCOVERY     = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_VIRTUAL_LOCATION  = SETTINGS_OPT_ENABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_POST_QUANTUM      = SETTINGS_OPT_DISABLED;
   private static String                           DEFAULT_NVPN_SETTINGS_PROTOCOL          = SETTINGS_OPT_UDP;
   private static String                           DEFAULT_NVPN_SETTINGS_ALLOWLIST_SUBNETS = SETTINGS_OPT_EMPTY;
   private static String                           DEFAULT_NVPN_SETTINGS_ALLOWLIST_PORTS   = SETTINGS_OPT_EMPTY;

   private String                                  m_autoConnect                           = DEFAULT_NVPN_SETTINGS_AUTOCONNECT;
   private String                                  m_tplite                                = DEFAULT_NVPN_SETTINGS_TPLITE;
   private String                                  m_dns                                   = DEFAULT_NVPN_SETTINGS_DNS;
   private String                                  m_firewall                              = DEFAULT_NVPN_SETTINGS_FIREWALL;
   private String                                  m_fwmark                                = DEFAULT_NVPN_SETTINGS_FWMARK;
   private String                                  m_ipv6                                  = DEFAULT_NVPN_SETTINGS_IPV6;
   private String                                  m_obfuscate                             = DEFAULT_NVPN_SETTINGS_OBFUSCATE;
   private String                                  m_routing                               = DEFAULT_NVPN_SETTINGS_ROUTING;
   private String                                  m_analytics                             = DEFAULT_NVPN_SETTINGS_ANALYTICS;
   private String                                  m_killswitch                            = DEFAULT_NVPN_SETTINGS_KILLSWITCH;
   private String                                  m_notify                                = DEFAULT_NVPN_SETTINGS_NOTIFY;
   private String                                  m_tray                                  = DEFAULT_NVPN_SETTINGS_TRAY;
   private String                                  m_technology                            = DEFAULT_NVPN_SETTINGS_TECHNOLOGY;
   private String                                  m_meshnet                               = DEFAULT_NVPN_SETTINGS_MESHNET;
   private String                                  m_lanDiscovery                          = DEFAULT_NVPN_SETTINGS_LAN_DISCOVERY;
   private String                                  m_virtualLocation                       = DEFAULT_NVPN_SETTINGS_VIRTUAL_LOCATION;
   private String                                  m_postQuantum                           = DEFAULT_NVPN_SETTINGS_POST_QUANTUM;
   private String                                  m_protocol                              = DEFAULT_NVPN_SETTINGS_PROTOCOL;

   private String                                  m_allowlistSubnets                      = DEFAULT_NVPN_SETTINGS_ALLOWLIST_SUBNETS;
   private String                                  m_allowlistPorts                        = DEFAULT_NVPN_SETTINGS_ALLOWLIST_PORTS;

   private static boolean                          m_requiresReconnect                     = false;

   /**
    * Dataset defining the NordVPN Settings values.
    * <p>
    * Contains the panel field description by Id:
    * <ul>
    * <li>Label text</li>
    * <li>Field Type, where: "T" - Text field / "N[min,max,step]" - Integer with optional range / "B" - Boolean
    * (CheckBox)</li>
    * <li>Mnemonic (-1 - no KeyEvent)</li>
    * <li>Field length</li>
    * <li>Default value</li>
    * </ul>
    */
   private static Map<String, JSettingsPanelField> m_settingsPanelFieldsMap  = new HashMap<String, JSettingsPanelField>();

   public NvpnSettingsData()
   {
      // call and parse 'nordvpn settings' command
      getNordVPNSettings();

      m_settingsPanelFieldsMap.put(ANALYTICS, new JSettingsPanelField("Send Analytics Data", "B", -1, 1, this.getAnalytics(true)));
      m_settingsPanelFieldsMap.put(AUTOCONNECT, new JSettingsPanelField("Autoconnect to Server", "T", KeyEvent.VK_A, 20, this.getAutoConnect(true)));
      m_settingsPanelFieldsMap.put(DNS, new JSettingsPanelField("Set custom DNS servers", "T", KeyEvent.VK_D, 20, this.getDns(true)));
      m_settingsPanelFieldsMap.put(FIREWALL, new JSettingsPanelField("Use of the firewall", "B", KeyEvent.VK_F, 1, this.getFirewall(true)));
      m_settingsPanelFieldsMap.put(FWMARK, new JSettingsPanelField("Firewall Mark", "T", -1, 20, this.getFwmark(true)));
      m_settingsPanelFieldsMap.put(IPV6, new JSettingsPanelField("Use of the IPv6", "B", KeyEvent.VK_I, 1, this.getIpv6(true)));
      m_settingsPanelFieldsMap.put(KILLSWITCH, new JSettingsPanelField("Enable Kill Switch", "B", KeyEvent.VK_K, 1, this.getKillswitch(true)));
      m_settingsPanelFieldsMap.put(LAN_DISCOVERY, new JSettingsPanelField("Enable LAN Discovery", "B", KeyEvent.VK_L, 20, this.getLanDiscovery(true)));
      m_settingsPanelFieldsMap.put(MESHNET, new JSettingsPanelField("Enable Meshnet", "B", KeyEvent.VK_M, 1, this.getMeshnet(true)));
      m_settingsPanelFieldsMap.put(NOTIFY, new JSettingsPanelField("Enable Notifications", "B", KeyEvent.VK_N, 1, this.getNotify(true)));
      m_settingsPanelFieldsMap.put(OBFUSCATE, new JSettingsPanelField("Enable Obfuscation (OPENVPN)", "B", KeyEvent.VK_O, 1, this.getObfuscate(true)));
      m_settingsPanelFieldsMap.put(POST_QUANTUM, new JSettingsPanelField("Enable Post-Quantum Encryption", "B", KeyEvent.VK_Q, 1, this.getPostQuantum(true)));
      m_settingsPanelFieldsMap.put(PROTOCOL, new JSettingsPanelField("Protocol (OPENVPN)", "L["+SETTINGS_OPT_TCP+","+SETTINGS_OPT_UDP+"]", KeyEvent.VK_P, 1, this.getProtocol(true)));
      m_settingsPanelFieldsMap.put(ROUTING, new JSettingsPanelField("Enable traffic routing", "B", KeyEvent.VK_R, 1, this.getRouting(true)));
      m_settingsPanelFieldsMap.put(TECHNOLOGY, new JSettingsPanelField("Technology", "C["+SETTINGS_OPT_NORDLYNX+","+SETTINGS_OPT_OPENVPN+","+SETTINGS_OPT_NORDWHISPER+"]", KeyEvent.VK_T, 1, this.getTechnology(true)));
      m_settingsPanelFieldsMap.put(TPLITE, new JSettingsPanelField("Threat Protection Lite", "B", -1, 1, this.getTplite(true)));
      m_settingsPanelFieldsMap.put(TRAY, new JSettingsPanelField("Enable Tray Icon", "B", -1, 1, this.getTray(true)));
      m_settingsPanelFieldsMap.put(VIRTUAL_LOCATION, new JSettingsPanelField("Enable Virtual Locations", "B", KeyEvent.VK_V, 1, this.getVirtualLocation(true)));
   }

   /**
    * Get and parse the NordVPN Settings.
    */
   private void getNordVPNSettings()
   {
      String msg = NvpnCommands.getSettings();
      if (UtilSystem.isLastError())
      {
         if (msg.contains("You are not logged in."))
         {
            Starter._m_logError.TraceCmd(msg);
         }
         else
         {
            msg = UtilSystem.getLastError();
            JModalDialog.showError("NordVPN Settings", msg);
         }
      }
      else
      {
         // OK - extract data
         if (false == parseData(msg))
         {
            // failed
            JModalDialog.showError("NordVPN Settings", "'nordvpn settings' information cannot be parsed.");
         }
      }
   }

   /**
    * Parse NOrdVPN Settings data
    * 
    * @param data
    *           is the result from 'nordvpn settings' command
    * @return true if parsing was successful, else false
    */
   private boolean parseData(String data)
   {
      HashMap<String,String> values = new HashMap<String, String>();
      StringBuffer sbAllowListSubnets = new StringBuffer();
      StringBuffer sbAllowListPorts = new StringBuffer();
      try
      {
         boolean bAllowListSubnets = false;
         boolean bAllowListPorts = false;

         String[] saLines = data.split("\\n");
         for (String line : saLines)
         {
            String[] parts = line.split(":");
            if (null != parts && parts.length == 2)
            {
               values.put(parts[0], parts[1].trim());
               bAllowListSubnets = false;
               bAllowListPorts = false;
            }
            else
            {
               if (line.toUpperCase().contains("ALLOWLIST") && line.toUpperCase().contains("SUBNET"))
               {
                  //nordvpn allowlist add subnet 127.0.0.1/24
                  //Allowlisted subnets:
                  //   127.0.0.1/24

                  bAllowListSubnets = true;
                  bAllowListPorts = false;
               }
               else if (line.toUpperCase().contains("ALLOWLIST") && line.toUpperCase().contains("PORT"))
               {
                  //nordvpn allowlist add ports 12345 12355 protocol TCP
                  //nordvpn allowlist add port 12555 protocol TCP
                  //Allowlisted ports:
                  //   12345 - 12355 (TCP)
                  //   12555 (TCP) 

                  bAllowListPorts = true;
                  bAllowListSubnets = false;
               }
               else
               {
                  if (bAllowListSubnets == true)
                  {
                     if (sbAllowListSubnets.length() > 0) sbAllowListSubnets.append(Starter.SEP_DATARECORD);
                     sbAllowListSubnets.append(line.replaceAll("\t", ""));
                  }
                  else if (bAllowListPorts == true)
                  {
                     if (sbAllowListPorts.length() > 0) sbAllowListPorts.append(Starter.SEP_DATARECORD);
                     sbAllowListPorts.append(line.replaceAll("\t", ""));
                  }
                  else
                  {
                     Starter._m_logError.LoggingError(10100,
                           "Parsing NordVPN Settings Information",
                           "Unknown Data in Settings: '" + line + "' (skipped).");
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         // Parsing Error
         Starter._m_logError.LoggingError(10100,
               "Parsing NordVPN Settings Information",
               data);
         return false;
      }

      // Parsing OK
      this.m_technology = values.get("Technology");
      this.m_firewall = values.get("Firewall");
      this.m_fwmark = values.get("Firewall Mark");
      this.m_routing = values.get("Routing");
      this.m_analytics = values.get("Analytics");
      this.m_killswitch = values.get("Kill Switch");
      this.m_tplite = values.get("Threat Protection Lite");
      this.m_notify = values.get("Notify");
      this.m_obfuscate = values.get("Obfuscate");
      this.m_tray = values.get("Tray");
      this.m_autoConnect = values.get("Auto-connect");
      this.m_ipv6 = values.get("IPv6");
      this.m_meshnet = values.get("Meshnet");
      this.m_dns = values.get("DNS");
      this.m_lanDiscovery = values.get("LAN Discovery");
      this.m_virtualLocation = values.get("Virtual Location");
      this.m_postQuantum = values.get("Post-quantum VPN");
      this.m_protocol = values.get("Protocol");

      // initialize settings - dependent of technology they may not be set
      if (null == this.m_obfuscate) this.m_obfuscate = SETTINGS_OPT_DISABLED;
      if (true == m_technology.equals(SETTINGS_OPT_NORDLYNX))
      {
         // NORDLYNX protocol is fix UDP
         this.m_protocol = SETTINGS_OPT_UDP;
      }
      else if (true == m_technology.equals(SETTINGS_OPT_NORDWHISPER))
      {
         // NORDWHISPER protocol is fix Webtunnel
         this.m_protocol = SETTINGS_OPT_WEBTUNNEL;
      }
      if (null == this.m_protocol) this.m_protocol = SETTINGS_OPT_UDP;
      if (null == this.m_postQuantum) this.m_postQuantum = SETTINGS_OPT_DISABLED;

      // the following values contain the text SETTINGS_OPT_DISABLED it they are not set -> reset them
      if (this.m_autoConnect.equals(SETTINGS_OPT_DISABLED)) this.m_autoConnect = SETTINGS_OPT_EMPTY;
      if (this.m_dns.equals(SETTINGS_OPT_DISABLED)) this.m_dns = SETTINGS_OPT_EMPTY;
      if (this.m_fwmark.equals(SETTINGS_OPT_DISABLED)) this.m_fwmark = SETTINGS_OPT_EMPTY;

      // TODO: Workaround! 'nordvpn settings' does not return the server that was set - it returns only enabled or
      // disabled :(
      if (this.m_autoConnect.equals(SETTINGS_OPT_ENABLED))
      {
         // Workaround: get the server from User Prefs
         this.m_autoConnect = getAutoConnect(true);
      }

      // AllowLists
      this.m_allowlistSubnets = sbAllowListSubnets.toString();
      this.m_allowlistPorts = sbAllowListPorts.toString();

      return true;
   }

   /**
    * Show the NordVPN Settings Panel.
    */
   public static void showNordVpnSettingsPanel()
   {
      JNordVpnSettingsDialog sp = new JNordVpnSettingsDialog(Starter.getMainFrame(), "NordVPN Settings", m_settingsPanelFieldsMap);
      sp.getResult();
   }

   /**
    * Action "Export" NordVPN settings to a file.
    * 
    * @param fileName
    *           is the file name where to store the data.
    */
   public boolean exportNordVpnSettings(String fileName, HashMap<String, String> hm)
   {
      Starter._m_logError.TraceDebug("Export NordVPN settings to file '" + fileName + "'.");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false)))
      {
         writer.write("INFO JNordVPN Settings Export File");
         writer.newLine();

         for (HashMap.Entry<String, String> entry : hm.entrySet())
         {
            String key = entry.getKey();
            String value = entry.getValue();
            if (null != value)
            {
               JSettingsPanelField field = m_settingsPanelFieldsMap.get(key);
               if (field.getElementType().startsWith("B"))
               {
                  // we get "0" and "1" back from settings panel - I work here with disabled/enabled
                  value = (StringFormat.string2boolean(value)) ? SETTINGS_OPT_ENABLED : SETTINGS_OPT_DISABLED;
               }
               String line = key + " " + value;
               writer.write(line);
               writer.newLine();
            }
         }
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionAbend(10901, e);
         return false;
      }
      return true;
   }

   /**
    * Action "Import" NordVPN settings from a file.
    * 
    * @param fileName
    *           is the file name where to read the data.
    */
   public HashMap<String, String> importNordVpnSettings(String fileName)
   {
      HashMap<String, String> hm = null;

      Starter._m_logError.TraceDebug("Import NordVPN settings from file '" + fileName + "'.");
      try (Stream<String> lines = Files.lines(Paths.get(fileName)))
      {
         hm = new HashMap<String, String>();
         int iLine = 0;
         for (String line : (Iterable<String>) lines::iterator)
         {
            iLine++;
            Pattern pattern = Pattern.compile("([^\\s]+)\\s+(.*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            boolean matchFound = matcher.find();
            if (matchFound)
            {
               hm.put(matcher.group(1), matcher.group(2));
            }
            else
            {
               Starter._m_logError.TraceDebug("Line '" + iLine + "' does not match the pattern [key value]!");
            }
         }
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionAbend(10901, e);
         hm = null;
      }

      return hm;
   }

   /*
    * Getter/Setter Methods to access the Settings Data...
    * ..from 2 locations: User Pref (defaults) or current NordVPN Settings
    */
   public String getAutoConnect(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.autoconnect", DEFAULT_NVPN_SETTINGS_AUTOCONNECT);
      }
      else
      {
         return m_autoConnect;
      }
   }

   public boolean setAutoConnect(String data, boolean def)
   {
      if (null == data) return false;

      if (data.equals(SETTINGS_OPT_DISABLED)) data = SETTINGS_OPT_EMPTY;
      // TODO: Workaround! 'nordvpn settings' does not return the server that was set - it returns only enabled or disabled :(
      if (data.equals(SETTINGS_OPT_ENABLED))
      {
         // Workaround: get the server from User Prefs
         this.m_autoConnect = getAutoConnect(true);
      }

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.autoconnect", data);
      }
      else
      {
         if (!m_autoConnect.equalsIgnoreCase(data))
         {
            // call set command
            NvpnCommands.autoConnectSettings(data);
            if (this.getAutoConnect(true).isBlank())
            {
               // Workaround: Store server name in User Prefs
               this.setAutoConnect(data, true);
            }
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_autoConnect = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getTplite(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.tplite", DEFAULT_NVPN_SETTINGS_TPLITE);
      }
      else
      {
         return m_tplite;
      }
   }

   public boolean setTplite(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.tplite", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_tplite, data))
         {
            // call set command
            NvpnCommands.tpliteSettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_tplite = data;
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_THREATPROTECTION);
               return true;
            }
         }
      }

      return false;
   }

   public String getDns(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.dns", DEFAULT_NVPN_SETTINGS_DNS);
      }
      else
      {
         return m_dns;
      }
   }

   public boolean setDns(String data, boolean def)
   {
      if (null == data) return false;

      if (data.equals(SETTINGS_OPT_DISABLED)) data = SETTINGS_OPT_EMPTY;
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.dns", data);
      }
      else
      {
         if (!m_dns.equalsIgnoreCase(data))
         {
            // call set command
            NvpnCommands.dnsSettings(data);
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_dns = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getFirewall(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.firewall", DEFAULT_NVPN_SETTINGS_FIREWALL);
      }
      else
      {
         return m_firewall;
      }
   }

   public boolean setFirewall(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.firewall", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_firewall, data))
         {
            // call set command
            NvpnCommands.firewallSettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_firewall = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getFwmark(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.fwmark", DEFAULT_NVPN_SETTINGS_FWMARK);
      }
      else
      {
         return m_fwmark;
      }
   }

   public boolean setFwmark(String data, boolean def)
   {
      if (null == data) return false;

      if (data.equals(SETTINGS_OPT_DISABLED)) data = SETTINGS_OPT_EMPTY;
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.fwmark", data);
      }
      else
      {
         if (!m_fwmark.equalsIgnoreCase(data))
         {
            // call set command
            NvpnCommands.fwmarkSettings(data);
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_fwmark = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getIpv6(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.ipv6", DEFAULT_NVPN_SETTINGS_IPV6);
      }
      else
      {
         return m_ipv6;
      }
   }

   public boolean setIpv6(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.ipv6", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_ipv6, data))
         {
            // call set command
            NvpnCommands.ipv6Settings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_ipv6 = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getRouting(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.routing", DEFAULT_NVPN_SETTINGS_ROUTING);
      }
      else
      {
         return m_routing;
      }
   }

   public boolean setRouting(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.routing", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_routing, data))
         {
            // call set command
            String msg = NvpnCommands.routingSettings(StringFormat.string2boolean(data));
            if (0 == UtilSystem.showResultDialog("NordVPN Set Routing", msg, true))
            {
               m_routing = data;
               setRequiresReconnect();
               return true;
            }
         }
      }

      return false;
   }

   public String getAnalytics(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.analytics", DEFAULT_NVPN_SETTINGS_ANALYTICS);
      }
      else
      {
         return m_analytics;
      }
   }

   public boolean setAnalytics(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.analytics", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_analytics, data))
         {
            // call set command
            NvpnCommands.analyticsSettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_analytics = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getKillswitch(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.killswitch", DEFAULT_NVPN_SETTINGS_KILLSWITCH);
      }
      else
      {
         return m_killswitch;
      }
   }

   public boolean setKillswitch(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.killswitch", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_killswitch, data))
         {
            // call set command
            NvpnCommands.killswitchSettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_killswitch = data;
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_KILLSWITCH);
               return true;
            }
         }
      }

      return false;
   }

   public String getNotify(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.notify", DEFAULT_NVPN_SETTINGS_NOTIFY);
      }
      else
      {
         return m_notify;
      }
   }

   public boolean setNotify(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.notify", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_notify, data))
         {
            // call set command
            NvpnCommands.notifySettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_notify = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getObfuscate(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.obfuscate", DEFAULT_NVPN_SETTINGS_OBFUSCATE);
      }
      else
      {
         return m_obfuscate;
      }
   }

   public boolean setObfuscate(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.obfuscate", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_obfuscate, data))
         {
            // call set command
            if (false == m_technology.equals(SETTINGS_OPT_OPENVPN))
            {
               Starter._m_logError.LoggingWarning(10500,
                     "Setting can not be changed",
                     "The Setting 'Obfuscated' is only supported for VPN Technology 'OPENVPN'.");
               m_obfuscate = SETTINGS_OPT_DISABLED;
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_OBFUSCATE);
            }
            else
            {
               String msg = NvpnCommands.obfuscateSettings(StringFormat.string2boolean(data));
               if (0 == UtilSystem.showResultDialog("NordVPN Set Obfuscate", msg, true))
               {
                  // ok
                  m_obfuscate = data;
                  setRequiresReconnect();
                  GuiCommandsToolBar.updateCommand(Command.VPN_SET_OBFUSCATE);
                  return true;
               }
            }
         }
      }

      return false;
   }

   public String getTray(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.tray", DEFAULT_NVPN_SETTINGS_TRAY);
      }
      else
      {
         return m_tray;
      }
   }

   public boolean setTray(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.tray", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_tray, data))
         {
            // call set command
            NvpnCommands.traySettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_tray = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getTechnology(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.technology", DEFAULT_NVPN_SETTINGS_TECHNOLOGY);
      }
      else
      {
         return m_technology;
      }
   }

   public boolean setTechnology(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.technology", data);
      }
      else
      {
         if (!m_technology.equalsIgnoreCase(data))
         {
            // call set command
            String msg = NvpnCommands.technologySettings(data);
            if (0 == UtilSystem.showResultDialog("NordVPN Set Technology", msg, true))
            {
               m_technology = data;
               // ok -> we need to re-read the settings
               getNordVPNSettings();
               setRequiresReconnect();
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_TECHNOLOGY);
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_OBFUSCATE);
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_POSTQUANTUM);
               return true;
            }
         }
      }

      return false;
   }

   public String getMeshnet(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.meshnet", DEFAULT_NVPN_SETTINGS_MESHNET);
      }
      else
      {
         return m_meshnet;
      }
   }

   public boolean setMeshnet(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.meshnet", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_meshnet, data))
         {
            // call set command
            NvpnCommands.meshnetSettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_meshnet = data;
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_POSTQUANTUM);
               return true;
            }
         }
      }

      return false;
   }

   public String getLanDiscovery(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.lan_discovery", DEFAULT_NVPN_SETTINGS_LAN_DISCOVERY);
      }
      else
      {
         return m_lanDiscovery;
      }
   }

   public boolean setLanDiscovery(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.lan_discovery", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_lanDiscovery, data))
         {
            // call set command
            NvpnCommands.lanDiscoverySettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_lanDiscovery = data;
               return true;
            }
         }
      }

      return false;
   }

   public String getVirtualLocation(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.virtual_location", DEFAULT_NVPN_SETTINGS_VIRTUAL_LOCATION);
      }
      else
      {
         return m_virtualLocation;
      }
   }

   public boolean setVirtualLocation(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.virtual_location", data);
      }
      else
      {
         if (!StringFormat.equalBoolean(m_virtualLocation, data))
         {
            // call set command
            NvpnCommands.virtualLocationSettings(StringFormat.string2boolean(data));
            if (UtilSystem.getLastExitCode() == 0)
            {
               m_virtualLocation = data;
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_VIRTUALLOCATION);
//               setRequiresReconnect(); -> in this case I don't force a reconnect... tbd...
               return true;
            }
         }
      }

      return false;
   }

   public String getPostQuantum(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.post_quantum", DEFAULT_NVPN_SETTINGS_POST_QUANTUM);
      }
      else
      {
         return m_postQuantum;
      }
   }

   public boolean setPostQuantum(String data, boolean def)
   {
      if (null == data) return false;
      if ((true == Starter.getCurrentAccountData(false).isVpnDedicatedIdIsActive()) ||
          (false == m_technology.equals(SETTINGS_OPT_NORDLYNX)) ||
          (true == StringFormat.string2boolean(Starter.getCurrentSettingsData().getMeshnet(false))))
      {
         // post-quantum is not compatible with a dedicated IP, Meshnet, and OpenVPN/NORDWHISPER.
         return false;
      }

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.post_quantum", data);
      }
      else
      {
         if ((null != m_postQuantum) && (false == StringFormat.equalBoolean(m_postQuantum, data)))
         {
            // call set command
            String msg = NvpnCommands.postQuantumSettings(StringFormat.string2boolean(data));
            if (0 == UtilSystem.showResultDialog("NordVPN Set Post Quantum Encryption", msg, true))
            {
               // ok
               m_postQuantum = data;
               setRequiresReconnect();
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_POSTQUANTUM);
               return true;
            }
         }
      }

      return false;
   }

   public String getProtocol(boolean def)
   {
      if (true == m_technology.equals(SETTINGS_OPT_NORDLYNX))
      {
         // NORDLYNX protocol is fix UDP - only OPENVPN supports TCP and UDP
         return SETTINGS_OPT_UDP;
      }
      else if (true == m_technology.equals(SETTINGS_OPT_NORDWHISPER))
      {
         // NORDWHISPER protocol is fix Webtunnel - only OPENVPN supports TCP and UDP
         return SETTINGS_OPT_WEBTUNNEL;
      }

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.protocol", DEFAULT_NVPN_SETTINGS_PROTOCOL);
      }
      else
      {
         return m_protocol;
      }
   }

   public boolean setProtocol(String data, boolean def)
   {
      if (null == data) return false;

      if ((true == m_technology.equals(SETTINGS_OPT_NORDLYNX)) && (!data.equals(SETTINGS_OPT_UDP)))
      {
         // NORDLYNX protocol is fix UDP - only OPENVPN supports TCP
         data = SETTINGS_OPT_UDP;
         Starter._m_logError.LoggingWarning(10500,
               "Setting can not be changed",
               "The Setting 'Protocol' for VPN Technology NORDVPN is fix 'UDP'.");
      }
      else if ((true == m_technology.equals(SETTINGS_OPT_NORDWHISPER)) && (!data.equals(SETTINGS_OPT_WEBTUNNEL)))
      {
         // NORDWHISPER protocol is fix UDP - only OPENVPN supports TCP
         data = SETTINGS_OPT_WEBTUNNEL;
         Starter._m_logError.LoggingWarning(10500,
               "Setting can not be changed",
               "The Setting 'Protocol' for VPN Technology MORDWHISPER is fix 'Webtunnel'.");
      }

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.protocol", data);
      }
      else
      {
         if (!m_protocol.equalsIgnoreCase(data))
         {
            // call set command
            String msg = NvpnCommands.protocolSettings(data);
            if (0 == UtilSystem.showResultDialog("NordVPN Set Protocol", msg, true))
            {
               m_protocol = data;
               setRequiresReconnect();
               GuiCommandsToolBar.updateCommand(Command.VPN_SET_TECHNOLOGY);
               return true;
            }
         }
      }

      return false;
   }

   public String getAllowlistSubnets(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.allowlist.subnets", DEFAULT_NVPN_SETTINGS_ALLOWLIST_SUBNETS);
      }
      else
      {
         return m_allowlistSubnets;
      }
   }

   public boolean setAllowlistSubnets(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.allowlist.subnets", data);
      }
      else
      {
         if (!m_allowlistSubnets.equalsIgnoreCase(data))
         {
            // TODO: call set Allowlist Subnets command
//            NvpnCommands.???Settings(data);
//            if (UtilSystem.getLastExitCode() == 0)
//            {
               m_allowlistSubnets = data;
               return true;
//            }
         }
      }

      return false;
   }


   public String getAllowlistPorts(boolean def)
   {
      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         return prefs.get("Settings.allowlist.ports", DEFAULT_NVPN_SETTINGS_ALLOWLIST_PORTS);
      }
      else
      {
         return m_allowlistPorts;
      }
   }

   public boolean setAllowlistPorts(String data, boolean def)
   {
      if (null == data) return false;

      if (true == def)
      {
         Preferences prefs = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/nordvpn");
         prefs.put("Settings.allowlist.ports", data);
      }
      else
      {
         if (!m_allowlistPorts.equalsIgnoreCase(data))
         {
            // TODO: call Allowlist Ports set command
//            NvpnCommands.???Settings(data);
//            if (UtilSystem.getLastExitCode() == 0)
//            {
               m_allowlistPorts = data;
               return true;
//            }
         }
      }

      return false;
   }
   
   /**
    * Get a dataset with all NordVPN Settings data
    * 
    * @param def
    *           is true for default settings from User Prefs or false for current NordVPN Settings
    * @return the dataset with all NordVPN Settings data.
    */
   public HashMap <String,String> getSettingsDataSet(boolean def)
   {
      HashMap <String,String> hm = new HashMap <String,String>();

      hm.put(AUTOCONNECT, getAutoConnect(def));
      hm.put(PROTOCOL, getProtocol(def));
      hm.put(TPLITE, getTplite(def));
      hm.put(DNS, getDns(def));
      hm.put(FIREWALL, getFirewall(def));
      hm.put(FWMARK, getFwmark(def));
      hm.put(IPV6, getIpv6(def));
      hm.put(ROUTING, getRouting(def));
      hm.put(ANALYTICS, getAnalytics(def));
      hm.put(KILLSWITCH, getKillswitch(def));
      hm.put(NOTIFY, getNotify(def));
      hm.put(OBFUSCATE, getObfuscate(def));
      hm.put(TRAY, getTray(def));
      hm.put(TECHNOLOGY, getTechnology(def));
      hm.put(MESHNET, getMeshnet(def));
      hm.put(LAN_DISCOVERY, getLanDiscovery(def));
      hm.put(VIRTUAL_LOCATION, getVirtualLocation(def));
      hm.put(POST_QUANTUM, getPostQuantum(def));
      hm.put(ALLOWLIST_SUBNETS, getAllowlistSubnets(def));
      hm.put(ALLOWLIST_PORTS, getAllowlistPorts(def));

      return hm;
   }

   /**
    * Set NordVPN Settings data with dataset values
    * <p>
    * In case of def=false, nordvpn set ... commands are called
    * 
    * @param def
    *           is true for set default settings to User Prefs and false for change current NordVPN Settings
    * @param hm
    *           is the data set with the new values
    * @return true if a current NordVPN setting was changed (needs Status line and/or tree update)
    */
   public boolean setSettingsDataSet(HashMap <String,String> hm, boolean def)
   {
      boolean rc = false;
      if (null == hm)
      {
         return rc;
      }

      Starter._m_logError.TraceDebug("Save all NordVPN Settings values to " + ((true == def) ? "User Preferences" : "current NordVPM Settings") + ".");
      for (HashMap.Entry<String, String> entry : hm.entrySet())
      {
         String key = entry.getKey();
         String value = entry.getValue();
         Starter._m_logError.TraceDebug(key + ": " + value);
      }

      rc |= setTechnology(hm.get(TECHNOLOGY), def);
      rc |= setAutoConnect(hm.get(AUTOCONNECT), def);
      rc |= setTplite(hm.get(TPLITE), def);
      rc |= setDns(hm.get(DNS), def);
      rc |= setFirewall(hm.get(FIREWALL), def);
      rc |= setFwmark(hm.get(FWMARK), def);
      rc |= setIpv6(hm.get(IPV6), def);
      rc |= setRouting(hm.get(ROUTING), def);
      rc |= setAnalytics(hm.get(ANALYTICS), def);
      rc |= setKillswitch(hm.get(KILLSWITCH), def);
      rc |= setNotify(hm.get(NOTIFY), def);
      rc |= setObfuscate(hm.get(OBFUSCATE), def);
      rc |= setTray(hm.get(TRAY), def);
      rc |= setMeshnet(hm.get(MESHNET), def);
      rc |= setLanDiscovery(hm.get(LAN_DISCOVERY), def);
      rc |= setVirtualLocation(hm.get(VIRTUAL_LOCATION), def);
      rc |= setPostQuantum(hm.get(POST_QUANTUM), def);
      rc |= setProtocol(hm.get(PROTOCOL), def);
      rc |= setAllowlistSubnets(hm.get(ALLOWLIST_SUBNETS), def);
      rc |= setAllowlistPorts(hm.get(ALLOWLIST_PORTS), def);

      Starter._m_logError.TraceDebug("Needs Update Flag=" + rc + ".");

      return rc;
   }

   /**
    * Reset NordVPN Settings to NordVPN default values
    */
   public void resetNordVPNSettingsValues()
   {
      Starter._m_logError.TraceDebug("Set all Settings to NordVPN default values.");

      String msg = NvpnCommands.resetSettings();
      if (UtilSystem.isLastError())
      {
         msg = UtilSystem.getLastError();
         JModalDialog.showError("Reset NordVPN Settings", msg);
      }
      else
      {
         JModalDialog.showMessage("Reset NordVPN Settings", msg);
      }
      getNordVPNSettings();
   }

   public static boolean reconnectRequired()
   {
      return m_requiresReconnect;
   }

   public static void setRequiresReconnect()
   {
      Starter._m_logError.TraceDebug("Set RequiresReconnect Flag.");
      m_requiresReconnect = true;     
   }

   public static void resetRequiresReconnect()
   {
      if (true == m_requiresReconnect)
      {
         Starter._m_logError.TraceDebug("Reset RequiresReconnect Flag.");
         m_requiresReconnect = false;      
      }
   }

   /**
    * Method to check/set Settings for the connection to a new server location.
    * <p>
    * This method checks, if the current NordVPN Setting for Obfuscation is conform the GUI Group filter and valid for
    * the selected Server.
    * 
    * @param loc
    *           is the new location to connect to
    * @return <code>true</code> if checks are ok, else <code>false</code>
    */
   public boolean checkForConnection(CurrentLocation loc)
   {

      // a) for static targets we use the data from the location and change the settings w/o request
      // b) for dynamic targets we use the data from the GUI- and VPN Settings - checks required and requester(s) for Settings changes
      //    - if legacy group is Obfuscated - Setting obfuscated must be enabled and Technology OPENVPN
      //    - if legacy group is not Obfuscated - Settings obfuscated must be disabled

      // Get the data
      boolean bObfuscate = StringFormat.string2boolean(m_obfuscate);
      NordVPNEnumGroups currentLegacyGroup = NvpnGroups.getCurrentFilterGroup();
      
      NordVPNEnumGroups locationLegacyGroup = NordVPNEnumGroups.get(loc.getLegacyGroup());
      String            locationTechnology = loc.getVpnTechnology();
      String            locationProtocol = loc.getVpnProtocol();

      Starter._m_logError.TraceDebug("Check Settings GUI+VPN [loc]: Obfuscate=" + bObfuscate
            + " / Legacy Group=" + currentLegacyGroup.name() + " [" + locationLegacyGroup.name()
            + "] / Technology/Protocol=" + m_technology + "/" + m_protocol  + " [" + locationTechnology+ "/" + locationProtocol
            + ".");

      boolean changedSettings = false;
      if (true == loc.isStatic())
      {
         if (false == locationTechnology.equals(m_technology))
         {
            changedSettings |= this.setTechnology(locationTechnology, false);
            if (true == locationTechnology.equals(SETTINGS_OPT_OPENVPN))
            {
               if (false == locationProtocol.equals(m_protocol))
               {
                  changedSettings |= this.setProtocol(locationProtocol, false);
               }
            }
         }

         if ((true == locationLegacyGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers)) && (!bObfuscate))
         {
            changedSettings |= this.setObfuscate(SETTINGS_OPT_ENABLED, false);            
         }
         else if ((false == locationLegacyGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers)) && (bObfuscate))
         {
            changedSettings |= this.setObfuscate(SETTINGS_OPT_DISABLED, false);            
         }
      }
      else
      {
         if ((true == currentLegacyGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers)) && (!bObfuscate))
         {
            // require to change setting 'obfuscate enabled' (only available for technology OPENVPN!)
            JModalDialog dlg = JModalDialog.JOptionDialog("Obfuscated Servers",
                  "Selected Filter Legacy Group is different from VPN Settings!\n"
                        + "\nTo connect to VPN servers with obfuscation, the Setting 'Obfuscate' must be enabled in NordVPN settings and the technology must be OPENVPN.\n"
                        + "\nPlease choose the OPENVPN protocol to enable obfuscation, No Obfuscation or choose Cancel.",
                  "OPENVPN TCP,OPENVPN UDP,No Obfuscation, Cancel");
            int rc = dlg.getResult();
            if (rc == 0)
            {
               // change settings, keep filter legacy group
               changedSettings |= this.setTechnology(SETTINGS_OPT_OPENVPN, false);
               changedSettings |= this.setProtocol(SETTINGS_OPT_TCP, false);
               changedSettings |= this.setObfuscate(SETTINGS_OPT_ENABLED, false);
            }
            else if (rc == 1)
            {
               // change settings, keep filter legacy group
               changedSettings |= this.setTechnology(SETTINGS_OPT_OPENVPN, false);
               changedSettings |= this.setProtocol(SETTINGS_OPT_UDP, false);
               changedSettings |= this.setObfuscate(SETTINGS_OPT_ENABLED, false);
            }
            else if (rc == 2)
            {
               // keep settings, change filter legacy group
               NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.Standard_VPN_Servers);
               loc.setLegacyGroup(null);
               changedSettings = true;
            }
            else // rc == 3
            {
               // cancel
               return false;
            }
         }
         else if ((false == currentLegacyGroup.equals(NordVPNEnumGroups.legacy_obfuscated_servers)) && (bObfuscate))
         {
            // require to change setting 'obfuscate disabled'
            JModalDialog dlg = JModalDialog.JOptionDialog("Change Legacy Group",
                  "Selected Filter Legacy Group is different from VPN Settings!\n"
                        + "\nThe VPN Setting 'Obfuscate' is currently enabled. To connect to VPN servers with the selected Filter Legacy Group, obfuscation must be deactivated.\n"
                        + "\nPlease change the Filter Group, disable obfuscation, use technology 'NORDLYNX' or 'NORDWHISPER' or choose Cancel.",
                  "Change Filter Group,Disable Obfuscation,NORDLYNX,NORDWHISPER,Cancel");
            int rc = dlg.getResult();
            if (rc == 0)
            {
               // keep settings, change Filter Legacy Group
               NvpnGroups.setCurrentFilterGroup(NordVPNEnumGroups.legacy_obfuscated_servers);
               loc.setLegacyGroup(null);
               changedSettings = true;
            }
            else if (rc == 1)
            {
               // change settings, keep Filter Legacy Group
               changedSettings |= this.setObfuscate(SETTINGS_OPT_DISABLED, false);
            }
            else if (rc == 2)
            {
               // change settings, keep Filter Legacy Group
               changedSettings |= this.setTechnology(SETTINGS_OPT_NORDLYNX, false); // disables Obfuscate
            }
            else if (rc == 3)
            {
               // change settings, keep Filter Legacy Group
               changedSettings |= this.setTechnology(SETTINGS_OPT_NORDWHISPER, false); // disables Obfuscate
            }
            else // rc = 4
            {
               // Cancel
               return false;
            }
         }
      }

      if (true == changedSettings)
      {
         Starter._m_logError.TraceDebug("(checkForConnection) Settings changed");
      }
      // ok
      return true;
   }
}
