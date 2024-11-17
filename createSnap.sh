#!/bin/bash

sudo snap remove j-nordvpn-manager

snapcraft clean #force build from GitHub
snapcraft

sudo snap install j-nordvpn-manager_2024.1.1_amd64.snap --devmode --dangerous

snapcraft upload --release=stable j-nordvpn-manager_2024.1.1_amd64.snap
