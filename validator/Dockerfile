FROM amazoncorretto:17 as build
COPY . /srv
WORKDIR /srv

RUN ./mvnw -DskipTests package

FROM amazoncorretto:17

COPY --from=build /srv/target/validator.jar /srv/
EXPOSE 5401

ENTRYPOINT [ "java","-jar","/srv/validator.jar"]