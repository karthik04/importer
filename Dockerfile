#1 Cache gradle dependencies
FROM gradle:7.0-jdk11 AS cache
WORKDIR /app
ENV GRADLE_USER_HOME /cache
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle --no-daemon build --stacktrace

#2 Docker build stage: build the project with Gradle
FROM gradle:7.0-jdk11 as builder
WORKDIR /project
COPY . /project/
COPY --from=cache /cache /home/gradle/.gradle
RUN gradle :build --no-daemon

#3 Docker build stage: copy builder output and configure entry point
FROM adoptopenjdk:11-jre-hotspot
ENV APP_DIR /application
ENV APP_FILE container-uber-jar.jar

EXPOSE 8080

WORKDIR $APP_DIR
COPY --from=builder /project/build/libs/*fat.jar $APP_DIR/$APP_FILE

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $APP_FILE"]


# Alternate way to run the app without using fat jar

#FROM vertx/vertx4
#
##                                                       (1)
#ENV VERTICLE_NAME emp.importer.MainVerticle
#ENV VERTICLE_FILE ./build/libs/importer-1.0.0-SNAPSHOT.jar
#
## Set the location of the verticles
#ENV VERTICLE_HOME /usr/verticles
#
#EXPOSE 8080
#
## Copy your verticle to the container                   (2)
#COPY $VERTICLE_FILE $VERTICLE_HOME/
#
## Launch the verticle
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
