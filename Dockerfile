FROM openjdk:11 as build
COPY main/build/libs/*.jar /
WORKDIR /
