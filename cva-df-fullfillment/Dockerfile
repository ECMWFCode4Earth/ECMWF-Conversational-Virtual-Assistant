FROM openjdk:14-alpine
COPY build/libs/cva-df-fullfillment-*-all.jar cva-df-fullfillment.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "cva-retrieval-pipeline.jar"]
