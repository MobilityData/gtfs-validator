FROM eclipse-temurin:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /newrelic/logs
RUN chown -R spring:spring /newrelic/logs

ARG PROPERTIES_FILE=build/resources/main/application.properties
ARG CREDS_FILE=src/main/resources/web-based-gtfs-validator-a088ec5f045d.json
COPY ${PROPERTIES_FILE} /application.properties
RUN chown spring:spring /application.properties
RUN chmod 0644 /application.properties
COPY ${CREDS_FILE} /web-based-gtfs-validator-a088ec5f045d.json
RUN chown spring:spring /web-based-gtfs-validator-a088ec5f045d.json
RUN chmod 0644 /web-based-gtfs-validator-a088ec5f045d.json
ADD ./newrelic/newrelic.yml /newrelic/newrelic.yml
RUN chown spring:spring /newrelic/newrelic.yml
RUN chmod 0644 /newrelic/newrelic.yml

USER spring:spring
ARG JAR_FILES=build/libs/\*.jar
ARG CURRENT_VERSION
RUN test -n "$CURRENT_VERSION" || { echo "missing required build arg: CURRENT_VERSION"; exit 1; }
ENV CURRENT_VERSION=${CURRENT_VERSION}

COPY ${JAR_FILES} /

ADD ./newrelic/newrelic.jar /newrelic/newrelic.jar

ENV SPRING_PROFILES_ACTIVE=cloud
ENV SENTRY_LOGGING_MINIMUM_EVENT_LEVEL=error
ENV SENTRY_LOGGING_MINIMUM_BREADCRUMB_LEVEL=info

ENTRYPOINT exec java -Xmx12g  -javaagent:/newrelic/newrelic.jar -jar /service-${CURRENT_VERSION}.jar
