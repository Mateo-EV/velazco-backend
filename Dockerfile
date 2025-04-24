FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x ./mvnw
RUN ./mvnw -B -DskipTests clean install

EXPOSE 8080

CMD ["java", "-jar", "target/velazco-backend-0.0.1-SNAPSHOT.jar"]
