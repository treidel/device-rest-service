#!/bin/bash

set +x

# step 1 - create a root key and create a self-signed certificate
if [ ! -e root.crt ]; then
	openssl req -x509 -new -nodes -days 7300 -newkey rsa:4096 -subj "/C=CA/ST=Ontario/L=Ottawa/O=Fancy Pants Ltd./CN=fancypants.io" -keyout root.key -out root.crt
fi

# step 2 - create the servers key
if [ ! -e servers.key ]; then
	openssl genrsa -out servers.key 4048
fi

# step 3 - create the servers certificate request
if [ ! -e servers.csr ]; then
	openssl req -new -key servers.key -out servers.csr -subj "/C=CA/ST=Ontario/L=Ottawa/O=Fancy Pants Ltd./CN=servers.fancypants.io" 
fi

# step 4 - sign the servers certificate
if [ ! -e servers.crt ]; then
	openssl x509 -req -in servers.csr -CA root.crt -CAkey root.key -CAcreateserial -extfile /etc/ssl/openssl.cnf -extensions v3_ca -out servers.crt -days 3650 
fi

# step 5 - create the websocket-device server key
if [ ! -e websocket-device.key ]; then
	openssl genrsa -out websocket-device.key 2048
fi

# step 6 - create the websocket-device certificate request
if [ ! -e websocket-device.csr ]; then
	openssl req -new -key websocket-device.key -out websocket-device.csr -subj "/C=CA/ST=Ontario/L=Ottawa/O=Fancy Pants Ltd./CN=websocket.device.fancypants.io" 
fi

# step 7 - sign the websocket-device certificate with the servers certificate
if [ ! -e websocket-device.crt ]; then
	openssl x509 -req -in websocket-device.csr -CA servers.crt -CAkey servers.key -CAcreateserial -out websocket-device.crt -days 365
fi

# step 8 - create the certificate chain for the websocket-device certificate
if [ ! -e websocket-device.chn ]; then
	cat websocket-device.crt servers.crt root.crt > websocket-device.chn
fi

# step 9 - create the devices key
if [ ! -e devices.key ]; then
	openssl genrsa -out devices.key 4096
fi

# step 10 - create the devices certificate request
if [ ! -e devices.csr ]; then
	openssl req -new -key devices.key -out devices.csr -subj "/C=CA/ST=Ontario/L=Ottawa/O=Fancy Pants Ltd./CN=devices.fancypants.io" 
fi

# step 11 - sign the devices certificate
if [ ! -e devices.crt ]; then
	openssl x509 -req -in devices.csr -CA root.crt -CAkey root.key -CAcreateserial -extfile /etc/ssl/openssl.cnf -extensions v3_ca -out devices.crt -days 3650 
fi

# step 12 - convert the websocket-device certificate to p12 format
if [ ! -e websocket-device.p12 ]; then
	openssl pkcs12 -export -in websocket-device.chn -inkey websocket-device.key -out websocket-device.p12 -password pass:websocket-device -name websocket-device
fi

# step 13 - convert the websocket-device keystore to JKS format
if [ ! -e websocket-device.jks ]; then
	keytool -importkeystore -srckeystore websocket-device.p12 -srcstoretype pkcs12 -srcstorepass websocket-device -srcalias websocket-device -destkeystore websocket-device.jks -deststoretype jks -deststorepass websocket-device -destalias websocket-device
	keytool -importcert -file devices.crt -alias devices -keystore websocket-device.jks -storepass websocket-device -noprompt
fi
