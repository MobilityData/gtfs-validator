FROM gradle:7-jdk11-alpine AS build

COPY --chown=gradle:gradle . /build
WORKDIR /build

RUN gradle shadowJar --no-daemon


FROM openjdk:11
COPY --from=build /build/main/build/libs/*.jar /
WORKDIR /

ENTRYPOINT [ "java", "-jar", "gtfs-validator-0.1.0-SNAPSHOT-cli.jar" ]
