FROM openjdk:17-jdk

WORKDIR /schedule

VOLUME /schedule

ARG JAR_FILE=presentation/build/libs/presentation.jar

COPY ${JAR_FILE} schedule.jar

EXPOSE 12043

ENTRYPOINT ["java", "-jar", "schedule.jar"]