# Coffee Pickles for Spring Boot

## Description

Coffee Pickles for Spring Boot allows users to track their coffee consumption over time. Coffee Pickles demonstrates the following features integrated with Spring Boot:

* Persistence with Hibernate/PostgreSQL
* X509 PKI Authentication with persistent user lookup
* REST API
* Drools/ BRMS for coffee pricing
* Charting data with Google Charts and AJAX/jQuery

### Screenshots

![coffeepickles1](http://i.imgur.com/ZFpAJly.png)

## Getting Started

### Creating a Postgres database

Coffee Pickles uses a Postgres database for persistence. Software Collections (SCL- https://www.softwarecollections.org) can be used to quickly get a database environment running:

```
# yum install rh-postgresql95
# systemctl start rh-postgresql95-postgresql
# su - postgres -c 'scl enable rh-postgresql95 -- createdb coffeepicklesdb'
# su - postgres -c 'scl enable rh-postgresql95 -- createuser --interactive coffeeuser'
```
Edit the pg_hba.conf file to allow the coffeeuser access to the coffeepicklesdb:

```
# cat /var/opt/rh/rh-postgresql95/lib/pgsql/data/pg_hba.conf
...

# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             all                                     peer
# local users
host    coffeepicklesdb coffeeuser      127.0.0.1/32            md5
# IPv4 local connections:
host    all             all             127.0.0.1/32            ident
...
```
Restart the Postgres service
```
# systemctl restart rh-postgresql95-postgresql
```
Ensure the `src/main/resources/application.properties` file is configured for the database:

```
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/coffeepicklesdb
spring.datasource.username=coffeeuser
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.password=password
```

### Creating Certificates

Coffee pickles uses Mutual TLS for X509 PKI authentication. This requires creating certificates for a Certificate Authority (CA), the server, and a PKCS12 certificate for a user which you can import into your browser.

Create a folder to hold the certs:
```
# mkdir src/main/certs
# cd /src/main/certs
```
Create a CA key:
```
# openssl genrsa -aes256 -out rootCA.key 2048
# openssl req -x509 -new -nodes -key rootCA.key -days 1024 -out rootCA.pem
```
Create a certificate for the server, and create a Java keystore for Spring Boot to access. Substitute 'password' for a secure password:
```
# openssl genrsa -out coffee-pickles.key 2048
# openssl req -new -key coffee-pickles.key -out coffee-pickles.csr
# openssl x509 -req -in coffee-pickles.csr -CA rootCA.pem -CAkey rootCA.key -CAcreateserial -out coffee-pickles.crt -days 500
# openssl pkcs12 -export -in coffee-pickles.crt -inkey coffee-pickles.key -out coffee-pickles.p12 -name COFFEE -CAfile rootCA.pem -caname rootCA
# keytool -importkeystore -deststorepass password -destkeypass password -destkeystore coffee-pickles.keystore -srckeystore coffee-pickles.p12 -srcstoretype PKCS12 -srcstorepass password -alias COFFEE
```
Create a truststore for certificates that the server trusts:
```
# keytool -import -alias "Root CA" -file rootCA.pem -keystore truststore.jks
```

Update the `src/main/resources/application.properties` file with the certificate details:
```
server.ssl.key-store=src/main/certs/coffee-pickles.keystore
server.ssl.key-store-password=password
server.ssl.key-alias=coffee
server.ssl.key-password=password
server.ssl.trust-store=src/main/certs/truststore.jks
server.ssl.trust-store-password=password
server.ssl.client-auth=need
server.port=8443
server.ssl.enabled=true
```
Create a user certificate:
```
# openssl genrsa -out user1.key 2048
# openssl req -new -key user1.key -out user1.csr
# openssl x509 -req -in user1.csr -CA rootCA.pem -CAkey rootCA.key -CACreateserial -out user1.crt -days 500
# openssl pkcs12 -export -in user1.crt -inkey user1.key -out user1.pfx -name USER1 -CAfile rootCA.pem -caname rootCA
```
Import the certificate into your browser.


### Create database tables

Spring Boot will create the correct tables in the database when the application is started. Now that the database is configured, build and run the application and verify that the tables have been created:

```
# su - postgres -c 'scl enable rh-postgresql95 -- psql -d coffeepicklesdb'
coffeepicklesdb=# \dt
            List of relations
 Schema |   Name   | Type  |   Owner
--------+----------+-------+------------
 public | coffee   | table | coffeeuser
 public | customer | table | coffeeuser
 public | payment  | table | coffeeuser
(3 rows)
```

### Create a database user matching the certificate name

Coffee Pickles performs a database lookup for a Customer object with a username matching the certificate name. To add the user to the database:

Check the CN that was created in the certificate:
```
# openssl x509 -in user1.crt -noout -text
...
Subject: C=AU, ST=SomeState, L=SomeTown, O=Rock Labs, OU=Test, CN=user1
...
```
Create the database user matching the CN:

```
# su - postgres -c 'scl enable rh-postgresql95 -- psql -d coffeepicklesdb'
coffeepicklesdb=# insert into customer (id,balance,user_name) VALUES (1, 0, 'user1');
```

## Drools/ BRMS pricing

Coffee Pickles uses Drools/ BRMS to calculate the price of a coffee when the user clicks the 'Coffee!' button. The rules are stored in `src/main/resources/drools/coffeepickles.drl`. You can customize the coffee pricing logic by updating the 'when' statements in the file. Eg; to make every 10th coffee free, and coffee normally cost $2, change the file like so:
```
rule "Normally coffee costs $2.00"

    when
        $user: Customer()
        eval((($user.getNumCoffees()+1) % 10 )!=0)
    then
        insertLogical(new Coffee(new BigDecimal("2.00"),$user));
end

rule "Every tenth coffee is free"

    when
        $user: Customer ()
        eval((($user.getNumCoffees()+1) % 10 )==0)
    then
        insertLogical(new Coffee(new BigDecimal("0.00"),$user));
end
```


