FROM openjdk:11
COPY main/springboot/build/libs/gtfs-validator*.jar /
WORKDIR /
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "gtfs-validator*.jar"]
