# JNordVPN Manager (Linux)

**Introducing JNordVPN Manager - a user-friendly, open-source graphical interface for managing NordVPN connections on Linux. This application simplifies VPN administration with features like a server/city list grouped by countries with filter functionality (by name, region or legacy group), connection management (login, logout, connect, disconnect, reconnect, pause), NordVPN Settings management and a world map showing all servers and the active one. Recent connections are also stored for easy reconnection by a single click.**

![JNordVPNManager_expanded](https://github.com/user-attachments/assets/c8affaa9-7456-4960-aef3-6481632372a1)

JNordVPN Manager is an open-source graphical user interface (GUI) application designed to make managing NordVPN connections on Linux easier and more intuitive than using the native nordvpn commands. Built with Java, this GUI provides a comprehensive set of features that cater to the needs of NordVPN users on the Linux platform.

Key Features:

* **Server Search**: Easily find and connect to VPN servers from all around the world using filters by country/city, technology/protocol, regions and legacy groups.
* **Legacy Groups**: Search and Connect Servers by: Standard servers, Peer to Peer, Double VPN, Onion over VPN, Dedicated IP, Obfuscated servers.
* **Connection Management**: Control your NordVPN connections with ease - (re)connect, disconnect, pause, or resume your sessions with a single click.
* **NordVPN Settings**: Manage your individual NordVPN settings direct from the GUI - export/import to/from local settings files for an easy switch between your different setups.
* **Recent Connections**: The application stores your recent connections for easy reconnection. Simply select one to reconnect instantly.
* **World Map View**: Visualize all available VPN servers on a world map and focus on the active server, providing an intuitive way to navigate through NordVPN's vast network.
* **Security**: The application does not store any login information (Account, Password). It is just an interface for the the native nordvpn commands.
* **Quick Access Commands Tool Bar** Free configurable commands Tool Bar for commonly used commands/settings.

The application is released under the Common Development and Distribution License 1.1 ([CDDL-1.1](https://spdx.org/licenses/CDDL-1.1.html)) with a Commons Clause License, ensuring that it remains free for private use only. This means that while you can download and enjoy NordVPN Manager without any costs, commercial exploitation of the software requires my explicit permission.

The newest version and the source code can be found on GitHub: [https://github.com/com-mr-apps/JNordVpnManager](https://github.com/com-mr-apps/JNordVpnManager).
Requirements, Bugs, etc. can be posted and will be administrated there.

Thank you for considering JNordVPN Manager as your go-to GUI for managing NordVPN connections on Linux.

## Execution

JNordVPNManager  is a Java program that is all bundled into a single jar file.  To run a jar file on the command line, simply type:

    java -jar JNordVPNManager_[version].jar
  
Please note that Java 11 or higher must be in your path and this assumes the JNordVPNManager.jar is in your current directory.

## SNAP

[![JNordVPNManager](https://snapcraft.io/j-nordvpn-manager/badge.svg)](https://snapcraft.io/j-nordvpn-manager)

See [Snapcraft Homepage](https://snapcraft.io) for more information. You can download, install, and keep JNordVPNManager up to date automatically by installing the snap via:

`sudo snap install j-nordvpn-manager`  (Assuming snap is installed)

This will install the application into a sandbox where it is separated from other applications. After the snap is installed, the application can be run directly from the command line as follows:

    /snap/j-nordvpn-manager/current/bin/java -jar /snap/j-nordvpn-manager/current/JNordVpnManager-current.jar

Optionally the starter [desktop file](https://github.com/com-mr-apps/JNordVPNManager/blob/main/snap/local/JNordVpnManager_Java.desktop) can be copied manually in the user `~/Desktop` folder.

### Important:
_In case you run JNordVPN Manager directly as snap application (with the command `j-nordvpn-manager` or by using the default created Application Desktop Icon):_

Because snaps restrict the execution of commands outside of their own environment, JNordVPN Manager will run in 'Installation' mode [_the snap created Desktop Icon has the extension '(install)'_]:

The application should run after the snap installation at least once from the snap in 'Installation' mode to create its - version independent - starter [desktop file](https://github.com/com-mr-apps/JNordVPNManager/blob/main/snap/local/JNordVpnManager_Java.desktop). With that JNordVPN Manager can be launched from the snap installation without restrictions outside of the encapsulated snap environment (see java command line above).

To deinstall type:

`sudo snap remove j-nordvpn-manager`

[![Get it from the Snap Store](https://snapcraft.io/en/dark/install.svg)](https://snapcraft.io/j-nordvpn-manager)

## References

### NordVPN backend (prerequisite!)
* [Installing and Using NordVPN on Linux](https://support.nordvpn.com/hc/en-us/articles/20196094470929-Installing-NordVPN-on-Linux-distributions)
* [Sign up and choose a plan (Affiliate link)](https://refer-nordvpn.com/ArNNOfynXcu)


### GeoTools
The world map is based on GeoTools, an open source (LGPL) Java code library that provides tools for geospatial data:
* https://geotools.org/about.html
* https://github.com/geotools/geotools

### World map data
Thanks to Natural Earth. Free vector and raster map data from:
* https://www.naturalearthdata.com/

### Speed Test
Based on JSpeedTest: Speed test client library for Java/Android
* https://github.com/bertrandmartel/speed-test-lib

## Donations
Funding for the ongoing development and maintenance of this application comes from donations made to me through [https://buymeacoffee.com/3dprototyping](https://buymeacoffee.com/3dprototyping). Your support in the form of a coffee or any other contribution is greatly appreciated and helps keep JNordVPN Manager alive and up-to-date.
If you like to be part of the Supporters group, which will have access to the Supporter Edition with more features, you can find more information there.

If you appreciate NordVPN's services, you can also support me by ordering NordVPN through my affiliate link: [https://refer-nordvpn.com/ArNNOfynXcu](https://refer-nordvpn.com/ArNNOfynXcu). This not only helps fund the development of JNordVPN Manager but also provides a financial incentive for further improving and maintaining this application.


## License 

The "Commons Clause" License Condition v1.0

The Software is provided to you by the Licensor under the License, as defined below, subject to the following condition.

Without limiting other conditions in the License, the grant of rights under the License will not include, and the License does not grant to you, the right to Sell the Software.

For purposes of the foregoing, "Sell" means practicing any or all of the rights granted to you under the License to provide to third parties, for a fee or other consideration (including without limitation fees for hosting or consulting/ support services related to the Software), a product or service whose value derives, entirely or substantially, from the functionality of the Software. Any license notice or attribution required by the License must also include this Commons Clause License Condition notice.

Software: JNordVPNManager

License: [The Common Development and Distribution License 1.1 ](https://spdx.org/licenses/CDDL-1.1.html)

Licensor: com.mr.apps
