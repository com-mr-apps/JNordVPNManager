#!/bin/bash

# 1 - build snap
cd /media/martin/writable/projekte/GitHub/com-mr-apps/JNordVPNManager
snapcraft clean j-nordvpn-manager
snapcraft

# 2 - install/test snap
sudo snap remove j-nordvpn-manager
sudo snap install j-nordvpn-manager_2025.2.0_amd64.snap --dangerous
j-nordvpn-manager

# 3 - upload snap [candidate|stable]
snapcraft upload --release=candidate j-nordvpn-manager_2025.2.0_amd64.snap

# 4 - install snap via snapstore
