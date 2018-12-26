FROM anapsix/alpine-java:8
COPY /target/spring-boot-react-0.0.1-SNAPSHOT.jar /home/spring-boot-react.jar
CMD ["java", "-jar", "/home/spring-boot-react.jar"]
