FROM openjdk:11

VOLUME /logs

#ADD target/lib

ARG JAR_FILE
ADD target/${JAR_FILE} /app.jar

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#ENTRYPOINT ["java","-Xms256m -Xmx256m -Xmn125m -Xss1m","-jar","/app.jar"]
ENTRYPOINT ["java","-jar","/app.jar"]
