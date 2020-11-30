FROM openjdk:11
VOLUME /logs
ADD ${JAR_FILE} app.jar
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-d64 -Xms128m -Xmx128m -Xmn32m -Xss1m","-jar","/app.jar"]
