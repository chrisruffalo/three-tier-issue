#!/bin/bash

# download wildfly
wget https://download.jboss.org/wildfly/18.0.0.Final/wildfly-18.0.0.Final.zip

# extract to wildfly folder
unzip wildfly-18.0.0.Final.zip
mv wildfly-18.0.0.Final wildfly

# remove artifact
rm -rf wildfly-18.0.0.Final.zip

# setup ejb application user
wildfly/bin/add-user.sh -a -u ejb -p ejb -e