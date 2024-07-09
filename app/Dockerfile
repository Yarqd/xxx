FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/app-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
