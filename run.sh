# run each eap

# export keystore location so it can be used in elytron config too
EAP_KEYSTORE=$(realpath ./keystore.jks || echo "$(pwd)/keystore.jks")
export EAP_KEYSTORE

EAP_TRUSTSTORE=$(realpath ./truststore.jks || echo "$(pwd)/truststore.jks")
export EAP_TRUSTSTORE

# shared args
SHARED_ARGS="-Djavax.net.ssl.keyStore=${EAP_KEYSTORE} -Djavax.net.ssl.keyStorePassword=locked -Djavax.net.ssl.trustStore=${EAP_TRUSTSTORE} -Djavax.net.ssl.trustStorePassword=locked"

# start all eap instances with their own standalone ssl copy (noticed corruption during testing)
cp wildfly/standalone/configuration/standalone-ssl{,-1}.xml
export REMOTE_HTTP_PORT=8180
export REMOTE_HTTPS_PORT=8543
export LOCAL_HTTP_PORT=8080
export LOCAL_HTTPS_PORT=8443
wildfly/bin/standalone.sh -c standalone-ssl-1.xml ${SHARED_ARGS} -Djboss.socket.binding.port-offset=0 -Djboss.node.name=ONE -Djboss.tx.node.id=1823 &
pid[0]=$!
# sleep because concurrent starts gives it fits
sleep 7

cp wildfly/standalone/configuration/standalone-ssl{,-2}.xml
export REMOTE_HTTP_PORT=8280
export REMOTE_HTTPS_PORT=8643
export LOCAL_HTTP_PORT=8180
export LOCAL_HTTPS_PORT=8543
wildfly/bin/standalone.sh -c standalone-ssl-2.xml ${SHARED_ARGS} -Djboss.socket.binding.port-offset=100 -Djboss.node.name=TWO -Djboss.tx.node.id=2425  &
pid[1]=$!
sleep 7

cp wildfly/standalone/configuration/standalone-ssl{,-3}.xml
export REMOTE_HTTP_PORT=8080
export REMOTE_HTTPS_PORT=8443
export LOCAL_HTTP_PORT=8280
export LOCAL_HTTPS_PORT=8643
wildfly/bin/standalone.sh -c standalone-ssl-3.xml ${SHARED_ARGS} -Djboss.socket.binding.port-offset=200 -Djboss.node.name=THREE -Djboss.tx.node.id=3933 &
pid[2]=$!

# after ctrl+c the first one the others will be killed as well
trap "ps aux | grep standalone-ssl | grep -v grep | awk '{print \$2}' | xargs kill -9; exit 1" EXIT
wait
