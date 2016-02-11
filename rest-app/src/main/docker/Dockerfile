FROM java:7
VOLUME /tmp
EXPOSE 8002
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xms256M","-Xmx256M","-jar","/lib/app.jar"]
ENV CLOUDWATCH_LOGGROUPNAME rest-app
