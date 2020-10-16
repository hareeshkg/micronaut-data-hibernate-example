FROM openjdk:14-alpine
COPY target/airport-service-*.jar airport-service.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "airport-service.jar"]