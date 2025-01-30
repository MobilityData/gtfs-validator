FROM eclipse-temurin:17-jdk-noble AS build

COPY --chown=gradle:gradle . /build
WORKDIR /build

ARG VERSION_TAG
RUN ./gradlew shadowJar \
    --no-daemon \
    -Prelease.forceVersion="${VERSION_TAG%-SNAPSHOT}"


FROM eclipse-temurin:17-jdk-noble AS runtime
COPY --from=build /build/cli/build/libs/gtfs-validator-*-cli.jar /gtfs-validator-cli.jar
WORKDIR /

ARG VERSION_TAG
ENV VERSION_TAG=$VERSION_TAG
ENTRYPOINT [ "java", "-jar", "gtfs-validator-cli.jar" ]
