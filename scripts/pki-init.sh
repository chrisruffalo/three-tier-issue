#!/bin/bash

# write keystore/truststore
keytool -genkey -alias localhost -keyalg RSA -keystore keystore.jks -dname "cn=localhost" -storepass locked

# add key's cert to truststore
keytool -export -alias localhost -file localhost.der -keystore keystore.jks -storepass locked
openssl x509 -inform der -in localhost.der -out localhost.pem
keytool -importcert -noprompt -file localhost.pem -alias localhost -keystore truststore.jks -storepass locked

# remove leftover files
rm localhost.dr
rm localhost.pem