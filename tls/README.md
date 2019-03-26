# Mutual TLS POC

This document provides commands used to create certs during the mutual TLS EAP POC. 

Note that a helper script is provided which can generate all certificates needed for a POC environment:

* root CA + truststore
* server keys/certs + keystore
* 3 x client certificates (user / admin / superuser)

The script will create two directories; `build` and `final`. You can discard the build directory once certificates are created. Copy the `server.keystore` and `server.truststore` files into the `roles/jboss-ag-eap/files/` dir, and import the `.pfx` files into your browser.

## Create root CA
```
openssl genrsa -aes256 -out rootCA.key 2048
openssl req -x509 -new -nodes -key rootCA.key -days 1024 -out rootCA.pem -subj '/C=au/O=redhat/OU=jboss/CN=root ca'
```
## Create server keystore
```
openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr -subj '/C=au/O=redhat/OU=jboss/CN=jboss.rock.lab'
opensl x509 -req -in server.csr -CA rootCA.pem -CAkey rootCA.key -CAcreateserial -out server.crt -days 500
openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -name server -CAfile rootCA.pem -caname rootCA
keytool -importkeystore -deststorepass password -destkeypass password -destkeystore server.keystore -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass password -alias server
```
## Create server truststore
```
keytool -import -alias "Root CA" -file rootCA.pem -keystore server.truststore
```
## Create a user certificate
```
openssl genrsa -out user1.key 2048
openssl req -new -key user1.key -out user1.csr -subj '/C=au/O=redhat/OU=jboss/CN=user1'
openssl x509 -req -in user1.csr -CA rootCA.pem -CAkey rootCA.key -CAcreateserial -out user1.crt -days 500
openssl pkcs12 -export -in user1.crt -inkey user1.key -out user1.pfx -name USER1 -CAfile rootCA.pem -caname rootCA
```
Import the `.pfx` file into your browser.

## Troubleshooting application client authentication

The AGD EAP 6 mutual TLS implementation uses a `roles.properties` file to manage user roles. This can be configured with the `jboss-ag-roles` Ansible role.

You can enable detailed security logging by adding the following to the console logging handler:
```
<logger category="org.jboss.as.web.security">
    <level name="TRACE"/>
</logger>
<logger category="org.jboss.security">
    <level name="TRACE"/>
</logger>
```
This will provide details of users attempting to login to the application, and whether roles are loaded correctly.

Note that the client authentication performed by the server performs a match on the certificate in the order that attributes are presented.

For example, if you create a certificate incorrectly:
```
openssl req -new -key user1.key -out user1.csr -subj '/CN=user1/OU=jboss/O=redhat/C=au'
```
Then the JBoss EAP framework will attempt to find a match in the `role.properties` file like:
```
10:44:48,072 TRACE [org.jboss.security] (http-0.0.0.0:8443-1) PBOX000209: defaultLogin, principal: C=au, O=redhat, OU=jboss, CN=broken
...
10:44:48,146 TRACE [org.jboss.as.web.security] (http-0.0.0.0:8443-1) User: C=au, O=redhat, OU=jboss, CN=broken is authenticated
...
10:44:48,148 TRACE [org.jboss.as.web.security] (http-0.0.0.0:8443-1) hasRole:RealmBase says:false::Authz framework says:true:final=false
```
In this case, your user would need to be added to `roles.properties` file backwards, ie;
```
C\=au,\ O\=redhat,\ OU\=jboss,\ CN\=broken=shadmin
```
If you now restart the JBoss server, you will see the user can now authenticate and access the protected page:
```
10:53:12,679 TRACE [org.jboss.as.web.security] (http-0.0.0.0:8443-1) User: C=au, O=redhat, OU=jboss, CN=broken is authenticated
10:53:12,682 TRACE [org.jboss.as.web.security] (http-0.0.0.0:8443-1) hasRole:RealmBase says:true::Authz framework says:true:final=true
10:53:12,682 TRACE [org.jboss.as.web.security] (http-0.0.0.0:8443-1) hasResourcePermission:RealmBase says:true::Authz framework says:true:final=true
```
However, this isn't a great practise. We should just create the user certificate properly:
```
openssl req -new -key user1.key -out user1.csr -subj '/C=au/O=redhat/OU=jboss/CN=user1'

```

