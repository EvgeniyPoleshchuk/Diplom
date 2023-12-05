FROM openjdk:18-jdk-alpine3.13

EXPOSE 8081

ADD target/Diplom-0.0.1-SNAPSHOT.jar diplom.jar

ENTRYPOINT ["java", "-jar", "diplom.jar"]