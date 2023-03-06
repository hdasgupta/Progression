#

FROM ubuntu:java

COPY ./target/Progression-1.0-SNAPSHOT.jar .

EXPOSE 80

ENTRYPOINT java -jar Progression-1.0-SNAPSHOT.jar

