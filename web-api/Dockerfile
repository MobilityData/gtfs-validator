FROM eclipse-temurin:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILES=build/libs/\*.jar
ARG PROPERTIES_FILE=build/resources/main/application.properties
ARG CREDS_FILE=src/main/resources/web-based-gtfs-validator-3c339fa28589.json
COPY ${JAR_FILES} /
COPY ${PROPERTIES_FILE} /
COPY ${CREDS_FILE} /
ENTRYPOINT ["java","-jar","/web-4.0.1-SNAPSHOT.jar"]