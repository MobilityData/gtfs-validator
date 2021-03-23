FROM java:8
WORKDIR /
COPY main/build/libs/gtfs-validator*.jar /
EXPOSE 8080
