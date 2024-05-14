FROM jelastic/maven:3.9.5-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests


FROM openjdk:21-jdk
COPY --from=build /target/cursor_clash_backend.jar cursor_clash_backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cursor_clash_backend.jar"]

