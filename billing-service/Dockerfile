# ============================================
# 1) Etapa de construcción (build)
# ============================================
FROM maven:3.9.8-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar y generar el JAR
RUN mvn -q -e -DskipTests=true package

# ============================================
# 2) Etapa final, liviana para producción
# ============================================
FROM eclipse-temurin:17-jdk-alpine AS runtime

WORKDIR /app

# Copiar solo el JAR generado desde el builder
COPY --from=builder /app/target/billing-service-0.0.1-SNAPSHOT.jar app.jar

# Puerto que expone tu aplicación
EXPOSE 8081

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
