FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

RUN echo '#!/bin/bash\nif [ -z "$SPRING_DATASOURCE_URL" ] && [ ! -z "$DB_HOST" ]; then\n  export SPRING_DATASOURCE_URL="jdbc:postgresql://$DB_HOST:${DB_PORT:-5432}/$DB_NAME"\nfi\nexec java -jar app.jar --spring.profiles.active=prod\n' > /app/entrypoint.sh && chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
