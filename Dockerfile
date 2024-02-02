FROM eclipse-temurin:17-jdk-alpine
COPY /target/upload-download-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
