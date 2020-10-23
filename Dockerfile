FROM openjdk:11
COPY application/cli-app/build/libs/*.jar /usr/gtfs-validator/cli-app/gtfs-validator-v1.3.0-SNAPSHOT_cli.jar
COPY application/cli-app/scripts/end_to_end.sh /usr/gtfs-validator/cli-app/end_to_end.sh
RUN chmod +x /usr/gtfs-validator/cli-app/end_to_end.sh
COPY application/web-app/spring-server/build/libs/*.war /usr/gtfs-validator/web-app/gtfs-validator-v1.3.0-SNAPSHOT_web.war
WORKDIR /usr/gtfs-validator/web-app
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "gtfs-validator-v1.3.0-SNAPSHOT_web.war"]

LABEL org.opencontainers.image.source = "https://github.com/MobilityData/gtfs-validator.git"