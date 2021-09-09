FROM openjdk:11
COPY build/libs/gtfs-validator*.jar /
WORKDIR /
EXPOSE 8080
ENTRYPOINT [ "/bin/sh", "-c", "java -Xmx6G -Xms6G -jar gtfs-validator*.jar"]
