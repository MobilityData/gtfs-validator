FROM java:8
COPY main/build/libs/*.jar /
WORKDIR /
