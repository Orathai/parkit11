FROM openjdk:11.0.1-jdk-slim-stretch

VOLUME /tmp

ADD parkit11-1.0-SNAPSHOT.jar parkit11.jar

RUN sh -c 'touch /parkit11.jar'

ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /parkit11.jar" ]