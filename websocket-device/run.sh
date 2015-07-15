#!/bin/bash

# get the IP address of the docker interface
PUBLIC_INTERFACE=`ifconfig docker0 | awk -F"[: ]+" '/inet addr:/ {print $4}'`

# start the container
docker run -p 7003:7000 -p 8003:8003 -p 8083:8083 -e ZOOKEEPER=$PUBLIC_INTERFACE:2181 -e KAFKA_BROKERS=$PUBLIC_INTERFACE:9092 -e KAFKA_TOPIC=raw -e KEYSTORE_PASSWORD=server -e AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE -e AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYzEXAMPLEKEY -e AWS_DYNAMODB_ENDPOINT=http://$PUBLIC_INTERFACE:8000 reideltj/websocket-device:DEVELOPER
