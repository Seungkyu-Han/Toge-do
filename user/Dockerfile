FROM openjdk:17-jdk

RUN mkdir /image

WORKDIR /user

VOLUME /user

ARG JAR_FILE=presentation/build/libs/presentation.jar

COPY ${JAR_FILE} user.jar

EXPOSE 12041

ENTRYPOINT ["java", "-jar", "user.jar"]