FROM openjdk:18
COPY ~/target/Investment_CQRS-0.0.1-SNAPSHOT.jar ~/target/
WORKDIR ~/target/

CMD java -jar Investment_CQRS-0.0.1-SNAPSHOT.jar