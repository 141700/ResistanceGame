### BUILD image
FROM maven:3.6.3-jdk-13 as builder
#Copy Custom Maven settings
#COPY settings.xml /root/.m2/
# create app folder for sources
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build
#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
#RUN mvn dependency:resolve-plugins
#Copy source code
COPY src /build/src
# Build application
#RUN mvn package
RUN mvn package spring-boot:repackage


FROM openjdk:13-jdk-alpine as runtime
EXPOSE 8080
#Set app home folder
ENV APP_HOME /app

#Create base app folder
RUN mkdir $APP_HOME
#Create folder to save configuration files
RUN mkdir $APP_HOME/config
#Create folder with application logs
RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

WORKDIR $APP_HOME
#Copy executable jar file from the builder image
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]
#Second option using shell form:
#ENTRYPOINT exec java -jar app.jar $0 $@
