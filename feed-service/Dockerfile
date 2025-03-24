FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY /build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=native

ENTRYPOINT ["java", "-jar", "app.jar"]