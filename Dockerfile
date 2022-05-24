FROM adoptopenjdk/openjdk11:alpine AS build

WORKDIR /home/app

COPY build.gradle.kts settings.gradle.kts gradlew gradlew.bat ./
COPY gradle gradle/
COPY src src/

RUN ./gradlew bootJar

RUN mv build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
