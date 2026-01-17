FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY authservice/build/libs/*.jar app.jar

USER root
RUN chown spring:spring app.jar
USER spring:spring

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]