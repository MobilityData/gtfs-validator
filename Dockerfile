FROM gradle:7-jdk11-alpine AS build

COPY --chown=gradle:gradle . /build
WORKDIR /build

ARG VERSION_TAG
RUN ./gradlew shadowJar \
    --no-daemon \
    -Prelease.forceVersion=$VERSION_TAG


FROM openjdk:11
COPY --from=build /build/main/build/libs/gtfs-validator-*-cli.jar /gtfs-validator-cli.jar
WORKDIR /

ARG VERSION_TAG
ENV VERSION_TAG=$VERSION_TAG
ENTRYPOINT [ "java", "-jar", "gtfs-validator-cli.jar" ]
