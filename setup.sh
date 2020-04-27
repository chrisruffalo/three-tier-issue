#!/bin/bash

# ensure widlfly is installed and available
if [[ ! -d "./wildfly" ]]; then
  $(which bash) scripts/install-eap.sh
fi

# configure pki
if [[ ! -f "./keystore.jks" && ! -f "./truststore.jks" ]]; then
  $(which bash) scripts/pki-init.sh
fi

# configure eap
if [[ -f "widlfly/standalone/configuration/standalone-ssl.xml" ]]; then
  rm widlfly/standalone/configuration/standalone-ssl.xml
fi
cp wildfly/standalone/configuration/standalone.xml wildfly/standalone/configuration/standalone-ssl.xml

# export keystore location so it can be used in elytron config too
EAP_KEYSTORE=$(realpath ./keystore.jks)
export EAP_KEYSTORE

# execute the cli script
$(which bash) ./wildfly/bin/jboss-cli.sh --echo-command --file=./scripts/configure-jboss.cli



