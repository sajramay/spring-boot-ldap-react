FROM openjdk:11-jre-slim
COPY /target/spring-boot-react-0.0.1-SNAPSHOT.jar /home/spring-boot-react.jar
CMD ["java", "-jar", "/home/spring-boot-react.jar"]
