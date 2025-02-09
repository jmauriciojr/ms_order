# Build stage
FROM maven AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Package stage
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /home/app/target/*.jar ms_order.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","ms_order.jar"]