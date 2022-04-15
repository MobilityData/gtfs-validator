FROM gradle:7-jdk11-alpine AS build

COPY --chown=gradle:gradle . /build
WORKDIR /build

ARG VERSION_TAG
ENV versionTag=$VERSION_TAG
RUN gradle build --no-daemon


FROM openjdk:11
COPY --from=build /build/main/build/libs/*.jar /
WORKDIR /

ARG VERSION_TAG
RUN echo "#!/bin/bash\nexec java -jar gtfs-validator-${VERSION_TAG}_cli.jar \"\$@\"" > /entrypoint.sh \
    && chmod +x /entrypoint.sh
ENTRYPOINT [ "/entrypoint.sh" ]
