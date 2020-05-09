FROM openjdk:14-alpine
COPY build/libs/ecmwf-telegram-*-all.jar ecmwf-telegram.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "ecmwf-telegram.jar"]
