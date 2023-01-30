FROM openjdk:21-jdk-buster
COPY target/ppro-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]