FROM openjdk:18
COPY /home/circleci/project/target/Investment_CQRS-0.0.1-SNAPSHOT.jar /home/circleci/project/target/
WORKDIR /home/circleci/project/target/
CMD java -jar Investment_CQRS-0.0.1-SNAPSHOT.jar