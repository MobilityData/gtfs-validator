FROM openjdk:11
COPY application/cli-app/build/libs/*.jar /usr/gtfs-validator/cli-app/
COPY application/cli-app/scripts/end_to_end.sh /usr/gtfs-validator/cli-app/end_to_end.sh
RUN chmod +x /usr/gtfs-validator/cli-app/end_to_end.sh
COPY application/web-app/spring-server/build/libs/*.war /usr/gtfs-validator/web-app/ 
EXPOSE 8090
CMD ["/bin/sh", "-c", "java -jar /usr/gtfs-validator/web-app/*.war"]
