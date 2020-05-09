FROM openjdk:14-alpine
COPY build/libs/cva-retrieval-pipeline-*-all.jar cva-retrieval-pipeline.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "cva-retrieval-pipeline.jar"]
