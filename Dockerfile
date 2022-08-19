FROM openjdk:18
COPY target/Investment.jar target/
WORKDIR target/

CMD java -jar Investment.jar