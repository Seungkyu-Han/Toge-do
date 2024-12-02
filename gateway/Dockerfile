FROM openjdk:17-jdk

WORKDIR /gateway

VOLUME /gateway

ARG JAR_FILE=presentation/build/libs/presentation.jar

COPY ${JAR_FILE} gateway.jar

EXPOSE 12040

ENTRYPOINT ["java", "-jar", "gateway.jar"]