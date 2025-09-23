# build
FROM ghcr.io/graalvm/jdk-community:21 AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN microdnf install -y findutils && microdnf clean all
RUN chmod +x gradlew
RUN ./gradlew bootJar

# run
FROM ghcr.io/graalvm/jdk-community:21 AS run
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
