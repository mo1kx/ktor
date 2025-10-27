FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

RUN gradle build -x test --no-daemon || return 0

COPY src ./src
RUN gradle build -x test --no-daemon || return 0
RUN gradle clean build -x test --no-daemon

FROM openjdk:17-jre-slim

WORKDIR /app

COPY --from=build /app/build/libs/*all.jar app.jar

ENV DATABASE_URL=jdbc:postgresql://localhost:5432/ktor_sample
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres
ENV JWT_SECRET=your-secret-key-change-in-production
ENV PORT=8080

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

