FROM gradle:8.14.3-jdk21-alpine AS builder

WORKDIR /app

COPY transaction-service ./transaction-service
COPY security-lib ./security-lib
COPY company-observability-starter ./company-observability-starter

WORKDIR /app/transaction-service

RUN gradle build --no-daemon -x test -x checkstyleMain -x checkstyleTest

RUN jlink \
      --add-modules java.base,java.compiler,java.instrument,java.logging,java.management,java.management.rmi,java.naming,java.net.http,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql,java.xml,jdk.crypto.cryptoki,jdk.crypto.ec,jdk.httpserver,jdk.management,jdk.naming.dns,jdk.unsupported,jdk.zipfs \
      --no-header-files \
      --no-man-pages \
      --compress=zip-6 \
      --strip-debug \
      --output /jre

FROM alpine:3.21

COPY --from=builder /jre /usr/local/java

ENV JAVA_HOME=/usr/local/java
ENV PATH="$JAVA_HOME/bin:$PATH"

RUN apk add --no-cache curl && \
    addgroup -S app && adduser -S app -G app

WORKDIR /app

COPY --from=builder /app/transaction-service/build/libs/*.jar app.jar

RUN chown app:app app.jar

USER app

ARG SERVER_PORT=8087
EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
