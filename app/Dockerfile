FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y wget unzip \
    && wget https://services.gradle.org/distributions/gradle-8.6-bin.zip \
    && unzip gradle-8.6-bin.zip -d /opt \
    && rm gradle-8.6-bin.zip \
    && ln -s /opt/gradle-8.6/bin/gradle /usr/bin/gradle

WORKDIR /app

COPY build.gradle settings.gradle gradlew gradlew.bat /app/
COPY gradle /app/gradle
COPY src /app/src

RUN ./gradlew shadowJar

COPY build/libs/app-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
