FROM java:7
VOLUME /tmp
EXPOSE 8004
ENV SNS_TOPIC_PREFIX device-
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xms256M","-Xmx256M","-jar","/lib/app.jar"]
ENV CLOUDWATCH_LOGGROUPNAME websocket-app
