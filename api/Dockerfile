FROM openjdk:8u242-jre
VOLUME /tmp
COPY target/data*.jar dc_api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "dc_api.jar"]
