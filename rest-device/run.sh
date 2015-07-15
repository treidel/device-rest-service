#!/bin/bash

# get the IP address of the docker interface
PUBLIC_INTERFACE=`ifconfig docker0 | awk -F"[: ]+" '/inet addr:/ {print $4}'`

# start the container
docker run -p 7001:7000 -p 8001:8001 -e AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE -e AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYzEXAMPLEKEY -e AWS_DYNAMODB_ENDPOINT=http://$PUBLIC_INTERFACE:8000 -e RABBITMQ_URI=amqp://guest@${PUBLIC_INTERFACE} -e RABBITMQ_PASSWORD=guest -e RABBITMQ_EXCHANGE=device reideltj/rest-device:DEVELOPER
