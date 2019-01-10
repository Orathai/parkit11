#RESTFul webservice

## To be able to run the project, first check out the project

### Option 1: Run with Maven

- Java 11 openjdk
- Maven : https://maven.apache.org/install.html

```
❯ mvn clean install
❯ mvn spring-boot:run

```

### Option 2: Run with Docker

Build Docker image and run the container locally

```
mvn clean package docker:build

❯ docker images
REPOSITORY                                  TAG                 IMAGE ID            CREATED             SIZE
io.dynamicus/parkit11                       latest              164c2ae5cac9        2 hours ago         508 MB

docker run -it --rm -p 9000:9000 io.dynamicus/parkit11:latest

```
Check running container

```
❯ docker ps -a
CONTAINER ID        IMAGE                          COMMAND                  CREATED             STATUS                    PORTS                    NAMES
33ec31f45f4a        io.dynamicus/parkit11:latest   "sh -c 'java $JAVA..."   13 seconds ago      Up 12 seconds             0.0.0.0:9000->9000/tcp   elated_spence
```

### Check out API and documentation

```
curl -i 'http://localhost:9000/api/price?minutes=<INTEGER>&zone=<M1,M2>'

```
http://localhost:9000/docs/index.html

### References

- Spring-boot : https://projects.spring.io/spring-boot
- Spring REST Docs
- Docker


