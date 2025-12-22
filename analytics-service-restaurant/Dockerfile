# Используем легковесный образ JRE 17 на базе Alpine Linux
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN chown javauser:javauser /app/app.jar

USER javauser
EXPOSE 9090 8081

ENTRYPOINT ["java", "-jar", "app.jar"]