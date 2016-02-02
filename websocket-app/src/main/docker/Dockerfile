FROM java:7
VOLUME /tmp
EXPOSE 8004
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xms128M","-Xmx128M","-jar","/lib/app.jar"]
ENV CLOUDWATCH_LOGGROUPNAME websocket-app
