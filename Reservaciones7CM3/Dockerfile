# Etapa 1: Compilar y empaquetar la aplicacion
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Crear la imagen de ejecucion ligera
FROM eclipse-temurin:21-jre-alpine
LABEL authors="Luis David Martinez"
WORKDIR /app
COPY --from=build /app/target/Reservaciones7CM3-0.0.1-SNAPSHOT.jar reservaciones.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "reservaciones.jar"]