FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Gunakan mirror resmi Google agar tidak terkena blokir 429 Too Many Requests
RUN mkdir -p /root/.m2 && echo '<settings><mirrors><mirror><id>google-maven-central</id><name>GCS Maven Central mirror</name><url>https://maven-central.storage-download.googleapis.com/maven2</url><mirrorOf>central</mirrorOf></mirror></mirrors></settings>' > /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx350m", "-Xms256m", "-jar", "app.jar"]