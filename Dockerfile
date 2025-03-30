FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/birthday-reminder-bot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

# Ожидание готовности БД перед запуском приложения
CMD ["sh", "-c", "echo 'Waiting for MySQL...' && sleep 10 && java -jar app.jar"]
