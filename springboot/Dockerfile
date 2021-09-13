FROM openjdk:11
COPY build/libs/gtfs-validator*.jar /
WORKDIR /
EXPOSE 8080
ENTRYPOINT [ "/bin/sh", "-c", "java -jar -Xmx16G -Xms16G gtfs-validator*.jar"]
