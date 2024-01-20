# Jar Building
FROM .../gradle:latest as builder
RUN mkdir /appbuild
COPY . /appbuild
WORKDIR /appbuild
RUN gradle :kafkatool-web:clean \
    && gradle :kafkatool-web:build

# Container Setup
FROM .../adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine
ARG APIGEE_CLIENT_ID
ARG APIGEE_CLIENT_SECRET
ARG KAFKA_KEYSTORE_PASSWORD
ARG KAFKA_KEY_PASSWORD
ARG KAFKA_TRUSTSTORE_PASSWORD
ARG KAFKA_KEYSTORE_PATH
ARG KAFKA_KEYSTORE_FILE
ARG HTTP_PROXY
ARG HTTPS_PROXY
ARG NO_PROXY
# Copy certs from local to the container
RUN mkdir -p usr/app/ssl/certs/
COPY etc/paas/openshift/certs/somekeystorecert.jks usr/app/ssl/certs/
COPY etc/paas/openshift/certs/sometruststorecert.jks usr/app/ssl/certs/
RUN mkdir /app \
    && mkdir /app/resources
COPY --from=builder /appbuild/kafkatool-web/build/libs/kafkatool-webapp.jar /app/kafkatool-webapp.jar
COPY --from=builder /appbuild/kafkatool-web/src/main/resources/ /app/resources/
COPY --from=builder /appbuild/kafkatool-web/wwwroot /app/wwwroot/
COPY /etc /app/etc/
WORKDIR /app
ENV apigeeClientId "$APIGEE_CLIENT_ID"
ENV apigeeClientSecret "$APIGEE_CLIENT_SECRET"
ENV kafkaKeystorePassword "$KAFKA_KEYSTORE_PASSWORD"
ENV kafkaKeyPassword "$KAFKA_KEY_PASSWORD"
ENV kafkaTruststorePassword "$KAFKA_TRUSTSTORE_PASSWORD"
ENV kafkaKeystorePath "$KAFKA_KEYSTORE_PATH"
ENV kafkaKeystoreFile "$KAFKA_KEYSTORE_FILE"
ENV HTTP_PROXY "$HTTP_PROXY"
ENV HTTPS_PROXY "$HTTPS_PROXY"
ENV NO_PROXY "$NO_PROXY"
CMD [ "java", "-jar", "-Djavax.net.ssl.trustStore=etc/paas/openshift/certs/sometruststorecert.jks", "-Djavax.net.ssl.trustStorePassword=pass", "kafkatool-webapp.jar", "-config=resources/application.conf" ]

