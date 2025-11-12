FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew ./
COPY gradle gradle
COPY settings.gradle* ./
COPY build.gradle* ./
RUN chmod +x gradlew

RUN ./gradlew --no-daemon build -x test || true

COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

ENV TZ=America/Bogota

ENV JAVA_OPTS="-Xms256m -Xmx512m"

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8082

ENV DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=ordersdb \
    DB_USER=postgres \
    DB_PASSWORD=postgres

ENV SPRING_APPLICATION_NAME=order-service \
    SERVER_PORT=8082 \
    SPRING_DATASOURCE_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
    SPRING_DATASOURCE_USERNAME=${DB_USER} \
    SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}

ENV EUREKA_URL=http://eureka:8761/eureka/
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=${EUREKA_URL} \
    EUREKA_CLIENT_REGISTER-WITH-EUREKA=true \
    EUREKA_CLIENT_FETCH-REGISTRY=true

ENV FEIGN_CLIENT_CONFIG_DEFAULT_CONNECTTIMEOUT=5000 \
    FEIGN_CLIENT_CONFIG_DEFAULT_READTIMEOUT=5000 \
    FEIGN_CLIENT_CONFIG_DEFAULT_LOGGERLEVEL=FULL

ENV CLIENTS_PRODUCT_URL=http://product-service:8080 \
    CLIENTS_PRODUCT_BASIC_USERNAME=service_order \
    CLIENTS_PRODUCT_BASIC_PASSWORD=secret123

ENV USER_CLIENT_AUTH_TOKEN="MI_TOKEN_DE_SERVICIO"

ENV SPRING_MAIL_HOST=smtp.gmail.com \
    SPRING_MAIL_PORT=587 \
    SPRING_MAIL_USERNAME="" \
    SPRING_MAIL_PASSWORD="" \
    SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true \
    SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

ENV APP_NOTIFICATIONS_FROM=arkaproyect0921@gmail.com \
    SECURITY_JWT_SECRET=short_secret_key_32_chars_len!!!

ENV SPRINGDOC_API_DOCS_PATH=/v3/api-docs \
    SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html

ENV LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG \
    LOGGING_LEVEL_ORG_HIBERNATE_SQL=DEBUG \
    LOGGING_LEVEL_ORG_HIBERNATE_TYPE_DESCRIPTOR_SQL_BASICBINDER=TRACE \
    LOGGING_LEVEL_FEIGN=DEBUG

RUN adduser --system --group spring && chown -R spring:spring /app
USER spring

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
