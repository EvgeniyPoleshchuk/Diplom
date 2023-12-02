FROM openjdk:17-jdk-alpine
ADD target/Diplom-0.0.1-SNAPSHOT.jar diplom.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","diplom.jar"]