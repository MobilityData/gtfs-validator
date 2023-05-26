FROM eclipse-temurin:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /newrelic/logs
RUN chown -R spring:spring /newrelic/logs

USER spring:spring
ARG JAR_FILES=build/libs/\*.jar
ARG PROPERTIES_FILE=build/resources/main/application.properties
ARG CREDS_FILE=src/main/resources/web-based-gtfs-validator-a088ec5f045d.json
ARG CURRENT_VERSION
RUN test -n "$CURRENT_VERSION" || { echo "missing required build arg: CURRENT_VERSION"; exit 1; }
ENV CURRENT_VERSION=${CURRENT_VERSION}

COPY ${JAR_FILES} /
COPY ${PROPERTIES_FILE} /
COPY ${CREDS_FILE} /

ADD ./newrelic/newrelic.jar /newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /newrelic/newrelic.yml

ENTRYPOINT exec java -Xmx12g  -javaagent:/newrelic/newrelic.jar -jar /service-${CURRENT_VERSION}.jar
