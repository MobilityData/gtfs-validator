FROM gradle:7-jdk11-alpine AS build

COPY --chown=gradle:gradle . /build
WORKDIR /build

ARG VERSION_TAG
ENV versionTag=$VERSION_TAG
RUN gradle build --no-daemon


FROM openjdk:11
COPY --from=build /build/main/build/libs/*.jar /
WORKDIR /
