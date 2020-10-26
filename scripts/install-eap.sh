#!/bin/bash

WILDFLY_VERSION=21.0.0.Final

# download wildfly
wget https://download.jboss.org/wildfly/${WILDFLY_VERSION}/wildfly-${WILDFLY_VERSION}.zip

# extract to wildfly folder
unzip wildfly-${WILDFLY_VERSION}.zip
mv wildfly-${WILDFLY_VERSION} wildfly

# remove artifact
rm -rf wildfly-${WILDFLY_VERSION}.zip