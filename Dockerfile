FROM openjdk:11
COPY application/cli-app/build/libs/*.jar /cli-app/gtfs-validator-v1.3.0-SNAPSHOT_cli.jar
COPY application/web-app/spring-server/build/libs/*.war /web-app/gtfs-validator-v1.3.0-SNAPSHOT_web.war
WORKDIR /web-app
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "gtfs-validator-v1.3.0-SNAPSHOT_web.war"]