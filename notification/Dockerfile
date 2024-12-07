FROM openjdk:17-jdk

WORKDIR /notification

VOLUME /notification

ARG JAR_FILE=presentation/build/libs/presentation.jar

COPY ${JAR_FILE} notification.jar

EXPOSE 12042

ENTRYPOINT ["java", "-jar", "notification.jar"]