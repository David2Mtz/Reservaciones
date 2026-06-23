FROM eclipse-temurin:25-jdk-alpine
LABEL authors="Luis David Martinez"
ARG JAR_FILE=target/Reservaciones7CM3-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} reservaciones.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "reservaciones.jar"]