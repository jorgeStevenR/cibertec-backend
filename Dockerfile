# --- Etapa 1: compilar el jar ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Primero solo el pom: las dependencias quedan en caché mientras no cambie.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
# Los tests se corren aparte: necesitan la base de Supabase y no deben correr en cada build de la imagen.
RUN mvn -q -B package -DskipTests

# --- Etapa 2: imagen liviana solo con el JRE ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S lms && adduser -S lms -G lms
COPY --from=build /app/target/*.jar app.jar
USER lms

# Render asigna el puerto en PORT (application.yml lo lee); 8080 en local.
EXPOSE 8080
# La JVM se ajusta a la memoria del contenedor (los planes chicos de Render tienen 512 MB).
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"
# Sin application-local.yml en la imagen: el perfil por defecto es "prod" y todo lo sensible llega por variables de entorno.
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
