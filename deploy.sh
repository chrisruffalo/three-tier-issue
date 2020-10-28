#!/bin/bash

VERSION=1.0-SNAPSHOT

# build module
mvn clean install

# install module into appropriate location
mkdir -p wildfly/modules/com/tier/api/main
rm wildfly/modules/com/tier/api/main/*
cp ./tier-api/target/tier-api-${VERSION}.jar wildfly/modules/com/tier/api/main/.
cp ./tier-api/module.xml wildfly/modules/com/tier/api/main/module.xml

# copy war and jar
cp ./tier-impl/target/tier-impl-${VERSION}.jar wildfly/standalone/deployments/.
cp ./tier-war/target/tier.war wildfly/standalone/deployments/.