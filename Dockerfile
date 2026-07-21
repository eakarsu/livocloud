FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline
COPY src ./src
COPY test ./test
COPY WebContent ./WebContent
RUN mvn -B clean verify

FROM tomcat:9.0-jdk11-temurin-jammy
RUN rm -rf /usr/local/tomcat/webapps/* \
    && groupadd --system livocloud \
    && useradd --system --gid livocloud --home-dir /usr/local/tomcat livocloud \
    && chown -R livocloud:livocloud /usr/local/tomcat
COPY --from=build --chown=livocloud:livocloud \
    /workspace/target/livocloud-0.0.1-SNAPSHOT.war \
    /usr/local/tomcat/webapps/LivoCloud.war
USER livocloud
EXPOSE 8080
CMD ["catalina.sh", "run"]
