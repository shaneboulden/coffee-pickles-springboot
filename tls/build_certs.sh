#!/bin/bash

# Shane Boulden <sboulden@redhat.com>
# Adapted from original by Derek Horton

function create_root_cert
{ 
  KEYFILE=$1
  CERTFILE=$2
  PASS=$3
  SUBJ=$4

  pushd build
  openssl genrsa -passout pass:$PASS -aes256 -out $KEYFILE 2048
  openssl req -passin pass:$PASS -x509 -new -nodes -key $KEYFILE -days 1024 -out $CERTFILE -subj "$4"
  popd
}

function create_cert
{
  KEYFILE=$1
  CSRFILE=$2
  CERTFILE=$3
  PASS=$4
  SUBJ=$5
  CACERT=$6
  CAKEY=$7

  pushd build
  openssl genrsa -passout pass:$PASS -out $KEYFILE 2048
  openssl req -new -key $KEYFILE -out $CSRFILE -subj $SUBJ
  openssl x509 -req -passin pass:$PASS -in $CSRFILE -CA $CACERT -CAkey $CAKEY -CAcreateserial -out $CERTFILE -days 500
  popd
}

function create_p12
{
  KEYFILE=$1
  CERTFILE=$2
  P12FILE=$3
  CACERT=$4
  NAME=$5
  PASS=$6
  DIR=$7

  pushd $DIR
  openssl pkcs12 -export -passout pass:$PASS -in $CERTFILE -inkey $KEYFILE -out $P12FILE -name $NAME -CAfile $CACERT -caname rootCA 
  popd
}

function create_keystore
{
  KEYSTORE=$1
  P12FILE=$2
  ALIAS=$3
  PASS=$4

  keytool -importkeystore -deststorepass $PASS -destkeypass $PASS -destkeystore $KEYSTORE -deststoretype pkcs12 -srckeystore $P12FILE -srcstoretype PKCS12 -srcstorepass $PASS -alias $ALIAS
}

function create_truststore
{
  KEYSTORE=$1
  CACERT=$2
  ALIAS=$3
  PASS=$4

  pushd final
  keytool -import -alias "$ALIAS" -file $CACERT -keystore $KEYSTORE -deststorepass $PASS
  popd
}

PASS="1800redhat"

# create dirs
rm -r build; mkdir build
rm -r final; mkdir final

# create root certificate
create_root_cert "rootCA.key" "rootCA.pem" $PASS "/C=au/O=redhat/OU=jboss/CN=poc root ca"

# create server certificates and keystore
create_cert "server.key" "server.csr" "server.crt" $PASS "/C=au/O=redhat/OU=jboss/CN=jboss.rock.lab" "rootCA.pem" "rootCA.key"
create_p12 "server.key" "server.crt" "server.p12" "rootCA.pem" "server" $PASS "build"
create_keystore "final/server.keystore" "build/server.p12" "server" $PASS

# create server truststore
create_truststore "server.truststore" "../build/rootCA.pem" "rootca" $PASS

# create client certificates
create_cert "user1.key" "user1.csr" "user1.crt" $PASS "/C=au/O=redhat/OU=jboss/CN=user1" "rootCA.pem" "rootCA.key" 
create_p12 "../build/user1.key" "../build/user1.crt" "user1.pfx" "../build/rootCA.pem" "user1" $PASS "final"

create_cert "admin1.key" "admin1.csr" "admin1.crt" $PASS "/C=au/O=redhat/OU=jboss/CN=admin1" "rootCA.pem" "rootCA.key"
create_p12 "../build/admin1.key" "../build/admin1.crt" "admin1.pfx" "../build/rootCA.pem" "admin1" $PASS "final"

create_cert "superuser1.key" "superuser1.csr" "superuser1.crt" $PASS "/C=au/O=redhat/OU=jboss/CN=superuser1" "rootCA.pem" "rootCA.key"
create_p12 "../build/superuser1.key" "../build/superuser1.crt" "superuser1.pfx" "../build/rootCA.pem" "superuser1" $PASS "final"
