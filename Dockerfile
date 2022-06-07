FROM openjdk:11-slim
COPY main/build/libs/*.jar /
WORKDIR /
