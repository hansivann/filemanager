FROM eclipse-temurin:17-jre

WORKDIR /app

RUN mkdir -p /app/data /app/uploads

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
