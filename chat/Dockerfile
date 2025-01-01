FROM openjdk:17-jdk

WORKDIR /chat

VOLUME /chat

ARG JAR_FILE=presentation/build/libs/presentation.jar

COPY ${JAR_FILE} chat.jar

EXPOSE 12044

ENTRYPOINT ["java", "-jar", "chat.jar"]