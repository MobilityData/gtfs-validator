FROM eclipse-temurin:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /newrelic/logs
RUN chown -R spring:spring /newrelic/logs

USER spring:spring
ARG JAR_FILES=build/libs/\*.jar
ARG PROPERTIES_FILE=build/resources/main/application.properties
ARG CREDS_FILE=src/main/resources/web-based-gtfs-validator-a088ec5f045d.json

COPY ${JAR_FILES} /
COPY ${PROPERTIES_FILE} /
COPY ${CREDS_FILE} /

ADD ./newrelic/newrelic.jar /newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /newrelic/newrelic.yml

ENTRYPOINT ["java","-Xmx12g","-javaagent:/newrelic/newrelic.jar","-jar","/service-4.0.1-SNAPSHOT.jar"]