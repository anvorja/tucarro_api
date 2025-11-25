# ====================================================================
# ETAPA 1: CONSTRUCCIÓN (Build Stage)q
# ====================================================================
FROM eclipse-temurin:17-jdk-jammy AS builder

LABEL stage="builder"
LABEL maintainer="anborja@tucarro.com"

RUN apt-get update && \
    apt-get install -y curl unzip && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src src
RUN ./gradlew clean build -x test --no-daemon
RUN ls -la build/libs/

# ====================================================================
# ETAPA 2: PRODUCCIÓN (Runtime Stage)
# ====================================================================
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="anborja@tucarro.com"
LABEL version="1.0.0"
LABEL description="TuCarro API - Sistema de gestión de autos personales"

RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean

RUN groupadd -r tucarro && useradd -r -g tucarro tucarro

WORKDIR /app

# 🔧 VARIABLES FLEXIBLES: Se obtienen del docker-compose.yml o Render
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
    JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
    TZ=America/Bogota

COPY --from=builder /workspace/app/build/libs/*.jar app.jar

RUN mkdir -p /var/log/tucarro && \
    chown -R tucarro:tucarro /app /var/log/tucarro

USER tucarro

EXPOSE $PORT

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# 🚀 COMANDO FLEXIBLE: Funciona para Docker Compose y Render
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
